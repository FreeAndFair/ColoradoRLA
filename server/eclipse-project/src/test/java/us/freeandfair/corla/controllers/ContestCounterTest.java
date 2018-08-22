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
  public void testMargin(){
    Map<String,Integer> voteTotal = exampleVoteTotal();
    Integer result = ContestCounter.margin(voteTotal);
    // 20 - 15 = 5
    Assert.assertEquals((Integer)5, (Integer)result);
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
