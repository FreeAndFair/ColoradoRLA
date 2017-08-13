/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 27, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.comparisonaudit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import us.freeandfair.corla.util.Pair;

/**
 * @description A class for running comparison audits on tabulated data
 * @explanation <explanation>
 */
public class ComparisonAudit {

  /**
   * Gamma, as presented in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  private static final double GENTLE_GAMMA = 1.03905;

  private static final double COLORADO_GAMMA = 1.1;
  
  /**
   * A map from a contest_ID into a Contest where a Contest is an integer
   * denoting the number of winners and a map from Candidate names into a number
   * of votes
   */
  private final Map<String, Pair<Integer, Map<String, Integer>>> my_contests;

  /**
   * The minimum margin in all contests
   */
  private BigDecimal my_min_margin = BigDecimal.ONE;

  /**
   * The maximum asn(number of expected ballots to draw) discovered across all
   * contests added to this audit
   */
  private int my_max_asn;

  /**
   * The risk limit for the audit. If the audit concludes successfully, this
   * will be the risk that the election results are incorrect. 1 - risk gives
   * confidence in the correctness of the election
   */
  private final BigDecimal my_risk;

  /**
   * The total number of ballots cast for these contests
   */
  private final int my_total_ballots;

  /**
   * <description> <explanation>
   * 
   * @param the_total_ballots The total number of ballots cast for this audit
   * @param the_risk The risk limit for the audit. If the audit concludes
   *          successfully, this will be the risk that the election results are
   *          incorrect. 1 - risk gives confidence in the correctness of the
   *          election
   */
  public ComparisonAudit(final int the_total_ballots, final BigDecimal the_risk) {
    my_total_ballots = the_total_ballots;
    my_contests = new HashMap<>();
    my_risk = the_risk;
  }

  /**
   * Create an audit for the given contests, sets the margin and the expected
   * ballot selection
   * 
   * @param the_total_ballots The total number of ballots cast for this audit
   * @param the_risk The risk limit for the audit. If the audit concludes
   *          successfully, this will be the risk that the election results are
   *          incorrect. 1 - risk gives confidence in the correctness of the
   *          election
   * @param the_contests map from contest identifiers to contests, where
   *          contests are a pair of the number of winners selected by the
   *          contest and a map from candidate identifiers to vote counts
   */
  public ComparisonAudit(final int the_total_ballots, final BigDecimal the_risk,
                         final Map<String, Pair<Integer, Map<String, Integer>>> the_contests) {
    my_total_ballots = the_total_ballots;
    my_contests = the_contests;
    my_risk = the_risk;
    updateMinMargin();
  }

  /**
   * 
   * The stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   * 
   * @param the_o1 the number of one-vote overstatements
   * @param the_o2 the number of two-vote overstatements
   * @param the_u1 the number of one-vote understatements
   * @param the_u2 the number of two-vote understatements
   */
  private int nmin(final double the_o1, final double the_o2, final double the_u1,
                   final double the_u2) {
    // Checkstyle isn't fine-grained enough to exclude just this method, so we
    // use -1 * 2 for -2.
    return (int) (Math.max(the_o1 + the_o2 + the_u1 + the_u1, Math.ceil(
        -1 * 2 * GENTLE_GAMMA *
              (Math.log(my_risk.doubleValue()) + 
               the_o1 * Math.log(1 - 1 / (2 * GENTLE_GAMMA)) +
               the_o2 * Math.log(1 - 1 / GENTLE_GAMMA) +
               the_u1 * Math.log(1 + 1 / (2 * GENTLE_GAMMA)) +
               the_u2 * Math.log(1 + 1 / GENTLE_GAMMA)) /
              my_min_margin.doubleValue())));
  }

  /**
   * 
   * This calculates the sample size, but using expected rates rather than
   * actual sampled rates.
   * 
   * Neal M. suggests that this function should always be called with
   * the following values:
   * <ul>
   * <li>the_r1 == 0.01</li>
   * <li>the_r2 == 0.01</li>
   * <li>the_s1 == 0.01</li>
   * <li>the_s2 == 0.01</li>
   * <li>the_round_up1 == true</li> 
   * <li>the_round_up2 == true</li>
   * </ul>
   * for Colorado.
   * 
   * @param the_r1 the rate of one-vote overstatements
   * @param the_r2 the rate of two-vote overstatements
   * @param the_s1 the rate of one-vote understatements
   * @param the_s2 the rate of one-vote understatements
   * @param the_round_up1 always round up the number of one-vote
   *          over/understatements
   * @param the_roud_up2 always round up the number of two-vote
   *          over/understatements
   */
  private int nminfromrates(final double the_r1, 
                            final double the_r2, 
                            final double the_s1,
                            final double the_s2, 
                            final boolean the_round_up1,
                            final boolean the_round_up2) {
    // Checkstyle isn't fine-grained enough to exclude just this method, so we
    // use -1 * 2 for -2.
    double n0 = -1 * 2 * GENTLE_GAMMA * Math.log(my_risk.doubleValue()) /
                (my_min_margin.doubleValue() + 2 * GENTLE_GAMMA *
                                               (the_r1 * Math.log(1 - 1 / (2 * GENTLE_GAMMA)) +
                                                the_r2 * Math.log(1 - 1 / GENTLE_GAMMA) +
                                                the_s1 * Math.log(1 + 1 / (2 * GENTLE_GAMMA)) +
                                                the_s2 * Math.log(1 + 1 / GENTLE_GAMMA)));
    double o1;
    double o2;
    double u1;
    double u2;

    // Checkstyle isn't fine-grained enough to exclude just this method, so we
    // make a final local variable for the loop bound
    final int loop_bound = 3;
    for (int i = 0; i < loop_bound; i++) {
      if (the_round_up1) {
        o1 = Math.ceil(the_r1 * n0);
        u1 = Math.ceil(the_s1 * n0);
      } else {
        o1 = Math.round(the_r1 * n0);
        u1 = Math.round(the_s1 * n0);
      }
      if (the_round_up2) {
        o2 = Math.ceil(the_r2 * n0);
        u2 = Math.ceil(the_s2 * n0);
      } else {
        o2 = Math.round(the_r2 * n0);
        u2 = Math.round(the_s2 * n0);
      }
      n0 = nmin(o1, o2, u1, u2);
    }
    return (int) n0;
  }

