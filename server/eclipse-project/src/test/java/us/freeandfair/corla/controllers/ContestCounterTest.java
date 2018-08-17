package us.freeandfair.corla.controllers;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import org.testng.annotations.Test;
import org.testng.Assert;

import us.freeandfair.corla.controller.ContestCounter;

public class ContestCounterTest {


  private ContestCounterTest (){};

  @Test()
  public void testAccumulateVoteTotals(){
    List<Map<String,Integer>> voteTotals = exampleVoteTotals();

    Map<String,Integer> results = ContestCounter.accumulateVoteTotals(voteTotals);
    Assert.assertEquals((int)results.size(), 2);
    Assert.assertEquals((int)results.get("Joe Frasier"), 18);
    Assert.assertEquals((int)results.get("Muhammed Ali"), 20);
  }

  @Test()
  public void testDilutedMargin(){
    Map<String,Integer> voteTotal = exampleVoteTotal();
    BigDecimal result = ContestCounter.dilutedMargin(voteTotal);
    // 15 - 20 = 5 | 15 + 20 = 35
    // 5 / 35 = 1/7 = 0.1428...
    Assert.assertEquals(result.setScale(3, RoundingMode.CEILING),
                        BigDecimal.valueOf(0.1428).setScale(3, RoundingMode.CEILING));
  }

  private Map<String,Integer> exampleVoteTotal (){
    Map<String,Integer> vt1 = new HashMap<String,Integer>();
    vt1.put("Joe Frasier", 15);
    vt1.put("Muhammed Ali", 20);
    return vt1;
  }

  private List<Map<String,Integer>> exampleVoteTotals(){
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
}
