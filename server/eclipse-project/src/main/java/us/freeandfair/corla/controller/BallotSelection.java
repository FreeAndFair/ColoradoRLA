/**
 * Prepare a list of ballots from a list of random numbers
 **/
package us.freeandfair.corla.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
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

  /**
   * create a random list of numbers and divide them into the appropriate
   * counties
   **/
  public static Map<Long,List<Integer>> segmentsForContest(final ContestResult contestResult,
                                                                 final String seed ,
                                                                 final Integer min_index,
                                                                 final Integer max_index) {
    final int globalTotal = ultimateTotal(contestResult.countyIDs()).intValue();
    final PseudoRandomNumberGenerator gen = new PseudoRandomNumberGenerator(seed,
                                                                      true, // allow dups
                                                                      1, // minimum value
                                                                      globalTotal);

    final List<Integer> globalRands = gen.getRandomNumbers(min_index, max_index);

    Main.LOGGER.info("seed:" + seed);
    Main.LOGGER.info("globalTotal:" + globalTotal);
    Main.LOGGER.info("min_index:" + min_index);
    Main.LOGGER.info("max_index:" + max_index);
    Main.LOGGER.info("globalRands:" + globalRands.size());
    return contestCVRs(globalRands,
                       contestResult.countyIDs());
  }

  /**
   * Divide a list of random numbers into Segments
   **/
  public static Map<Long,List<Integer>> contestCVRs(final List<Integer> rands,
                                                          final Set<Long> countyIds) {
    return contestCVRs(rands, countyIds, BallotManifestInfoQueries::getMatching);
  }

  /**
   * Divide a list of random numbers into Segments
   **/
  public static Map<Long,List<Integer>> contestCVRs(final List<Integer> globalRands,
                                                    final Set<Long> countyIds,
                                                    final MATCHINGQ queryMatching) {

    final Set<BallotManifestInfo> contestBmis = queryMatching.apply(countyIds);
    final Map<Long,List<Integer>> countyRands = new HashMap<Long,List<Integer>>();
    globalRands.forEach(rand -> {
        final BallotManifestInfo bmi = selectCountyId(Long.valueOf(rand), contestBmis);
        countyRands.put(bmi.countyID(),
                        addToList(countyRands.get(bmi.countyID()),
                                  bmi.unUltimate(rand)));
      });

    return countyRands;
  }

  /**
   * Combine two segment maps. Useful for accumulating a single audit
   * subsequence for contests that have different ballot universes.
   **/
  public static Map<Long,List<Integer>> combineSegment(final Map<Long,List<Integer>> acc,
                                                       final Map<Long,List<Integer>> seg) {

    // we iterate over seg because it may have a key that the accumulator has not
    // seen yet
    seg.forEach((k,v) -> acc.merge(k, v, (v1,v2) -> addAllToList(v1, v2)));
    return acc;
  }

  /**
   * @description add an element to a list, l, where l may be null.
   **/
  @SuppressWarnings({"PMD.AvoidReassigningParameters"}) // FIXME Don't reassign.
  public static <T> List<T> addToList(List<T> l, final T t) {
    l = (null == l) ? new ArrayList<T>() : l;
    l.add(t);
    return l;
  }

  /**
   * FIXME this is where all the duplicate questions will be answered
   *
   * @description combine two lists, where l may be null.
   * @return a List<T> corresponding to the arguments
   * @param l the list to add into
   * @param t the list being added
   */
  @SuppressWarnings({"PMD.AvoidReassigningParameters"}) // FIXME Don't reassign.
  public static <T> List<T> addAllToList(List<T> l, final List<T> t) {
    l = (null == l) ? new ArrayList<T>() : l;
    l.addAll(t);
    return l;
  }

  /**
   * Select CVRs from random numbers through ballot manifest info in
   * "audit sequence order"
   **/
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                final Long countyId) {
    return selectCVRs(rands,
                      countyId,
                      BallotManifestInfoQueries::holdingSequenceNumber,
                      CastVoteRecordQueries::atPosition);
  }

  /**
   * Same as the two-arity version of _selectCVRs_, but with dependency
   * injection.
   **/
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                final Long countyId,
                                                final BMIQ queryBMI,
                                                final CVRQ queryCVR) {
    final List<CastVoteRecord> cvrs = new LinkedList<CastVoteRecord>();

    for (final Integer r: rands) {
      final Long rand = Long.valueOf(r);

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
  public static Set<BallotManifestInfo> projectUltimateSequence(final Set<BallotManifestInfo> bmis) {
    Long last = 0L;
    for (final BallotManifestInfo bmi: bmis) {
      // plus one to make the sequence start and end inclusive in bmi.isHolding
      bmi.setUltimate(last + 1L);
      last = bmi.ultimateSequenceEnd;
    }
    return bmis;
  }

  /**
   * Find the manifest entry holding a random selection
   */
  public static BallotManifestInfo selectCountyId(final Long rand,
                                                  final Set<BallotManifestInfo> bmis) {
    final Optional<BallotManifestInfo> holding = projectUltimateSequence(bmis).stream()
      .filter(bmi -> bmi.isHolding(rand))
      .findFirst();
    if (holding.isPresent()) {
      return holding.get();
    } else {
      final String msg = "Could not find BallotManifestInfo holding random number: " + rand;
      throw new MissingBallotManifestException(msg);
    }
  }

  /**
   * Calculate the total number of ballots across a set of counties
   **/
  public static Long ultimateTotal(final Set<Long> countyIds) {
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

  /**
   * a functional interface to pass a function as an argument
   **/
  public interface MATCHINGQ {

    /** how to query the database **/
    Set<BallotManifestInfo> apply(final Set<Long> county_ids);
  }
}
