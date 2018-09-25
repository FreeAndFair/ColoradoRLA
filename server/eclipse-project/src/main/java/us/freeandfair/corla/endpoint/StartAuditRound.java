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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

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
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.PhantomBallots;

/**
 * Starts a new audit round for one or more counties.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
                   "PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity",
                   "PMD.NPathComplexity", "PMD.ExcessiveImports",
                   "PMD.TooManyStaticImports"})
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

  /**
   * Count ContestResults, create ComparisonAudits and assign them to
   * CountyDashboards.
   */
  public void initializeAuditData(final DoSDashboard dosdb) {
    final List<ContestResult> contestResults = initializeContests(dosdb.contestsToAudit());
    final List<ComparisonAudit> comparisonAudits = initializeAudits(contestResults, dosdb.auditInfo().riskLimit());
    final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);
    for (final CountyDashboard cdb : cdbs) {
      initializeCountyDashboard(cdb, comparisonAudits);
    }
  }

  /**
   * Create a ContestResult for every contest to audit.
   */
  public List<ContestResult> initializeContests(final Set<ContestToAudit> cta) {
    final List<ContestResult> countedCRs = countAndSaveContests(cta);
    LOGGER.debug(String.format("[initializeContests: cta=%s, countedCRs=%s]",
                               cta, countedCRs));
    return countedCRs;
  }

  /**
   * Warning: Contains Side Effects
   */
  public List<ComparisonAudit> initializeAudits(final List<ContestResult> contestResults,
                                                final BigDecimal riskLimit) {
    final List<ComparisonAudit> comparisonAudits = contestResults.stream()
      .map(cr -> ComparisonAuditController.createAudit(cr, riskLimit))
      .collect(Collectors.toList());

    LOGGER.debug(String.format("[initializeAudits: contestResults=%s, "
                               + "comparisonAudits=%s]",
                               contestResults, comparisonAudits));

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

    if (countyDashboardASM.currentState() !=
        CountyDashboardState.BALLOT_MANIFEST_AND_CVRS_OK) {
      LOGGER.info(String.format("[%s County missed the file upload deadline]",
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
   * counties uploading their data and before the ballot selection
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
  public List<Selection> makeSelections(final List<ComparisonAudit> comparisonAudits,
                                        final String seed,
                                        final BigDecimal riskLimit) {

    final List<Selection> selections = new ArrayList<>();
    // maybe...
    // comparisonAudits.stream()
    //   .filter(ca -> ca.isTargeted())
    //   .map(BallotSelection::randomSelection)

    for(final ComparisonAudit comparisonAudit: comparisonAudits) {
      final ContestResult contestResult = comparisonAudit.contestResult();
      // only make selection for targeted contests
      if (contestResult.getAuditReason().isTargeted()) {
        final Integer startIndex = BallotSelection.auditedPrefixLength(contestResult.getContestCVRIds());
        final Integer endIndex = comparisonAudit.optimisticSamplesToAudit();

        final Selection selection =
          BallotSelection.randomSelection(contestResult, seed,
                                          startIndex, endIndex);

        LOGGER.debug(String.format("[makeSelections for ContestResult: contestName=%s, "
                                   + "contestResult.contestCVRIds=%s, selection=%s, "
                                   + "selection.contestCVRIds=%s, startIndex=%d, endIndex=%d]",
                                   contestResult.getContestName(),
                                   contestResult.getContestCVRIds(),
                                   selection, selection.contestCVRIds(),
                                   startIndex, endIndex));

        contestResult.addContestCVRIds(selection.contestCVRIds());

        selections.add(selection);
      }
    }
    return selections;
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
    try {
      // this flag starts off true if we're going to conjoin it with all
      // the ASM states, and false otherwise as we just assume audit
      // reasonableness in the absence of ASMs. We'll remind you about
      // it at the end.
      boolean audit_complete = true;

      // FIXME map a function over a collection of dashboardsToStart
      // FIXME extract-fn (for days): update every county dashboard with
      // a list of ballots to audit
      for (final CountyDashboard cdb : dashboardsToStart()) {
        try {
          final CountyDashboardASM countyDashboardASM =
            ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));

          // If a county still has an audit underway, check to see if
          // they've achieved their risk limit before starting anything
          // else. A county that has met the risk limit is done.
          if (countyDashboardASM.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {

            // There are two ways of looking at this. The original way,
            // by estimatedSamplesToAudit: a counter in each dashboard.
            // Meh, that's state that we have to maintain.
            // TODO strike this flavor.
            if (cdb.estimatedSamplesToAudit() <= 0) {
            LOGGER
              .debug(String.format("[startRound: %s County needs to audit 0 ballots to"
                                   + " achieve its risk limit, its audit is complete.]",
                                   cdb.county().name()));
            ASMUtilities.step(RISK_LIMIT_ACHIEVED_EVENT, AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));
            countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

            ASMUtilities.save(countyDashboardASM);
            continue;
            }
            // Another way might be to ask if every audit has met its
            // risk limit. This feels a little nicer to me.
            if (cdb.auditsFinished()) {
              LOGGER.debug
                (String.format
                 ("[startRound: %s County is FINISHED. auditsFinished=%s, cdb.estimatedSamplesToAudit()=%d]",
                  cdb.county().name(),
                  cdb.auditsFinished(), cdb.estimatedSamplesToAudit()));
              ASMUtilities.step(RISK_LIMIT_ACHIEVED_EVENT, AuditBoardDashboardASM.class,
                                String.valueOf(cdb.id()));
              countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

              ASMUtilities.save(countyDashboardASM);
              continue;
            }
          }

          // Risk limit hasn't been achieved and we were never given any
          // audits to work on.
          if (cdb.comparisonAudits().isEmpty()) {
            LOGGER.debug("[startRound: county made its deadline but was assigned no contests to audit]");
            ASMUtilities.step(NO_CONTESTS_TO_AUDIT_EVENT, AuditBoardDashboardASM.class,
                              String.valueOf(cdb.id()));
            countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
            ASMUtilities.save(countyDashboardASM);
            continue; // this county is completely finished.
          }

          // Find the ballot selections for all contests that this
          // county is participating in.
          final Segment segment = Selection.combineSegments(selections.stream()
                                                            .map(s -> s.forCounty(cdb.county().id()))
                                                            .collect(Collectors.toList()));

          // Obtain all de-duplicated, ordered CVRs, then audit phantom ballots,
          // removing them from the sequence to audit so the boards don’t have
          // to.
          final List<CastVoteRecord> ballotSequenceCVRs =
              PhantomBallots.removePhantomRecords(
                  PhantomBallots.auditPhantomRecords(
                      cdb,
                      segment.cvrsInBallotSequence()));

          // ballotSequence is *just* the CVR IDs, as expected.
          final List<Long> ballotSequence = ballotSequenceCVRs.stream()
              .map(cvr -> cvr.id())
              .collect(Collectors.toList());

          LOGGER.debug(String.format("[startRound:"
                                     + " county=%s, round=%s, segment.auditSequence()=%s,"
                                     + " segment.ballotSequence()=%s, cdb.comparisonAudits=%s,",
                                     cdb.county(), cdb.currentRound(), segment.auditSequence(),
                                     ballotSequence, cdb.comparisonAudits()));
          // Risk limit hasn't been achieved. We were given some audits
          // to work on, but have nothing to do in this round. Please
          // wait patiently.
          if (ballotSequence.isEmpty()) {
            LOGGER.debug(String.format("[startRound: no ballots to audit in %s County, skipping round]",
                                       cdb.county()));
            cdb.startRound(0, 0, 0, Collections.emptyList(), Collections.emptyList());
            Persistence.saveOrUpdate(cdb);
            ASMUtilities.step(ROUND_COMPLETE_EVENT, AuditBoardDashboardASM.class, String.valueOf(cdb.id()));
            continue;
          }

          // Risk limit hasn't been achieved and we finally have something to
          // do in this round!
          ComparisonAuditController.startRound(cdb, cdb.comparisonAudits(),
                                               segment.auditSequence(),
                                               ballotSequence);
          Persistence.saveOrUpdate(cdb);

          LOGGER.debug
            (String.format
             ("[startRound: Round %d for %s County started normally."
              + " Estimated to audit %d ballots.]",
              cdb.currentRound().number(),
              cdb.county().name(), cdb.estimatedSamplesToAudit()));

          ASMUtilities.step(ROUND_START_EVENT, AuditBoardDashboardASM.class,
                            String.valueOf(cdb.id()));
          ASMUtilities.save(countyDashboardASM);

          // figure out whether this county is done, or whether there's
          // an audit to run (audit_complete is initially true, so if
          // any dashboards are NOT in a final state, we're not done.
          audit_complete &= countyDashboardASM.isInFinalState();

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
      } // end of dashboard twiddling

      // At this point, anyone who needed another round should have one.
      // If everyone is done, we're all done.

      // FIXME hoist me
      if (audit_complete) {
        my_event.set(DOS_AUDIT_COMPLETE_EVENT);
        ok(the_response, "audit complete");
      } else {
        ok(the_response, "round started");
      }
      // end of extraction. Now we can talk about HTTP requests / responses again!
    } catch (final PersistenceException e) {
      LOGGER.error("PersistenceException " + e);
      serverError(the_response, "could not start round");
    }

    return my_endpoint_result.get();
  }

  /**
   *
   * @return true if a county should be started
   */
  public Boolean isReadyToStartAudit(final CountyDashboard cdb) {
    final CountyDashboardASM countyDashboardASM =
      ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
    if (countyDashboardASM.isInInitialState() ||
        countyDashboardASM.isInFinalState()) {

      return false;
    } else {
      return true;
    }
  }

  /**
   * A dashboard is ready to start if it isn't in an initial or final
   * state.
   * @return a list of the dashboards to start.
   */
  public List<CountyDashboard> dashboardsToStart() {
    final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);

    final List<CountyDashboard> result =
      cdbs.stream()
      .filter(cdb -> isReadyToStartAudit(cdb))
      .collect(Collectors.toList());

    LOGGER.debug("[dashboardsToStart: " + result);
    return result;
  }
}
