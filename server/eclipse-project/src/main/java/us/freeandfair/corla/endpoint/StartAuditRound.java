/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;
import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PUBLISH_BALLOTS_TO_AUDIT_EVENT;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.RANDOM_SEED_PUBLISHED;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.SubmittedAuditRoundStart;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Starts a new audit round for one or more counties.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
                   "PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity",
                   "PMD.NPathComplexity"})
public class StartAuditRound extends AbstractDoSDashboardEndpoint {
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
    return "/start-audit-round";
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
  public String endpoint(final Request the_request,
                         final Response the_response) {
    if (my_asm.get().currentState() == RANDOM_SEED_PUBLISHED) {
      // the audit hasn't started yet, so start round 1 and ignore the parameters
      // we were sent
      my_event.set(PUBLISH_BALLOTS_TO_AUDIT_EVENT);
      return startRoundOne(the_request, the_response);
    } else {
      // start a subsequent round
      my_event.set(null);
      return startSubsequentRound(the_request, the_response);
    }
  }
  
  /**
   * Starts the first audit round.
   * 
   * @param the_request The HTTP request.
   * @param the_response The HTTP response.
   * @return the result for endpoint.
   */
  public String startRoundOne(final Request the_request, final Response the_response) {
    // update every county dashboard with a list of ballots to audit
    try {
      final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);
      
      for (final CountyDashboard cdb : cdbs) {
        try {
          if (cdb.cvrUploadTimestamp() == null) {
            Main.LOGGER.info("county " + cdb.id() + " missed the file upload deadline");
          } else {
            // find the initial window
            ComparisonAuditController.initializeAuditData(cdb);
            Main.LOGGER.info("county " + cdb.id() + " initially estimated to audit " + 
                             cdb.estimatedBallotsToAudit() + " ballots");
            Persistence.saveOrUpdate(cdb);
          } 
          // update the ASMs for the county and audit board
          if (!DISABLE_ASM) {
            final CountyDashboardASM asm = 
                ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
            asm.stepEvent(COUNTY_START_AUDIT_EVENT);
            final ASMEvent audit_event;
            if (asm.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY) &&
                cdb.comparisonAudits().isEmpty()) {
              // the county made its deadline but was assigned no contests to audit
              audit_event = NO_CONTESTS_TO_AUDIT_EVENT;
              asm.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
            } else if (asm.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {
              // the audit started normally
              audit_event = AUDIT_BOARD_START_AUDIT_EVENT;
            } else {
              // the county missed its deadline
              audit_event = COUNTY_DEADLINE_MISSED_EVENT;
            }
            ASMUtilities.step(audit_event, AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));
            ASMUtilities.save(asm);
          }
        } catch (final IllegalArgumentException e) {
          e.printStackTrace(System.out);
          serverError(the_response, "could not start round 1 for county " + 
                      cdb.id());
          Main.LOGGER.info("could not start round 1 for county " + cdb.id());
        }
      }
      
      ok(the_response, "round 1 started");
    } catch (final PersistenceException e) {
      serverError(the_response, "could not start round 1");
    }
    
    return my_endpoint_result.get();
  }

  /**
   * Starts a subsequent audit round.
   * 
   * @param the_request The HTTP request.
   * @param the_response The HTTP response.
   * @return the result for endpoint.
   */
  // FindBugs thinks there's a possible NPE, but there's not because 
  // badDataContents() would bail on the method before it happened.
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
  public String startSubsequentRound(final Request the_request, final Response the_response) {
    SubmittedAuditRoundStart start = null;
    try {
      start = Main.GSON.fromJson(the_request.body(), SubmittedAuditRoundStart.class);
      if (start == null) {
        badDataContents(the_response, "malformed request data");
      }
    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed request data: " + e.getMessage());
    }
    
    try {
      // first, figure out what counties we need to do this for, if the list is limited
      final List<CountyDashboard> cdbs;
      if (start.countyBallots() == null || start.countyBallots().isEmpty()) {
        cdbs = Persistence.getAll(CountyDashboard.class);
      } else {
        cdbs = new ArrayList<>();
        for (final Long id : start.countyBallots().keySet()) {
          cdbs.add(Persistence.getByID(id, CountyDashboard.class));
        }
      }
    
      for (final CountyDashboard cdb : cdbs) {
        final AuditBoardDashboardASM asm = 
            ASMUtilities.asmFor(AuditBoardDashboardASM.class, cdb.id().toString());
        if (asm.isInInitialState() || asm.isInFinalState()) {
          // there is no audit happening in this county, so go to the next one
          Main.LOGGER.debug("no audit ongoing in county " + cdb.id() + 
                           ", skipping round start");
          continue;
        }
        
        // if the county is in the middle of a round, error out
        if (cdb.currentRound() != null) {
          invariantViolation(the_response, 
                             "audit round already in progress for county " + cdb.id());
        }
        final List<Round> rounds = cdb.rounds();
        int start_index = 0;
        if (rounds.isEmpty()) {
          invariantViolation(the_response, "no previous audit rounds for county " + cdb.id());
        } else {
          final Round previous_round = rounds.get(rounds.size() - 1);
          // we start the next round where the previous round actually ended 
          // in the audit sequence
          start_index = previous_round.actualAuditedPrefixLength();
        }
        final int round_length;
        Integer expected_prefix_length = null;
        if (start.useEstimates()) {
          // use estimates based on current error rate to get length of round
          expected_prefix_length = 
              ComparisonAuditController.computeEstimatedBallotsToAudit(cdb, true);
          round_length = 
              ComparisonAuditController.computeBallotOrder(cdb, start_index, 
                                                           expected_prefix_length).size();
        } else {
          round_length = start.countyBallots().get(cdb.id());
          // expected prefix length stays null because we don't know it
        }
 
        Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
                         cdb.id() + " at audit sequence number " + start_index + 
                         " with " + round_length + " ballots to audit");
        cdb.startRound(round_length, expected_prefix_length, start_index);
      }      
      ok(the_response, "new audit round started");
    } catch (final PersistenceException e) {
      serverError(the_response, "could not start new audit round");
    }
    
    return my_endpoint_result.get();
  }
}
