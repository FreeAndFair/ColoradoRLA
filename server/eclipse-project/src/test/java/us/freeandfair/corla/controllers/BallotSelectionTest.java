package us.freeandfair.corla.controllers;

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

import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.Setup;

@Test(groups = {"integration"})
public class BallotSelectionTest {

  private BallotSelectionTest() {};

  private Boolean return_cvr = true;

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
  public void testAuditedPrefixLengthWithNone() {
    List<Long> cvrIds = new ArrayList<>();
    Integer result = BallotSelection.auditedPrefixLength(cvrIds);
    assertEquals((int)result, (int)0);
  }

  @Test()
  public void testAuditedPrefixLengthWithSome() {
    CastVoteRecord cvr1 = fakeAuditedCVR(1);
    CastVoteRecord cvr2 = fakeAuditedCVR(2);
    CastVoteRecord cvr3 = fakeAuditedCVR(3);
    CastVoteRecord cvr4 = fakeAuditedCVR(4);
    CastVoteRecord cvr5 = fakeAuditedCVR(5);
    CastVoteRecord cvr6 = fakeCVR(6);
    CastVoteRecord cvr7 = fakeAuditedCVR(7);
    CastVoteRecord cvr8 = fakeAuditedCVR(8);

    List<Long> cvrIds = new ArrayList<>();
    cvrIds.add(cvr1.id());
    cvrIds.add(cvr2.id());
    cvrIds.add(cvr3.id());
    cvrIds.add(cvr4.id());
    cvrIds.add(cvr5.id());
    cvrIds.add(cvr6.id());
    cvrIds.add(cvr7.id());
    cvrIds.add(cvr8.id());
    Integer result = BallotSelection.auditedPrefixLength(cvrIds);
    assertEquals((int)result, (int)5);
  }

  @Test()
  public void testCombineSegmentsWorksWhenEmpty() {
    List<Segment> segments = new ArrayList<>();
    segments.add(new Segment());
    Segment result = BallotSelection.Selection.combineSegments(segments);
    assertEquals(new ArrayList<>(), result.cvrsInBallotSequence());
  }

  @Test()
  public void testCombineSegmentsAuditSequence() {
    List<Segment> segments = new ArrayList<>();
    Segment segment = new Segment();
    Segment segment2 = new Segment();
    CastVoteRecord cvr1 = fakeCVR(1);
    CastVoteRecord cvr2 = fakeCVR(2);
    CastVoteRecord cvr3 = fakeCVR(3);
    CastVoteRecord cvr4 = fakeCVR(4);
    List<CastVoteRecord> exampleCVRs = Stream.of(cvr1, cvr3, cvr2, cvr2).collect(Collectors.toList());
    List<CastVoteRecord> exampleCVRs2 = Stream.of(cvr3, cvr4).collect(Collectors.toList());
    // have to keep the raw data separate from the ordered, sorted data
    segment.addCvrs(exampleCVRs);
    segment.addCvrIds(exampleCVRs);
    segment2.addCvrs(exampleCVRs2);
    segment2.addCvrIds(exampleCVRs2);
    segments.add(segment);
    segments.add(segment2);

    List<Long> expectedAuditSequence = Stream.of(1L, 3L, 2L, 2L, 3L, 4L).collect(Collectors.toList());
    List<Long> expectedBallotSequence = Stream.of(1L, 2L, 3L, 4L).collect(Collectors.toList());
    Segment result = BallotSelection.Selection.combineSegments(segments);
    assertEquals(result.auditSequence(), expectedAuditSequence);
    assertEquals(
        result.cvrsInBallotSequence()
            .stream()
            .map(cvr -> cvr.id())
            .collect(Collectors.toList()),
        expectedBallotSequence);
  }

  // @Test()
  // public void testSelectBallotsReturnsListOfOnes(){
  //   Long rand = 1L;
  //   Long sequence_start = 1L;
  //   List<CastVoteRecord> results = makeSelection(rand, sequence_start);
  //   assertEquals(1, results.size());
  //   assertEquals(results.get(0).imprintedID(), "1-Batch1-1");
  // }

  // @Test()
  // public void testSelectBallotsReturnsListHappyPath() {
  //   Long rand = 47L;
  //   Long sequence_start = 41L;
  //   List<CastVoteRecord> results = makeSelection(rand, sequence_start);
  //   assertEquals(1, results.size());
  //   assertEquals(results.get(0).imprintedID(), "1-Batch1-1");
  // }

