/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.ElectionInfo;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the election information.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity"})
public class SetElectionInfo extends AbstractDoSDashboardEndpoint {  
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
    return "/update-election-info";
  }
  
  /**
   * Attempts to set the election information.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  @SuppressWarnings("PMD.UselessParentheses")
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final ElectionInfo info = 
          Main.GSON.fromJson(the_request.body(), ElectionInfo.class);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      if (dosdb == null) {
        Main.LOGGER.error("could not get department of state dashboard");
        serverError(the_response, "could not update election info");
      } 
      if (validateElectionInfo(info, dosdb, the_response)) {
        dosdb.updateElectionInfo(info);
        ok(the_response, "election information updated");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to update election information: " + e);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed election information specified");
    }
    return my_endpoint_result.get();
  }
  
  /**
   * Validates the specified election info.
   * 
   * @param the_info The info.
   * @param the_dosdb The DoS dashboard.
   * @param the_response The response (for reporting failures).
   */
  @SuppressWarnings({"PMD.UselessParentheses", "PMD.NPathComplexity"})
  private boolean validateElectionInfo(final ElectionInfo the_info,
                                       final DoSDashboard the_dosdb,
                                       final Response the_response) {
    boolean result = true;
    
    // check for valid relationship between meeting date and election date
    final Instant effective_public_meeting_date;
    if (the_info.publicMeetingDate() == null) {
      effective_public_meeting_date = the_dosdb.electionInfo().publicMeetingDate();
    } else {
      effective_public_meeting_date = the_info.publicMeetingDate();
    }
    
    final Instant effective_election_date;
    if (the_info.electionDate() == null) {
      effective_election_date = the_dosdb.electionInfo().electionDate();
    } else {
      effective_election_date = the_info.electionDate();
    }
    
    if (effective_public_meeting_date != null && effective_election_date != null &&
        !effective_public_meeting_date.isAfter(effective_election_date)) {
      result = false;
      invariantViolation(the_response, "public meeting must be after election");
    }
    
    // check that the 0 <= risk limit <= 1
    if (the_info.riskLimit() != null &&
        0 < BigDecimal.ZERO.compareTo(the_info.riskLimit()) || 
        0 < the_info.riskLimit().compareTo(BigDecimal.ONE)) {
      result = false;
      invariantViolation(the_response, "invalid risk limit specified");
    }
    
    // check for valid seed
    if (the_info.seed() != null && !DoSDashboard.isValidSeed(the_info.seed())) {
      result = false;
      invariantViolation(the_response, "invalid random seed specified");
    }
    
    return result;
  }
}
