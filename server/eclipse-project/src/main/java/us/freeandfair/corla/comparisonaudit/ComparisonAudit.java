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
import java.util.List;
import java.util.Map;

import us.freeandfair.corla.util.Pair;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class ComparisonAudit {

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

  private int my_max_asn;

  private final double my_gamma = 1.03905;

  private final BigDecimal my_risk;
  /**
   * The total number of ballots cast for these contests
   */
  private final int my_total_ballots;

  /**
   * <description> <explanation>
   * 
   * @param the_total_ballots The total number of ballots cast for this audit
   */
  public ComparisonAudit(final int the_total_ballots, final BigDecimal the_risk) {
    my_total_ballots = the_total_ballots;
    my_contests = new HashMap<>();
    my_risk = the_risk;
  }

  /**
   * <description> <explanation>
   * 
   * @param the_total_ballots The total number of ballots cast for this audit
   */
  public ComparisonAudit(final int the_total_ballots, final BigDecimal the_risk,
                         final Map<String, Pair<Integer, Map<String, Integer>>> the_contests) {
    my_total_ballots = the_total_ballots;
    my_contests = the_contests;
    my_risk = the_risk;
    updateMinMargin();
  }

  public static int nmin(final double the_alpha, final double the_gamma, final double the_m,
                         final double the_o1, final double the_o2, final double the_u1,
                         final double the_u2) {
    return (int) (Math
        .max(the_o1 + the_o2 + the_u1 + the_u1,
             Math.ceil(-2 * the_gamma *
                       (Math.log(the_alpha) + the_o1 * Math.log(1 - 1 / (2 * the_gamma)) +
                        the_o2 * Math.log(1 - 1 / the_gamma) +
                        the_u1 * Math.log(1 + 1 / (2 * the_gamma)) +
                        the_u2 * Math.log(1 + 1 / the_gamma)) /
                       the_m)));
  }

  public static int nminfromrates(final double the_alpha, final double the_gamma,
                                  final double the_m, final double the_r1, final double the_r2,
                                  final double the_s1, final double the_s2,
                                  final boolean the_round_up1, final boolean the_round_up2) {
    double n0 = -2 * the_gamma * Math.log(the_alpha) /
                (the_m + 2 * the_gamma *
                         (the_r1 * Math.log(1 - 1 / (2 * the_gamma)) +
                          the_r2 * Math.log(1 - 1 / the_gamma) +
                          the_s1 * Math.log(1 + 1 / (2 * the_gamma)) +
                          the_s2 * Math.log(1 + 1 / the_gamma)));
    double o1, o2, u1, u2;
    for (int i = 0; i < 3; i++) {
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
      n0 = nmin(the_alpha, the_gamma, the_m, o1, o2, u1, u2);
    }
    return (int) (n0);
  }

  // TODO: This is for Comparison audits
  public static int asn(final int the_winner_votes, final int the_loser_votes,
                        final BigDecimal the_risk, final int the_total_ballots) {
    final double risk = the_risk.doubleValue();

    final double s_w = ((double) the_winner_votes) / (the_winner_votes + the_loser_votes);
    final double s_l = ((double) the_loser_votes) / (the_winner_votes + the_loser_votes);
    final double p_w = the_winner_votes / ((double) the_total_ballots);
    final double p_l = the_loser_votes / ((double) the_total_ballots);
    final double z_w = Math.log(2 * s_w);
    final double z_l = Math.log(2 * s_l);
    final double numerator = Math.log(1.0 / risk) + z_w / 2;
    final double denominator = p_w * z_w + p_l * z_l;

    return (int) Math.ceil(numerator / denominator);
  }

  /**
   * 
   * <description> <explanation>
   * 
   * @param the_contest_id The identifier for the contest
   * @return True iff the contest has been added to this audit
   */
  public boolean contestExists(final String the_contest_id) {
    return my_contests.containsKey(the_contest_id);
  }

  /**
   * 
   * <description> <explanation>
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
    final Pair<BigDecimal, Integer> contest_margin_asn =
        getMarginASNForContest(contest.getFirst(), contest.getSecond());

    my_min_margin = contest_margin_asn.getFirst().min(my_min_margin);
    my_max_asn = Math.max(my_max_asn, contest_margin_asn.getSecond());
    return true;
  }

  /**
   * 
   * <description> <explanation>
   * 
   * @param the_number_of_winners the number of winners allowed for the contest
   * @param the_votes the votes of the contest
   * @return the diluted margin for the contest
   */
  private Pair<BigDecimal, Integer> getMarginASNForContest(final int the_number_of_winners,
                                                   final Map<String, Integer> the_votes) {

    final ArrayList<Integer> sorted_votes = new ArrayList<>(the_votes.values());

    // if we only have winners there is no margin
    if (the_number_of_winners >= sorted_votes.size()) {
      return new Pair<BigDecimal, Integer>(BigDecimal.ONE, 0);
    }

    Collections.sort(sorted_votes, Collections.reverseOrder());

    final int lowest_winning_vote = sorted_votes.get(the_number_of_winners - 1);
    final int hightest_losing_vote = sorted_votes.get(the_number_of_winners);

    final int margin = lowest_winning_vote - hightest_losing_vote;

    // TODO: What do we do in a tie?
    assert margin != 0;

    final BigDecimal dilutedmargin =
        BigDecimal.valueOf(margin).divide(BigDecimal.valueOf(my_total_ballots));

    final List<Integer> winners = sorted_votes.subList(0, the_number_of_winners);
    final List<Integer> losers =
        sorted_votes.subList(the_number_of_winners, sorted_votes.size());

    int max_asn = 0;
    for (final Integer winnervotes : winners) {
      for (final Integer loservotes : losers) {
        max_asn =
            Math.max(my_max_asn, asn(winnervotes, loservotes, my_risk, my_total_ballots));
      }
    }
    return new Pair<BigDecimal, Integer>(dilutedmargin, max_asn);
  }

  public boolean auditComplete(final int the_audited, final int the_one_over,
                               final int the_two_over, final int the_one_under,
                               final int the_two_under) {
    double nmin = nmin(my_risk.doubleValue(), my_gamma, my_min_margin.doubleValue(),
                       the_one_over, the_two_over, the_one_under, the_two_under);
    return the_audited >= nmin;
  }

}
