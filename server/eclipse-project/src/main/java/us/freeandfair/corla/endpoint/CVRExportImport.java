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
import java.util.Map;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

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
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class CVRExportImport extends AbstractCountyDashboardEndpoint {
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
   * @param the_response The response object (for error reporting).
   * @param the_county_id The county ID.
   * @param the_timestamp The timestamp.
   */
  private void updateCountyDashboard(final Response the_response, 
                                     final Long the_county_id, 
                                     final Instant the_timestamp) {
    final CountyDashboard cdb = Persistence.getByID(the_county_id, CountyDashboard.class);
    if (cdb == null) {
      serverError(the_response, "could not locate county dashboard");
    } else {
      cdb.setCVRUploadTimestamp(the_timestamp);
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
  // the CSV parser can throw arbitrary runtime exceptions, which we must catch
  @SuppressWarnings({"PMD.AvoidCatchingGenericException"})
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
        updateCountyDashboard(the_response, the_file.countyID(), the_file.timestamp());
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
        Main.LOGGER.info("could not parse malformed CVR export file " + the_file.id());
        badDataContents(the_response, "malformed CVR export file " + the_file.id());
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
      ufs.stop();
      // we have to halt manually because a transaction failure doesn't halt
      halt(the_response);
    } catch (final RuntimeException | IOException e) {
      Main.LOGGER.info("could not parse malformed CVR export file " + the_file.id() + 
                       ": " + e);
      try {
        cleanup(the_file.countyID());
      } catch (final PersistenceException ex) {
        // if we couldn't clean up, there's not much we can do about it
      }
      badDataContents(the_response, "malformed CVR export file " + the_file.id());
    }
    
    ufs.stop();
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
    final County county = Authentication.authenticatedCounty(the_request);

    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for CVR export upload");
      return my_endpoint_result.get();
    }
    
    try {
      final UploadedFile file =
          Main.GSON.fromJson(the_request.body(), UploadedFile.class);
      if (file == null) {
        badDataContents(the_response, "nonexistent file");
      } else if (!file.countyID().equals(county.id())) {
        unauthorized(the_response, "county " + county.id() + " attempted to import " + 
                                   "file uploaded by county " + file.countyID());
      } else if (file.hashStatus() == HashStatus.VERIFIED) {
        parseFile(the_response, file);
      } else {
        badDataContents(the_response, "attempt to import a file without a verified hash");
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
    }
    
    return my_endpoint_result.get();
  }
}
