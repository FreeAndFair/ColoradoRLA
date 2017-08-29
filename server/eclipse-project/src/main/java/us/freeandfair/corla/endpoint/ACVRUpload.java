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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.REPORT_MARKINGS_EVENT;

import java.time.Instant;

import javax.persistence.PersistenceException;

import com.google.gson.JsonSyntaxException;

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
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
// TODO: consider rewriting along the same lines as CVRExportUpload
public class ACVRUpload extends AbstractAuditBoardDashboardEndpoint {
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
    return REPORT_MARKINGS_EVENT;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final SubmittedAuditCVR submission =
          Main.GSON.fromJson(the_request.body(), SubmittedAuditCVR.class);
      if (submission.auditCVR() == null || submission.cvrID() == null) {
        Main.LOGGER.error("empty audit CVR upload");
        badDataContents(the_response, "empty audit CVR upload");
      } else {
        final CastVoteRecord acvr = submission.auditCVR();
        acvr.setID(null);
        final CastVoteRecord real_acvr = 
            new CastVoteRecord(RecordType.AUDITOR_ENTERED, Instant.now(), 
                               acvr.countyID(), acvr.cvrNumber(), null, acvr.scannerID(), 
                               acvr.batchID(), acvr.recordID(), acvr.imprintedID(), 
                               acvr.ballotType(), acvr.contestInfo());
        Persistence.saveOrUpdate(real_acvr);
        Main.LOGGER.info("Audit CVR for CVR id " + submission.cvrID() + 
                         " parsed and stored as id " + real_acvr.id());
        final CountyDashboard cdb = 
            Persistence.getByID(Authentication.authenticatedCounty(the_request).id(),
                                CountyDashboard.class);
        if (cdb == null) {
          Main.LOGGER.error("could not get audit board dashboard");
          serverError(the_response, "Could not save ACVR to dashboard");
        } else {
          final CastVoteRecord cvr = Persistence.getByID(submission.cvrID(), 
                                                         CastVoteRecord.class);
          if (cvr == null) {
            Main.LOGGER.error("could not find original CVR");
            this.badDataContents(the_response, "could not find original CVR");
          } else {
            if (ComparisonAuditController.submitAuditCVR(cdb, cvr, real_acvr)) {
              Persistence.saveOrUpdate(cdb);
              ok(the_response, "ACVR submitted");
            } else {
              Main.LOGGER.error("invalid audit CVR uploaded");
              badDataContents(the_response, "invalid audit CVR uploaded");
            }
          }
        }
      }
    } catch (final JsonSyntaxException e) {
      Main.LOGGER.error("malformed audit CVR upload");
      badDataContents(the_response, "malformed audit CVR upload");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save audit CVR");
      serverError(the_response, "Unable to save audit CVR");
    }
    return my_endpoint_result.get();
  }
}