  // @Test()
  // public void testSelectBallotsReturnsPhantomRecord() {
  //   Long rand = 47L;
  //   Long sequence_start = 41L;
  //   // overwrite var
  //   return_cvr = false;
  //   List<CastVoteRecord> results = makeSelection(rand, sequence_start);
  //   assertEquals(1, results.size());
  //   assertEquals(results.get(0).imprintedID(), "");
  //   assertEquals(results.get(0).ballotType(), "PHANTOM RECORD");
  //   assertEquals((int)results.get(0).cvrNumber(), (int)0);
  // }

  // @Test()

  // public void testCombineSegments() {
  //   List<Integer> rands1 = Stream.of(1,3).collect(Collectors.toList());
  //   List<Integer> rands2 = Stream.of(2,3).collect(Collectors.toList());
  //   List<Integer> expected = Stream.of(1,3,2,3).collect(Collectors.toList());
  //   Map<Long,List<Integer>> acc = new HashMap<Long,List<Integer>>();
  //   Map<Long,List<Integer>> seg = new HashMap<Long,List<Integer>>();
  //   acc.put(123L, rands1);
  //   seg.put(123L, rands2);

  //   Map<Long,List<Integer>> newAcc = BallotSelection.combineSegment(acc, seg);

  //   // 1,3 combined with 2,3 = 1,3,2,3
  //   assertEquals(newAcc.get(123L), expected);
  // }

  // private List<CastVoteRecord> makeSelection(Long rand, Long sequence_start) {
  //   // setup
  //   Long sequence_end = rand - sequence_start + 1L;
  //   List<Long> rands = new ArrayList<Long>();
  //   rands.add(rand);

  //   BallotManifestInfo bmi = fakeBMI(sequence_start, sequence_end);
  //   BallotSelection.BMIQ query = (Long r, Long c) -> Optional.of(bmi);
  //   BallotSelection.CVRQ queryCVR = (Long county_id,
  //                                    Integer scanner_id,
  //                                    String batch_id,
  //                                    Long position) -> fakeCVR();
  //   List<Integer> lols = rands.stream().map(l -> (int)l.intValue()).collect(Collectors.toList());

  //   // subject under test
  //   return BallotSelection.selectCVRs(lols, 0L, query, queryCVR);
  // }


  public CastVoteRecord fakeCVR(Integer recordId) {
    if (return_cvr) {
      Instant now = Instant.now();
      CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED,
                                              now,
                                              64L,          // county_id
                                              1,            // cvr_number
                                              45,           // sequence_number
                                              1,            // scanner_id
                                              "Batch1",     // batch_id
                                              recordId,     // record_id
                                              "1-Batch1-1", // imprinted_id
                                              "paper",      // ballot_type
                                              null          // contest_info
                                              );

      cvr.setID(Long.valueOf(recordId));//??whatev
      return cvr;
    } else {
      return null;
    }
  }

  public CastVoteRecord fakeAuditedCVR(final Integer recordId) {
    final CastVoteRecord cvr = fakeCVR(recordId);
    Persistence.saveOrUpdate(cvr);

    final CastVoteRecord acvr = new CastVoteRecord(CastVoteRecord.RecordType.AUDITOR_ENTERED, Instant.now(),
                                                   64L, 1, null, 1,
                                                   "Batch1", recordId, "1-Batch1-1",
                                                   "paper", null);
    acvr.setID(null);
    Persistence.saveOrUpdate(acvr);

    final CVRAuditInfo cai = new CVRAuditInfo(cvr);
    cai.setACVR(acvr);
    Persistence.saveOrUpdate(cai);
    return cvr;
  }

  // public BallotManifestInfo fakeBMI(Long sequence_start,Long sequence_end){
  //   BallotManifestInfo bmi = new BallotManifestInfo(1L,             // county_id
  //                                                   1,              // scanner_id
  //                                                   "1",            // batch_id
  //                                                   1,              // batch_size
  //                                                   "bin-1",        // storage_location
  //                                                   sequence_start, // sequence_start
  //                                                   sequence_end    // sequence_end
  //                                                   );
  //   return bmi;
  // }

  // public ContestResult
  //   fakeContestResult(String contestName,
  //                     Set<County> counties,
  //                     Set<Contest> contests) {
  //   return new ContestResult();
  // }
}
