/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Colorado Department of State
 * @license GNU Affero General Public License v3 with Classpath Exception
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CVRAuditInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;

/**
 * Controller methods relevant to comparison audits.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public final class ComparisonAuditController {
  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditController() {
    // empty
  }
  
  /**
   * Get the cast vote records to audit, in order, for the given county dashboard
   * in the specified range in the audit sequence.
   * 
   * @param the_cdb The county dashboard.
   * @param the_min_index The minimum index to return.
   * @param the_max_index The maximum index to return.
   * @return the list of ballot cards, of size the_max_index - the_min_index + 1; 
   * the first element of this list will be the "min_index"th ballot card to audit, 
   * and the last will be the "max_index"th. 
   */
  public static List<CastVoteRecord> getCVRsInAuditSequence(final CountyDashboard the_cdb,
                                                            final int the_min_index,
                                                            final int the_max_index) {
    final OptionalLong count = 
        CastVoteRecordQueries.countMatching(the_cdb.id(), RecordType.UPLOADED);
    
    if (!count.isPresent()) {
      throw new IllegalStateException("unable to count CVRs for county " + the_cdb.id());
    }

    final String seed = 
        Persistence.getByID(DoSDashboard.ID, DoSDashboard.class).auditInfo().seed();
    final boolean with_replacement = true;
    // assuming that CVRs are indexed from 0
    final int minimum = 0;
    // the number of CVRs for the_contest_to_audit - note that the sequence
    // generator generates a sequence of the numbers minimum ... maximum 
    // inclusive, so we subtract 1 from the number of CVRs to give it the
    // correct range for our actual list of CVRs (indexed from 0).
    final int maximum = (int) count.getAsLong() - 1;

    final PseudoRandomNumberGenerator prng = 
        new PseudoRandomNumberGenerator(seed, with_replacement,
                                        minimum, maximum);
    final List<Integer> list_of_cvrs_to_audit = 
        prng.getRandomNumbers(the_min_index, the_max_index);
    final Map<Integer, CastVoteRecord> matching_cvrs = 
        CastVoteRecordQueries.get(the_cdb.id(), RecordType.UPLOADED, list_of_cvrs_to_audit);
    final List<CastVoteRecord> result = new ArrayList<>();
    
    for (final int index : list_of_cvrs_to_audit) {
      result.add(matching_cvrs.get(index));
    }
    
    return result;
  }
  
  /**
   * Gets all CVRs to audit in the specified round for the specified county
   * dashboard. This returns a list in audit random sequence order.
   * 
   * @param the_dashboard The dashboard.
   * @param the_round_number The round number (indexed from 1).
   * @return the CVRs to audit in the specified round.
   * @exception IllegalArgumentException if the specified round doesn't exist.
   */
  public static List<CVRAuditInfo> cvrsToAuditInRound(final CountyDashboard the_cdb,
                                                      final int the_round_number) {
    if (the_round_number < 1 || the_cdb.rounds().size() < the_round_number) {
      throw new IllegalArgumentException("invalid round specified");
    }
    final Round round = the_cdb.rounds().get(the_round_number - 1);
    return CVRAuditInfoQueries.rangeUnique(the_cdb, round.startAuditedPrefixLength(), 
                                           round.expectedAuditedPrefixLength());
  }
  
  /**
   * @return the CVR IDs remaining to audit in the current round, or an empty 
   * list if there are no CVRs remaining to audit or if no round is in progress.
   */
  public static List<Long> cvrIDsRemainingInCurrentRound(final CountyDashboard the_cdb) {
    List<Long> result = new ArrayList<Long>();
    final Round round = the_cdb.currentRound();
    if (round != null) {
      result = 
          CVRAuditInfoQueries.unauditedCVRIDsInRange(the_cdb, round.startAuditedPrefixLength(),
                                                     round.expectedAuditedPrefixLength());
    }
    return result;
  }
  
  /**
   * Compute the ballot (cards) for audit, for a particular county dashboard and 
   * start index. This returns the specified number of cards, with or without 
   * duplicates (as requested).
   * 
   * @param the_cdb The dashboard.
   * @param the_start_index The start index.
   * @param the_ballot_count The number of ballots.
   * @param the_duplicates true to include duplicates, false otherwise.
   * @param the_audited true to include already-audited ballots, false otherwise.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_start_index,
                                                        final int the_ballot_count,
                                                        final boolean the_duplicates,
                                                        final boolean the_audited) {
    final OptionalLong county_ballots_found = 
        CastVoteRecordQueries.countMatching(the_cdb.id(), RecordType.UPLOADED);
    final long county_ballots;
    if (county_ballots_found.isPresent()) {
      county_ballots = county_ballots_found.getAsLong();
    } else {
      county_ballots = 0;
    }
    final Set<CastVoteRecord> cvr_set = new HashSet<>();
    final List<CastVoteRecord> cvr_to_audit_list = new ArrayList<>();
    
    // we need to get the CVRs for the county's sequence, starting at START, and 
    // look up their locations; note we may have to ask for the sequence more than
    // once because we may find duplicates in the sequence
    
    int start = the_start_index;
    int end = start + the_ballot_count - 1; // end is inclusive
    
    final int possible_ballots = Math.min(the_ballot_count, (int) county_ballots);
    
    // if duplicates is set we go until the list has the right number; if not,
    // we go until we hit the end of our CVR pool
    while ((the_duplicates && cvr_to_audit_list.size() < the_ballot_count) || 
           (!the_duplicates && cvr_set.size() < possible_ballots)) {
      final List<CastVoteRecord> new_cvrs = getCVRsInAuditSequence(the_cdb, start, end);
      for (int i = 0; i < new_cvrs.size(); i++) {
        final CastVoteRecord cvr = new_cvrs.get(i);
        if (!cvr_set.contains(cvr)) {
          cvr.setAuditFlag(audited(the_cdb, cvr));
        }
        if ((the_duplicates || !cvr_set.contains(cvr)) && 
            (the_audited || !cvr.auditFlag())) {
          cvr_to_audit_list.add(cvr);
        }
        cvr_set.add(cvr);
      }
      start = end + 1; // end is inclusive
      end = start + (the_ballot_count - cvr_to_audit_list.size()) - 1; // end is inclusive
      // at this point, if cvr_to_audit_list.size() < ballot_count, 
      // this is an empty range
    }
    
    return cvr_to_audit_list;
  }
  
  /**
   * Compute the ballot (cards) for audit, for a particular county dashboard,
   * a particular start index, and a particular desired audit prefix length.
   * 
   * @param the_cdb The dashboard.
   * @param the_start_index The start index.
   * @param the_desired_prefix_length The desired prefix length.
   * @param the_audited true to include already-audited ballots, false otherwise.
   * @exception IllegalArgumentException if 
   * the_start_index >= the_desired_prefix_length
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_start_index,
                                                        final int the_desired_prefix_length,
                                                        final boolean the_audited) {
    if (the_start_index >= the_desired_prefix_length) {
      throw new IllegalArgumentException("invalid sequence bounds");
    }
    
    // we need to get the CVRs for the county's sequence, starting at the_start_index,
    // and eliminate duplicates
    
    final List<CastVoteRecord> cvrs = 
        getCVRsInAuditSequence(the_cdb, the_start_index, 
                               the_desired_prefix_length - 1); // end is inclusive
    final Set<CastVoteRecord> cvr_set = new HashSet<>();
    final Set<CastVoteRecord> previous_cvr_set = new HashSet<>();
    final List<CastVoteRecord> cvr_to_audit_list = new ArrayList<>();
    
    // we should always exclude records that occurred in the sequence before 
    // the_start_index, since they are no longer relevant to the requested
    // stage of the audit
    
    if (the_start_index > 0) {
      previous_cvr_set.addAll(getCVRsInAuditSequence(the_cdb, 0, the_start_index - 1));
    }

    for (int i = 0; i < cvrs.size(); i++) {
      final CastVoteRecord cvr = cvrs.get(i);
      if (!cvr_set.contains(cvr) && !previous_cvr_set.contains(cvr)) {
        cvr.setAuditFlag(audited(the_cdb, cvr));
        if (the_audited || !cvr.auditFlag()) {
          cvr_to_audit_list.add(cvr);
        }
      }
      cvr_set.add(cvr);
    }
    
    return cvr_to_audit_list;
  }
  
  /**
   * Compute the ballot (cards) for audit, for a particular county dashboard and 
   * round. The returned list does not have duplicates.
   * 
   * @param the_cdb The dashboard.
   * @param the_round The round number.
   * @param the_audited True to include already-audited ballots, false otherwise.
   * @exception IllegalArgumentException if the specified dashboard does not have
   * a round with the specified number, or if a round number <= 0 is specified.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_round,
                                                        final boolean the_audited) {
    if (the_round <= 0 || the_cdb.rounds().size() < the_round) {
      throw new IllegalArgumentException("invalid round number");
    }
    // round numbers are 1-based, not 0-based
    final Round round = the_cdb.rounds().get(the_round - 1);
    return computeBallotOrder(the_cdb, round.startAuditedPrefixLength(), 
                              round.expectedAuditedPrefixLength(), 
                              the_audited);
  }
  
  /**
   * Initializes the audit data for the specified county dashboard and starts its
   * first audit round.
   * 
   * @param the_cdb The dashboard.
   * @return true if an audit round is started for the dashboard, false otherwise; 
   * an audit round might not be started if there are no driving contests in the
   * county, or if the county needs to audit 0 ballots to meet the risk limit.
   */
  public static boolean initializeAuditData(final CountyDashboard the_cdb) {
    boolean result = true;
    final DoSDashboard dosdb =
        Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    final BigDecimal risk_limit = dosdb.auditInfo().riskLimit();
    final Set<Contest> all_driving_contests = new HashSet<>();
    final Set<Contest> county_driving_contests = new HashSet<>();
    final Set<CountyContestComparisonAudit> comparison_audits = new HashSet<>();
    final Map<Contest, AuditReason> contest_reasons = new HashMap<>();
    
    int to_audit = Integer.MIN_VALUE;
    
    for (final ContestToAudit cta : dosdb.contestsToAudit()) {
      if (cta.audit() == AuditType.COMPARISON) {
        all_driving_contests.add(cta.contest());
      }
      contest_reasons.put(cta.contest(), cta.reason());
    }
    
    the_cdb.setAuditedPrefixLength(0);
    the_cdb.setAuditedSampleCount(0);
    for (final CountyContestResult ccr : 
         CountyContestResultQueries.forCounty(the_cdb.county())) {
      AuditReason reason = contest_reasons.get(ccr.contest());
      if (reason == null) {
        reason = AuditReason.OPPORTUNISTIC_BENEFITS;
      }
      final CountyContestComparisonAudit audit = 
          new CountyContestComparisonAudit(the_cdb, ccr, risk_limit, reason);
      final Contest contest = audit.contest();
      comparison_audits.add(audit);
      if (all_driving_contests.contains(contest)) {
        to_audit = Math.max(to_audit, audit.initialSamplesToAudit());
        county_driving_contests.add(contest);
      }
    }
    the_cdb.setComparisonAudits(comparison_audits);
    Main.LOGGER.info("driving contests setting: " + county_driving_contests);
    the_cdb.setDrivingContests(county_driving_contests);
    the_cdb.setEstimatedSamplesToAudit(Math.max(0,  to_audit));
    the_cdb.setOptimisticSamplesToAudit(Math.max(0,  to_audit));
    if (!county_driving_contests.isEmpty() && 0 < to_audit) {
      the_cdb.setCVRsToAudit(getCVRsInAuditSequence(the_cdb, 0, to_audit - 1));
      the_cdb.startRound(computeBallotOrder(the_cdb, 0, to_audit, false).size(),
                         to_audit, 0);
    } else {
      result = false;
    }
    
    return result;
  }
  
  /**
   * Starts a new round on the specified dashboard with the specified number
   * of physical ballots.
   * 
   * @param the_cdb The dashboard.
   * @param the_round_length The count.
   * @param the_multiplier The multiplier.
   * @return true if a round is started, false otherwise (a round might not
   * be started because the risk limit has already been achieved).
   * @exception IllegalStateException if a round cannot be started from 
   * estimates because there are no previous rounds.
   */
  public static boolean startNewRoundOfLength(final CountyDashboard the_cdb,
                                              final int the_round_length,
                                              final BigDecimal the_multiplier) {
    final List<Round> rounds = the_cdb.rounds();
    int start_index = 0;
    if (rounds.isEmpty()) {
      throw new IllegalArgumentException("no previous audit rounds");
    } else {
      final Round previous_round = rounds.get(rounds.size() - 1);
      // we start the next round where the previous round actually ended 
      // in the audit sequence
      start_index = previous_round.actualAuditedPrefixLength();
    }

    final List<CastVoteRecord> new_cvrs = 
        ComparisonAuditController.getCVRsInAuditSequence(the_cdb, 
                                                         start_index, 
                                                         the_round_length);
    List<CastVoteRecord> extra_cvrs = new_cvrs;
    final Set<CastVoteRecord> unique_new_cvrs = new HashSet<>(new_cvrs);
    while (!extra_cvrs.isEmpty() && unique_new_cvrs.size() < the_round_length) {
      extra_cvrs =
          ComparisonAuditController.
               getCVRsInAuditSequence(the_cdb, start_index + new_cvrs.size(),
                                      the_round_length - unique_new_cvrs.size());
      new_cvrs.addAll(extra_cvrs);
      unique_new_cvrs.addAll(extra_cvrs);
    }
    the_cdb.addCVRsToAudit(new_cvrs);
    Persistence.saveOrUpdate(the_cdb);
    final int expected_prefix_length = the_cdb.cvrAuditInfo().size();
    for (int i = start_index; i < expected_prefix_length; i++) {
      final CVRAuditInfo cvrai = the_cdb.cvrAuditInfo().get(i);
      if (cvrai.acvr() != null && !unique_new_cvrs.contains(cvrai.cvr())) {
        unique_new_cvrs.remove(cvrai.cvr());
        CVRAuditInfoQueries.updateMatching(the_cdb, cvrai.cvr(), cvrai.acvr());
      }
    }
    if (unique_new_cvrs.isEmpty()) {
      return false;
    } else {
      Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
          the_cdb.id() + " at audit sequence number " + start_index + 
          " with " + unique_new_cvrs.size() + " ballots to audit");
      the_cdb.startRound(the_round_length, expected_prefix_length, start_index);
      return true;
    } 
  }
  
  /**
   * Starts a new round on the specified dashboard, using the current error
   * rates to estimate the necessary number of ballots to audit.
   * 
   * @param the_cdb The dashboard.
   * @param the_multiplier The multiplier.
   * @return true if a round is started, false otherwise (a round might not
   * be started because the risk limit has already been achieved).
   * @exception IllegalStateException if a round cannot be started from 
   * estimates because there are no previous rounds or because there are no CVRs.
   */
  public static boolean 
      startNewRoundFromEstimates(final CountyDashboard the_cdb,
                                 final BigDecimal the_multiplier) {
    final OptionalLong cvr_count = 
        CastVoteRecordQueries.countMatching(the_cdb.id(), RecordType.UPLOADED);
    if (!cvr_count.isPresent()) {
      throw new IllegalArgumentException("no cvrs");
    }
    final List<Round> rounds = the_cdb.rounds();
    int start_index = 0;
    if (rounds.isEmpty()) {
      throw new IllegalArgumentException("no previous audit rounds");
    } else {
      final Round previous_round = rounds.get(rounds.size() - 1);
      // we start the next round where the previous round actually ended 
      // in the audit sequence
      start_index = previous_round.actualAuditedPrefixLength();
    }
    boolean result = false;
    if (the_cdb.ballotsAudited() == cvr_count.getAsLong()) {
      // if we've audited all the CVRs already, we're done
      Main.LOGGER.info("all CVRs have been audited, not starting new round");
    } else {
      // use estimates based on current error rate to get length of round
      // we keep doing this until we find a CVR to actually audit
      final Set<CastVoteRecord> unique_new_cvrs = new HashSet<>();
      int expected_prefix_length = 0;
      while (unique_new_cvrs.isEmpty()) {
        expected_prefix_length = computeEstimatedSamplesToAudit(the_cdb);
        if (the_cdb.auditedPrefixLength() < expected_prefix_length) {
          final List<CastVoteRecord> new_cvrs = 
              getCVRsInAuditSequence(the_cdb, start_index, 
                                     expected_prefix_length - 1);
          Main.LOGGER.info("cvrs in audit sequence: " + new_cvrs.size());
          the_cdb.addCVRsToAudit(new_cvrs);
          Persistence.saveOrUpdate(the_cdb);
          unique_new_cvrs.addAll(new_cvrs);
          final Set<CastVoteRecord> updated_cvrs = new HashSet<>();
          for (int i = 0; i < expected_prefix_length; i++) {
            final CVRAuditInfo cvrai = the_cdb.cvrAuditInfo().get(i);
            if (cvrai.acvr() != null && !updated_cvrs.contains(cvrai.cvr())) {
              CVRAuditInfoQueries.updateMatching(the_cdb, cvrai.cvr(), cvrai.acvr());
              updated_cvrs.add(cvrai.cvr());
            }
          }
          unique_new_cvrs.removeAll(updated_cvrs);
          if (unique_new_cvrs.isEmpty()) { 
            // we didn't find a CVR, so "audit" the CVRs we've added and try again
            updateCVRUnderAudit(the_cdb);
          }
        }

        final int round_length = unique_new_cvrs.size();
        Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
            the_cdb.id() + " at audit sequence number " + start_index + 
            " with " + round_length + " ballots to audit");
        the_cdb.startRound(round_length, expected_prefix_length, 
                           start_index);
        result = true;
      }
    }
    return result;
  }
  
  /**
   * Submit an audit CVR for a CVR under audit to the specified county dashboard.
   * 
   * @param the_cdb The dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The corresponding audit CVR.
   * @return true if the audit CVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit, or the specified CVR under audit was
   * not in fact under audit.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
  public static boolean submitAuditCVR(final CountyDashboard the_cdb,
                                       final CastVoteRecord the_cvr_under_audit, 
                                       final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;
    
    final List<CVRAuditInfo> info = 
        CVRAuditInfoQueries.matching(the_cdb, the_cvr_under_audit);
    
    if (info == null || info.isEmpty()) {
      Main.LOGGER.info("attempt to submit ACVR for county " + 
                       the_cdb.id() + ", cvr " +
                       the_cvr_under_audit.id() + " not under audit");
    } else if (checkACVRSanity(the_cvr_under_audit, the_audit_cvr)) {
      // if the record is the current CVR under audit, or if it hasn't been
      // audited yet, we can just process it
      final CastVoteRecord old_audit_cvr = info.get(0).acvr();
      if (old_audit_cvr == null) {
        // this audits all instances of the ballot in our current sequence; 
        // they might be out of order, but that's OK because we have strong
        // requirements about finishing rounds before looking at results as
        // final and valid
        the_cdb.addAuditedBallot();
        for (final CVRAuditInfo c : info) {
          c.setACVR(the_audit_cvr);
        }
        audit(the_cdb, info, true);
        the_cdb.setAuditedSampleCount(the_cdb.auditedSampleCount() + info.size());
      } else {
        // the record has been audited before, so we need to "unaudit" it 
        // this requires a linear search over the matching records to see
        // how many have been counted, since we don't know what order our
        // query returned them in and we can't order them by list index
        final List<CVRAuditInfo> info_to_undo = new ArrayList<>();
        for (final CVRAuditInfo c : info) {
          if (c.counted()) {
            info_to_undo.add(c);
          }
        }
        unaudit(the_cdb, info_to_undo);
        for (final CVRAuditInfo c : info) {
          c.setACVR(the_audit_cvr);
        }
        audit(the_cdb, info, true);
      }
      result = true;
    }  else {
      Main.LOGGER.info("attempt to submit non-corresponding ACVR " +
                       the_audit_cvr.id() + " for county " + the_cdb.id() + 
                       ", cvr " + the_cvr_under_audit.id());
    }
    Persistence.flush();
    updateCVRUnderAudit(the_cdb);
    the_cdb.
        setEstimatedSamplesToAudit(computeEstimatedSamplesToAudit(the_cdb) -
                                   the_cdb.auditedSampleCount());
    the_cdb.
        setOptimisticSamplesToAudit(computeOptimisticSamplesToAudit(the_cdb) -
                                    the_cdb.auditedSampleCount());
    the_cdb.updateAuditStatus();
    return result;
  }
  
  /**
   * Computes the estimated total number of samples to audit on the specified
   * county dashboard. This uses the minimum samples to audit calculation, 
   * increased by the percentage of discrepancies seen in the audited ballots
   * so far. 
   * 
   * @param the_cdb The dashboard.
   */
  public static int 
      computeEstimatedSamplesToAudit(final CountyDashboard the_cdb) {
    int to_audit = Integer.MIN_VALUE;
    final Set<Contest> driving_contests = the_cdb.drivingContests();
    for (final CountyContestComparisonAudit ccca : the_cdb.comparisonAudits()) {
      if (driving_contests.contains(ccca.contest())) {
        final int bta = ccca.estimatedSamplesToAudit();
        to_audit = Math.max(to_audit, bta);
      }
    }
    return Math.max(0,  to_audit);
  }
  
  /**
   * Computes the optimistic total number of samples to audit on the specified
   * county dashboard. This uses the minimum samples to audit calculation.
   * 
   * @param the_cdb The dashboard.
   */
  public static int 
      computeOptimisticSamplesToAudit(final CountyDashboard the_cdb) {
    int to_audit = Integer.MIN_VALUE;
    final Set<Contest> driving_contests = the_cdb.drivingContests();
    for (final CountyContestComparisonAudit ccca : the_cdb.comparisonAudits()) {
      // we compute this even for non-driving contests, so we can see if their
      // risk limits were achieved
      final int bta = ccca.optimisticSamplesToAudit();
      if (driving_contests.contains(ccca.contest())) {
        to_audit = Math.max(to_audit, bta);
      }
    }
    return Math.max(0,  to_audit);
  }
  
  /**
   * Checks to see if the specified CVR has been audited on the specified county
   * dashboard. This check sets the audit flag on the CVR record in memory, 
   * so its result can be accessed later without an expensive database hit.
   * 
   * @param the_cdb The county dashboard.
   * @param the_cvr The CVR.
   * @return true if the specified CVR has been audited, false otherwise.
   */
  public static boolean audited(final CountyDashboard the_cdb, 
                                final CastVoteRecord the_cvr) {
    final List<CVRAuditInfo> info = 
        CVRAuditInfoQueries.matching(the_cdb, the_cvr);
    final boolean result;
    if (info.isEmpty() || info.get(0).acvr() == null) {
      result = false;
    } else {
      result = true;
    }
    return result;
  }
  
  /**
   * Audits a CVR/ACVR pair by adding it to all the audits in progress.
   * This also updates the local audit counters, as appropriate.
   * 
   * @param the_cdb The dashboard.
   * @param the_info The CVRAuditInfo records to audit; it is assumed that
   * they are all identical with respect to CVR/ACVR pairs.
   * @param the_count The number of times to count this ballot in the
   * audit.
   * @param the_update_counters true to update the county dashboard 
   * counters, false otherwise; false is used when this ballot 
   * has already been audited once.
   */
  @SuppressWarnings({"PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
      "PMD.NPathComplexity"})
  private static void audit(final CountyDashboard the_cdb,
                            final List<CVRAuditInfo> the_info, 
                            final boolean the_update_counters) {
    final Set<Contest> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.get(0).cvr();
    final CastVoteRecord audit_cvr = the_info.get(0).acvr();
    
    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest());
      }
    }
    
    for (final CountyContestComparisonAudit ca : the_cdb.comparisonAudits()) {
      final OptionalInt discrepancy = 
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (final CVRAuditInfo cvrai : the_info) {
          ca.recordDiscrepancy(cvrai, discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }
      if (contest_disagreements.contains(ca.contest())) {
        for (final CVRAuditInfo cvrai : the_info) {
          ca.recordDisagreement(cvrai);
        }
        disagreements.add(ca.auditReason());
      }
      ca.signalSampleAudited(the_info.size());
      Persistence.saveOrUpdate(ca);
    }
    
    for (final CVRAuditInfo cvrai : the_info) {
      cvrai.setDiscrepancy(discrepancies);
      cvrai.setDisagreement(disagreements);
      cvrai.setCounted(true);
      Persistence.saveOrUpdate(cvrai);
    }
    
    if (the_update_counters) {
      the_cdb.addDiscrepancy(discrepancies);
      the_cdb.addDisagreement(disagreements);
    }
  }
  
  /**
   * "Unaudits" a CVR/ACVR pair by removing it from all the audits in 
   * progress in the specified county dashboard. This also updates the
   * dashboard's counters as appropriate.
   *
   * @param the_cdb The county dashboard.
   * @param the_info The list of CVRAuditInfo to unaudit. It is assumed
   * they are all identical with respect to CVR/ACVR.
   */
  @SuppressWarnings("PMD.NPathComplexity")
  private static void unaudit(final CountyDashboard the_cdb,
                              final List<CVRAuditInfo> the_info) {
    final Set<Contest> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.get(0).cvr();
    final CastVoteRecord audit_cvr = the_info.get(0).acvr();
    
    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest());
      }
    }
    
    for (final CountyContestComparisonAudit ca : the_cdb.comparisonAudits()) {
      final OptionalInt discrepancy = 
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < the_info.size(); i++) {
          ca.removeDiscrepancy(the_info.get(i), discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }
      if (contest_disagreements.contains(ca.contest())) {
        for (final CVRAuditInfo cvrai : the_info) {
          ca.removeDisagreement(cvrai);
        }
        disagreements.add(ca.auditReason());
      }
      ca.signalSampleUnaudited(the_info.size());
      Persistence.saveOrUpdate(ca);
    }
    
    for (final CVRAuditInfo cvrai : the_info) {
      cvrai.setDisagreement(null);
      cvrai.setDiscrepancy(null);
      cvrai.setCounted(false);
      Persistence.saveOrUpdate(cvrai);
    }
    
    the_cdb.removeDiscrepancy(discrepancies);
    the_cdb.removeDisagreement(disagreements);
  }
  
  /**
   * Updates the current CVR to audit index of the specified county
   * dashboard to the first CVR after the current CVR under audit that
   * lacks an ACVR. This "audits" all the CVR/ACVR pairs it finds 
   * in between, and extends the sequence of ballots to audit if it
   * reaches the end and the audit is not concluded.
   * 
   * @param the_cdb The dashboard.
   */
  private static void 
      updateCVRUnderAudit(final CountyDashboard the_cdb) {
    final List<CVRAuditInfo> cvr_audit_info = 
        CVRAuditInfoQueries.range(the_cdb, 
                                  the_cdb.auditedPrefixLength(),
                                  the_cdb.currentRound().
                                  expectedAuditedPrefixLength());
    int index = the_cdb.auditedPrefixLength();
    for (final CVRAuditInfo cai : cvr_audit_info) {
      if (cai.acvr() == null) {
        break;
      } else if (!cai.counted()) {
        // we only count this CVRAuditInfo if we didn't count it before, 
        // because it might have been counted earlier in its round
        audit(the_cdb, Arrays.asList(new CVRAuditInfo[]{cai}), false);
        the_cdb.setAuditedSampleCount(the_cdb.auditedSampleCount() + 1);
      }
      index = index + 1;
    }
    the_cdb.setAuditedPrefixLength(index);
  }
  
  /**
   * Checks that the specified CVR and ACVR are an audit pair, and that
   * the specified ACVR is auditor generated.
   * 
   * @param the_cvr The CVR.
   * @param the_acvr The ACVR.
   */
  private static boolean checkACVRSanity(final CastVoteRecord the_cvr,
                                         final CastVoteRecord the_acvr) {
    return the_cvr.isAuditPairWith(the_acvr) &&
           the_acvr.recordType().isAuditorGenerated();
  }
}
