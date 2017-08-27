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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.json.CVRToAuditResponse.BallotOrderComparator;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;

/**
 * The CVR to audit list endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CVRToAuditList extends AbstractEndpoint {
  /**
   * The "start" parameter.
   */
  public static final String START = "start";
  
  /**
   * The "ballot_count" parameter.
   */
  public static final String BALLOT_COUNT = "ballot_count";
  
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
    return "/cvr-to-audit-list";
  }
  
  /**
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }

  /**
   * Validate the request parameters. In this case, the two parameters
   * must exist and both be non-negative integers.
   * 
   * @param the_request The request.
   */
  @Override
  protected boolean validateParameters(final Request the_request) {
    final String start = the_request.params(START);
    final String ballot_count = the_request.params(BALLOT_COUNT);
    
    boolean result = start != null && ballot_count != null;
    
    if (result) {
      try {
        final int s = Integer.parseInt(start);
        result = s >= 0;
        final int b = Integer.parseInt(ballot_count);
        result &= b >= 0;
      } catch (final NumberFormatException e) {
        result = false;
      }
    }
    
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      // get the request parameters
      final int index = Integer.parseInt(the_request.params(START));
      final int ballot_count = Integer.parseInt(the_request.params(BALLOT_COUNT));
      
      // get other things we need
      final County county = Authentication.authenticatedCounty(the_request);
      final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
      final Set<CastVoteRecord> cvr_set = new HashSet<>();
      final List<CVRToAuditResponse> cvr_to_audit_list = new ArrayList<>();
      
      // we need to get the CVRs for the county's sequence, starting at START, and 
      // look up their locations; note we may have to ask for the sequence more than
      // once because we may find duplicates in the sequence
      
      int start = index;
      int end = start + ballot_count - 1; // end is inclusive
      
      while (cvr_set.size() < ballot_count) {
        final List<CastVoteRecord> new_cvrs = 
            ComparisonAuditController.computeBallotOrder(cdb, start, end);
        for (int i = 0; i < new_cvrs.size(); i++) {
          final CastVoteRecord cvr = new_cvrs.get(i);
          if (!cvr_set.contains(cvr)) {
            // this is legitimately a new CVR
            final String location = BallotManifestInfoQueries.locationFor(cvr);
            cvr_to_audit_list.add(new CVRToAuditResponse(start + i, cvr.scannerID(), 
                                                         cvr.batchID(), cvr.recordID(), 
                                                         cvr.imprintedID(),
                                                         cvr.ballotType(), location));
          }
        }
        start = end + 1; // end is inclusive
        end = start + (ballot_count - cvr_set.size()) - 1; // end is inclusive
        // at this point, if cvr_set.size() < ballot_count, this is an empty range
      }
      
      cvr_to_audit_list.sort(new BallotOrderComparator());
      okJSON(the_response, Main.GSON.toJson(cvr_to_audit_list));
    } catch (final PersistenceException e) {
      serverError(the_response, "could not generate cvr list");
    }
    return my_endpoint_result.get();
  }
}
