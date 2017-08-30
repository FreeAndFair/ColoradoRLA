/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.ContestToAudit.AuditType;
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
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity"})
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

    final String seed = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class).randomSeed();
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
    final List<CastVoteRecord> result = new ArrayList<>();
    
    for (final int index : list_of_cvrs_to_audit) {
      result.add(CastVoteRecordQueries.get(the_cdb.id(), RecordType.UPLOADED, index));
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
   * @param the_duplicates true to return duplicates, false otherwise.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_start_index,
                                                        final int the_ballot_count,
                                                        final boolean the_duplicates) {
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
        if ((the_duplicates || !cvr_set.contains(cvr)) && !audited(the_cdb, cvr)) {
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
   * @exception IllegalArgumentException if 
   * the_start_index >= the_desired_prefix_length
   */
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_start_index,
                                                        final int the_desired_prefix_length) {
    if (the_start_index >= the_desired_prefix_length) {
      throw new IllegalArgumentException("invalid sequence bounds");
    }
    
    // we need to get the CVRs for the county's sequence, starting at the_start_index,
    // and eliminate duplicates
    
    final List<CastVoteRecord> cvrs = 
        getCVRsInAuditSequence(the_cdb, the_start_index, 
                               the_desired_prefix_length - 1); // end is inclusive
    final Set<CastVoteRecord> cvr_set = new HashSet<>();
    final List<CastVoteRecord> cvr_to_audit_list = new ArrayList<>();
    for (int i = 0; i < cvrs.size(); i++) {
      final CastVoteRecord cvr = cvrs.get(i);
      if (!cvr_set.contains(cvr) && !audited(the_cdb, cvr)) {
        cvr_to_audit_list.add(cvr);
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
   * @exception IllegalArgumentException if the specified dashboard does not have
   * a round with the specified number, or if a round number <= 0 is specified.
   */
  @SuppressWarnings("PMD.UselessParentheses")
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb,
                                                        final int the_round) {
    if (the_round <= 0 || the_cdb.rounds().size() < the_round) {
      throw new IllegalArgumentException("invalid round number");
    }
    // round numbers are 1-based, not 0-based
    final Round round = the_cdb.rounds().get(the_round - 1);
    return computeBallotOrder(the_cdb, round.startAuditedPrefixLength(), 
                              round.expectedAuditedPrefixLength());
  }
  
  /**
   * Initializes the audit data for the specified county dashboard.
   * 
   * @param the_dashboard The dashboard.
   */
  public static void initializeAuditData(final CountyDashboard the_dashboard) {
    final DoSDashboard dosdb =
        Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    final BigDecimal risk_limit = dosdb.riskLimitForComparisonAudits();
    final Set<Contest> all_driving_contests = new HashSet<>();
    final Set<Contest> county_driving_contests = new HashSet<>();
    final Set<CountyContestComparisonAudit> comparison_audits = new HashSet<>();
    int to_audit = Integer.MIN_VALUE;
    
    for (final ContestToAudit cta : dosdb.contestsToAudit()) {
      if (cta.audit() == AuditType.COMPARISON) {
        all_driving_contests.add(cta.contest());
      }
    }
    
    the_dashboard.setAuditedPrefixLength(0);
    for (final CountyContestResult ccr : 
         CountyContestResultQueries.forCounty(the_dashboard.county())) {
      final CountyContestComparisonAudit audit = 
          new CountyContestComparisonAudit(the_dashboard, ccr, risk_limit);
      final Contest contest = audit.contest();
      comparison_audits.add(audit);
      if (all_driving_contests.contains(contest)) {
        to_audit = Math.max(to_audit, audit.initialBallotsToAudit());
        county_driving_contests.add(contest);
      }
    }
    the_dashboard.setComparisonAudits(comparison_audits);
    the_dashboard.setDrivingContests(county_driving_contests);
    the_dashboard.startRound(computeBallotOrder(the_dashboard, 0, to_audit).size(),
                             to_audit, 0);
    the_dashboard.setEstimatedBallotsToAudit(Math.max(0,  to_audit));
    if (!county_driving_contests.isEmpty()) {
      the_dashboard.setCVRsToAudit(getCVRsInAuditSequence(the_dashboard, 0, to_audit - 1));
    }
  }
  
  /**
   * Starts a new round on the specified dashboard with the specified number
   * of physical ballots.
   * 
   * @param the_dashboard The dashboard.
   * @param the_round_length The count.
   * @return true if a round is started, false otherwise (a round might not
   * be started because the risk limit has already been achieved).
   * @exception IllegalStateException if a round cannot be started from 
   * estimates because there are no previous rounds.
   */
  public static boolean startNewRoundOfLength(final CountyDashboard the_dashboard,
                                              final int the_round_length) {
    final List<Round> rounds = the_dashboard.rounds();
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
        ComparisonAuditController.getCVRsInAuditSequence(the_dashboard, 
                                                         start_index, 
                                                         the_round_length);
    List<CastVoteRecord> extra_cvrs = new_cvrs;
    final Set<CastVoteRecord> unique_new_cvrs = new HashSet<>(new_cvrs);
    while (!extra_cvrs.isEmpty() && unique_new_cvrs.size() < the_round_length) {
      extra_cvrs =
          ComparisonAuditController.
               getCVRsInAuditSequence(the_dashboard, start_index + new_cvrs.size(),
                                      the_round_length - unique_new_cvrs.size());
      new_cvrs.addAll(extra_cvrs);
      unique_new_cvrs.addAll(extra_cvrs);
    }
    the_dashboard.addCVRsToAudit(new_cvrs);
    Persistence.saveOrUpdate(the_dashboard);
    final int expected_prefix_length = the_dashboard.cvrAuditInfo().size();
    for (int i = start_index; i < expected_prefix_length; i++) {
      final CVRAuditInfo cvrai = the_dashboard.cvrAuditInfo().get(i);
      if (cvrai.acvr() != null && !unique_new_cvrs.contains(cvrai.cvr())) {
        unique_new_cvrs.remove(cvrai.cvr());
        CVRAuditInfoQueries.updateMatching(the_dashboard, cvrai.cvr(), cvrai.acvr());
      }
    }
    if (unique_new_cvrs.isEmpty()) {
      return false;
    } else {
      Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
          the_dashboard.id() + " at audit sequence number " + start_index + 
          " with " + unique_new_cvrs.size() + " ballots to audit");
      the_dashboard.startRound(the_round_length, expected_prefix_length, start_index);
      return true;
    } 
  }
  
  /**
   * Starts a new round on the specified dashboard, using the current error
   * rates to estimate the necessary number of ballots to audit.
   * 
   * @param the_dashboard The dashboard.
   * @return true if a round is started, false otherwise (a round might not
   * be started because the risk limit has already been achieved).
   * @exception IllegalStateException if a round cannot be started from 
   * estimates because there are no previous rounds.
   */
  public static boolean 
      startNewRoundFromEstimates(final CountyDashboard the_dashboard) {
    final List<Round> rounds = the_dashboard.rounds();
    int start_index = 0;
    if (rounds.isEmpty()) {
      throw new IllegalArgumentException("no previous audit rounds");
    } else {
      final Round previous_round = rounds.get(rounds.size() - 1);
      // we start the next round where the previous round actually ended 
      // in the audit sequence
      start_index = previous_round.actualAuditedPrefixLength();
    }
    // use estimates based on current error rate to get length of round
    boolean result = false;
    final int expected_prefix_length = 
        Math.max(computeEstimatedBallotsToAudit(the_dashboard, true),
                 computeEstimatedBallotsToAudit(the_dashboard, false));
    if (the_dashboard.auditedPrefixLength() < expected_prefix_length) {
      final List<CastVoteRecord> new_cvrs = 
          getCVRsInAuditSequence(the_dashboard, start_index, 
                                 expected_prefix_length - 1);
      final List<Long> ids = new ArrayList<Long>();
      for (final CVRAuditInfo cvrai : the_dashboard.cvrAuditInfo()) {
        ids.add(cvrai.cvr().id());
      }
      ids.add(-1L);
      for (final CastVoteRecord cvr : new_cvrs) {
        ids.add(cvr.id());
      }
      the_dashboard.addCVRsToAudit(new_cvrs);
      Persistence.saveOrUpdate(the_dashboard);
      final Set<CastVoteRecord> unique_new_cvrs = new HashSet<>(new_cvrs);
      for (int i = start_index; i < expected_prefix_length; i++) {
        final CVRAuditInfo cvrai = the_dashboard.cvrAuditInfo().get(i);
        if (cvrai.acvr() != null && !unique_new_cvrs.contains(cvrai.cvr())) {
          unique_new_cvrs.remove(cvrai.cvr());
          CVRAuditInfoQueries.updateMatching(the_dashboard, cvrai.cvr(), cvrai.acvr());
        }
      }
      final int round_length = unique_new_cvrs.size();
      Main.LOGGER.info("starting audit round " + (rounds.size() + 1) + " for county " + 
          the_dashboard.id() + " at audit sequence number " + start_index + 
          " with " + round_length + " ballots to audit");
      the_dashboard.startRound(round_length, expected_prefix_length, 
                               start_index);
      result = true;
    }
    return result;
  }
  
  /**
   * Submit an audit CVR for a CVR under audit to the specified county dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The corresponding audit CVR.
   * @return true if the audit CVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit, or the specified CVR under audit was
   * not in fact under audit.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
  public static boolean submitAuditCVR(final CountyDashboard the_dashboard,
                                       final CastVoteRecord the_cvr_under_audit, 
                                       final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;
    
    final List<CVRAuditInfo> info = 
        CVRAuditInfoQueries.matching(the_dashboard, the_cvr_under_audit);
    
    if (info == null || info.isEmpty()) {
      Main.LOGGER.info("attempt to submit ACVR for county " + 
                       the_dashboard.id() + ", cvr " +
                       the_cvr_under_audit.id() + " not under audit");
    } else if (checkACVRSanity(the_cvr_under_audit, the_audit_cvr)) {
      // if the record is the current CVR under audit, or if it hasn't been
      // audited yet, we can just process it
      final CastVoteRecord old_audit_cvr = info.get(0).acvr();
      if (old_audit_cvr == null) {
        for (final CVRAuditInfo c : info) {
          c.setACVR(the_audit_cvr);
        }
        // this just updates the counters; the actual "audit" happens later
        the_dashboard.addAuditedBallot();
        audit(the_dashboard, the_cvr_under_audit, the_audit_cvr, 0, true);
      } else {
        // the record has been audited before, so we need to "unaudit" it 
        // this requires a linear search over the matching records to see
        // how many have been counted, since we don't know what order our
        // query returned them in and we can't order them by list index
        int undo_count = 0;
        for (final CVRAuditInfo c : info) {
          if (c.counted()) {
            undo_count = undo_count + 1;
          }
          c.setACVR(the_audit_cvr);
          Persistence.saveOrUpdate(c);
        }
        unaudit(the_dashboard, the_cvr_under_audit, old_audit_cvr, undo_count);
        audit(the_dashboard, the_cvr_under_audit, the_audit_cvr, undo_count, true);
      }
      result = true;
    }  else {
      Main.LOGGER.info("attempt to submit non-corresponding ACVR " +
                       the_audit_cvr.id() + " for county " + the_dashboard.id() + 
                       ", cvr " + the_cvr_under_audit.id());
    }

    updateCVRUnderAudit(the_dashboard);
    the_dashboard.
        setEstimatedBallotsToAudit(computeEstimatedBallotsToAudit(the_dashboard, false) -
                                   the_dashboard.auditedPrefixLength());
    return result;
  }
  
  /**
   * Computes the estimated total number of ballots to audit on the specified
   * county dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @param the_error_rates true to project from the current error rates, 
   * false to get the minimum given the current over- and understatements. 
   */
  public static int 
      computeEstimatedBallotsToAudit(final CountyDashboard the_dashboard,
                                     final boolean the_error_rates) {
    int to_audit = Integer.MIN_VALUE;
    final Set<Contest> driving_contests = the_dashboard.drivingContests();
    for (final CountyContestComparisonAudit ccca : the_dashboard.comparisonAudits()) {
      if (driving_contests.contains(ccca.contest())) {
        final int bta;
        if (the_error_rates) {
          bta = ccca.expectedBallotsToAuditFromCurrentRates();
        } else {
          bta = ccca.ballotsToAudit();
        }
        to_audit = Math.max(to_audit, bta);
      }
    }
    return Math.max(0,  to_audit);
  }
  
  /**
   * Checks to see if the specified CVR has been audited on the specified county
   * dashboard.
   * 
   * @param the_dashboard The county dashboard.
   * @param the_cvr The CVR.
   * @return true if the specified CVR has been audited, false otherwise.
   */
  public static boolean audited(final CountyDashboard the_dashboard, 
                                final CastVoteRecord the_cvr) {
    final List<CVRAuditInfo> info = 
        CVRAuditInfoQueries.matching(the_dashboard, the_cvr);
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
   * @param the_dashboard The dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The audit CVR.
   * @param the_count The number of times to count this ballot in the
   * audit.
   * @param the_update_counters true to update the county dashboard 
   * counters, false otherwise; false is used when this ballot 
   * has already been audited once.
   */
  private static void audit(final CountyDashboard the_dashboard,
                            final CastVoteRecord the_cvr_under_audit,
                            final CastVoteRecord the_audit_cvr, 
                            final int the_count,
                            final boolean the_update_counters) {
    boolean discrepancy_found = false;
    for (final CountyContestComparisonAudit ca : the_dashboard.comparisonAudits()) {
      final int discrepancy = ca.computeDiscrepancy(the_cvr_under_audit, the_audit_cvr);
      for (int i = 0; i < the_count; i++) {
        ca.recordDiscrepancy(discrepancy);
      }
      discrepancy_found |= discrepancy != 0;
    }
    if (the_update_counters) {
      if (discrepancy_found) {
        the_dashboard.addDiscrepancy();
      }
      boolean disagree = false;
      for (final CVRContestInfo ci : the_audit_cvr.contestInfo()) {
        disagree |= ci.consensus() == ConsensusValue.NO;
      }
      if (disagree) {
        the_dashboard.addDisagreement();
      }
    }
  }
  
  /**
   * "Unaudits" a CVR/ACVR pair by removing it from all the audits in 
   * progress in the specified county dashboard. This also updates the
   * dashboard's counters as appropriate.
   *
   * @param the_dashboard The county dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The audit CVR.
   * @param the_count The number of times to remove this ballot from the audit.
   */
  private static void unaudit(final CountyDashboard the_dashboard,
                              final CastVoteRecord the_cvr_under_audit,
                              final CastVoteRecord the_audit_cvr,
                              final int the_count) {
    boolean discrepancy_found = false;
    for (final CountyContestComparisonAudit ca : the_dashboard.comparisonAudits()) {
      final int discrepancy = ca.computeDiscrepancy(the_cvr_under_audit, the_audit_cvr);
      for (int i = 0; i < the_count; i++) {
        ca.removeDiscrepancy(discrepancy);
      }
      discrepancy_found |= discrepancy != 0;
    }
    if (discrepancy_found) {
      the_dashboard.removeDiscrepancy();
    }
    boolean disagree = false;
    for (final CVRContestInfo ci : the_audit_cvr.contestInfo()) {
      disagree |= ci.consensus() == ConsensusValue.NO;
    }
    if (disagree) {
      the_dashboard.removeDisagreement();
    }
  }
  
  /**
   * Updates the current CVR to audit index of the specified county
   * dashboard to the first CVR after the current CVR under audit that
   * lacks an ACVR. This "audits" all the CVR/ACVR pairs it finds 
   * in between, and extends the sequence of ballots to audit if it
   * reaches the end and the audit is not concluded.
   * 
   * @param the_dashboard The dashboard.
   */
  private static void 
      updateCVRUnderAudit(final CountyDashboard the_dashboard) {
    final List<CVRAuditInfo> cvr_audit_info = the_dashboard.cvrAuditInfo();
    int index = the_dashboard.auditedPrefixLength();
    while (index < cvr_audit_info.size()) {
      final CVRAuditInfo cai = cvr_audit_info.get(index);
      if (cai.acvr() == null) {
        break;
      } else {
        audit(the_dashboard, cai.cvr(), cai.acvr(), 1, false);
        cai.setCounted(true);
      }
      index = index + 1;
    }
    the_dashboard.setAuditedPrefixLength(index);
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
