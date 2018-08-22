package us.freeandfair.corla.controllers;

import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.controller.ContestCounter;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;

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

public class BallotSelectionTest {

  private BallotSelectionTest() {};

  private Boolean return_cvr = true;

  @Test()
  public void testSelectBallotsReturnsListOfOnes(){
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

  @Test()

  public void testCombineSegments() {
    List<Integer> rands1 = Stream.of(1,3).collect(Collectors.toList());
    List<Integer> rands2 = Stream.of(2,3).collect(Collectors.toList());
    List<Integer> expected = Stream.of(1,3,2,3).collect(Collectors.toList());
    Map<Long,List<Integer>> acc = new HashMap<Long,List<Integer>>();
    Map<Long,List<Integer>> seg = new HashMap<Long,List<Integer>>();
    acc.put(123L, rands1);
    seg.put(123L, rands2);

    Map<Long,List<Integer>> newAcc = BallotSelection.combineSegment(acc, seg);

    // 1,3 combined with 2,3 = 1,3,2,3
    assertEquals(newAcc.get(123L), expected);
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
}
