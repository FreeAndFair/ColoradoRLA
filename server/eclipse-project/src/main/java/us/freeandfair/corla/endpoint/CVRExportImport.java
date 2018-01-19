/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.IMPORT_CVRS_EVENT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.codehaus.plexus.util.ExceptionUtils;

import com.google.gson.JsonParseException;

import spark.HaltException;
import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.ImportStatus;
import us.freeandfair.corla.model.ImportStatus.ImportState;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.util.ExponentialBackoffHelper;
import us.freeandfair.corla.util.UploadedFileStreamer;

/**
 * The "CVR export import" endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports",
    "PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
    "PMD.StdCyclomaticComplexity", "PMD.GodClass", "PMD.DoNotUseThreads"})
public class CVRExportImport extends AbstractCountyDashboardEndpoint {
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
    return IMPORT_CVRS_EVENT;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"PMD.ConfusingTernary"})
  public String endpointBody(final Request the_request, final Response the_response) {    
    // we know we have county authorization, so let's find out which county
    final County county = Main.authentication().authenticatedCounty(the_request);

    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for CVR import");
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
    }
    
    try {
      final UploadedFile file =
          Main.GSON.fromJson(the_request.body(), UploadedFile.class);
      if (file == null) {
        badDataContents(the_response, "nonexistent file");
      } else if (!file.county().id().equals(county.id())) {
        unauthorized(the_response, "county " + county.id() + " attempted to import " + 
                                   "file " + file.filename() + "uploaded by county " + 
                                   file.county().id());
      } else if (file.hashStatus() == HashStatus.VERIFIED) {
        // make sure the old CVR file is now marked as "not imported", since the CVRs
        // will be wiped
        final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
        if (cdb.cvrFile() != null) {
          cdb.cvrFile().setStatus(FileStatus.NOT_IMPORTED);
          Persistence.saveOrUpdate(cdb.cvrFile());
        }
        final Map<String, Instant> result = new HashMap<>();
        result.put("import_start_time", Instant.now());
        // spawn a thread to do the import; this endpoint always immediately 
        // returns a successful result if we get to this point
        synchronized (COUNTIES_RUNNING) {
          // signal that we're starting the import
          COUNTIES_RUNNING.add(county.id());
        }
        (new Thread(new CVRImporter(file))).start();
        
        okJSON(the_response, Main.GSON.toJson(result));
      } else {
        badDataContents(the_response, "attempt to import a file without a verified hash");
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request: " + e.getMessage());
    }
    
    return my_endpoint_result.get();
  }
  
  /**
   * @return the COUNTIES_RUNNING set.
   */
  protected static final Set<Long> countiesRunning() {
    return COUNTIES_RUNNING;
  }
  
  /**
   * The (internal) exception that gets thrown when a CVR import fails.
   */
  private static class CVRImportException extends RuntimeException {
    private static final long serialVersionUID = 1;
    
    /**
     * Constructs a new CVRImportException with the specified description.
     * 
     * @param the_description The description.
     */
    CVRImportException(final String the_description) {
      super(the_description);
    }
  }
  
  /**
   * The Runnable class that implements the actual CVR import. 
   */
  private static class CVRImporter implements Runnable {
    /**
     * The valid states in which CVR imports can cause state changes.
     */
    private static final List<ASMState> VALID_STATES =
        Arrays.asList(CountyDashboardState.CVRS_IMPORTING,
                      CountyDashboardState.BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING);
    
    /**
     * The " in " string.
     */
    private static final String IN = " in ";
    
    /**
     * The " tries" string.
     */
    private static final String TRIES = " tries";
    
    /**
     * The ", county " string.
     */
    private static final String COUNTY = ", county ";
    
    /**
     * The number of times to retry a DoS dashboard update operation.
     */
    private static final int UPDATE_RETRIES = 15;
    
    /**
     * The number of milliseconds to sleep between transaction retries.
     */
    private static final long TRANSACTION_SLEEP_MSEC = 10;
    
    /**
     * The " (id " string.
     */
    private static final String PAREN_ID = " (id ";
    
    /**
     * The file that this importer is importing.
     */
    private final UploadedFile my_file;
    
    /**
     * Constructs a new CVRImporter for the specified county and file.
     * 
     * @param the_file The file.
     */
    CVRImporter(final UploadedFile the_file) {
      my_file = the_file;
    }
    
    /**
     * The run method for this CVRImporter.
     */
    public void run() {
      try {
        // this outer try block is the "last resort" cleanup block
        Persistence.beginTransaction();
        try {
          parseFile(my_file);
          updateStateMachine(true);
          Persistence.commitTransaction();
          Main.LOGGER.info("CVR import complete for county " + my_file.county().id());
        } catch (final PersistenceException e) {
          // the import failed for DB reasons, so clean up
          Main.LOGGER.error("CVR import failed for county " + my_file.county().id() + ": " + 
              ExceptionUtils.getStackTrace(e));
          cleanup(my_file.county(), true, "import failed because of database problem");
          updateStateMachine(false);
          Persistence.commitTransaction();
        } catch (final CVRImportException e) {
          // we intentionally failed the import, so clean up
          Main.LOGGER.error("CVR import failed for county " + my_file.county().id() + ": " + 
              ExceptionUtils.getStackTrace(e));
          cleanup(my_file.county(), true, e.getMessage());
          updateStateMachine(false);
          Persistence.commitTransaction();
        } 
      } catch (final PersistenceException | CVRImportException e) {
        // at this point, either a cleanup failed or a state machine update failed,
        // so log it and say there's nothing we can do
        
        Main.LOGGER.error("Critical CVR import error for county " + 
                          my_file.county().id() +
                          ", system may be in unstable state: " + e);
        if (Persistence.canTransactionRollback()) {
          Persistence.rollbackTransaction();
        }
      } finally {
        // release the lock on CVR imports for this county
        final Set<Long> counties_running = countiesRunning();
        synchronized (counties_running) {
          // signal that we're ending the import
          counties_running.remove(my_file.county().id());
        }
      }
    }
    
    /**
     * Aborts the import with the specified error description.
     * 
     * @param the_description The error description.
     * @exception CVRImportException always, to cancel execution
     */
    private void error(final String the_description) 
        throws CVRImportException {
      throw new CVRImportException(the_description);
    }
    
    /**
     * Updates the county state machine based on whether the import succeeded
     * or failed.
     * 
     * @param the_success_flag true if the import was successful, false otherwise.
     */
    private void updateStateMachine(final boolean the_success_flag) {
      if (Persistence.isTransactionActive()) {
        Persistence.commitTransaction();
      }
      final String status;
      final ASMEvent event;
      if (the_success_flag) {
        status = "successful";
        event = CountyDashboardEvent.CVR_IMPORT_SUCCESS_EVENT;
      } else {
        status = "unsuccessful";
        event = CountyDashboardEvent.CVR_IMPORT_FAILURE_EVENT;
      }
      Main.LOGGER.info("updating county " + my_file.county().id() + " state after " +
                       status + " CVR import");
      boolean success = false;
      int retries = 0;
      while (!success && retries < UPDATE_RETRIES) {
        try {
          retries = retries + 1;
          Main.LOGGER.debug("updating state machine, attempt " + retries + 
                            COUNTY + my_file.county().id());
          Persistence.beginTransaction();
          final CountyDashboardASM cdb_asm = 
              ASMUtilities.asmFor(CountyDashboardASM.class, my_file.county().id().toString());
          if (VALID_STATES.contains(cdb_asm.currentState())) {
            // the dashboard is in a state we can legitimately change, which means
            // the actual import endpoint committed its transaction
            cdb_asm.stepEvent(event);
            ASMUtilities.save(cdb_asm);
            Persistence.commitTransaction();
            success = true;
          } else {
            // the dashboard is not in a state we can legitimately change, so let's
            // wait until it is
            Persistence.rollbackTransaction();
            // let's give other transactions time to breathe
            try {
              final long delay = 
                  ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
              Main.LOGGER.info("waiting for county " + my_file.county().id() + 
                               " state update, retrying in " + delay + "ms");
              Thread.sleep(delay);
            } catch (final InterruptedException ex) {
              // it's OK to be interrupted
            }
          }
        } catch (final PersistenceException e) {
          // something went wrong, let's try again
          if (Persistence.canTransactionRollback()) {
            try {
              Persistence.rollbackTransaction();
            } catch (final PersistenceException ex) {
              // not much we can do about it
            }
          }
          // let's give other transactions time to breathe
          try {
            final long delay = 
                ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
            Main.LOGGER.info("retrying state update for county " + 
                              my_file.county().id() + IN + delay + "ms");
            Thread.sleep(delay);          
          } catch (final InterruptedException ex) {
            // it's OK to be interrupted
          }
        }
      }
      // we always need a running transaction
      Persistence.beginTransaction();
      if (success && retries > 1) {
        Main.LOGGER.info("updated state machine for county " + my_file.county().id() + 
                         IN + retries + TRIES);
      } else if (!success) {
        error("could not update state machine for county " + my_file.county().id() + 
              " after " + retries + TRIES);
      } 
    }
    
    /**
     * Updates the appropriate county dashboard to reflect a new 
     * CVR export upload.
     * 
     * @param the_file The uploaded CVR file.
     * @param the_status The import status.
     * @param the_cvrs_imported The number of CVRs imported.
     */
    private void updateCountyDashboard(final UploadedFile the_file,
                                       final ImportStatus the_status,
                                       final Integer the_cvrs_imported) {
      if (Persistence.isTransactionActive()) {
        Persistence.commitTransaction();
      }
      boolean success = false;
      int retries = 0;
      while (!success && retries < UPDATE_RETRIES) {
        try {
          retries = retries + 1;
          Main.LOGGER.debug("updating county dashboard, attempt " + retries + 
                            COUNTY + my_file.county().id());
          Persistence.beginTransaction();
          final CountyDashboard cdb = 
              Persistence.getByID(the_file.county().id(), CountyDashboard.class);
          if (cdb == null) {
            error("could not locate county dashboard");
          } else {
            cdb.setCVRFile(the_file);
            cdb.setCVRImportStatus(the_status);
            cdb.setCVRsImported(the_cvrs_imported);
            Persistence.saveOrUpdate(cdb);
          } 
          Persistence.commitTransaction();
          success = true;
        } catch (final PersistenceException e) {
          // something went wrong, let's try again
          if (Persistence.canTransactionRollback()) {
            try {
              Persistence.rollbackTransaction();
            } catch (final PersistenceException ex) {
              // not much we can do about it
            }
          }
          // let's give other transactions time to breathe
          try {
            final long delay = 
                ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
            Main.LOGGER.info("retrying county " + my_file.county().id() + 
                             " dashboard update in " + delay + "ms");
            Thread.sleep(delay);         
          } catch (final InterruptedException ex) {
            // it's OK to be interrupted
          }
        }
      }
      // we always need a running transaction
      Persistence.beginTransaction();
      if (success && retries > 1) {
        Main.LOGGER.info("updated state machine for county " + my_file.county().id() + 
                         IN + retries + TRIES);
      } else if (!success) {
        error("could not update state machine for county " + my_file.county().id() + 
              " after " + retries + TRIES);
      } 
    }
    
    /**
     * Parses an uploaded CVR export and attempts to persist it to the database.
     * 
     * @param the_file The uploaded file.
     */
    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidRethrowingException"})
    private void parseFile(final UploadedFile the_file) {
      final UploadedFileStreamer ufs = new UploadedFileStreamer(the_file);
      (new Thread(ufs)).start();
        
      try {
        final InputStreamReader bmi_isr = new InputStreamReader(ufs.inputStream(), "UTF-8");
        final DominionCVRExportParser parser = 
            new DominionCVRExportParser(bmi_isr, 
                                        Persistence.getByID(the_file.county().id(), 
                                                            County.class),
                                        Main.properties(),
                                        true);
        try {
          final int deleted = cleanup(the_file.county());
          if (deleted > 0) {
            Main.LOGGER.info("deleted " + deleted + " previously-uploaded CVRs");
          }
        } catch (final PersistenceException ex) {
          error("unable to delete previously uploaded CVRs");
        }
        
        updateCountyDashboard(the_file, new ImportStatus(ImportState.IN_PROGRESS), 0);
                
        if (parser.parse()) {
          final int imported = parser.recordCount().getAsInt();
          Main.LOGGER.info(imported + " CVRs parsed from file " + the_file.id() + 
                           " for county " + the_file.county().id());
          updateCountyDashboard(the_file, new ImportStatus(ImportState.SUCCESSFUL), imported);
          handleTies(the_file.county());
          the_file.setStatus(FileStatus.IMPORTED_AS_CVR_EXPORT);
          Persistence.saveOrUpdate(the_file);
        } else {
          try {
            cleanup(the_file.county(), true, parser.errorMessage());
          } catch (final PersistenceException e) {
            error("couldn't clean up after " + parser.errorMessage() + " [file " + 
                the_file.filename() + PAREN_ID + the_file.id() + ")]");
          }
          error(parser.errorMessage() + " [file " + 
                the_file.filename() + PAREN_ID + the_file.id() + ")]");
        }
      } catch (final PersistenceException e) {
        Main.LOGGER.info("parse transactions did not complete successfully, " + 
                         "attempting cleanup");
        try {
          cleanup(the_file.county(), true, "could not clean up");
        } catch (final PersistenceException ex) {
          // if we couldn't clean up, there's not much we can do about it
        }
        error("cvr import transaction failed: " + e.getMessage());
      } catch (final HaltException e) {
        // we don't want to intercept these, so we just rethrow it
        throw e;
      } catch (final RuntimeException | IOException e) {
        Main.LOGGER.info("could not parse malformed CVR export file " + 
                         the_file.filename() + PAREN_ID + the_file.id() +
                         "): " + ExceptionUtils.getStackTrace(e));
        try {
          cleanup(the_file.county(), true, "malformed CVR export file");
        } catch (final PersistenceException ex) {
          // if we couldn't clean up, there's not much we can do about it
        }
        error("malformed CVR export file " + 
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
     * @param the_county The county to wipe.
     * @return the number of deleted CVR records, if any were deleted.
     * @exception PersistenceException if the wipe was unsuccessful.
     */
    private int cleanup(final County the_county) {
      return cleanup(the_county, false, null);
    }
    
    /**
     * Attempts to wipe all CVR records for a specific county. This ends any current
     * transaction, does the delete in its own transaction, and starts a new 
     * transaction so that one is open at all times during endpoint execution.
     * 
     * @param the_county The county to wipe.
     * @param the_failure_flag true to set the CVR import status on the county
     * dashboard to FAILED, false otherwise.
     * @param the_failure_message The failure message to report, if the_failure_flag
     * is true.
     * @return the number of deleted CVR records, if any were deleted.
     * @exception PersistenceException if the wipe was unsuccessful.
     */
    private int cleanup(final County the_county, final boolean the_failure_flag, 
                        final String the_failure_message) {
      if (Persistence.isTransactionActive()) {
        Persistence.commitTransaction();
      }
      boolean success = false;
      int retries = 0;
      int result = 0;
      while (!success && retries < UPDATE_RETRIES) {
        try {
          retries = retries + 1;
          Main.LOGGER.debug("updating DoS dashboard, attempt " + retries + 
                            COUNTY + the_county.id());
          Persistence.beginTransaction();
          final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
          dosdb.removeContestsToAuditForCounty(the_county);
          // prevent Hibernate from reordering the ContestsToAudit deletion after the
          // Contest and CountyContestResult deletion in the following queries
          Persistence.flush(); 
          result = 
              CastVoteRecordQueries.deleteMatching(the_county.id(), RecordType.UPLOADED);
          CountyContestResultQueries.deleteForCounty(the_county.id());
          final CountyDashboard cdb = 
              Persistence.getByID(the_county.id(), CountyDashboard.class);
          cdb.setCVRFile(null);
          cdb.setCVRsImported(0);
          if (the_failure_flag) {
            cdb.setCVRImportStatus(new ImportStatus(ImportState.FAILED, the_failure_message));
          }
          Persistence.commitTransaction();
          success = true;
        } catch (final PersistenceException e) {
          // something went wrong, let's try again
          if (Persistence.canTransactionRollback()) {
            try {
              Persistence.rollbackTransaction();
            } catch (final PersistenceException ex) {
              // not much we can do about it
            }
          }
          result = 0;
          // let's give other transactions time to breathe
          try {
            final long delay = 
                ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
            Main.LOGGER.info("retrying DoS dashboard update for county " + the_county.id() + 
                             IN + delay + "ms");
            Thread.sleep(delay);
          } catch (final InterruptedException ex) {
            // it's OK to be interrupted
          }
        }
      }
      // we always need a running transaction
      Persistence.beginTransaction();
      if (success && retries > 1) {
        Main.LOGGER.info("updated DoS dashboard for county " + the_county.id() + 
                         " CVR reset in " + retries + TRIES);
      } else if (!success) {
        error("could not update DoS dashboard for county " + the_county.id() + 
              " CVR reset after " + retries + TRIES);
      }
      return result;
    }
    
    /**
     * Registers all tied contests in an uploaded CVR export as non-auditable contests
     * in the DoS dashboard.
     * 
     * @param the_county The county to handle ties in.
     * @return the number of tied contests detected.
     */
    private int handleTies(final County the_county) {
      if (Persistence.isTransactionActive()) {
        Persistence.commitTransaction();
      }
      boolean success = false;
      int retries = 0;
      int result = 0;
      while (!success && retries < UPDATE_RETRIES) {
        try {
          retries = retries + 1;
          Main.LOGGER.debug("updating DoS dashboard, attempt " + retries + 
                            COUNTY + the_county.id());
          Persistence.beginTransaction();
          final Set<CountyContestResult> contest_results = 
              CountyContestResultQueries.forCounty(the_county);
          final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

          for (final CountyContestResult ccr : contest_results) {
            if (ccr.minMargin() == 0) { 
              // this is a tied contest
              final ContestToAudit cta = new ContestToAudit(ccr.contest(), 
                                                            AuditReason.TIED_CONTEST, 
                                                            AuditType.NOT_AUDITABLE);
              dosdb.updateContestToAudit(cta);
              result = result + 1;
            }
          }
          Persistence.commitTransaction();
          success = true;
        } catch (final PersistenceException e) {
          // something went wrong, let's try again
          if (Persistence.canTransactionRollback()) {
            try {
              Persistence.rollbackTransaction();
            } catch (final PersistenceException ex) {
              // not much we can do about it
            }
          }
          result = 0;
          // let's give other transactions time to breathe
          try {
            final long delay = 
                ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
            Main.LOGGER.info("retrying DoS dashboard update for county " + the_county.id() + 
                             IN + delay + "ms");
            Thread.sleep(delay);
          } catch (final InterruptedException ex) {
            // it's OK to be interrupted
          }
        }
      }
      // we always need a running transaction
      Persistence.beginTransaction();
      if (success && retries > 1) {
        Main.LOGGER.info("updated DoS dashboard for county " + the_county.id() + 
                         " tied contests in " + retries + TRIES);
      } else if (!success) {
        error("could not update DoS dashboard for county " + the_county.id() + 
              " tied contests after " + retries + TRIES);
      } 
      return result;
    }
  }
}
