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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;

import java.time.Instant;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The "audit CVR upload" endpoint.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity"})
// TODO: consider rewriting along the same lines as CVRExportUpload
public class ACVRUpload extends AbstractAuditBoardDashboardEndpoint {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(ACVRUpload.class);
  /**
   * The event we will return for the ASM.
   */
  private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();

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
    return "/upload-audit-cvr";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return my_event.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void reset() {
    my_event.set(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    try {
      final SubmittedAuditCVR submission =
        Main.GSON.fromJson(the_request.body(), SubmittedAuditCVR.class);
      if (submission.auditCVR() == null || submission.cvrID() == null) {
        LOGGER.error("empty audit CVR upload");
        badDataContents(the_response, "empty audit CVR upload");
      } else {
        // FIXME extract-fn: handleACVR
        final CountyDashboard cdb =
            Persistence.getByID(Main.authentication().authenticatedCounty(the_request).id(),
                                CountyDashboard.class);
        if (cdb == null) {
          LOGGER.error("could not get audit board dashboard");
          serverError(the_response, "Could not save ACVR to dashboard");
        } else if (cdb.ballotsRemainingInCurrentRound() > 0) {
          // FIXME extract-fn: setupACVR
          final CastVoteRecord acvr = submission.auditCVR();
          acvr.setID(null);

          final CastVoteRecord real_acvr =
              new CastVoteRecord(RecordType.AUDITOR_ENTERED, Instant.now(),
                                 acvr.countyID(), acvr.cvrNumber(), null, acvr.scannerID(),
                                 acvr.batchID(), acvr.recordID(), acvr.imprintedID(),
                                 acvr.ballotType(), acvr.contestInfo());
          Persistence.saveOrUpdate(real_acvr);
          LOGGER.info("Audit CVR for CVR id " + submission.cvrID() +
                           " parsed and stored as id " + real_acvr.id());
          // FIXME extract-fn: setupACVR
          // Now we have a thing we can give our controller, maybe.
          final CastVoteRecord cvr = Persistence.getByID(submission.cvrID(),
                                                         CastVoteRecord.class);
          if (cvr == null) {
            LOGGER.error("could not find original CVR");
            // FIXME throw and push HTTP response up.
            this.badDataContents(the_response, "could not find original CVR");
          } else {

            // The positive outcome is a little hard to notice in all the noise
            // FIXME return an appropriate value and push HTTP response up
            if (ComparisonAuditController.submitAuditCVR(cdb, cvr, real_acvr)) {
              LOGGER.debug("ACVR OK");
              Persistence.saveOrUpdate(cdb);
              ok(the_response, "ACVR submitted");
            } else {
              // FIXME throw and push HTTP response up
              LOGGER.error("invalid audit CVR uploaded");
              badDataContents(the_response, "invalid audit CVR uploaded");
            }
          }
        } else {
          // FIXME throw and push HTTP response up
          LOGGER.error("ballot submission with no remaining ballots in round");
          invariantViolation(the_response,
                             "ballot submission with no remaining ballots in round");
        }

        if (cdb.ballotsRemainingInCurrentRound() == 0) {
          // TODO this has to happen before we can say RISK_LIMIT_ACHIEVED!
          LOGGER.debug("The round is over and set ROUND_COMPLETE_EVENT");
          my_event.set(ROUND_COMPLETE_EVENT);
        } else {
          LOGGER.debug("Some ballots remaining according to the CDB: REPORT_MARKING_EVENT");
          my_event.set(REPORT_MARKINGS_EVENT);
        }
      } // extract-fn: handleACVR will have returned some value or thrown
    } catch (final JsonParseException e) {
      LOGGER.error("malformed audit CVR upload");
      badDataContents(the_response, "malformed audit CVR upload");
    } catch (final PersistenceException e) {
      LOGGER.error("could not save audit CVR");
      serverError(the_response, "Unable to save audit CVR");
    }
    return my_endpoint_result.get();
  }
}