  /**
   * 
   * @param the_contest_id The identifier for the contest
   * @return True iff the contest has been added to this audit
   */
  public boolean contestExists(final String the_contest_id) {
    return my_contests.containsKey(the_contest_id);
  }

  /**
   * 
   * @param the_contest_id The identifier for the contest the candidate belongs
   *          to
   * @param the_candidate_id The identifier for the candidate
   * @return true iff the contest exists, and contains the candidate
   */
  public boolean candidateExistsInContest(final String the_contest_id,
                                          final String the_candidate_id) {
    return contestExists(the_contest_id) &&
           my_contests.get(the_contest_id).getSecond().containsKey(the_candidate_id);
  }

  /**
   * 
   * <description> <explanation>
   * 
   * @param the_contest_id The identifier for the contest the candidate belongs
   *          to
   * @param the_candidate_id The identifier for the candidate
   * @param the_candidate_votes The number of votes the candidate received in
   *          the contest
   * @return True for success, False if the candidate existed or the contest did
   *         not
   */
  public boolean addCandidateVotes(final String the_contest_id, final String the_candidate_id,
                                   final int the_candidate_votes) {
    // We only return true if the contest exists and the candidate does not yet
    if (!contestExists(the_contest_id) ||
        candidateExistsInContest(the_contest_id, the_candidate_id)) {
      return false;
    }
    my_contests.get(the_contest_id).getSecond().put(the_candidate_id, the_candidate_votes);
    updateMinMarginForContest(the_contest_id);
    return true;
  }

  /**
   * 
   * @param the_contest_id The identifier for the contest the candidate belongs
   *          to
   * @param the_number_of_winners The number of winners in the contest
   * @param the_votes A map of candidate identifiers to the number of votes they
   *          received
   * @return true if the contest did not already exist
   */
  public boolean addContest(final String the_contest_id, final int the_number_of_winners,
                            final Map<String, Integer> the_votes) {
    if (contestExists(the_contest_id)) {
      return false;
    }

    my_contests.put(the_contest_id,
                    new Pair<Integer, Map<String, Integer>>(the_number_of_winners, the_votes));
    return true;
  }

  /**
   * Update the minimum margin for the entire contest
   * 
   * @param
   */
  private void updateMinMargin() {
    for (final String contest_id : my_contests.keySet()) {
      updateMinMarginForContest(contest_id);
    }
  }

  /**
   * 
   * <description> <explanation>
   * 
   * @param the_contest_id the contest ID that was changed, and as such needs
   *          its margin updated
   * @return true iff the contest exists
   */
  private boolean updateMinMarginForContest(final String the_contest_id) {
    final Pair<Integer, Map<String, Integer>> contest = my_contests.get(the_contest_id);
    if (contest == null) {
      return false;
    }

    final BigDecimal contest_margin =
        getMarginForContest(contest.getFirst(), contest.getSecond());

    my_min_margin = contest_margin.min(my_min_margin);

    // this depends on the update to my_min_margin
    final int max_asn =
        Math.max(my_max_asn, nminfromrates(.001, .0001, .001, .0001, true, false));

    my_max_asn = Math.max(my_max_asn, max_asn);
    return true;
  }

  /**
   * 
   * @param the_number_of_winners the number of winners allowed for the contest
   * @param the_votes the votes of the contest
   * @return the diluted margin for the contest
   */
  private BigDecimal getMarginForContest(final int the_number_of_winners,
                                         final Map<String, Integer> the_votes) {

    final ArrayList<Integer> sorted_votes = new ArrayList<>(the_votes.values());

    // if we only have winners there is no margin
    if (the_number_of_winners >= sorted_votes.size()) {
      return BigDecimal.ONE;
    }

    Collections.sort(sorted_votes, Collections.reverseOrder());

    final int lowest_winning_vote = sorted_votes.get(the_number_of_winners - 1);
    final int hightest_losing_vote = sorted_votes.get(the_number_of_winners);

    final int margin = lowest_winning_vote - hightest_losing_vote;

    // TODO: What do we do in a tie?
    assert margin != 0;

    return BigDecimal.valueOf(margin).divide(BigDecimal.valueOf(my_total_ballots));
  }

  /**
   * 
   * @param the_audited The total number of ballots that have been audited for
   *          this audit
   * @param the_one_over the total number of ballots with one-vote overstatement
   * @param the_two_over the total number of ballots with two-vote overstatement
   * @param the_one_under the total number of ballots with one_vote
   *          understatement
   * @param the_one_under the total number of ballots with two_vote
   *          understatement
   * @return True if the audit is completed
   */
  // @ behavior
  // @ requires P;
  // @ ensures Q;
  /*
   * @ pure @
   */
  public boolean auditComplete(final int the_audited, final int the_one_over,
                               final int the_two_over, final int the_one_under,
                               final int the_two_under) {
    final double nmin = nmin(the_one_over, the_two_over, the_one_under, the_two_under);
    return the_audited >= nmin;
  }

}
