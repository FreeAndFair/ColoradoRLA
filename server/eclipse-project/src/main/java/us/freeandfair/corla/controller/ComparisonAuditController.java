/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
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
   * Gets the sequence numbers, in audit sequence order, of the CVRs to audit 
   * for the given county dashboard in the specified range in the audit sequence.
   * 
   * @param the_cdb The county dashboard.
   * @param the_min_index The minimum index to return.
   * @param the_max_index The maximum index to return.
   * @return the list of sequence numbers, of size 
   * the_max_index - the_min_index + 1; the first element of this list will be
   * the "min_index"th ballot card to audit, and the last will be the "max_index"th. 
   */
  public static List<Integer> getCVRSeqNumsInAuditSequence(final County the_county,
                                                           final int the_min_index,
                                                           final int the_max_index) {
    final OptionalLong count = 
        CastVoteRecordQueries.countMatching(the_county.id(), RecordType.UPLOADED);
    
    if (!count.isPresent()) {
      throw new IllegalStateException("unable to count CVRs for county " + the_county.id());
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
    return prng.getRandomNumbers(the_min_index, the_max_index);
  }
  
  /**
   * Get the CVRs for the specified list of sequence numbers for the specified 
   * county.
   * 
   * @param the_county The county.
   * @param the_seq_num_list The sequence number list.
   */
  public static List<CastVoteRecord> 
      getCVRsForSequenceNumbers(final County the_county,
                                final List<Integer> the_seq_num_list) {
    final Map<Integer, CastVoteRecord> matching_cvrs = 
        CastVoteRecordQueries.get(the_county.id(), RecordType.UPLOADED, the_seq_num_list);
    final List<CastVoteRecord> result = new ArrayList<>();
    
    for (final int index : the_seq_num_list) {
      result.add(matching_cvrs.get(index));
    }
    
    return result;    
  }
  
  /**
   * Get the cast vote records to audit, in order, for the given county
   * in the specified range in the audit sequence.
   * 
   * @param the_county The county.
   * @param the_min_index The minimum index to return.
   * @param the_max_index The maximum index to return.
   * @return the list of ballot cards, of size the_max_index - the_min_index + 1; 
   * the first element of this list will be the "min_index"th ballot card to audit, 
   * and the last will be the "max_index"th. 
   */
  public static List<CastVoteRecord> getCVRsInAuditSequence(final County the_county,
                                                            final int the_min_index,
                                                            final int the_max_index) {
    final List<Integer> list_of_cvrs_to_audit = 
        getCVRSeqNumsInAuditSequence(the_county, the_min_index, the_max_index);
    return getCVRsForSequenceNumbers(the_county, list_of_cvrs_to_audit);
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
    final Set<Long> id_set = new HashSet<>();
    final List<CVRAuditInfo> result = new ArrayList<>();
    
    for (final Long cvr_id : round.auditSubsequence()) {
      if (!id_set.contains(cvr_id)) {
        id_set.add(cvr_id);
        result.add(Persistence.getByID(cvr_id, CVRAuditInfo.class));
      }
    }
    
    return result;
  }
  
  /**
   * @return the CVR IDs remaining to audit in the current round, or an empty 
   * list if there are no CVRs remaining to audit or if no round is in progress.
   */
  public static List<Long> cvrIDsRemainingInCurrentRound(final CountyDashboard the_cdb) {
    final List<Long> result = new ArrayList<Long>();
    final Round round = the_cdb.currentRound();
    if (round != null) {
      for (int i = 0; 
           i + round.actualAuditedPrefixLength() < round.expectedAuditedPrefixLength(); 
           i++) {
        result.add(round.auditSubsequence().get(i + round.actualAuditedPrefixLength()));
      }
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
    if (county_ballots_found.isPresent() && 0 < county_ballots_found.getAsLong()) {
      county_ballots = county_ballots_found.getAsLong();
    } else {
      // we can't generate a list for this county
      return new ArrayList<>();
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
      final List<CastVoteRecord> new_cvrs = 
          getCVRsInAuditSequence(the_cdb.county(), start, end);
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
   * @return the list of ballot (cards) for audit; if the query does not result
   * in any ballot (cards), as when the prefix length or start index is invalid,
   * the returned list is empty.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_start_index,
                                                        final int the_desired_prefix_length,
                                                        final boolean the_audited) {
    if (the_start_index < 0 || the_desired_prefix_length <= the_start_index ||
        the_cdb.cvrFile() == null) {
      return new ArrayList<>();
    }
    
    // we need to get the CVRs for the county's sequence, starting at the_start_index,
    // and eliminate duplicates
    
    final List<CastVoteRecord> cvrs = 
        getCVRsInAuditSequence(the_cdb.county(), the_start_index, 
                               the_desired_prefix_length - 1); // end is inclusive
    final Set<CastVoteRecord> cvr_set = new HashSet<>();
    final Set<CastVoteRecord> previous_cvr_set = new HashSet<>();
    final List<CastVoteRecord> cvr_to_audit_list = new ArrayList<>();
    
    // we should always exclude records that occurred in the sequence before 
    // the_start_index, since they are no longer relevant to the requested
    // stage of the audit
    
    if (the_start_index > 0) {
      previous_cvr_set.addAll(getCVRsInAuditSequence(the_cdb.county(), 0, 
                                                     the_start_index - 1));
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
   * Compute the ballot (cards) for audit, for a particular county and 
   * round. The returned list does not have duplicates, and is in _arbitrary order_.
   * 
   * @param the_cdb The dashboard.
   * @param the_round The round number.
   * @param the_audited True to include already-audited ballots, false otherwise.
   * @return the list of ballot (cards) for audit; if the query does not result
   * in any ballot (cards), as when the round number is invalid, the returned list 
   * is empty.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> ballotsToAudit(final CountyDashboard the_cdb,
                                                    final int the_round,
                                                    final boolean the_audited) {
    if (the_round <= 0 || the_cdb.rounds().size() < the_round) {
      return new ArrayList<>();
    }
    // round numbers are 1-based, not 0-based
    final Round round = the_cdb.rounds().get(the_round - 1);
    
    // we already have the list of CVR IDs for the round
    final List<CastVoteRecord> cvrs = CastVoteRecordQueries.get(round.ballotSequence());
    
    for (final CastVoteRecord cvr : cvrs) {
      cvr.setAuditFlag(audited(the_cdb, cvr));
    }

    return cvrs;
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
      // the list of CVRs to audit, in audit sequence order
      final List<CastVoteRecord> cvrs_to_audit =
          getCVRsInAuditSequence(the_cdb.county(), 0, to_audit - 1);
      
      // the IDs of the CVRs to audit, in audit sequence order
      final List<Long> audit_subsequence_ids = new ArrayList<Long>();
      for (final CastVoteRecord cvr : cvrs_to_audit) {
        audit_subsequence_ids.add(cvr.id());
      }
      
      // deduplicate the CVRs and put them in ballot order
      final SortedSet<CastVoteRecord> sorted_deduplicated_cvrs = 
          new TreeSet<CastVoteRecord>(new CastVoteRecord.BallotOrderComparator());
      sorted_deduplicated_cvrs.addAll(cvrs_to_audit);
      final List<Long> ballot_ids_to_audit = new ArrayList<Long>();
      for (final CastVoteRecord cvr : sorted_deduplicated_cvrs) {
        ballot_ids_to_audit.add(cvr.id());
      }
      
      the_cdb.startRound(sorted_deduplicated_cvrs.size(),
                         to_audit, 0, 
                         ballot_ids_to_audit,
                         audit_subsequence_ids);
      updateRound(the_cdb, the_cdb.currentRound());
      updateCVRUnderAudit(the_cdb);
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
    
    // the list of CVRs to audit, in audit sequence order
    final List<CastVoteRecord> new_cvrs =
        getCVRsInAuditSequence(the_cdb.county(), start_index, the_round_length);
    
    List<CastVoteRecord> extra_cvrs = new_cvrs;
    final SortedSet<CastVoteRecord> sorted_deduplicated_new_cvrs = 
        new TreeSet<>(new CastVoteRecord.BallotOrderComparator());
    sorted_deduplicated_new_cvrs.addAll(new_cvrs);
    while (!extra_cvrs.isEmpty() && 
           sorted_deduplicated_new_cvrs.size() < the_round_length) {
      extra_cvrs =
          getCVRsInAuditSequence(the_cdb.county(), start_index + new_cvrs.size(),
                                 the_round_length - sorted_deduplicated_new_cvrs.size());
      new_cvrs.addAll(extra_cvrs);
      sorted_deduplicated_new_cvrs.addAll(extra_cvrs);
    }
    
    // the IDs of the CVRs to audit, in audit sequence order
    final List<Long> new_cvr_ids = new ArrayList<>();
    for (final CastVoteRecord cvr : new_cvrs) {
      new_cvr_ids.add(cvr.id());
    }
    
    // the unique IDs of the CVRs to audit
    final Set<Long> unique_new_cvr_ids = new HashSet<>(new_cvr_ids);
    
    for (final Round round : the_cdb.rounds()) {
      for (final Long cvr_id : round.ballotSequence()) {
        if (unique_new_cvr_ids.contains(cvr_id)) {
          unique_new_cvr_ids.remove(cvr_id);
          sorted_deduplicated_new_cvrs.remove(Persistence.getByID(cvr_id, 
                                                                  CastVoteRecord.class));
        }
      }
    }
    
    if (sorted_deduplicated_new_cvrs.isEmpty()) {
      return false;
    } else {
      Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
          the_cdb.id() + " at audit sequence number " + start_index + 
          " with " + sorted_deduplicated_new_cvrs.size() + " ballots to audit");
      final List<Long> ballot_ids_to_audit = new ArrayList<>();
      for (final CastVoteRecord cvr : sorted_deduplicated_new_cvrs) {
        ballot_ids_to_audit.add(cvr.id());
      }
      the_cdb.startRound(sorted_deduplicated_new_cvrs.size(), 
                         start_index + new_cvrs.size(), 
                         start_index, ballot_ids_to_audit, new_cvr_ids);
      updateRound(the_cdb, the_cdb.currentRound());
      updateCVRUnderAudit(the_cdb);
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
      final SortedSet<CastVoteRecord> sorted_deduplicated_new_cvrs = 
          new TreeSet<>(new CastVoteRecord.BallotOrderComparator());
      final List<CastVoteRecord> new_cvrs = new ArrayList<>();
      int expected_prefix_length = 0;
      while (sorted_deduplicated_new_cvrs.isEmpty()) {
        expected_prefix_length = computeEstimatedSamplesToAudit(the_cdb);
        if (the_cdb.auditedPrefixLength() < expected_prefix_length) {
          final List<CastVoteRecord> extra_cvrs = 
              getCVRsInAuditSequence(the_cdb.county(), start_index, 
                                     expected_prefix_length - 1);
          new_cvrs.addAll(extra_cvrs);
          Persistence.saveOrUpdate(the_cdb);
          sorted_deduplicated_new_cvrs.addAll(new_cvrs);
          
          final Set<Long> unique_new_cvr_ids = new HashSet<>();
          for (final CastVoteRecord cvr : sorted_deduplicated_new_cvrs) {
            unique_new_cvr_ids.add(cvr.id());
          }
          for (final Round round : the_cdb.rounds()) {
            for (final Long cvr_id : round.ballotSequence()) {
              if (unique_new_cvr_ids.contains(cvr_id)) {
                unique_new_cvr_ids.remove(cvr_id);
                sorted_deduplicated_new_cvrs.remove(Persistence.getByID(cvr_id, 
                                                                        CastVoteRecord.class));
              }
            }
          }
        }
      }

      final int round_length = sorted_deduplicated_new_cvrs.size();
      
      // the ids of the CVRs to audit, in audit sequence order
      final List<Long> new_cvr_ids = new ArrayList<>();
      for (final CastVoteRecord cvr : new_cvrs) {
        new_cvr_ids.add(cvr.id());
      }

      // the ids of the CVRs to audit, deduplicated, in ballot order
      final List<Long> ballot_ids_to_audit = new ArrayList<>();
      for (final CastVoteRecord cvr : sorted_deduplicated_new_cvrs) {
        ballot_ids_to_audit.add(cvr.id());
      }
      Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
          the_cdb.id() + " at audit sequence number " + start_index + 
          " with " + round_length + " ballots to audit");
      the_cdb.startRound(round_length, expected_prefix_length, 
                         start_index, ballot_ids_to_audit, new_cvr_ids);
      updateRound(the_cdb, the_cdb.currentRound());
      updateCVRUnderAudit(the_cdb);
      result = true;
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
    
    final CVRAuditInfo info = 
        Persistence.getByID(the_cvr_under_audit.id(), CVRAuditInfo.class);
    
    if (info == null) {
      Main.LOGGER.info("attempt to submit ACVR for county " + 
                       the_cdb.id() + ", cvr " +
                       the_cvr_under_audit.id() + " not under audit");
    } else if (checkACVRSanity(the_cvr_under_audit, the_audit_cvr)) {
      // if the record is the current CVR under audit, or if it hasn't been
      // audited yet, we can just process it
      if (info.acvr() == null) {
        // this audits all instances of the ballot in our current sequence; 
        // they might be out of order, but that's OK because we have strong
        // requirements about finishing rounds before looking at results as
        // final and valid
        info.setACVR(the_audit_cvr);
        final int new_count = audit(the_cdb, info, true);
        the_cdb.addAuditedBallot();
        the_cdb.setAuditedSampleCount(the_cdb.auditedSampleCount() + 
                                      new_count);
      } else {
        // the record has been audited before, so we need to "unaudit" it
        final int former_count = unaudit(the_cdb, info);
        info.setACVR(the_audit_cvr);
        final int new_count = audit(the_cdb, info, true);
        the_cdb.setAuditedSampleCount(the_cdb.auditedSampleCount() -
                                      former_count + new_count);
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
    final CVRAuditInfo info = Persistence.getByID(the_cvr.id(), CVRAuditInfo.class);
    final boolean result;
    if (info == null || info.acvr() == null) {
      result = false;
    } else {
      result = true;
    }
    return result;
  }
  

  /**
   * Updates a round object with the disagreements and discrepancies
   * that already exist for CVRs in its audit subsequence, creates
   * any CVRAuditInfo objects that don't exist but need to, and
   * increases the multiplicity of any CVRAuditInfo objects that already
   * exist and are duplicated in this round.
   * 
   * @param the_cdb The county dashboard to update.
   * @param the_round The round to update.
   */
  private static void updateRound(final CountyDashboard the_cdb, 
                                  final Round the_round) {
    for (final Long cvr_id : new HashSet<>(the_round.auditSubsequence())) {
      final Map<Contest, AuditReason> audit_reasons = new HashMap<>();
      final Set<AuditReason> discrepancies = new HashSet<>();
      final Set<AuditReason> disagreements = new HashSet<>();
      CVRAuditInfo cvrai = Persistence.getByID(cvr_id, CVRAuditInfo.class);
      
      if (cvrai == null) {
        // create it if it doesn't exist
        cvrai = new CVRAuditInfo(Persistence.getByID(cvr_id, CastVoteRecord.class));
        cvrai.setMultiplicity(Collections.frequency(the_round.auditSubsequence(), cvr_id));
        Persistence.saveOrUpdate(cvrai);
      } else if (cvrai.acvr() != null) {
        // update the round statistics as necessary
        for (final CountyContestComparisonAudit ca : the_cdb.comparisonAudits()) {
          audit_reasons.put(ca.contest(), ca.auditReason());
          if (!discrepancies.contains(ca.auditReason()) && 
              ca.computeDiscrepancy(cvrai.cvr(), cvrai.acvr()).isPresent()) {
            discrepancies.add(ca.auditReason());
          }
        }
        for (final CVRContestInfo ci : cvrai.acvr().contestInfo()) {
          final AuditReason reason = audit_reasons.get(ci.contest());
          if (ci.consensus() == ConsensusValue.NO) {
            disagreements.add(reason);
          }
        }
        
        final int multiplicity = Collections.frequency(the_round.auditSubsequence(),
                                                       cvr_id);
        for (int i = 0; i < multiplicity; i++) {
          the_round.addDiscrepancy(discrepancies);
          the_round.addDisagreement(disagreements);
        }
        
        cvrai.setMultiplicity(cvrai.multiplicity() + multiplicity);
        cvrai.setCounted(cvrai.counted() + multiplicity);
      }
    }
  }
  
  /**
   * Audits a CVR/ACVR pair by adding it to all the audits in progress.
   * This also updates the local audit counters, as appropriate.
   * 
   * @param the_cdb The dashboard.
   * @param the_info The CVRAuditInfo to audit.
   * @param the_update_counters true to update the county dashboard 
   * counters, false otherwise; false is used when this ballot 
   * has already been audited once.
   * @return the number of times the record was audited.
   */
  @SuppressWarnings({"PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
      "PMD.NPathComplexity"})
  private static int audit(final CountyDashboard the_cdb,
                           final CVRAuditInfo the_info, 
                           final boolean the_update_counters) {
    final Set<Contest> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.cvr();
    final CastVoteRecord audit_cvr = the_info.acvr();
    
    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest());
      }
    }
    
    final int audit_count = the_info.multiplicity() - the_info.counted();
    for (final CountyContestComparisonAudit ca : the_cdb.comparisonAudits()) {
      final OptionalInt discrepancy = 
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < audit_count; i++) {
          ca.recordDiscrepancy(the_info, discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }
      if (contest_disagreements.contains(ca.contest())) {
        for (int i = 0; i < audit_count; i++) {
          ca.recordDisagreement(the_info);
        }
        disagreements.add(ca.auditReason());
      }
      ca.signalSampleAudited(audit_count);
      Persistence.saveOrUpdate(ca);
    }
    
    the_info.setDiscrepancy(discrepancies);
    the_info.setDisagreement(disagreements);
    the_info.setCounted(the_info.multiplicity());
    Persistence.saveOrUpdate(the_info);

    if (the_update_counters) {
      the_cdb.addDiscrepancy(discrepancies);
      the_cdb.addDisagreement(disagreements);
    }
    
    return audit_count;
  }
  
  /**
   * "Unaudits" a CVR/ACVR pair by removing it from all the audits in 
   * progress in the specified county dashboard. This also updates the
   * dashboard's counters as appropriate.
   *
   * @param the_cdb The county dashboard.
   * @param the_info The CVRAuditInfo to unaudit.
   */
  @SuppressWarnings("PMD.NPathComplexity")
  private static int unaudit(final CountyDashboard the_cdb,
                             final CVRAuditInfo the_info) {
    final Set<Contest> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.cvr();
    final CastVoteRecord audit_cvr = the_info.acvr();
    final int result = the_info.counted();

    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest());
      }
    }
    
    for (final CountyContestComparisonAudit ca : the_cdb.comparisonAudits()) {
      final OptionalInt discrepancy = 
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < result; i++) {
          ca.removeDiscrepancy(the_info, discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }
      if (contest_disagreements.contains(ca.contest())) {
        for (int i = 0; i < result; i++) {
          ca.removeDisagreement(the_info);
        }
        disagreements.add(ca.auditReason());
      }
      ca.signalSampleUnaudited(result);
      Persistence.saveOrUpdate(ca);
    }
    
    the_info.setDisagreement(null);
    the_info.setDiscrepancy(null);
    the_info.setCounted(0);
    Persistence.saveOrUpdate(the_info);
    
    the_cdb.removeDiscrepancy(discrepancies);
    the_cdb.removeDisagreement(disagreements);
    
    return result;
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
    // start from where we are in the current round
    final Round round = the_cdb.currentRound();
    if (round != null) {
      final Set<Long> checked_ids = new HashSet<>();
      int index = round.actualAuditedPrefixLength() - round.startAuditedPrefixLength();
      while (index < round.auditSubsequence().size()) {
        final Long cvr_id = round.auditSubsequence().get(index);
        if (!checked_ids.contains(cvr_id)) {
          checked_ids.add(cvr_id);
          final CVRAuditInfo cai = Persistence.getByID(cvr_id, CVRAuditInfo.class);
          if (cai.acvr() == null) {
            break;
          } else {
            final int audit_count = audit(the_cdb, cai, false);
            the_cdb.setAuditedSampleCount(the_cdb.auditedSampleCount() + audit_count);
          }
        }
        index = index + 1;
      }
      the_cdb.setAuditedPrefixLength(index + round.startAuditedPrefixLength());
    }
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
