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
import java.util.OptionalLong;
import java.util.Set;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.json.CVRToAuditResponse.BallotOrderComparator;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.CVRAuditInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;

/**
 * The CVR to audit list endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity"})
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
   * The "include duplicates" parameter.
   */
  public static final String INCLUDE_DUPLICATES = "include_duplicates";
  
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
    final String start = the_request.queryParams(START);
    final String ballot_count = the_request.queryParams(BALLOT_COUNT);
    final String duplicates = the_request.queryParams(INCLUDE_DUPLICATES);
    
    boolean result = start != null && ballot_count != null;
    
    if (result) {
      try {
        final int s = Integer.parseInt(start);
        result = s >= 0;
        final int b = Integer.parseInt(ballot_count);
        result &= b >= 0;
        if (duplicates != null) {
          Boolean.parseBoolean(duplicates);
        }
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
      final int index = Integer.parseInt(the_request.queryParams(START));
      final int ballot_count = Integer.parseInt(the_request.queryParams(BALLOT_COUNT));
      final String duplicates_param = the_request.queryParams(INCLUDE_DUPLICATES);
      final boolean duplicates;
      if (duplicates_param == null) {
        duplicates = false;
      } else {
        duplicates = Boolean.parseBoolean(the_request.queryParams(INCLUDE_DUPLICATES));
      }
      
      // get other things we need
      final County county = Authentication.authenticatedCounty(the_request);
      final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
      final OptionalLong county_ballots_found = 
          CastVoteRecordQueries.countMatching(county.id(), RecordType.UPLOADED);
      final long county_ballots;
      if (county_ballots_found.isPresent()) {
        county_ballots = county_ballots_found.getAsLong();
      } else {
        county_ballots = 0;
      }
      final Set<CastVoteRecord> cvr_set = new HashSet<>();
      final List<CVRToAuditResponse> cvr_to_audit_list = new ArrayList<>();
      
      // we need to get the CVRs for the county's sequence, starting at START, and 
      // look up their locations; note we may have to ask for the sequence more than
      // once because we may find duplicates in the sequence
      
      int start = index;
      int end = start + ballot_count - 1; // end is inclusive
      
      // if duplicates is set we go until the list has the right number; if not,
      // we go until we hit the end of our CVR pool
      while (duplicates && cvr_to_audit_list.size() < ballot_count || 
             !duplicates && cvr_set.size() < ballot_count && cvr_set.size() < county_ballots) {
        final List<CastVoteRecord> new_cvrs = 
            ComparisonAuditController.computeBallotOrder(cdb, start, end);
        for (int i = 0; i < new_cvrs.size(); i++) {
          final CastVoteRecord cvr = new_cvrs.get(i);
          if ((duplicates || !cvr_set.contains(cvr)) && notAudited(cdb, cvr)) {
            // get the CVR location
            final String location = BallotManifestInfoQueries.locationFor(cvr);
            cvr_to_audit_list.add(new CVRToAuditResponse(start + i, cvr.scannerID(), 
                                                         cvr.batchID(), cvr.recordID(), 
                                                         cvr.imprintedID(), 
                                                         cvr.cvrNumber(), cvr.id(),
                                                         cvr.ballotType(), location));
          }
          cvr_set.add(cvr);
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
  
  
  /**
   * Checks to see if the specified CVR has been audited on the specified county
   * dashboard.
   * 
   * @param the_dashboard The county dashboard.
   * @param the_cvr The CVR.
   * @return true if the specified CVR has not been audited yet, false otherwise.
   */
  private boolean notAudited(final CountyDashboard the_dashboard, 
                             final CastVoteRecord the_cvr) {
    final List<CVRAuditInfo> info = 
        CVRAuditInfoQueries.matching(the_dashboard, the_cvr);
    final boolean result;
    if (info.isEmpty() || info.get(0).acvr() == null) {
      result = true;
    } else {
      result = false;
    }
    return result;
  }
}
