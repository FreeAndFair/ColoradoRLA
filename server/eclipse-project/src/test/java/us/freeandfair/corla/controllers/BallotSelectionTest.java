package us.freeandfair.corla.controllers;

import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;

// FIXME this next chunk is more of an integration test?
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.County;
// integration?
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestResultQueries;
import us.freeandfair.corla.query.Setup;

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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import static org.testng.Assert.*;

public class BallotSelectionTest {

  @BeforeTest(enabled=true)
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();
  }

  @AfterTest(enabled=true)
  public void tearDown() {
    try {
    Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  private BallotSelectionTest() {};

  private Boolean return_cvr = true;

  @Test()
  public void testSelectBallotsReturnsListOfOnes() {
    Long rand = 1L;
    Long sequence_start = 1L;
    List<CastVoteRecord> results = makeSelection(rand, sequence_start);
    assertEquals(1, results.size());
    assertEquals(results.get(0).imprintedID(), "1-Batch1-1");
  }

  @Test()
  public void testSelectBallotsReturnsListHappyPath() {
    Long rand = 47L;
    Long sequence_start = 41L;
    List<CastVoteRecord> results = makeSelection(rand, sequence_start);
    assertEquals(1, results.size());
    assertEquals(results.get(0).imprintedID(), "1-Batch1-1");
  }

  @Test()
  public void testSelectBallotsReturnsPhantomRecord() {
    Long rand = 47L;
    Long sequence_start = 41L;
    // overwrite var
    return_cvr = false;
    List<CastVoteRecord> results = makeSelection(rand, sequence_start);
    assertEquals(1, results.size());
    assertEquals(results.get(0).imprintedID(), "");
    assertEquals(results.get(0).ballotType(), "PHANTOM RECORD");
    assertEquals((int)results.get(0).cvrNumber(), (int)0);
  }

  private List<CastVoteRecord> makeSelection(Long rand, Long sequence_start) {
    // setup
    Long sequence_end = rand - sequence_start + 1L;
    List<Long> rands = new ArrayList<Long>();
    rands.add(rand);

    BallotManifestInfo bmi = fakeBMI(sequence_start, sequence_end);
    BallotSelection.BMIQ query = (Long r, Long c) -> Optional.of(bmi);
    BallotSelection.CVRQ queryCVR = (Long county_id,
                                     Integer scanner_id,
                                     String batch_id,
                                     Long position) -> fakeCVR();
    List<Integer> lols = rands.stream().map(l -> (int)l.intValue()).collect(Collectors.toList());

    // subject under test
    return BallotSelection.selectCVRs(lols, 0L, query, queryCVR);
  }

  public CastVoteRecord fakeCVR() {
    if (return_cvr) {
      Instant now = Instant.now();
      CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED,
                                              now,
                                              64L,          // county_id
                                              1,            // cvr_number
                                              45,           // sequence_number
                                              1,            // scanner_id
                                              "Batch1",     // batch_id
                                              1,            // record_id
                                              "1-Batch1-1", // imprinted_id
                                              "paper",      // ballot_type
                                              null          // contest_info
                                              );

      cvr.setID(1L);
      return cvr;
    } else {
      return null;
    }
  }

  public BallotManifestInfo fakeBMI(Long sequence_start,Long sequence_end){
    BallotManifestInfo bmi = new BallotManifestInfo(1L,             // county_id
                                                    1,              // scanner_id
                                                    "1",            // batch_id
                                                    1,              // batch_size
                                                    "bin-1",        // storage_location
                                                    sequence_start, // sequence_start
                                                    sequence_end    // sequence_end
                                                    );
    return bmi;
  }

  public ContestResult
    fakeContestResult(String contestName,
                      Set<County> counties,
                      Set<Contest> contests) {
    return new ContestResult();
  }

  @Test(enabled=true)
  public void testCountyCreation() {
    // Two counties have submitted CVRs and Ballot Manifests
    County chaffee = new County("Chaffee", 8L);
    Persistence.saveOrUpdate(chaffee);
    assertEquals(chaffee.name(), "Chaffee");

    County denver = new County("Denver", 16L);
    Persistence.saveOrUpdate(denver);
    assertEquals(denver.name(), "Denver");

    Set<County> countyUniverse = new HashSet<County>();
    countyUniverse.add(chaffee); countyUniverse.add(denver);

    // Chaffee County has two batches of ballots
    BallotManifestInfo chaffeeInfo1 = new BallotManifestInfo(chaffee.id(), 1, "1", 50, "chaffee-bin-1", 1L, 50L);
    BallotManifestInfo chaffeeInfo2 = new BallotManifestInfo(chaffee.id(), 1, "2", 50, "chaffee-bin-2", 51L, 100L);

    Persistence.saveOrUpdate(chaffeeInfo1);
    Persistence.saveOrUpdate(chaffeeInfo2);

    // Denver County is larger and has six batches of ballots over two scanners. Also, the
    // scanners have a higher capacity.
    BallotManifestInfo denverInfo1 = new BallotManifestInfo(denver.id(), 1, "1", 200, "denver-bin-1", 1L, 200L);
    BallotManifestInfo denverInfo2 = new BallotManifestInfo(denver.id(), 1, "2", 200, "denver-bin-2", 201L, 400L);
    BallotManifestInfo denverInfo3 = new BallotManifestInfo(denver.id(), 1, "3", 200, "denver-bin-3", 4011L, 600L);
    BallotManifestInfo denverInfo4 = new BallotManifestInfo(denver.id(), 2, "1", 200, "denver-bin-4", 601L, 800L);
    BallotManifestInfo denverInfo5 = new BallotManifestInfo(denver.id(), 2, "2", 200, "denver-bin-5", 801L, 1000L);
    BallotManifestInfo denverInfo6 = new BallotManifestInfo(denver.id(), 2, "3", 200, "denver-bin-6", 1001L, 1200L);

    // List<BallotManifestInfo> chaffeeManifest = new ArrayList<BallotManifestInfo>();
    // chaffeeManifest.add(chaffeeInfo1);
    // chaffeeManifest.add(chaffeeInfo2);

    Persistence.saveOrUpdate(denverInfo1);
    Persistence.saveOrUpdate(denverInfo2);
    Persistence.saveOrUpdate(denverInfo3);
    Persistence.saveOrUpdate(denverInfo4);
    Persistence.saveOrUpdate(denverInfo5);
    Persistence.saveOrUpdate(denverInfo6);

    // Two options for governor
    Choice govRep = new Choice("Walker Stapelton", "current state treasurer", false, false);
    Choice govDem = new Choice("Jared Polis", "the rep from Boulder", false, false);

    List<Choice> govChoices = new ArrayList<Choice>();
    govChoices.add(govRep); govChoices.add(govDem);

    // Two options for secretary of state
    Choice sosRep = new Choice("Wayne", "the incumbent", false, false);
    Choice sosDem = new Choice("Anti-Wayne", "the new person", false, false);

    List<Choice> sosChoices = new ArrayList<Choice>();
    sosChoices.add(sosRep); sosChoices.add(sosDem);

    // When two counties have the same contest name and choices, we'll
    // combine them into one logical contest

    Contest chaffeeGov = new Contest("Governor", chaffee, "2018 Governor's Race", govChoices, 1, 1, 1);
    Contest chaffeeSOS = new Contest("SOS", chaffee, "2018 SOS Race", sosChoices, 1, 1, 1);
    CountyContestResult chaffeeRes = new CountyContestResult(chaffee, chaffeeGov);
    Persistence.saveOrUpdate(chaffeeGov); Persistence.saveOrUpdate(chaffeeRes);


    Contest denverGov = new Contest("Governor", denver, "2018 Governor's Race", govChoices, 1, 1, 1);
    Contest denverSOS = new Contest("SOS", denver, "2018 SOS Race", sosChoices, 1, 1, 1);
    CountyContestResult denverRes = new CountyContestResult(denver, denverGov);
    Persistence.saveOrUpdate(denverGov); Persistence.saveOrUpdate(denverRes);

    assertEquals(chaffeeGov.county().name(), "Chaffee");
    assertEquals(denverGov.name(), "Governor");

    final List<Contest> multiCountyContests = new ArrayList<Contest>();
    multiCountyContests.add(chaffeeGov);
    multiCountyContests.add(denverGov);

    final List<ContestResult> contestResults = new ArrayList<ContestResult>();
    for (Contest c: multiCountyContests) {
      contestResults.add(ContestCounter.countContest(c.name()));
    }

    for (ContestResult cr: contestResults) {
      Persistence.saveOrUpdate(cr);
    }

    final ContestResult governorContest = ContestResultQueries.findOrCreate("Governor");
    assertEquals(governorContest.getCounties(), countyUniverse);

    List<BallotManifestInfo> bmis = new ArrayList<BallotManifestInfo>();
    bmis = Persistence.getAll(BallotManifestInfo.class);
    // no county grouping to begin with
    assertEquals(bmis.size(), 8);

    // county id -> county manifest
    Map<Long, List<BallotManifestInfo>> universe = ballotUniverse(bmis);

    assertEquals(universe.get(8L).size(), 2);
    assertEquals(universe.get(16L).size(), 6);

    // From a Map<CountyID, List<BallotManifestInfo>
    // we
    Long ballots =
      universe.values()
      .stream()
      .mapToLong((x) -> ballotsInManifest(x))
      .reduce(0L, (a, b) -> a + b);

    assertEquals(ballots, (Long) 1300L);

    // FIXME I fail?
    assertEquals(countyForBallotSample(100L), chaffee);
    assertEquals(countyForBallotSample(101L), denver);
  }

  // If we can combine many "election districts" (counties, in our
  // case) into a "ballot universe" for a contest, then we'll need to
  // be able to get at where a particular sample (any number in our
  // audit sequence) lives. Which county has ballot(card) 826445?
  public County countyForBallotSample(final Long idx) {
    return new County();
  }

  public Long ballotsInManifest(List<BallotManifestInfo> xs) {
    return xs.stream()
      .mapToLong((x) -> x.sequenceEnd())
      .max()
      .getAsLong();
  }


  // return a ballot universe from a collection of manifest info objects
  public Map<Long, List<BallotManifestInfo>> ballotUniverse(List<BallotManifestInfo> xs) {
    Map<Long, List<BallotManifestInfo>> universe =
      new HashMap<Long, List<BallotManifestInfo>>();

    // FIXME looking for a nice way to return a universe map, this is
    // close, but not quite right...
    //
    // sortedBMIs = xs.stream()
    //   .sorted(Comparator.comparingLong((x) -> x.countyID()))
    //   .collect(Collectors.toMap((x) -> x.countyID(),
    //                             (x) -> x,
    //                             (old, new) -> {
    //                               if (old.contains(new)) {
    //                                 return old;
    //                               } else {
    //                                 old.add(new);
    //                                 return old;
    //                               }
    //                             },
    //                             HashMap::new));

    for(BallotManifestInfo x : xs) {
      List<BallotManifestInfo> manifest;
      final Long id = x.countyID();

      if (universe.containsKey(id)){
        manifest = universe.get(id);
        manifest.add(x);
        universe.put(id, manifest);
      } else {
        manifest = new ArrayList<BallotManifestInfo>();
        manifest.add(x);
        universe.put(id, manifest);
      }
    }

    return universe;
    }
}
