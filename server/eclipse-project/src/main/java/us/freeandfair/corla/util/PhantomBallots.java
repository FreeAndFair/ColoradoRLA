/*
 * Colorado RLA System
 */

package us.freeandfair.corla.util;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import us.freeandfair.corla.controller.ComparisonAuditController;

import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.CountyDashboard;

import us.freeandfair.corla.persistence.Persistence;

import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.ContestQueries;

/**
 * Phantom ballot handling.
 */
public final class PhantomBallots {
  /**
   * Prevent public construction
   */
  private PhantomBallots() {
  }

  /**
   * Audit phantom records as if by an audit board.
   */
  public static List<CastVoteRecord> auditPhantomRecords(
      final CountyDashboard cdb,
      final List<CastVoteRecord> cvrs) {
    return cvrs.stream()
        .map(cvr -> {
           return isPhantomRecord(cvr)
               ? auditPhantomRecord(cdb, cvr)
               : cvr;
        })
        .collect(Collectors.toList());
  }

  /**
   * Returns a list of CastVoteRecords with phantom records removed.
   */
  public static List<CastVoteRecord> removePhantomRecords(
      final List<CastVoteRecord> cvrs) {
    return cvrs.stream()
      .filter(cvr -> !isPhantomRecord(cvr))
      .collect(Collectors.toList());
  }

  private static boolean isPhantomRecord(final CastVoteRecord cvr) {
    return cvr.recordType() == CastVoteRecord.RecordType.PHANTOM_RECORD;
  }

  /**
   * Audit a phantom record as if by an audit board.
   */
  private static CastVoteRecord auditPhantomRecord(final CountyDashboard cdb,
                                                   final CastVoteRecord cvr) {
    // get the one with the ID
    final CastVoteRecord existing = CastVoteRecordQueries.atPosition(cvr);
    if (null != existing) {
      // only create one
      // multiplicity is handled elsewhere
      return existing;
    }

    // we need to create a discrepancy for every contest that COULD have
    // appeared on the ballot, which we take to mean all the contests that occur
    // in the county
    final Set<Contest> contests = ContestQueries.forCounty(cdb.county());

    final List<CVRContestInfo> phantomContestInfos = contests.stream()
      .map(c -> {return new CVRContestInfo(c,
                                           "PHANTOM_RECORD - CVR not found",
                                           null,
                                           new ArrayList<String>()); } )
      .collect(Collectors.toList());

    cvr.setContestInfo(phantomContestInfos);
    Persistence.saveOrUpdate(cvr);
    // this relation is created when a cvr is prepared for audit
    // we won't be actually auditing this cvr, but we need this relation to make
    // the app work
    final CVRAuditInfo cvrAuditInfo = new CVRAuditInfo(cvr);
    Persistence.saveOrUpdate(cvrAuditInfo);
    final CastVoteRecord acvr = new CastVoteRecord(
        CastVoteRecord.RecordType.PHANTOM_RECORD_ACVR,
        Instant.now(),
        cvr.countyID(),
        cvr.cvrNumber(),
        null,
        cvr.scannerID(),
        cvr.batchID(),
        cvr.recordID(),
        cvr.imprintedID(),
        cvr.ballotType(),
        phantomContestInfos);
    Persistence.saveOrUpdate(acvr);

    ComparisonAuditController.submitAuditCVR(cdb, cvr, acvr);

    return cvr;
  }
}
