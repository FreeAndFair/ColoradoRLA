package us.freeandfair.corla.controllers;

import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.Persistence;

import java.time.Instant;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.testng.annotations.Test;
import org.testng.Assert;

public class BallotSelectionTest {


  private BallotSelectionTest (){};

  private Boolean return_cvr = true;

  @Test()
  public void testSelectBallotsReturnsListOfOnes(){
    Long rand = 1L;
    Long sequence_start = 1L;
    List<CVRToAuditResponse> results = makeSelection(rand,sequence_start);
    Assert.assertEquals(1, results.size());
    Assert.assertEquals(results.get(0).imprintedID(), "1-1-1");
  }

  @Test()
  public void testSelectBallotsReturnsListHappyPath(){
    Long rand = 47L;
    Long sequence_start = 41L;
    List<CVRToAuditResponse> results = makeSelection(rand,sequence_start);
    Assert.assertEquals(1, results.size());
    Assert.assertEquals(results.get(0).imprintedID(), "1-1-7");
  }

  @Test()
  public void testSelectBallotsReturnsPhantomRecord(){
    Long rand = 47L;
    Long sequence_start = 41L;
    // overwrite var
    return_cvr = false;
    List<CVRToAuditResponse> results = makeSelection(rand,sequence_start);
    Assert.assertEquals(1, results.size());
    Assert.assertEquals(results.get(0).imprintedID(), "1-1-7");
    Assert.assertEquals(results.get(0).ballotType(), "PHANTOM RECORD");
    Assert.assertEquals(results.get(0).cvrNumber(), 0);
  }

  private List<CVRToAuditResponse> makeSelection(Long rand, Long sequence_start) {
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

    // subject under test
    return BallotSelection.selectBallots(rands, 0L, query, queryCVR);
  }

  public CastVoteRecord fakeCVR() {
    if (return_cvr) {
      Instant now = Instant.now();
      CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.UPLOADED,
                                              now,
                                              64L,          // county_id
                                              1,            // cvr_number
                                              1,            // sequence_number
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
}
