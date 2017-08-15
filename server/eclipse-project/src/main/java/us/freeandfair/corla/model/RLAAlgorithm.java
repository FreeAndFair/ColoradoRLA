/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import us.freeandfair.corla.comparisonaudit.ComparisonAudit;
// I don't know why Checkstyle wants this blank line here

import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.ContestToAudit.AuditType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.DoSDashboardQueries;
import us.freeandfair.corla.util.Pair;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The top-level class providing the API to drive RLAs for Colorado.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
    "PMD.StdCyclomaticComplexity", "PMD.GodClass"})
public class RLAAlgorithm {
  /**
   * The RLA comparison audit risk computation algorithm.
   */
  private final ComparisonAudit my_comparison_audit;
  
  /**
   * My county dashboard.
   */
  private final CountyDashboard my_dashboard;
  
  /**
   * Instantiate the RLA algorithm for the RLA Tool.
   * 
   * This constructor assumes that we know the number of CVRs in the
   * election, the risk limit, and the tabulation result.
   * 
   * @exception IllegalArgumentException if the specified county dashboard
   * has no votes to count.
   */
  public RLAAlgorithm(final CountyDashboard the_dashboard) {
    // count CVRs for contest under audit
    final Long cvr_count = 
        CastVoteRecordQueries.countMatching(the_dashboard.cvrUploadTimestamp(), 
                                            the_dashboard.countyID(),
                                            RecordType.UPLOADED);
    if (cvr_count == 0) {
      throw new IllegalArgumentException("no votes in county " + the_dashboard.countyID());
    }
    // what is the risk limit for this contest?
    final BigDecimal risk_limit = 
        DoSDashboardQueries.get().getRiskLimitForComparisonAudits();
    
    // a map describing all contests in an election that are under audit.
    // as documented in ComparisonAudit:
    // map from contest identifiers to contests, where
    // contests are a pair of the number of winners selected by the
    // contest and a map from candidate identifiers to vote counts
    final Map<String, Pair<Integer, Map<String, Integer>>> contest_map = 
        getContestMap(the_dashboard);
    my_comparison_audit = 
        new ComparisonAudit(cvr_count.intValue(), risk_limit, contest_map);
    my_dashboard = the_dashboard;
  }

  /**
   * Compute the contest map for the specified county dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the map.
   */
  private Map<String, Pair<Integer, Map<String, Integer>>> 
      getContestMap(final CountyDashboard the_dashboard) {
    final Map<String, Pair<Integer, Map<String, Integer>>> result =
        new HashMap<String, Pair<Integer, Map<String, Integer>>>();
    // <contest-name, Pair<number-of-winners, Map<candidate, total>>>
    // first, get all the contests    
    final DoSDashboard dosdb = DoSDashboardQueries.get();
    
    // brute force
    for (final ContestToAudit contest : dosdb.contestsToAudit()) {
      if (contest.audit() != AuditType.COMPARISON) {
        continue;
      }
      final String name = contest.contest().name();
      final Integer num_winners = contest.contest().votesAllowed();
      final Map<String, Integer> vote_totals = new HashMap<String, Integer>();
      for (final Choice choice : contest.contest().choices()) {
        vote_totals.put(choice.name(), 0);
      }
      result.put(name, new Pair<Integer, Map<String, Integer>>(num_winners, vote_totals));
    }
    
    final boolean transaction = Persistence.beginTransaction();
    final Stream<CastVoteRecord> cvrs =
        CastVoteRecordQueries.getMatching(the_dashboard.cvrUploadTimestamp(), 
                                          the_dashboard.countyID(),
                                          RecordType.UPLOADED);

    cvrs.forEach((the_cvr) -> {
      for (final CVRContestInfo contest_info : the_cvr.contestInfo()) {
        final Pair<Integer, Map<String, Integer>> record = 
            result.get(contest_info.contest().name());
        if (record == null) {
          // this wasn't one of the contests to audit
          continue;
        }
        for (final String choice : record.getSecond().keySet()) {
          if (contest_info.choices().contains(choice)) {
            // we assume no overvotes in uploaded CVRs
            record.getSecond().put(choice, record.getSecond().get(choice) + 1);
          }
        }
      }
      Persistence.currentSession().evict(the_cvr);
    });
    if (transaction) {
      Persistence.commitTransaction();
    }
    
    return result;
  }
  
