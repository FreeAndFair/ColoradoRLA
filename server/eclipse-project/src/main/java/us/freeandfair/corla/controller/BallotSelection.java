/**
 * Prepare a list of ballots from a list of random numbers
 **/
package us.freeandfair.corla.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;

public final class BallotSelection {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(BallotSelection.class);

  /**
   * Prevent construction
   */
  private BallotSelection() {
  }

  /**
   * Select CVRs associated with the ballots in the manifest, if they exist.
   *
   * CVRs are returned in an order corresponding to the order in rands.
   *
   * @param rands random numbers corresponding to ballot manifest positions
   * @param countyId the county ID
   */
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                final Long countyId) {
    return selectCVRs(rands,
                      countyId,
                      BallotManifestInfoQueries::holdingSequenceNumber,
                      CastVoteRecordQueries::atPosition);
  }

  /**
   * Select CVRs associated with the ballots in the manifest, if they exist.
   *
   * CVRs are returned in an order corresponding to the order in rands.
   *
   * @param rands random numbers corresponding to ballot manifest positions
   * @param countyId the county ID
   * @param queryBMI supplied ballot manifest info query
   * @param queryCVR supplied CVR query
   */
  public static List<CastVoteRecord> selectCVRs(final List<Integer> rands,
                                                final Long countyId,
                                                final BMIQ queryBMI,
                                                final CVRQ queryCVR) {
    final List<CastVoteRecord> cvrs = new LinkedList<CastVoteRecord>();
    for (final Integer r: rands) {
      final Long rand = Long.valueOf(r);
      // could we get them all at once? I'm not sure
      final Optional<BallotManifestInfo> bmiMaybe = queryBMI.apply(
          rand, countyId);
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
        LOGGER.warn(
            String.format("Corresponding CVR not found for selected ballot"
                + " manifest entry; creating a phantom CVR as a placeholder"
                + " [countyId=%d, scannerId=%d, batchId=%s, ballotPosition=%d]",
                bmi.countyID(),
                bmi.scannerID(),
                bmi.batchID(),
                bmi.ballotPosition(rand)));
        // a discrepancy will be created for this later on
        cvr = createPhantomRecord(
            bmi.countyID(),
            bmi.scannerID(),
            bmi.batchID(),
            bmi.ballotPosition(rand));
      }

      cvrs.add(cvr);
    }

    return cvrs;
  }

  /**
   * Joins provided CVRs to the ballot manifest.
   *
   * Produces a list of CVRToAuditResponse elements which represent the CVRs
   * augmented with ballot manifest data.
   *
   * @return CVRs joind with ballot manifest data
   */
  public static List<CVRToAuditResponse>
      toResponseList(final List<CastVoteRecord> cvrs) {
    return toResponseList(cvrs, BallotManifestInfoQueries::locationFor);
  }


  /**
   * Joins provided CVRs to the ballot manifest.
   *
   * Produces a list of CVRToAuditResponse elements which represent the CVRs
   * augmented with ballot manifest data.
   *
   * Uses a passed-in BallotManifestInfo query.
   *
   * @return CVRs joind with ballot manifest data
   */
  public static List<CVRToAuditResponse>
      toResponseList(final List<CastVoteRecord> cvrs, final BMILOCQ bmiq) {

    final List<CVRToAuditResponse> responses = new LinkedList<CVRToAuditResponse>();

    int i = 0;
    for (final CastVoteRecord cvr: cvrs) {
      final BallotManifestInfo bmi =
          bmiMaybe(bmiq.apply(cvr), Long.valueOf(cvr.cvrNumber()));

      responses.add(toResponse(i, bmi, cvr));
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
   * Create a new phantom record.
   */
  // PHANTOM_RECORD conspiracy theory time
  private static CastVoteRecord createPhantomRecord(final Long countyId,
                                                   final Integer scannerId,
                                                   final String batchId,
                                                   final Long position) {
    final String imprintedId = String.format("%d-%s-%d",
        scannerId, batchId, position);
    // Dummy CVR number (this would have been in the imported CVR)
    final Integer cvrNumber = 0;
    // Dummy sequence number (this would have been set in the file read loop)
    final Integer sequenceNumber = 0;
    return new CastVoteRecord(CastVoteRecord.RecordType.PHANTOM_RECORD,
        // timestamp
        null,
        countyId,
        cvrNumber,
        sequenceNumber,
        scannerId,
        batchId,
        // record ID
        position.intValue(),
        imprintedId,
        // ballot type
        "PHANTOM RECORD",
        // contest info
        null);
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
