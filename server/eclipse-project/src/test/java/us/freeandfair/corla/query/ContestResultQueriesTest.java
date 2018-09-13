package us.freeandfair.corla.query;

import java.util.List;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.Assert;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;

@Test(groups = {"integration"})
public class ContestResultQueriesTest {


  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();
  }

  @AfterTest()
  public void tearDown() {
    try {
    Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  @Test()
  public void findOrCreateTest() {
    County county = new County("abc", 321L);
    Persistence.saveOrUpdate(county);
    Assert.assertEquals((Long)county.version(),(Long) 0L);
    Contest contest = new Contest("def",
                                  county,
                                  "desc",
                                  new ArrayList<>(),
                                  1,
                                  1,
                                  0);
    Persistence.saveOrUpdate(contest);
    ContestResult crSetup = new ContestResult(contest.name());
    Persistence.saveOrUpdate(crSetup);
    ContestResult cr = ContestResultQueries.findOrCreate("def");
    Assert.assertEquals(cr.getContestName(), "def");

    // contructed from database
    Assert.assertEquals(cr.id(), crSetup.id());
    ContestResult cr2 = ContestResultQueries.findOrCreate("ghi");
    // newly contructed and persisted!
    Assert.assertNotEquals(cr2.id(), cr.id());

    //ride along to avoid state mgmt
    Integer result = ContestResultQueries.count();
    Assert.assertEquals((int)result, (int) 2);
  }

  // @Test()
  // public void testCount() {
  //   County county = new County("abc", 321L);
  //   Persistence.saveOrUpdate(county);
  //   Contest contest = new Contest("def",
  //                                 county,
  //                                 "desc",
  //                                 new ArrayList<>(),
  //                                 1,
  //                                 1,
  //                                 0);

  //   Persistence.saveOrUpdate(contest);
  //   Integer result = ContestResultQueries.count();
  //   Assert.assertEquals((int)result, (int) 1);
  // }

}
