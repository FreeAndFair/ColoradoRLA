/**
 * Prepare a list of ballots from a list of random numbers
 **/
package us.freeandfair.corla.controller;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.util.PhantomBallots;

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

  /** I, cvr123, volunteer as tribute  **/
  public static class Tribute {
    public Long countyId;
    public Integer scannerId;
    public String batchId;
    public Integer ballotPosition;
  }

  public static class Segment {
    public Set<CastVoteRecord> cvrs = new TreeSet<>(); // to audit ordered, deduped
    public List<Long> cvrIds = new ArrayList<>(); // to audit selection order, possible dups
    public List<Integer> sequencePositions = new ArrayList<>(); // raw data, county scope of generatedNumbers
    public Long countyId; // to avoid mapEntry,getKey,etc.
    public List<Tribute> tributes = new ArrayList<>();

    public void addTribute(BallotManifestInfo bmi, Integer ballotPosition) {
      Tribute t = new Tribute();
      t.countyId = bmi.countyID();
      t.scannerId = bmi.scannerID();
      t.batchId = bmi.batchID();
      t.ballotPosition = ballotPosition;
      tributes.add(t);
    }

    public void addCvrs(Collection<CastVoteRecord> cvrs) {
      this.cvrs.addAll(cvrs);
    }

    public void addCvrIds(Collection<CastVoteRecord> cvrs) {
      this.cvrIds.addAll(cvrs.stream().map(cvr -> cvr.id()).collect(Collectors.toList()));
    }

    public void addCvrIds(List<Long> cvrIds) {
      this.cvrIds.addAll(cvrIds);
    }

    /** in the order of the random selection, not deduped and not sorted **/
    public List<Long> auditSequence() {
      return cvrIds;
    }

    /** deduped and sorted **/
    public List<Long> ballotSequence() {
      return cvrs.stream()
        .map(cvr -> cvr.id())
        .collect(Collectors.toList());
    }

    public String toString() {
      return String.format("[Segment auditSequence=%s ballotSequence=%s ballotPositions=%s ",
                           auditSequence(),
                           ballotSequence(),
                           tributes.stream().map(t -> t.ballotPosition).collect(Collectors.toList()));
    }
  }

  public static class Selection {
    public Map<Long,Segment> segments = new HashMap<>(); //fast access
    public Integer domainSize;
    public List<Integer> generatedNumbers;
    public String contestName;
    public ContestResult contestResult;
    public String seed;
    public BigDecimal riskLimit;
    public Integer minIndex;
    public Integer maxIndex;

    public static Segment combineSegments(Collection<Segment> segments) {
      return segments.stream()
        .filter(s -> null != s)
        .reduce(new Segment(),
                (acc,s) -> {
                  // can't ask segment.cvrs for raw data because it is a TreeSet
                  // so we get the cvrIds
                  acc.addCvrIds(s.cvrIds);
                  acc.addCvrs(s.cvrs);
                  return acc;});
    }

    public void initCounty(Long countyId) {
      if (null == forCounty(countyId)) {
        Segment segment = new Segment();
        segment.countyId = countyId;
        this.segments.put(countyId, segment);
      }
    }

    public void addBallotPosition(BallotManifestInfo bmi, Integer ballotPosition) {
      this.forCounty(bmi.countyID()).addTribute(bmi, ballotPosition);
    }

    public Segment forCounty(Long countyId) {
      return this.segments.get(countyId);
    }

    public Collection<Segment> allSegments() {
      return segments.values();
    }

    public List<Long> contestCVRIds() {
      return contestResult.countyIDs().stream()
        .map(id -> forCounty(id))
        .filter(s -> s != null)
        .map(segment -> segment.cvrIds)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    }

    public String toString() {
      return String.format("[Selection contestName=%s generatedNumbers=%s domainSize=%s]",
                           contestName,
                           generatedNumbers,
                           domainSize);
    }
  }

  /**
   * create a random list of numbers and divide them into the appropriate
   * counties
   * FIXME: setSegments on contestResult for now
   **/
  public static Selection randomSelection(final ContestResult contestResult,
                                          final String seed,
                                          final Integer minIndex,
                                          final Integer maxIndex) {
    final int domainSize = ballotsCast(contestResult.countyIDs()).intValue();
    final PseudoRandomNumberGenerator gen =
      new PseudoRandomNumberGenerator(seed, true, 1, domainSize);

    final List<Integer> generatedNumbers = gen.getRandomNumbers(minIndex, maxIndex);

    // make the theoretical selections (avoiding cvrs)
    Selection selection = select(generatedNumbers, contestResult.countyIDs());

    selection.contestResult = contestResult;
    selection.contestName = contestResult.getContestName();//posterity
    selection.domainSize = domainSize; //posterity
    selection.generatedNumbers = generatedNumbers; //posterity
    selection.seed = seed; //posterity
    selection.minIndex = minIndex;
    selection.maxIndex = maxIndex;

    LOGGER.info("randomSelection: selection= " + selection);
    // get the CVRs from the theoretical
    resolveSelection(selection);
    return selection;
  }

  /**
   * Divide a list of random numbers into segments by county
   **/
  public static Selection select(final List<Integer> generatedNumbers,
                                 final Set<Long> countyIds) {
    return select(generatedNumbers, countyIds, BallotManifestInfoQueries::getMatching);
  }

  /**
   * Divide a list of random numbers into segments
   * transitional refactor step (3 arities is too many)
   **/
  public static Selection select(final List<Integer> generatedNumbers,
                                 final Set<Long> countyIds,
                                 final MATCHINGQ queryMatching) {

    final Set<BallotManifestInfo> contestBmis = queryMatching.apply(countyIds);
    return select(generatedNumbers, countyIds, contestBmis);
  }

  /**
   * Divide a list of random numbers into segments by county
   **/
  public static Selection select(final List<Integer> generatedNumbers,
                                 final Set<Long> countyIds,
                                 Set<BallotManifestInfo> contestBmis) {
    final Selection selection = new Selection();
    countyIds.forEach(id -> selection.initCounty(id));
    generatedNumbers.forEach(rand -> {
        final BallotManifestInfo bmi = selectCountyId(Long.valueOf(rand), contestBmis);
        selection.addBallotPosition(bmi,
                                    // translate rand from Contest scope to bmi/batch scope
                                    bmi.translateRand(rand));
    });
    return selection;
  }

  /**
   * When we draw more than one phantom ballot, we need to make sure
   * that the persistence context knows about only one instance of each.
   * (Phantom ballots are POJOs, so every phantom ballot looks identical
   * to the persistence context.)
   *
   * @param county The county.
   * @param cvrs A list of CastVoteRecord objects that might contain phantom ballots
   */
  public static List<CastVoteRecord> dedupePhantomBallots(final List<CastVoteRecord> cvrs) {
    // A map of a CVR to a CVR so we can get a unique persisted entity from the
    // database.
    final Map<CastVoteRecord, CastVoteRecord> phantomCvrs =
        cvrs.stream()
            .filter(cvr -> PhantomBallots.isPhantomRecord(cvr))
            .collect(Collectors.toMap(
                Function.identity(),
                Function.identity(),
                (a, b) -> b));

    // Assign database identifiers to newly-created phantom CVRs.
    phantomCvrs.entrySet().stream()
        .forEach(e -> Persistence.saveOrUpdate(e.getValue()));

    // Use the database-mapped CVR if it exists.
    return cvrs.stream()
        .map(cvr -> phantomCvrs.getOrDefault(cvr, cvr))
        .collect(Collectors.toList());
  }

  /** look for the cvrs, some may be phantom records **/
  public static Selection resolveSelection(final Selection selection) {
    selection.allSegments().forEach(segment -> {
        final List<CastVoteRecord> cvrs =
          dedupePhantomBallots(segment.tributes.stream()
                               .map(CastVoteRecordQueries::atPosition)
                               .collect(Collectors.toList()));
        segment.addCvrs(cvrs);
        segment.addCvrIds(cvrs); // keep raw data separate
      });
    LOGGER.info("resolveSelection = " + selection.segments);
    LOGGER.info("resolveSelection = " + Selection.combineSegments(selection.allSegments()).cvrIds);
    return selection;
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
   * How much of an audit sequence have we checked?
   *
   * @param cvrIds A list of IDs to check. Presumably the unsorted
   * original sampling.
   * @return the number of ballot cards that have been audited
   */
  public static Integer auditedPrefixLength(List<Long> cvrIds) {
    // FIXME extract-fn, then use
    // Map <Long, Boolean> isAuditedById = checkAudited(cvrIds);

    if (cvrIds.isEmpty()) { return 0;}

    Map <Long, Boolean> isAuditedById = new HashMap<>();
    for (final Long cvrId: cvrIds) {
      CVRAuditInfo cvrai = Persistence.getByID(cvrId, CVRAuditInfo.class);
      // has an acvr
      boolean isAudited = (cvrai != null && cvrai.acvr() != null);
      isAuditedById.put(cvrId, isAudited);
    }

    Integer idx = 0;
    for (int i=0; i < cvrIds.size(); i++) {
      boolean audited = isAuditedById.get(cvrIds.get(i));
      if (audited) {
        idx = i + 1;
      } else { break; }
    }
    LOGGER.debug(String.format("[auditedPrefixLength: isAuditedById=%s, apl=%d]",
                                isAuditedById, idx));
    return idx;
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
   * The total number of ballots across a set of counties
   * @param countyIds a set of counties to count
   * @return the number of ballots in the ballot manifests belonging to
   * countyIds
   **/
  public static Long ballotsCast(final Set<Long> countyIds) {
    // could use voteTotals but that would be impure; using cvr data
    //
    // If a county has only one ballot for a contest, all the ballots from that
    // county are used to get a total number of ballots
    return BallotManifestInfoQueries.totalBallots(countyIds);
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

  /**
   * a functional interface to pass a function as an argument
   **/
  public interface MATCHINGQ {

    /** how to query the database **/
    Set<BallotManifestInfo> apply(final Set<Long> county_ids);
  }
}
