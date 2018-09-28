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
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.*;

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_AUDIT_COMPLETE_EVENT;

import java.lang.reflect.Type;

import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;

import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.Round;

import us.freeandfair.corla.persistence.Persistence;

/**
 * Signs off on the current audit round for a county.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.NPathComplexity",
    "PMD.StdCyclomaticComplexity"})
public class SignOffAuditRound extends AbstractAuditBoardDashboardEndpoint {
  /**
   * The type of the JSON request.
   */
  private static final Type AUDIT_BOARD =
      new TypeToken<List<Elector>>() { }.getType();

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(SignOffAuditRound.class);

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
   * @param request The request.
   * @param response The response.
   */
  @Override
  @SuppressWarnings({"PMD.ExcessiveMethodLength"})
  public String endpointBody(final Request request,
                             final Response response) {
    final County county = Main.authentication().authenticatedCounty(request);

    if (county == null) {
      LOGGER.error("could not get authenticated county");
      unauthorized(response, "not authorized to sign off on the round");
    }

    final JsonParser parser = new JsonParser();
    final JsonObject o;

    try {
      o = parser.parse(request.body()).getAsJsonObject();
      final int auditBoardIndex =
          o.get("index").getAsInt();
      final List<Elector> signatories =
          Main.GSON.fromJson(o.get("audit_board"), AUDIT_BOARD);

      if (signatories.size() < CountyDashboard.MIN_ROUND_SIGN_OFF_MEMBERS) {
        LOGGER.error("[signoff: too few signatories for round sign-off]");
        invariantViolation(response, "too few signatories for round sign-off sent");
      }

      final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);

      if (cdb == null) {
        LOGGER.error(String.format("[signoff: Could not get county dashboard for %s County id=%d]",
                                   county.name(), county.id()));
        serverError(response, "could not get county dashboard");
      }

      if (cdb.currentRound() == null) {
        LOGGER.error(String.format("[signoff: No current round for %s County]", cdb.county().name()));
        invariantViolation(response, "no current round on which to sign off");
      }

      final Round currentRound = cdb.currentRound();

      currentRound.setSignatories(auditBoardIndex, signatories);

      if (cdb.auditBoardCount() == null) {
        LOGGER.error(String.format(
            "[signoff: Audit board count unset for %s County]",
            cdb.county().name()));
        invariantViolation(response, "audit board count unset");
      }

      // If we have not seen all the boards sign off yet, we do not want to end
      // the round.
      if (currentRound.signatories().size() < cdb.auditBoardCount()) {
        LOGGER.info(String.format(
            "%d of %d audit boards have signed off for county %d",
            currentRound.signatories().size(),
            cdb.auditBoardCount(),
            cdb.id()));
      } else {
        // We're done!
        cdb.endRound();

        final AuditBoardDashboardASM asm =
          ASMUtilities.asmFor(AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));

       if (null != asm && asm.currentState() == ROUND_IN_PROGRESS) {
          ASMUtilities.step(ROUND_COMPLETE_EVENT,
                            AuditBoardDashboardASM.class,
                            String.valueOf(cdb.id()));
        }

        // update the ASM state for the county and maybe DoS
        if (!DISABLE_ASM) {
          final boolean auditComplete;
          LOGGER.debug
            (String.format
             ("[signoff for %s County: cdb.estimatedSamplesToAudit()=%d,"
              + " cdb.auditedSampleCount()=%d,"
              + " cdb.ballotsAudited()=%d]",
              cdb.county().name(),
              cdb.estimatedSamplesToAudit(),
              cdb.auditedSampleCount(),
              cdb.ballotsAudited()));

          if (cdb.allAuditsComplete()) {
            my_event.set(RISK_LIMIT_ACHIEVED_EVENT);
            final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
            LOGGER.debug(String.format("[signoff: all targeted audits finished in %s County."
                                       + " Terminated these audits: %s]",
                                       cdb.county().name(), terminated));
            auditComplete = true;
          } else if (cdb.cvrsImported() <= cdb.ballotsAudited()) {
            final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
            auditComplete = cdb.allAuditsComplete();
            LOGGER.debug(String.format("[signoff: no more ballots; terminated single-county audits"
                                       + " %s in %s County. All complete? (%b)]",
                                       terminated, cdb.county().name(), auditComplete));
            my_event.set(ROUND_SIGN_OFF_EVENT);
          } else {
            LOGGER.debug("[signoff: the round ended normally]");
            auditComplete = false;
            my_event.set(ROUND_SIGN_OFF_EVENT);
          }

          if (auditComplete) {
            LOGGER.debug(String.format("[signoff: audit complete in %s County]", cdb.county().name()));
            notifyAuditComplete();
          } else {
            LOGGER.debug(String.format("[signoff: round complete in %s County]", cdb.county().name()));
            notifyRoundComplete(cdb.id());
          }
        }
      }
    } catch (final PersistenceException e) {
      LOGGER.error("[signoff: unable to sign off round.]");
      serverError(response, "unable to sign off round: " + e);
    } catch (final JsonParseException e) {
      LOGGER.error("[signoff: bad data sent in an attempt to sign off on round]", e);
      badDataContents(response, "invalid request body attempting to sign off on round");
    }
    LOGGER.debug("[signoff: a-ok]");
    ok(response, "audit board signed off");

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
      LOGGER.debug(String.format("[notifyRoundComplete: finished=%b, the_id=%d, cdb=%s]",
                                 finished, the_id, cdb));
    }

    if (finished) {
      ASMUtilities.step(DOS_ROUND_COMPLETE_EVENT,
                        DoSDashboardASM.class,
                        DoSDashboardASM.IDENTITY);

      LOGGER.debug("[notifyRoundComplete stepped DOS_ROUND_COMPLETE_EVENT]");
    }
  }

  /**
   * Notifies the county and DoS dashboards that the audit is complete.
   */
  private void notifyAuditComplete() {
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
  }
}
