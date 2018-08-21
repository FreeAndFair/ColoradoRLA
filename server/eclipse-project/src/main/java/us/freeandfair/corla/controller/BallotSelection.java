/**
 * Prepare a list of ballots from a list of random numbers
 **/
package us.freeandfair.corla.controller;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Set;
import java.util.stream.Collectors;

import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;

public final class BallotSelection {

  /** prevent construction **/
  private BallotSelection() {
  }

  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                final Long countyId
                                                ) {
    return null;
  }

  /** select CVRs from random numbers through ballot manifest info
      in "audit sequence order"
   **/
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                // final Long countyId
                                                final ContestResult contestResult
                                                ) {
    return selectCVRs(rands,
                      contestResult,
                      BallotManifestInfoQueries::holdingSequenceNumber,
                      CastVoteRecordQueries::atPosition);
  }

  /** same as above with dependency injection **/
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                // final Long countyId,
                                                final ContestResult contestResult,
                                                final BMIQ queryBMI,
                                                final CVRQ queryCVR) {
    final List<CastVoteRecord> cvrs = new LinkedList<CastVoteRecord>();
    final List<Long> countyIds = contestResult.getCounties().stream()
      .map(c -> c.id())
      .collect(Collectors.toList());
    final Set<BallotManifestInfo> contestBmis =
        new TreeSet<BallotManifestInfo>(new BallotManifestInfo.Sort());
    contestBmis.addAll(BallotManifestInfoQueries.getMatching(countyIds));

    for (final Integer r: rands) {
      final Long rand = Long.valueOf(r);
      // project a random number onto a list of
      final Long countyId = selectCountyId(rand, contestBmis);

      // could we get them all at once? I'm not sure
      final Optional<BallotManifestInfo> bmiMaybe = queryBMI.apply(rand, countyId);
      if (!bmiMaybe.isPresent()) {
        final String msg = "could not find a ballot manifest for random number: "
            + rand;
        throw new BallotSelection.MissingBallotManifestException(msg);
      }
      final BallotManifestInfo bmi = bmiMaybe.get();
      CastVoteRecord cvr = queryCVR.apply(bmi.countyID(),
                                          bmi.scannerID(),
                                          bmi.batchID(),
                                          bmi.ballotPosition(rand));
      if (cvr == null) {
        // TODO: create a discrepancy when this happens
        cvr = phantomRecord();
      }

      cvrs.add(cvr);
    }
    return cvrs;
  }

  /**
   * project a sequence across counties
   *
   * Uses special fields on BallotManifestInfo to hold temorary values.
   * These values are only valid in this set of BallotManifestInfos
   **/
  public static Set<BallotManifestInfo> projectUltimateSequence(Set<BallotManifestInfo> bmis) {
    Long last = 0L;
    for (BallotManifestInfo bmi: bmis) {
      bmi.setUltimate(last + 1L);
      System.out.println(last);
      System.out.println(bmi.ultimateSequenceEnd);
      last = bmi.ultimateSequenceEnd;
    }
    return bmis;
  }

  public static Long selectCountyId(Long rand, Set<BallotManifestInfo> bmis) {
    Optional<BallotManifestInfo> holding = projectUltimateSequence(bmis).stream()
      .filter(bmi -> bmi.isHolding(rand))
      .findFirst();
    if (holding.isPresent()) {
      return holding.get().countyID();
    } else {
      String msg = "Could not find BallotManifestInfo holding random number: " + rand;
      throw new MissingBallotManifestException(msg);
    }
  }

  //** calculate the total number of ballots for a Contest across counties **/
  public static Long ultimateTotal(List<Long> countyIds) {
    // could use voteTotals but that would be impure; using cvr data
    //
    // If a county has only one ballot for a contest, all the ballots from that
    // county are used to get a total number of ballots
    return countyIds.stream()
      .map(BallotManifestInfoQueries::maxSequence)
      .map(l -> l.getAsLong())
      .mapToLong(Number::longValue)
      .sum();
  }

  /** PHANTOM_RECORD conspiracy theory time **/
  public static CastVoteRecord phantomRecord() {
    final CastVoteRecord cvr = new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_RECORD,
                                                  null,
                                                  0L,
                                                  0,
                                                  0,
                                                  0,
                                                  "",
                                                  0,
                                                  "",
                                                  "PHANTOM RECORD",
                                                  null);
    // TODO prevent the client from requesting info about this cvr
    cvr.setID(0L);
    return cvr;
  }

  /** render cvrs using BallotManifestInfo **/
  public static List<CVRToAuditResponse>
      toResponseList(final List<CastVoteRecord> cvrs) {
    return toResponseList(cvrs, BallotManifestInfoQueries::locationFor);
  }

  /** render cvrs using BallotManifestInfo **/
  public static List<CVRToAuditResponse>
      toResponseList(final List<CastVoteRecord> cvrs,
                   final BMILOCQ bmiq) {

    final List<CVRToAuditResponse> responses = new LinkedList<CVRToAuditResponse>();

    int i = 0;
    for (final CastVoteRecord cvr: cvrs) {
      final BallotManifestInfo bmi =
          bmiMaybe(bmiq.apply(cvr), Long.valueOf(cvr.cvrNumber()));

      responses.add(toResponse(i,
                               bmi,
                               cvr));
      i++;
    }
    return responses;
  }

  /** get the bmi or blow up with a hopefully helpful message **/
  public static BallotManifestInfo
      bmiMaybe(final Optional<BallotManifestInfo> bmi, final Long rand) {

    if (!bmi.isPresent()) {
      final String msg = "could not find a ballot manifest for number: " + rand;
      throw new BallotSelection.MissingBallotManifestException(msg);
    }
    return bmi.get();
  }

  /**
   * get ready to render the data
   **/
  public static CVRToAuditResponse toResponse(final int i,
                                              final BallotManifestInfo bmi,
                                              final CastVoteRecord cvr) {

    return new CVRToAuditResponse(i,
                                  bmi.scannerID(),
                                  bmi.batchID(),
                                  cvr.recordID(),
                                  cvr.imprintedID(),
                                  cvr.cvrNumber(),
                                  cvr.id(),
                                  cvr.ballotType(),
                                  bmi.storageLocation(),
                                  cvr.auditFlag());
  }

  /**
   * this is bad, it could be one of two things:
   * - a random number was generated outside of the number of (theoretical) ballots
   * - there is a gap in the sequence_start and sequence_end values of the
   *   ballot_manifest_infos
   **/
  public static class MissingBallotManifestException extends RuntimeException {
    /** constructor **/
    public MissingBallotManifestException(final String msg) {
      super(msg);
    }
  }

 /**
   * a functional interface to pass a function as an argument that takes two
   * arguments
   **/
  public interface CVRQ {

    /** how to query the database **/
    CastVoteRecord apply(Long county_id,
                         Integer scanner_id,
                         String batch_id,
                         Long position);
  }

  /**
   * a functional interface to pass a function as an argument that takes two
   * arguments
   **/
  public interface BMIQ {

    /** how to query the database **/
    Optional<BallotManifestInfo> apply(Long rand,
                                       Long countyId);
  }


  /**
   * a functional interface to pass a function as an argument
   **/
  public interface BMILOCQ {

    /** how to query the database **/
    Optional<BallotManifestInfo> apply(CastVoteRecord cvr);
  }
}