  /**
   * Compute the order of ballot (cards) for audit within a county.
   * 
   * @param the_seed the seed provided by the Department of State.
   * @param the_cvr_count the total number of CVRs in a given county.
   */
  public List<Long> computeBallotOrder(final String the_seed) {
    final boolean with_replacement = true;
    // assuming that CVRs are indexed from 0
    final int minimum = 0;
    // the number of CVRs for the_contest_to_audit - note that the sequence
    // generator generates a sequence of the numbers minimum ... maximum 
    // inclusive, so we subtract 1 from the number of CVRs to give it the
    // correct range for our actual list of CVRs (indexed from 0).
    final Long max_long = 
        CastVoteRecordQueries.countMatching(my_dashboard.cvrUploadTimestamp(), 
                                            my_dashboard.countyID(),
                                            RecordType.UPLOADED) - 1;

    final int maximum = max_long.intValue();

    final PseudoRandomNumberGenerator prng = 
        new PseudoRandomNumberGenerator(the_seed, with_replacement,
                                        minimum, maximum);
    final List<Integer> list_of_cvrs_to_audit = 
        prng.getRandomNumbers(minimum, maximum);
    final List<Long> list_of_cvr_ids = 
        CastVoteRecordQueries.idsForMatching(my_dashboard.cvrUploadTimestamp(), 
                                             my_dashboard.countyID(),
                                             RecordType.UPLOADED);
    final List<Long> result = new ArrayList<>();
    for (final int index : list_of_cvrs_to_audit) {
      result.add(list_of_cvr_ids.get(index));
    }
    return result;
  }
  
  /**
   * Compute the estimated number of ballots to audit, given the ballots
   * under audit described in the constructor.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public int estimatedInitialSampleSize() {
    return my_comparison_audit.nminfromrates(0.01, 0.01, 0.01, 0.01, true, true);
  }

  /**
   * Calculates a discrepancy based on the results from a CVR and an ACVR.
   * 
   * @param the_winners The winners of the contest.
   * @param the_losers The losers of the contest.
   * @param the_cvr_choices The CVR choices.
   * @param the_acvr_choices The ACVR choices.
   * @return the discrepancy as a number in the range [-2 .. 2], where a negative
   * number indicates an understatement and a positive number an overstatement.
   */
  @SuppressWarnings({"PMD.ConfusingTernary", "checkstyle:magicnumber"})
  private int calculateDiscrepancy(final Set<String> the_winners,
                                   final Set<String> the_losers,
                                   final Set<String> the_cvr_choices,
                                   final Set<String> the_acvr_choices) {
    int result = 0;
    if (!the_cvr_choices.equals(the_acvr_choices)) {
      // this is a very quick calculation, and will likely not work correctly 
      // for elections with multiple winners, but will err on the side of being
      // pessimistic

      // find the candidates who gained and lost votes in the ACVR

      final Set<String> gained_votes = new HashSet<>(the_acvr_choices);
      gained_votes.removeAll(the_cvr_choices);
      final Set<String> winner_gains = new HashSet<>(gained_votes);
      winner_gains.retainAll(the_winners);
      final Set<String> loser_gains = new HashSet<>(gained_votes);
      loser_gains.removeAll(the_winners);
      
      final Set<String> lost_votes = new HashSet<>(the_cvr_choices);
      lost_votes.removeAll(the_acvr_choices);
      final Set<String> winner_losses = new HashSet<>(lost_votes);
      winner_losses.retainAll(the_winners);
      final Set<String> loser_losses = new HashSet<>(lost_votes);
      loser_losses.removeAll(the_winners);
      
      // now, we have several cases:
      
      if (!winner_gains.isEmpty() && loser_losses.isEmpty()) {
        // if only winners gained votes and no losers lost votes, 
        // it's a 1-vote understatement       
        result = -1;
      } else if (!winner_gains.isEmpty() && !loser_losses.isEmpty()) {        
        // if only winners gained votes and any losers lost votes, 
        // it's a 2-vote understatement       
        result = -2;
      } else if (!winner_losses.isEmpty() && loser_gains.isEmpty()) {
        // if any winners lost votes and no losers gained votes, 
        // it's a 1-vote overstatement
        result = 1;
      } else if (!winner_losses.isEmpty() && !loser_gains.isEmpty()) {
        // if any winners lost votes and any losers gained votes,
        // it's a 2-vote overstatement
        result = 2;
      } else if (loser_gains.isEmpty()) {
        // no votes changed for winners, and no losers gained votes,
        // so losers must have lost votes; if _all_ the losers lost
        // votes, it'd be a 1-vote understatement
        if (loser_losses.equals(the_losers)) {
          result = -1;
        }
      } else {
        // no votes changed for winners, and losers gained votes, 
        // so it's a 1-vote overstatement
        result = 1;
      }
    }
    
    return result;
  }
  
