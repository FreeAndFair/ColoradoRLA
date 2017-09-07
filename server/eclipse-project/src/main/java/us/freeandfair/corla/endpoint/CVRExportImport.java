/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.UPLOAD_CVRS_EVENT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.HaltException;
import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.util.UploadedFileStreamer;

/**
 * The "CVR export import" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports",
    "PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
    "PMD.StdCyclomaticComplexity"})
public class CVRExportImport extends AbstractCountyDashboardEndpoint {
  /**
   * The " (id " string.
   */
  private static final String PAREN_ID = " (id ";

  /**
   * The static set of counties that are currently running imports. This is
   * used to prevent multiple counties from importing CVRs at the same time, 
   * which would cause issues since this endpoint is not a single transaction.
   */
  private static final Set<Long> COUNTIES_RUNNING = new HashSet<Long>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/import-cvr-export";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return UPLOAD_CVRS_EVENT;
  }

  /**
   * Updates the appropriate county dashboard to reflect a new 
   * CVR export upload.
   * 
   * @param the_response The response object (for error reporting).
   * @param the_county_id The county ID.
   * @param the_timestamp The timestamp.
   * @param the_ballots_cast The number of ballots in the CVR export.
   */
  private void updateCountyDashboard(final Response the_response, 
                                     final Long the_county_id, 
                                     final Instant the_timestamp,
                                     final Integer the_ballots_cast) {
    final CountyDashboard cdb = Persistence.getByID(the_county_id, CountyDashboard.class);
    if (cdb == null) {
      serverError(the_response, "could not locate county dashboard");
    } else {
      cdb.setCVRUploadTimestamp(the_timestamp);
      cdb.setCVRsImported(the_ballots_cast);
      try {
        Persistence.saveOrUpdate(cdb);
      } catch (final PersistenceException e) {
        serverError(the_response, "could not update county dashboard");
      }
    }
  }
  
  /**
   * Parses an uploaded CVR export and attempts to persist it to the database.
   * 
   * @param the_response The response (for error reporting).
   * @param the_file The uploaded file.
   */
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidRethrowingException"})
  private void parseFile(final Response the_response, final UploadedFile the_file) {
    final UploadedFileStreamer ufs = new UploadedFileStreamer(the_file);
    @SuppressWarnings("PMD.DoNotUseThreads")
    final Thread thread = new Thread(ufs);
    thread.start();
    
    try {
      final InputStreamReader bmi_isr = new InputStreamReader(ufs.inputStream(), "UTF-8");
      final DominionCVRExportParser parser = 
          new DominionCVRExportParser(bmi_isr, 
                                      Persistence.getByID(the_file.countyID(), County.class),
                                      true);
      int deleted = 0;
      try {
        deleted = cleanup(the_file.countyID());
      } catch (final PersistenceException ex) {
        transactionFailure(the_response, "unable to delete previously uploaded CVRs");
        // we have to halt manually
        halt(the_response);
      }
      if (parser.parse()) {
        final int imported = parser.recordCount().getAsInt();
        Main.LOGGER.info(imported + " CVRs parsed from file " + the_file.id());
        updateCountyDashboard(the_response, the_file.countyID(), 
                              the_file.timestamp(), imported);
        the_file.setStatus(FileStatus.IMPORTED_AS_CVR_EXPORT);
        Persistence.saveOrUpdate(the_file);
        final Map<String, Integer> response = new HashMap<String, Integer>();
        response.put("records_imported", imported);
        if (deleted > 0) {
          response.put("records_deleted", deleted);
        }
        okJSON(the_response, Main.GSON.toJson(response));
      } else {
        try {
          cleanup(the_file.countyID());
        } catch (final PersistenceException e) {
          // if we couldn't clean up, there's not much we can do about it
        }
        Main.LOGGER.info(parser.errorMessage() + " [file " + 
            the_file.filename() + PAREN_ID + the_file.id() + ")]");
        badDataContents(the_response, parser.errorMessage() + " [file " + 
                                      the_file.filename() + PAREN_ID + the_file.id() + ")]");
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.info("parse transactions did not complete successfully, " + 
                       "attempting cleanup");
      try {
        cleanup(the_file.countyID());
      } catch (final PersistenceException ex) {
        // if we couldn't clean up, there's not much we can do about it
      }
      transactionFailure(the_response, "cvr import transaction failed: " + e.getMessage());
      // we have to halt manually because a transaction failure doesn't halt
      halt(the_response);
    } catch (final HaltException e) {
      // we don't want to intercept these, so we just rethrow it
      throw e;
    } catch (final RuntimeException | IOException e) {
      Main.LOGGER.info("could not parse malformed CVR export file " + 
                       the_file.filename() + PAREN_ID + the_file.id() +
                       "): " + e);
      try {
        cleanup(the_file.countyID());
      } catch (final PersistenceException ex) {
        // if we couldn't clean up, there's not much we can do about it
      }
      badDataContents(the_response, "malformed CVR export file " + 
                                    the_file.filename() + PAREN_ID + the_file.id() + ")");
    } finally {
      ufs.stop();
    }
  }

  /**
   * Attempts to wipe all CVR records for a specific county. This ends any current
   * transaction, does the delete in its own transaction, and starts a new 
   * transaction so that one is open at all times during endpoint execution.
   * 
   * @param the_county_id The county ID to wipe.
   * @return the number of deleted CVR records, if any were deleted.
   * @exception PersistenceException if the wipe was unsuccessful.
   */
  private int cleanup(final Long the_county_id) {
    if (Persistence.isTransactionActive()) {
      Persistence.commitTransaction();
    }
    Persistence.beginTransaction();
    final int result = 
        CastVoteRecordQueries.deleteMatching(the_county_id, RecordType.UPLOADED);
    CountyContestResultQueries.deleteForCounty(the_county_id);
    final CountyDashboard cdb = Persistence.getByID(the_county_id, CountyDashboard.class);
    cdb.setCVRUploadTimestamp(null);
    Persistence.commitTransaction();
    Persistence.beginTransaction();
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"PMD.ConfusingTernary"})
  public String endpoint(final Request the_request, final Response the_response) {    
    // we know we have county authorization, so let's find out which county
    final County county = Main.authentication().authenticatedCounty(the_request);

    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for CVR export upload");
      return my_endpoint_result.get();
    }
    
    // check to be sure that the same county isn't in the middle of a CVR import
    synchronized (COUNTIES_RUNNING) {
      if (COUNTIES_RUNNING.contains(county.id())) {
        transactionFailure(the_response, 
                           "county " + county.id() + " is already importing CVRs, try later");
        // for a transaction failure, we have to halt explicitly
        halt(the_response);
      }
      // signal that we're starting the import
      COUNTIES_RUNNING.add(county.id());
    }
    
    try {
      final UploadedFile file =
          Main.GSON.fromJson(the_request.body(), UploadedFile.class);
      if (file == null) {
        badDataContents(the_response, "nonexistent file");
      } else if (!file.countyID().equals(county.id())) {
        unauthorized(the_response, "county " + county.id() + " attempted to import " + 
                                   "file " + file.filename() + "uploaded by county " + 
                                   file.countyID());
      } else if (file.hashStatus() == HashStatus.VERIFIED) {
        parseFile(the_response, file);
      } else {
        badDataContents(the_response, "attempt to import a file without a verified hash");
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
    } finally {
      // signal that we're done with the import
      synchronized (COUNTIES_RUNNING) {
        COUNTIES_RUNNING.remove(county.id());
      }
    }
    
    return my_endpoint_result.get();
  }
}
