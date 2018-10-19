package us.freeandfair.corla.controllers;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import static org.testng.Assert.*;

import us.freeandfair.corla.controller.ContestCounter;

public class ContestCounterTest {


  private ContestCounterTest (){};

  @Test()
  public void testAccumulateVoteTotals(){
    List<Map<String,Integer>> voteTotals = exampleVoteTotals();

    Map<String,Integer> results = ContestCounter.accumulateVoteTotals(voteTotals);
    assertEquals((int)results.size(), 2);
    assertEquals((int)results.get("Joe Frasier"), 18);
    assertEquals((int)results.get("Muhammed Ali"), 20);
  }

  @Test()
  public void rankTotalsTest(){
    List<Entry<String, Integer>> results = ContestCounter.rankTotals(townTrustees());
    assertFalse(results.isEmpty());
    assertEquals(results.get(0).getKey(), "Donnita");
  }

  @Test()
  public void pairwiseMarginsTest() {
    final Set<String> winners = Stream.of("Muhammed Ali").collect(Collectors.toSet());
    final Set<String> losers = Stream.of("Joe Frasier").collect(Collectors.toSet());
    final Set<Integer> expectedMargins = Stream.of(5).collect(Collectors.toSet());

    assertEquals(ContestCounter.pairwiseMargins(winners, losers, exampleVoteTotal()),
                 expectedMargins, "Vote for one of two");
  }

  @Test()
  public void multipleWinnerMargins() {
    final Set<String> winners =
      Stream.of("Donnita", "Eva", "John", "Steve").collect(Collectors.toSet());
    final Set<String> losers = Stream.of("William").collect(Collectors.toSet());
    final Set<Integer> expectedMargins = Stream.of(15, 3, 1, 8).collect(Collectors.toSet());

    assertEquals(ContestCounter.pairwiseMargins(winners, losers, townTrustees()),
                 expectedMargins, "Vote for four of five");
  }

  private Map<String,Integer> exampleVoteTotal() {
    Map<String,Integer> vt1 = new HashMap<String,Integer>();
    vt1.put("Joe Frasier", 15);
    vt1.put("Muhammed Ali", 20);
    return vt1;
  }

  private List<Map<String,Integer>> exampleVoteTotals() {
    List<Map<String,Integer>> voteTotals = new ArrayList<Map<String,Integer>>();
    Map<String,Integer> vt1 = new HashMap<String,Integer>();
    Map<String,Integer> vt2 = new HashMap<String,Integer>();
    vt1.put("Joe Frasier", 9);
    vt1.put("Muhammed Ali", 10);
    vt2.put("Joe Frasier", 9);
    vt2.put("Muhammed Ali", 10);
    voteTotals.add(vt1);
    voteTotals.add(vt2);
    return voteTotals;
  }

  private Map<String, Integer> townTrustees() {
    Map<String, Integer> voteTotals = new HashMap<String, Integer>();
    voteTotals.put("John", 5);
    voteTotals.put("Donnita", 19);
    voteTotals.put("William", 4);
    voteTotals.put("Steve", 7);
    voteTotals.put("Eva", 12);
    return voteTotals;
  }
}