  /**
   * Computes the discrepancy between a CVR to audit and a submitted audit CVR.
   * 
   * @param the_cvr The CVR to audit.
   * @param the_acvr The audit CVR.
   * @return the discrepancy as a number in the range [-2 .. 2], where a negative
   * number indicates an understatement and a positive number an overstatement.
   */
  @SuppressWarnings("PMD.NPathComplexity")
  public int discrepancy(final CastVoteRecord the_cvr, final CastVoteRecord the_acvr) {
    if (the_cvr == null || the_acvr == null) {
      throw new IllegalStateException("nonexistent cvr or acvr in audit list");
    }
    final Set<ContestToAudit> contests = DoSDashboardQueries.get().contestsToAudit();
    int worst_discrepancy = Integer.MIN_VALUE;
    for (final ContestToAudit cta : contests) {
      final CVRContestInfo cvr_ci = the_cvr.contestInfoForContest(cta.contest());
      final CVRContestInfo acvr_ci = the_acvr.contestInfoForContest(cta.contest());
      final Set<String> winners = 
          my_comparison_audit.winnersForContest(cta.contest().name());
      final Set<String> losers = new HashSet<>();
      for (final Choice c : cta.contest().choices()) {
        if (!winners.contains(c.name())) {
          losers.add(c.name());
        }
      }
      final Set<String> cvr_choices = new HashSet<>();
      final Set<String> acvr_choices = new HashSet<>();
      if (cvr_ci != null) {
        cvr_choices.addAll(cvr_ci.choices());
      }
      if (acvr_ci != null) {
        acvr_choices.addAll(acvr_ci.choices());
        if (acvr_choices.size() > cta.contest().votesAllowed()) {
          // this is an overvote, so we don't count the votes
          acvr_choices.clear();
        }
      }
      final int discrepancy = 
          calculateDiscrepancy(winners, losers, cvr_choices, acvr_choices);
      if (discrepancy != 0) {
        worst_discrepancy = Math.max(discrepancy, worst_discrepancy);
      }
    }
    int result = 0;
    if (worst_discrepancy > Integer.MIN_VALUE) {
      result = worst_discrepancy;
    }
    return result;
  }
  
  /**
   * Computes the discrepancies between CVRs to audit and submitted audit CVRs.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  // false positive: there is in fact a default in the switch statement
  @SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
  private Discrepancies calculateDiscrepancies() {
    final Discrepancies result = new Discrepancies();
    // for every CVR under audit in the prefix for which aCVRs exist, 
    // for every contest under audit, calculate the discrepancy and record it
    
    final List<Long> cvrs_to_audit = my_dashboard.cvrsToAudit();
    final List<Long> audit_cvrs = my_dashboard.submittedAuditCVRs();
    
    int count = 0;
    while (count < audit_cvrs.size() && count < cvrs_to_audit.size()) {
      if (audit_cvrs.get(count) == null) {
        break;
      } else {
        final CastVoteRecord cvr = 
            Persistence.getByID(cvrs_to_audit.get(count), CastVoteRecord.class);
        final CastVoteRecord acvr = 
            Persistence.getByID(audit_cvrs.get(count), CastVoteRecord.class);
        final int discrepancy = discrepancy(cvr, acvr);
        switch (discrepancy) {
          case -2: 
            result.my_two_votes_under = result.my_two_votes_under + 1;
            break;
          
          case -1:
            result.my_one_vote_under = result.my_one_vote_under + 1;
            break;
          
          case 1:
            result.my_one_vote_over = result.my_one_vote_over + 1;
            break;
            
          case 2:
            result.my_two_votes_over = result.my_two_votes_over + 1;
            break;
            
          default:
        }
      }
      count = count + 1;
    }
    
    // for every remaining CVR under audit, conservatively assume a two-vote 
    // overstatement
    
    while (count < audit_cvrs.size()) {
      result.my_two_votes_over = result.my_two_votes_over + 1;
      count = count + 1;
    }
    
    return result;
  }
  
  /**
   * Estimates the number of ballots left to audit after the audit has started.
   */
  public int estimatedBallotsToAudit() {
    final Discrepancies discrepancies = calculateDiscrepancies();    
    return Math.max(my_comparison_audit.nmin(discrepancies.my_one_vote_over, 
                                             discrepancies.my_two_votes_over,
                                             discrepancies.my_one_vote_under,
                                             discrepancies.my_two_votes_under) - 
                    my_dashboard.ballotsAudited(), 0);
  }
  
  /**
   * Overstatements and understatements.
   */
  private static class Discrepancies {
    /**
     * one vote overstatements
     */
    protected int my_one_vote_over;

    /**
     * two vote overstatements
     */
    protected int my_two_votes_over;

    /**
     * one vote understatements
     */
    protected int my_one_vote_under;

    /**
     * two vote understatements
     */
    protected int my_two_votes_under;
  }
}
