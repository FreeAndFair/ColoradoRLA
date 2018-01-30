/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the election information.
 * 
 * @author Daniel M Zimmerman
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity"})
public class UpdateAuditInfo extends AbstractDoSDashboardEndpoint {
  /**
   * The event to return for this endpoint.
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
    return "/update-audit-info";
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
   * Attempts to set the election information.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  @SuppressWarnings("PMD.UselessParentheses")
  public String endpointBody(final Request the_request, final Response the_response) {
    try {
      final AuditInfo info = 
          Main.GSON.fromJson(the_request.body(), AuditInfo.class);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      if (dosdb == null) {
        Main.LOGGER.error("could not get department of state dashboard");
        serverError(the_response, "could not update audit information");
      } 
      if (validateElectionInfo(info, dosdb, the_response)) {
        dosdb.updateAuditInfo(info);
        my_event.set(nextEvent(dosdb));
        ok(the_response, "audit information updated");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to update audit information: " + e);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed audit information specified");
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
  private boolean validateElectionInfo(final AuditInfo the_info,
                                       final DoSDashboard the_dosdb,
                                       final Response the_response) {
    boolean result = true;
    
    // check for valid relationship between meeting date and election date
    final Instant effective_public_meeting_date;
    if (the_info.publicMeetingDate() == null) {
      effective_public_meeting_date = the_dosdb.auditInfo().publicMeetingDate();
    } else {
      effective_public_meeting_date = the_info.publicMeetingDate();
    }
    
    final Instant effective_election_date;
    if (the_info.electionDate() == null) {
      effective_election_date = the_dosdb.auditInfo().electionDate();
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
        (0 < BigDecimal.ZERO.compareTo(the_info.riskLimit()) || 
         0 < the_info.riskLimit().compareTo(BigDecimal.ONE))) {
      result = false;
      invariantViolation(the_response, "invalid risk limit specified");
    }
    
    // check that no seed is specified
    if (the_info.seed() != null) {
      result = false;
      invariantViolation(the_response, "cannot specify random seed through this endpoint");
    }
    
    return result;
  }
  
  /**
   * Computes the event of this endpoint based on audit info completeness.
   * 
   * @param the_dosdb The DoS dashboard.
   */
  private ASMEvent nextEvent(final DoSDashboard the_dosdb) {
    final ASMEvent result;
    final AuditInfo info = the_dosdb.auditInfo();

    if (info.electionDate() == null || info.electionType() == null ||
        info.publicMeetingDate() == null || info.riskLimit() == null) {
      Main.LOGGER.debug("partial audit information submitted");
      result = PARTIAL_AUDIT_INFO_EVENT;
    } else {
      Main.LOGGER.debug("complete audit information submitted");
      result = COMPLETE_AUDIT_INFO_EVENT;
    }

    return result;
  }
}
