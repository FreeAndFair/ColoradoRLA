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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_AUDIT_COMPLETE_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;

import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Signs off on the current audit round for a county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports",
    "PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
    "PMD.StdCyclomaticComplexity"})
public class SignOffAuditRound extends AbstractAuditBoardDashboardEndpoint {
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
    return "/sign-off-audit-round";
  }

  /**
   * @return COUNTY authorization is required for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
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
   * Signs off on the current audit round, regardless of its state of
   * completion.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  // false positive about inner class declaration
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  @SuppressWarnings("checkstyle:nestedifdepth")
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    try {
      final Type list_type = new TypeToken<List<Elector>>() { }.getType();
      final List<Elector> parsed_signatories = 
          Main.GSON.fromJson(the_request.body(), list_type);
      if (parsed_signatories.size() >= CountyDashboard.MIN_ROUND_SIGN_OFF_MEMBERS) {
        final County county = Main.authentication().authenticatedCounty(the_request); 
        if (county == null) {
          Main.LOGGER.error("could not get authenticated county");
          unauthorized(the_response, "not authorized to set an audit board");
        } else {
          final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
          if (cdb == null) {
            Main.LOGGER.error("could not get county dashboard");
            serverError(the_response, "could not sign off round");
          } else if (cdb.currentRound() == null) {
            invariantViolation(the_response, "no round to sign off");
          } else {
            cdb.endRound(parsed_signatories);
            // update the ASM state for the county and maybe DoS
            if (!DISABLE_ASM) {
              final boolean audit_complete;
              if (cdb.estimatedSamplesToAudit() <= 0) {
                // we've reached the risk limit, so the county is done
                my_event.set(RISK_LIMIT_ACHIEVED_EVENT);
                cdb.endAudits();
                audit_complete = true;
              } else if (cdb.cvrsImported() <= cdb.ballotsAudited()) {
                // there are no more ballots in the county
                my_event.set(BALLOTS_EXHAUSTED_EVENT);
                cdb.endAudits();
                audit_complete = true;
              } else {
                // the round ended normally
                my_event.set(ROUND_SIGN_OFF_EVENT);
                audit_complete = false;
              }
              if (audit_complete) {
                notifyAuditComplete(cdb);
              } else {
                notifyRoundComplete(cdb.id());
              }
            }
          }
        }
      } else {
        invariantViolation(the_response, "invalid round sign off signatories");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to sign off round: " + e);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "invalid round signatories");
    }
    ok(the_response, "audit round signed off");
    return my_endpoint_result.get();
  }
  
  /**
   * Notifies the DoS dashboard that the round is over if all the counties
   * _except_ for the one identified in the parameter have completed their
   * audit round, or are not auditing (the excluded county is not counted
   * because its transition will not happen until this endpoint returns). 
   * 
   * @param the_id The ID of the county to exclude.
   */
  private void notifyRoundComplete(final Long the_id) {
    boolean finished = true;
    for (final CountyDashboard cdb : Persistence.getAll(CountyDashboard.class)) {
      if (!cdb.id().equals(the_id)) {
        finished &= cdb.currentRound() == null;
      }
    }
    
    if (finished) {
      ASMUtilities.step(DOS_ROUND_COMPLETE_EVENT, 
                        DoSDashboardASM.class, 
                        DoSDashboardASM.IDENTITY);
    }
  }
  
  /**
   * Notifies the county and DoS dashboards that the audit is complete.
   * 
   * @param the_cdb The county dashboard for this county.
   */
  private void notifyAuditComplete(final CountyDashboard the_cdb) {
    ASMUtilities.step(COUNTY_AUDIT_COMPLETE_EVENT, 
                      CountyDashboardASM.class, my_asm.get().identity());
    // check to see if all counties are complete
    boolean all_complete = true;
    for (final County c : Persistence.getAll(County.class)) {
      final CountyDashboardASM asm = 
          ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(c.id()));
      all_complete &= asm.isInFinalState();       
    }
    if (all_complete) {
      ASMUtilities.step(DOS_AUDIT_COMPLETE_EVENT, 
                        DoSDashboardASM.class, 
                        DoSDashboardASM.IDENTITY);
    } else {
      ASMUtilities.step(DOS_COUNTY_AUDIT_COMPLETE_EVENT,
                        DoSDashboardASM.class, 
                        DoSDashboardASM.IDENTITY);
    }
    the_cdb.signOutAuditBoard();
  }
}
