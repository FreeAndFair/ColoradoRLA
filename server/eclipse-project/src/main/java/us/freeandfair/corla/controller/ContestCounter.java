/** Copyright (C) 2018 the Colorado Department of State  **/
package us.freeandfair.corla.controller;


import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.ContestResultQueries;

public final class ContestCounter {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ContestCounter.class);

  /** prevent contruction **/
  private ContestCounter() {
  }

  /**
   * Group all CountyContestResults by contest name and tally the votes
   * across all counties that have reported results.
   *
   * @return List<ContestResult> A high level view of contests and their
   * participants.
   */
  public static List<ContestResult> countAllContests() {
    return
      Persistence.getAll(CountyContestResult.class)
      .stream()
      .collect(Collectors.groupingBy(x -> x.contest().name()))
      .entrySet()
      .stream()
      .map(ContestCounter::countContest)
      .collect(Collectors.toList());
  }

  /**
   * Set voteTotals on CONTEST based on all counties that have that
   * Contest name in their uploaded CVRs
   **/
  public static ContestResult countContest(final Map.Entry<String,
                                           List<CountyContestResult>>
                                           countyContestResults) {
    final ContestResult contestResult =
      ContestResultQueries.findOrCreate(countyContestResults.getKey());

    final Map<String,Integer> voteTotals =
      accumulateVoteTotals(countyContestResults.getValue().stream()
                           .map((cr) -> cr.voteTotals())
                           .collect(Collectors.toList()));
    contestResult.setVoteTotals(voteTotals);
    // TODO how many winners are allowed?
    contestResult.setWinners(winners(voteTotals));
    contestResult.setLosers(losers(voteTotals, contestResult.getWinners()));

    contestResult.addContests(countyContestResults.getValue().stream()
                              .map(cr -> cr.contest())
                              .collect(Collectors.toSet()));
    contestResult.addCounties(countyContestResults.getValue().stream()
                              .map(cr -> cr.county())
                              .collect(Collectors.toSet()));
    final Long ballotCount =
      BallotManifestInfoQueries.totalBallots(contestResult.countyIDs());

    if (ballotCount == 0L) {
      LOGGER.error("contest has no ballot manifests!:"
                   + contestResult.getContestName()
                   + " for countyIDs: "
                   + contestResult.countyIDs());
    }

    // dilutedMargin of zero is ok here, it means the contest is uncontested
    // and the contest will not be auditable, so samples should not be selected for it
    final BigDecimal dilutedMargin =
      Audit.dilutedMargin(margin(contestResult.getVoteTotals()), ballotCount);
    contestResult.setDilutedMargin(dilutedMargin);
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
    vt.forEach((k,v) -> acc.merge(k, v,
                                  (v1,v2) -> { return (null == v1) ? v2 : v1 + v2; }));
    return acc;
  }

  /** extract totals, then sort reversed so the winner is first  **/
  public static List<Entry> rankTotals(final Map<String,Integer> voteTotals) {
    return voteTotals.entrySet().stream()
      .sorted(Comparator.comparing((Entry e) -> -(Integer)e.getValue()))
      .collect(Collectors.toList());
  }

  /** gap between winner and runner-up **/
  public static Integer margin(final Map<String,Integer> voteTotals) {
    final List<Entry> totals = rankTotals(voteTotals);
    final int two = 2;//pmd
    if (two <= totals.size()) {
      return (Integer)totals.get(0).getValue() - (Integer)totals.get(1).getValue();
    } else {
      // uncontested
      return 0;
    }
  }

  /**
   * Find the set of winners for the ranking of voteTotals. Assumes only
   * one winner allowed.
   * @param voteTotals a map of choice name to number of votes
   */
  public static Set<String> winners(final Map<String,Integer> voteTotals) {
    return winners(voteTotals, 1);
  }

  /**
   * Find the set of winners for the ranking of voteTotals.
   * @param voteTotals a map of choice name to number of votes
   * @param winnersAllowed how many can win this contest?
   */
  public static Set<String> winners(final Map<String,Integer> voteTotals,
                                    final Integer winnersAllowed) {
    return rankTotals(voteTotals).stream()
      .limit(winnersAllowed)
      .map(Entry::getKey)
      .map(k -> k.toString()) // types?
      .collect(Collectors.toSet());
  }

  /**
   * Find the set of losers give a ranking of voteTotals and some set
   * of contest winners.
   * @param voteTotals a map of choice name to number of votes
   * @param winners the choices that aren't losers
   */
  public static Set<String> losers(final Map<String,Integer> voteTotals,
                                   final Set<String> winners) {
    final Set<String> l = new HashSet<String>();
    l.addAll((Set<String>)voteTotals.keySet());
    l.removeAll(winners);
    return l;
  }
}
