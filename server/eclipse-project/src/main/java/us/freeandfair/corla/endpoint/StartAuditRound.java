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
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.COMPLETE_AUDIT_INFO_SET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.SubmittedAuditRoundStart;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Starts a new audit round for one or more counties.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
                   "PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity",
                   "PMD.NPathComplexity", "PMD.ExcessiveImports"})
public class StartAuditRound extends AbstractDoSDashboardEndpoint {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(StartAuditRound.class);

  /**
   * The "county " string.
   */
  private static final String COUNTY = "county ";

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
  protected void reset() {
    my_event.set(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    if (my_asm.get().currentState() == COMPLETE_AUDIT_INFO_SET) {
      // the audit hasn't started yet, so start round 1 and ignore the parameters
      // we were sent
      my_event.set(DOS_START_ROUND_EVENT);
      return startRoundOne(the_request, the_response);
    } else {
      // start a subsequent round
      my_event.set(DOS_START_ROUND_EVENT);
      return startSubsequentRound(the_request, the_response);
    }
  }

  /**
   * Provide the reasons for auditing each targeted contest
   * @return a map of contest name to audit reason
   */
  public Map<String, AuditReason> targetedContestReasons(final Set<ContestToAudit> ctas) {
    final Map<String,List<ContestToAudit>> contestToAudits = ctas.stream()
      .collect(Collectors.groupingBy((ContestToAudit cta) -> cta.contest().name()));

    return contestToAudits
      .entrySet()
      .stream()
      .collect(Collectors.toMap((Map.Entry<String,List<ContestToAudit>> e) -> e.getKey(),
                                // every getValue has at least one because of groupingBy
                                // every ContestToAudit has a reason
                                (Map.Entry<String,List<ContestToAudit>> e) -> e.getValue().get(0).reason()));
  }

  /**
   * Update every - targeted and opportunistic both - contest's
   * voteTotals from the counties. This needs to happen between all
   * counties uploading there data and before the ballot selection
   * happens
   */
  public List<ContestResult> countAndSaveContests(final Set<ContestToAudit> cta) {
    return
      ContestCounter.countAllContests().stream()
      .map(cr -> {cr.setAuditReason(targetedContestReasons(cta)
                                    .getOrDefault(cr.getContestName(),
                                                  AuditReason.OPPORTUNISTIC_BENEFITS));
                  return cr; })
      .map(Persistence::persist)
      .collect(Collectors.toList());
  }

  /**
   * Select random ballots for each targeted contest and group them by
   * county id.
   * @return a map of county to audit sequence.
   */
  public Map<Long, List<Integer>> combineSegments(final String seed,
                                                  final BigDecimal riskLimit,
                                                  final List<ContestResult>
                                                  targetedContestResults) {
    final List<Map<Long, List<Integer>>> segments =
      new ArrayList<Map<Long, List<Integer>>>();

    for(final ContestResult contestResult: targetedContestResults) {
      final BigDecimal optimistic =
        Audit.optimistic(riskLimit, contestResult.getDilutedMargin());

      LOGGER.info(String.format("Random ballot selection for: "
                                + "[contestName= %s,"
                                + " riskLimit= %f,"
                                + " dilutedMargin= %f,"
                                + " optimistic= %f]",
                                contestResult.getContestName(),
                                riskLimit,
                                contestResult.getDilutedMargin(),
                                optimistic));

      // translate 1-based number-of-samples to audit(optimistic) to 0-based
      // random number list index maximum
      final Integer startIndex = 0;
      final Integer endIndex = optimistic.intValue() - 1;

      // FIXME: use a DTO instead of mutating the contestResult
      // warning: cr and contestResult are the same object
      ContestResult cr = BallotSelection.segmentsForContest(contestResult, seed,
                                                            startIndex, endIndex);
      segments.add(cr.getSegments());
    }

    return segments.stream()
      .reduce(new TreeMap<Long,List<Integer>>(), // to keep counties in order
              (a, seg) -> BallotSelection.combineSegment(a, seg));
  }

  /**
   * Starts the first audit round.
   *
   * @param the_request The HTTP request.
   * @param the_response The HTTP response.
   * @return the result for endpoint.
   */
  // FIXME With some refactoring, we won't have excessive method length.
  @SuppressWarnings({"PMD.ExcessiveMethodLength"})
  public String startRoundOne(final Request the_request, final Response the_response) {
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    final BigDecimal riskLimit = dosdb.auditInfo().riskLimit();
    final String seed = dosdb.auditInfo().seed();
    // TODO we're checking this later, but at that point we should have
    // the ContestResults setup...
    final Set<String> targetedContestNames =
      dosdb.targetedContests()
      .map(x -> x.name())
      .collect(Collectors.toSet());

    final List<ContestResult> persistedContestResults = countAndSaveContests(dosdb.contestsToAudit());

    final List<ContestResult> targetedContestResults = persistedContestResults.stream()
      .filter(cr -> cr.getAuditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .collect(Collectors.toList());
    final Map<Long, List<Integer>> auditSegments = combineSegments(seed, riskLimit, targetedContestResults);

    Set<ComparisonAudit> comparisonAudits =
      ComparisonAuditController.createAudits(riskLimit, persistedContestResults);

    LOGGER.info("comparisonAudits = " + comparisonAudits);


    // Nothing in this try-block should know about HTTP requests / responses
    // update every county dashboard with a list of ballots to audit
    try {
      final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);

      // this flag starts off true if we're going to conjoin it with all the ASM
      // states, and false otherwise as we just assume audit reasonableness in the
      // absence of ASMs
      boolean audit_complete = !DISABLE_ASM;

      for (final CountyDashboard cdb : cdbs) {
        try {
          if (cdb.cvrFile() == null || cdb.manifestFile() == null) {
            LOGGER.info(COUNTY + cdb.id() + " missed the file upload deadline");
          } else {
            // find the initial window
            final List<Integer> subsequence = auditSegments.get(cdb.county().id());
            final Set<ComparisonAudit> auditsForCounty = comparisonAudits.stream()
              .filter(ca -> ca.isForCounty(cdb.county().id()))
              .collect(Collectors.toSet());

            LOGGER.info("county = " + cdb.county() + " auditsForCounty = " + auditsForCounty);
            LOGGER.info("county = " + cdb.county() + " subsequence = " + subsequence);
            final boolean started =
              ComparisonAuditController.startFirstRound(cdb,
                                                        auditsForCounty,
                                                        subsequence);

            if (started) {
              LOGGER.info(COUNTY + cdb.id() + " estimated to audit " +
                          cdb.estimatedSamplesToAudit() + " ballots in round 1");
            } else if (cdb.drivingContestNames().isEmpty()) {
              LOGGER.info(COUNTY + cdb.id() + " has no driving contests, its " +
                          "audit is complete.");
            } else if (cdb.estimatedSamplesToAudit() == 0) {
              LOGGER.info(COUNTY + cdb.id() + " needs to audit 0 ballots to " +
                          "achieve its risk limit, its audit is complete.");
            } else {
              LOGGER.error("unable to start audit for county " + cdb.id());
            }
            Persistence.saveOrUpdate(cdb);
          }
          // FIXME extract-fn: updateASMs(dashboardID, ,,,)
          // update the ASMs for the county and audit board
          if (!DISABLE_ASM) {
            final CountyDashboardASM asm =
                ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
            asm.stepEvent(COUNTY_START_AUDIT_EVENT);
            final ASMEvent audit_event;
            if (asm.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {
              if (cdb.comparisonAudits().isEmpty()) {
                // the county made its deadline but was assigned no contests to audit
                audit_event = NO_CONTESTS_TO_AUDIT_EVENT;
                asm.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
              } else if (cdb.estimatedSamplesToAudit() <= 0) {
                // the county made its deadline but has already achieved its risk limit
                audit_event = RISK_LIMIT_ACHIEVED_EVENT;
                asm.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
              } else {
                // the audit started normally
                audit_event = ROUND_START_EVENT;
              }
            } else {
              // the county missed its deadline
              audit_event = COUNTY_DEADLINE_MISSED_EVENT;
            }
            ASMUtilities.step(audit_event, AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));
            ASMUtilities.save(asm);

            // figure out whether this county is done, or whether there's an audit to run
            audit_complete &= asm.isInFinalState();
          }
        // FIXME hoist me; we don't need to know about HTTP requests or
        // responses at this level.
        } catch (final IllegalArgumentException e) {
          e.printStackTrace(System.out);
          serverError(the_response, "could not start round 1 for county " +
                      cdb.id());
          LOGGER.info("could not start round 1 for county " + cdb.id());
        } catch (final IllegalStateException e) {
          illegalTransition(the_response, e.getMessage());
        }
      }
      // FIXME hoist me
      if (audit_complete) {
        my_event.set(DOS_AUDIT_COMPLETE_EVENT);
        ok(the_response, "audit complete");
      } else {
        ok(the_response, "round 1 started");
      }
      // end of extraction. Now we can talk about HTTP requests / responses again!
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
          LOGGER.debug("no audit ongoing in county " + cdb.id() +
                           ", skipping round start");
          continue;
        }
        // if the county is in the middle of a round, error out
        if (cdb.currentRound() != null) {
          invariantViolation(the_response,
                             "audit round already in progress for county " + cdb.id());
        }

        final ASMEvent audit_event;
        final boolean round_started;
        final BigDecimal multiplier;
        if (start.multiplier() == null) {
          multiplier = BigDecimal.ONE;
        } else {
          multiplier = start.multiplier();
        }
        if (start.useEstimates()) {
          round_started =
              ComparisonAuditController.startNewRoundFromEstimates(cdb, multiplier);
        } else {
          round_started = ComparisonAuditController.
              startNewRoundOfLength(cdb, start.countyBallots().get(cdb.id()), multiplier);
        }
        if (round_started) {
          LOGGER.debug("round started for county " + cdb.id());
          audit_event = ROUND_START_EVENT;
        } else {
          // we don't know why the round didn't start, so we need to abort the audit
          LOGGER.debug("no round started for county " + cdb.id());
          audit_event = ABORT_AUDIT_EVENT;
        }

        // update the ASM for the audit board
        if (!DISABLE_ASM) {
          asm.stepEvent(audit_event);
          ASMUtilities.save(asm);
        }
      }
      ok(the_response, "new audit round started");
    } catch (final PersistenceException e) {
      serverError(the_response, "could not start new audit round");
    }

    return my_endpoint_result.get();
  }
}
