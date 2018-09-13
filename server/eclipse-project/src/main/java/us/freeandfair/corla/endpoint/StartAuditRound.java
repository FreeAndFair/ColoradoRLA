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
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.*;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.UNABLE_TO_AUDIT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.COMPLETE_AUDIT_INFO_SET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import us.freeandfair.corla.controller.BallotSelection.Segment;
import us.freeandfair.corla.controller.BallotSelection.Selection;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.SubmittedAuditRoundStart;
import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.ContestResultQueries;
import us.freeandfair.corla.query.ComparisonAuditQueries;

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

  /** Count ContestResults, Create ComparisonAudits and assign them to CountyDashboards **/
  public void initializeAuditData(DoSDashboard dosdb) {
    final List<ContestResult> contestResults = initializeContests(dosdb.contestsToAudit());
    final List<ComparisonAudit> comparisonAudits = initializeAudits(contestResults, dosdb.auditInfo().riskLimit());
    final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);
    for (final CountyDashboard cdb : cdbs) {
      initializeCountyDashboard(cdb, comparisonAudits);
    }
  }

  public List<ContestResult> initializeContests(Set<ContestToAudit> cta) {
    final List<ContestResult> countedCRs = countAndSaveContests(cta);
    LOGGER.debug(String.format("[initializeContests: cta=%s, countedCRs=%s]",
                               cta, countedCRs));
    return countedCRs;
  }

  /**
   * A debugging helper that is liberal with its definition of
   * "targeted".
   *
   * @return a filtered list by whatever target means today.
   */
  public List<ContestResult> targeted(final List<ContestResult> crs) {
    return crs.stream()
      .filter(cr -> cr.getAuditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .collect(Collectors.toList());
  }

  /**
   * Warning: Contains Side Effects
   */
  public List<ComparisonAudit> initializeAudits(final List<ContestResult> contestResults,
                                                final BigDecimal riskLimit) {
    List<ComparisonAudit> comparisonAudits = contestResults.stream()
      .map(cr -> ComparisonAuditController.createAudit(cr, riskLimit))
      .collect(Collectors.toList());

    LOGGER.debug(String.format("[initializeAudits: contestResults=%s, "
                               + "targetedContestResults=%s, comparisonAudits=%s]",
                               contestResults, targeted(contestResults), comparisonAudits));

    return comparisonAudits;
  }

  /**
   * Setup a county dashboard. Puts the dashboard into the
   * COUNTY_START_AUDIT_EVENT state.
   *
   * Puts the right set of comparison audits on the cdb.
   *
   * Builds comparison audits for the driving contests.
   */
  public void initializeCountyDashboard(final CountyDashboard cdb,
                                        final List<ComparisonAudit> comparisonAudits) {
    // FIXME extract-fn
    final Set<String> drivingContestNames = comparisonAudits.stream()
      .filter(ca -> ca.contestResult().getAuditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .map(ca -> ca.contestResult().getContestName())
      .collect(Collectors.toSet());

    // OK.
    cdb.setAuditedSampleCount(0);
    cdb.setAuditedPrefixLength(0);
    cdb.setDrivingContestNames(drivingContestNames);

    // FIXME extract-fn
    Set<ComparisonAudit> countyAudits = new HashSet<>();
    if (cdb.getAudits().isEmpty()) {
      countyAudits =
        comparisonAudits.stream()
        .filter(ca -> ca.isForCounty(cdb.county().id()))
        .collect(Collectors.toSet());
      cdb.setAudits(countyAudits);
    }

    // FIXME extract-fn
    // The county missed its deadline, nothing to start, so let's mark it so
    final CountyDashboardASM countyDashboardASM = ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
    final AuditBoardDashboardASM auditDashboardASM = ASMUtilities.asmFor(AuditBoardDashboardASM.class, String.valueOf(cdb.id()));

    if (countyDashboardASM.currentState() != BALLOT_MANIFEST_AND_CVRS_OK) {
      LOGGER.info(String.format("[%s County missed the file upload deadline.",
                                cdb.county().name()));
      auditDashboardASM.stepEvent(NO_CONTESTS_TO_AUDIT_EVENT);
    }
    countyDashboardASM.stepEvent(COUNTY_START_AUDIT_EVENT);
    ASMUtilities.save(countyDashboardASM);
    ASMUtilities.save(auditDashboardASM);

    if (!countyDashboardASM.isInInitialState() && !countyDashboardASM.isInFinalState()) {
      LOGGER.debug(String.format("[initializeCountyDashboard: "
                                 + " cdb=%s, comparisonAudits=%s, "
                                 + " drivingContestNames=%s, countyAudits=%s]",
                                 cdb, comparisonAudits, drivingContestNames, countyAudits));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request,
                             final Response the_response) {

    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    if (my_asm.get().currentState() == COMPLETE_AUDIT_INFO_SET) {
      // this is the first round
      // this needs to happen after uploading is done but before the audit is started
      initializeAuditData(dosdb);
    }

    my_event.set(DOS_START_ROUND_EVENT);
    return startRound(the_request, the_response);
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
   * sets selection on each contestResult, the results of
   * BallotSelection.randomSelection
   */
  public List<Selection> makeSelections(List<ComparisonAudit> comparisonAudits,
                                        final String seed,
                                        final BigDecimal riskLimit) {

    List<Selection> selections = new ArrayList<>();
    // maybe...
    // comparisonAudits.stream()
    //   .filter(ca -> ca.isTargeted())
    //   .map(BallotSelection::randomSelection)

    for(final ComparisonAudit comparisonAudit: comparisonAudits) {
      final ContestResult contestResult = comparisonAudit.contestResult();
      // only make selection for targeted contests
      // the only AuditReasons in play are county, state and opportunistic
      if (contestResult.getAuditReason() != AuditReason.OPPORTUNISTIC_BENEFITS) {
        LOGGER.debug(String.format("[makeSelections for ContestResult: contestName=%s, contestResult.contestCVRIds=%s]",
                                   contestResult.getContestName(),
                                   contestResult.getContestCVRIds()));

        final Integer startIndex = BallotSelection.auditedPrefixLength(contestResult.getContestCVRIds());
        final Integer endIndex = comparisonAudit.optimisticSamplesToAudit();

        Selection selection = BallotSelection.randomSelection(contestResult,
                                                              seed,
                                                              startIndex,
                                                              endIndex);
        LOGGER.debug(String.format("[makeSelections selection=%s, "
                                   + "selection.contestCVRIds=%s, startIndex=%d, endIndex=%d]",
                                   selection,
                                   selection.contestCVRIds(),
                                   startIndex, endIndex));
        selection.riskLimit = riskLimit;
        contestResult.selection = selection;
        contestResult.addContestCVRIds(selection.contestCVRIds());

        selections.add(selection);
      }
    }
    return selections;
  }

  /**
   * All contests for county and their selections combined into a
   * single segment
   **/
  public Segment combinedSegment(CountyDashboard cdb) {
    List<Segment> countyContestSegments = cdb.comparisonAudits().stream()
      .map(ca -> (Segment)ca.contestResult().selection.forCounty(cdb.county().id()))
      .collect(Collectors.toList());
    return Selection.combineSegments(countyContestSegments);
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
  public String startRound(final Request the_request, final Response the_response) {
    final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    final BigDecimal riskLimit = dosdb.auditInfo().riskLimit();
    final String seed = dosdb.auditInfo().seed();
    final List<ComparisonAudit> comparisonAudits = Persistence.getAll(ComparisonAudit.class);
    final List<Selection> selections = makeSelections(comparisonAudits, seed, riskLimit);

    // Nothing in this try-block should know about HTTP requests / responses
    // update every county dashboard with a list of ballots to audit
    try {
      final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);

      // this flag starts off true if we're going to conjoin it with all the ASM
      // states, and false otherwise as we just assume audit reasonableness in the
      // absence of ASMs
      boolean audit_complete = !DISABLE_ASM;

      for (final CountyDashboard cdb : dashboardsToStart()) {
        try {
          // Selections for all contests that this county is participating in
          // final Segment segment = combinedSegment(cdb);
          final Segment segment = Selection.combineSegments(selections.stream()
                                                            .map(s -> s.forCounty(cdb.county().id()))
                                                            .collect(Collectors.toList()));

          LOGGER.debug(String.format("[startRound:"
                                     + " county=%s, round=%s, segment.auditSequence()=%s,"
                                     + " segment.ballotSequence()=%s,"
                                     + " cdb.comparisonAudits=%s,",
                                     cdb.county(), cdb.currentRound(), segment.auditSequence(),
                                     segment.ballotSequence(), cdb.comparisonAudits()));
          final boolean started =
            ComparisonAuditController.startRound(cdb,
                                                 cdb.comparisonAudits(),
                                                 segment.auditSequence(),
                                                 segment.ballotSequence());

          if (started) {
            LOGGER.debug(String.format(
                         "[startRound: %s County estimated to audit %d ballots in round %s]",
                         cdb.county().name(), cdb.estimatedSamplesToAudit(),
                         cdb.currentRound()));
          } else if (cdb.drivingContestNames().isEmpty()) {
            LOGGER
              .debug(String.format("[startRound: %s County has no driving contests, audit complete.]",
                                   cdb.county().name()));
          } else if (cdb.estimatedSamplesToAudit() == 0) {
            // FIXME I think this might be removed by using dashboardsToStart()...
            LOGGER
              .debug(String.format("[startRound: %s County needs to audit 0 ballots to"
                                   + " achieve its risk limit, its audit is complete.]",
                                   cdb.county().name()));
          } else {
            LOGGER
              .error(String.format("[startRound: Unable to start audit for %s County.",
                                   cdb.county().name()));
          }
          Persistence.saveOrUpdate(cdb);

          // FIXME extract-fn: updateASMs(dashboardID, ,,,)
          // update the ASMs for the county and audit board
          if (!DISABLE_ASM) {
            final CountyDashboardASM countyDashboardASM =
                ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));

            if (countyDashboardASM.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {
              if (cdb.comparisonAudits().isEmpty()) {
                LOGGER.debug("[startRound: county made its deadline but was assigned no contests to audit]");
                ASMUtilities.step(NO_CONTESTS_TO_AUDIT_EVENT, AuditBoardDashboardASM.class,
                                  String.valueOf(cdb.id()));
                countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
              } else if (cdb.estimatedSamplesToAudit() <= 0) {
                LOGGER.debug("[startRound: county made its deadline but has already achieved its risk limit]");
                ASMUtilities.step(RISK_LIMIT_ACHIEVED_EVENT, AuditBoardDashboardASM.class,
                                  String.valueOf(cdb.id()));
                countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
              } else {
                LOGGER.debug("[startRound: the round started normally]");
                ASMUtilities.step(ROUND_START_EVENT, AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));
              }
            }
            ASMUtilities.save(countyDashboardASM);

            // figure out whether this county is done, or whether there's an audit to run
            audit_complete &= countyDashboardASM.isInFinalState();
          }
        // FIXME hoist me; we don't need to know about HTTP requests or
        // responses at this level.
        } catch (final IllegalArgumentException e) {
          e.printStackTrace(System.out);
          final String msg = String.format("could not start round for %s County",
                                           cdb.county().name());
          serverError(the_response, msg);
          LOGGER.error(msg);
        } catch (final IllegalStateException e) {
          LOGGER.error("IllegalStateException " + e);
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
      LOGGER.error("PersistenceException " + e);
      serverError(the_response, "could not start round");
    }

    return my_endpoint_result.get();
  }

  public Boolean isReadyToStartAudit(final CountyDashboard cdb) {
    final CountyDashboardASM countyDashboardASM =
      ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
    if (countyDashboardASM.isInInitialState() ||
        countyDashboardASM.isInFinalState() ) {
        // || !countyDashboardASM.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)

      return false;
    } else {
      return true;
    }
  }

  /**
   * Given a request to start a round thingy, return the dashboards to start.
   */
  public List<CountyDashboard> dashboardsToStart() {
    final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);

    return cdbs.stream()
      .filter(cdb -> isReadyToStartAudit(cdb))
      .collect(Collectors.toList());
  }
}
