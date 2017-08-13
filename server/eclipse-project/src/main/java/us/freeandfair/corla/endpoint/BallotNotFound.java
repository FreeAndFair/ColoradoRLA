/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.time.Instant;
import java.util.ArrayList;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.model.AuditBoardDashboard;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AuditBoardDashboardQueries;

/**
 * The endpoint for reporting ballots that could not be found by auditors.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotNotFound extends AbstractAuditBoardDashboardEndpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/ballot-not-found";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ASMEvent endpointEvent() {
    return AuditBoardDashboardEvent.REPORT_BALLOT_NOT_FOUND_EVENT;
  }
  
  /**
   * Marks the specified ballot as "not found" by the audit board.
   * The ballot to so mark is indicated by the ID of its corresponding
   * CVR, which must match a ballot under audit by the authenticated
   * county.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    // we must be authenticated as a county
    final County county = Authentication.authenticatedCounty(the_request);
    if (county == null) {
      unauthorized(the_response, "not authorized for audit board operations");
      return my_endpoint_result;
    }
    
    final AuditBoardDashboard abd = 
        AuditBoardDashboardQueries.get(county.identifier());
    if (abd == null) {
      serverError(the_response, "could not load audit board information");
      return my_endpoint_result;
    }
    
    // attempt to read the CVR ID from the request
    try {
      final Long cvr_id = Long.valueOf(the_request.body());
      final int index = abd.cvrsToAudit().indexOf(cvr_id);
      if (index >= 0) {
        final CastVoteRecord cvr = Persistence.getByID(cvr_id, CastVoteRecord.class);
        if (cvr == null) {
          badDataContents(the_response, "could not find CVR");
        } else {
          final CastVoteRecord acvr =
              new CastVoteRecord(RecordType.PHANTOM_BALLOT,
                                 Instant.now(), cvr.countyID(), cvr.scannerID(),
                                 cvr.batchID(), cvr.recordID(),
                                 cvr.imprintedID(), cvr.ballotType(),
                                 new ArrayList<CVRContestInfo>());
          Persistence.saveOrUpdate(acvr);
          if (abd.submitAuditCVR(cvr, acvr)) {
            ok(the_response, "audit CVR submitted");
          }
        }
      }
    } catch (final NumberFormatException e) {
      this.badDataContents(the_response, "invalid CVR id");
    } catch (final PersistenceException e) {
      this.serverError(the_response, "unable to save audit CVR");
    }
    return my_endpoint_result;
  }

}
