package us.freeandfair.corla.endpoint;

import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.controller.BallotSelection.Selection;
import us.freeandfair.corla.controller.BallotSelection.Segment;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.CountyDashboard;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import java.util.function.Function;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import static org.testng.Assert.*;

import us.freeandfair.corla.query.Setup;

@Test(groups = {"integration"})
public class StartAuditRoundTest {

  private StartAuditRoundTest() {};


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

  // this test doesn't do much yet
  @Test()
  public void testReadyToStartFalse() {
    StartAuditRound sar = new StartAuditRound();
    County county = new County("c1", 1L);
    CountyDashboard cdb = new CountyDashboard(county);
    CountyDashboardASM cdbAsm = new CountyDashboardASM(cdb.id().toString());

    assertEquals(false, (boolean)sar.isReadyToStartAudit(cdb));
  }

}
