/** Copyright (C) 2018 the Colorado Department of State  **/
package us.freeandfair.corla.controller;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.stream.Collectors;

import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.query.ContestResultQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;

public final class ContestCounter {

  /** prevent contruction **/
  private ContestCounter() {
  }

  /** set voteTotals on CONTEST based on all counties that have that Contest
   * name in their uploaded CVRs
   **/
  public static ContestResult countContest(final String contestName) {
    final List<CountyContestResult> countyResults =
        CountyContestResultQueries.withContestName(contestName);
    final ContestResult contestResult = ContestResultQueries.findOrCreate(contestName);
    contestResult.setVoteTotals(accumulateVoteTotals(countyResults.stream()
                                                     .map((cr) -> cr.voteTotals())
                                                     .collect(Collectors.toList())));
    return contestResult;
  }

  /** add em up **/
  public static Map<String,Integer>
    accumulateVoteTotals(final List<Map<String,Integer>> voteTotals) {
    final Map<String,Integer> acc = new HashMap<String,Integer>();
    return voteTotals.stream().reduce(acc,
                                      (a, vt) -> addVoteTotal(a, vt));
  }

  /** add one vote total to another **/
  public static Map<String,Integer> addVoteTotal(final Map<String,Integer> acc,
                                                 final Map<String,Integer> vt) {
    // we iterate over vt because it may have a key that the accumulator has not
    // seen yet
    vt.forEach((k,v) -> acc.merge(k, v, (v1,v2) -> { return (null == v1) ? v2 : v1 + v2; } ));
    return acc;
  }


  /** extract totals, then sort reversed so the winner is first  **/
  public static List<Integer> rankTotals(final Map<String,Integer> voteTotals) {
    return voteTotals.entrySet().stream()
        .map(Entry::getValue)
        .sorted(Comparator.reverseOrder())
        .collect(Collectors.toList());
  }

  /** gap between winner and runner-up **/
  public static Integer minMargin(final Map<String,Integer> voteTotals) {
    final List<Integer> totals = rankTotals(voteTotals);
    final int two = 2;//pmd
    if (two <= totals.size()) {
      return totals.get(0) - totals.get(1);
    } else {
      // uncontested
      return 0;
    }
  }

  /** simple sum of all totals **/
  public static Integer ballotCount(final Map<String,Integer> voteTotals) {
    return rankTotals(voteTotals).stream().reduce(0, Integer::sum);
  }

  /** calculate the diluted margin - gap between first and second, divided by
   * total ballots
   **/
  public static BigDecimal dilutedMargin(final Map<String,Integer> voteTotals) {
    final BigDecimal min_margin = BigDecimal.valueOf(minMargin(voteTotals));
    final BigDecimal ballot_count = BigDecimal.valueOf(ballotCount(voteTotals));
    return min_margin.divide(ballot_count, MathContext.DECIMAL128);
  }
}
