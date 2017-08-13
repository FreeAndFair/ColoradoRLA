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
import java.util.OptionalLong;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.json.SubmittedAuditCVR;
import us.freeandfair.corla.model.AuditBoardDashboard;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AuditBoardDashboardQueries;

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
      final CastVoteRecord acvr = submission.auditCVR();
      final CastVoteRecord real_acvr = 
          new CastVoteRecord(RecordType.AUDITOR_ENTERED, Instant.now(), 
                             acvr.countyID(), acvr.scannerID(), acvr.batchID(), 
                             acvr.recordID(), acvr.imprintedID(), acvr.ballotType(), 
                             acvr.contestInfo());
      Persistence.saveOrUpdate(real_acvr);
      Main.LOGGER.info("Audit CVR parsed and stored as id " + real_acvr.id());
      final OptionalLong count = count();
      if (count.isPresent()) {
        Main.LOGGER.info(count.getAsLong() + " ACVRs in storage");
      }
      final AuditBoardDashboard abdb = 
          AuditBoardDashboardQueries.get(Authentication.
                                         authenticatedCounty(the_request).identifier());
      if (abdb == null) {
        Main.LOGGER.error("could not get audit board dashboard");
        serverError(the_response, "Could not save ACVR to dashboard");
      } else {
        final CastVoteRecord cvr = Persistence.getByID(submission.cvrID(), 
                                                       CastVoteRecord.class);
        if (cvr == null) {
          this.badDataContents(the_response, "could not find original CVR");
        } else {
          abdb.submitAuditCVR(cvr, real_acvr);
        }
      }
      Persistence.saveOrUpdate(abdb);
    } catch (final JsonSyntaxException e) {
      Main.LOGGER.error("malformed audit CVR upload");
      badDataContents(the_response, "Invalid audit CVR upload");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save audit CVR");
      serverError(the_response, "Unable to save audit CVR");
    }
    ok(the_response, "ACVR submitted");
    return my_endpoint_result;
  }
  
  /**
   * Count the ACVRs in storage.
   * 
   * @return the number of ACVRs, or -1 if the count could not be determined.
   */
  private OptionalLong count() {
    OptionalLong result = OptionalLong.empty();
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(cb.count(root)).where(cb.equal(root.get("my_record_type"), 
                                               RecordType.AUDITOR_ENTERED));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      // ignore
    }
    
    return result;
  }
}
