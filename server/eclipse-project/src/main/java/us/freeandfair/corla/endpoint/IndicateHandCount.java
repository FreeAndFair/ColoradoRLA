/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.ABORT_AUDIT_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_AUDIT_COMPLETE_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.COUNTY_AUDIT_UNDERWAY;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for indicating that a contest must be hand-counted.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class IndicateHandCount extends AbstractDoSDashboardEndpoint {
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
    return "/hand-count";
  }

  /**
   * @return STATE authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
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
   * Indicate that a contest must be hand-counted.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public synchronized String endpointBody(final Request the_request, 
                                      final Response the_response) {
    try {
      final ContestToAudit[] supplied_ctas = 
          Main.GSON.fromJson(the_request.body(), ContestToAudit[].class);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      if (dosdb == null) {
        serverError(the_response, "Could not select contests");
      } else {
        boolean hand_count = false;
        final Set<County> hand_count_counties = new HashSet<>();
        for (final ContestToAudit c : fixReasons(dosdb, supplied_ctas)) {
          if (c.audit() == AuditType.HAND_COUNT &&
              dosdb.updateContestToAudit(c)) {
            hand_count = true;
            hand_count_counties.add(c.contest().county());
          }
        }
        if (hand_count) {
          updateStateMachines(the_response, hand_count_counties);
        } else {
          // bad data was submitted for hand count selection
          badDataContents(the_response, "Invalid contest selection data");
        }
      }
      Persistence.saveOrUpdate(dosdb);
      ok(the_response, "Contests selected for hand count");
    } catch (final JsonParseException e) {
      badDataContents(the_response, "Invalid contest selection data");
    } catch (final PersistenceException e) {
      serverError(the_response, "Unable to save contest selection");
    }
    return my_endpoint_result.get();
  }
  
  /**
   * Updates the supplied CTAs with the reasons that were originally specified 
   * on the DoS dashboard.
   * 
   * @param the_dosdb The DoS dashboard.
   * @param the_supplied_ctas The supplied CTAs. 
   */
  @SuppressWarnings("PMD.UseVarargs")
  private Set<ContestToAudit> fixReasons(final DoSDashboard the_dosdb,
                                         final ContestToAudit[] the_supplied_ctas) {
    final Set<ContestToAudit> result = new HashSet<>();
    final Set<ContestToAudit> existing_ctas = the_dosdb.contestsToAudit();
    final Map<Contest, ContestToAudit> contest_cta = new HashMap<>();
    
    // let's iterate over these only once, instead of once for each array element in 
    // the_supplied_ctas
    for (final ContestToAudit c : existing_ctas) {
      contest_cta.put(c.contest(), c);
    }
    
    // update the supplied CTAs with the dashboard reasons
    for (final ContestToAudit c : the_supplied_ctas) {
      ContestToAudit real_cta = c;
      if (contest_cta.containsKey(c.contest())) {
        real_cta = new ContestToAudit(c.contest(), 
                                      contest_cta.get(c.contest()).reason(), 
                                      c.audit());
      }
      result.add(real_cta);
    }
    
    return result;
  }
  
  /**
   * Updates the ASMs of the counties where a contest is selected for hand count.
   * Currently, this aborts the audit entirely in those counties 
   * (if it is still running). This may also end the audit on the DoS dashboard.
   * 
   * @param the_response The response (for error reporting).
   * @param the_counties The counties.
   */
  private void updateStateMachines(final Response the_response, 
                                   final Set<County> the_counties) {
    boolean aborted_audit = false;
    // for each county, if the audit is actually running, abort it
    for (final County c : the_counties) {
      final CountyDashboard cdb = Persistence.getByID(c.id(), CountyDashboard.class);
      final AbstractStateMachine county_asm = 
          ASMUtilities.asmFor(CountyDashboardASM.class, c.id().toString());
      final AbstractStateMachine audit_asm =
          ASMUtilities.asmFor(AuditBoardDashboardASM.class, c.id().toString());
      
      if (county_asm.currentState() == COUNTY_AUDIT_UNDERWAY &&
          !audit_asm.isInFinalState()) {
        // end the audit in the county
        county_asm.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
        audit_asm.stepEvent(ABORT_AUDIT_EVENT);
        ASMUtilities.save(county_asm);
        ASMUtilities.save(audit_asm);
        cdb.endAudits();
        aborted_audit = true;
      } else if (!county_asm.isInFinalState() || !audit_asm.isInFinalState()) {
        // this was done in an invalid state - it can only happen if the audit is
        // either underway, or over
        illegalTransition(the_response,
                          "attempt to change contest to hand count for county " + 
                          c.id() + " in invalid state (" + county_asm.currentState() +
                          ", " + audit_asm.currentState() + ")");
        // must halt explicitly on an illegal transition call
        halt(the_response);
      }
    }
   
    // the DoS dashboard event is either null, DOS_COUNTY_AUDIT_COMPLETE_EVENT or 
    // DOS_AUDIT_COMPLETE_EVENT, depending on whether a county state changed and
    // whether all counties are done
    
    if (aborted_audit) {
      boolean all_done = true;
      for (final County c : Persistence.getAll(County.class)) {
        all_done &= 
            ASMUtilities.asmFor(CountyDashboardASM.class, c.id().toString()).isInFinalState();
      }
      if (all_done) {
        my_event.set(DOS_AUDIT_COMPLETE_EVENT);
      } else {
        my_event.set(DOS_COUNTY_AUDIT_COMPLETE_EVENT);
      }
    } else {
      // no county's audit was actually aborted, so from the DoS dashboard ASM 
      // perspective, this is a no-op
      my_event.set(null);
    }
  }
}
