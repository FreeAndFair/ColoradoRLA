/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;

/**
 * Controller methods relevant to comparison audits.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public final class ComparisonAuditController {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ComparisonAuditController.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditController() {
    // empty
  }

  /**
   * Gets all CVRs to audit in the specified round for the specified county
   * dashboard. This returns a list in audit random sequence order.
   *
   * @param the_dashboard The dashboard.
   * @param the_round_number The round number (indexed from 1).
   * @return the CVRs to audit in the specified round.
   * @exception IllegalArgumentException if the specified round doesn't exist.
   */
  public static List<CVRAuditInfo> cvrsToAuditInRound(final CountyDashboard the_cdb,
                                                      final int the_round_number) {
    if (the_round_number < 1 || the_cdb.rounds().size() < the_round_number) {
      throw new IllegalArgumentException("invalid round specified");
    }
    final Round round = the_cdb.rounds().get(the_round_number - 1);
    final Set<Long> id_set = new HashSet<>();
    final List<CVRAuditInfo> result = new ArrayList<>();

    for (final Long cvr_id : round.auditSubsequence()) {
      if (!id_set.contains(cvr_id)) {
        id_set.add(cvr_id);
        result.add(Persistence.getByID(cvr_id, CVRAuditInfo.class));
      }
    }

    return result;
  }

  /**
   * @return the CVR IDs remaining to audit in the current round, or an empty
   * list if there are no CVRs remaining to audit or if no round is in progress.
   */
  public static List<Long> cvrIDsRemainingInCurrentRound(final CountyDashboard the_cdb) {
    final List<Long> result = new ArrayList<Long>();
    final Round round = the_cdb.currentRound();
    if (round != null) {
      for (int i = 0;
           i + round.actualAuditedPrefixLength() < round.expectedAuditedPrefixLength();
           i++) {
        result.add(round.auditSubsequence().get(i + round.actualAuditedPrefixLength()));
      }
    }
    return result;
  }

  /**
   * Return the ballot cards to audit for a particular county and round.
   *
   * The returned list will not have duplicates and is in an undefined order.
   *
   * @param countyDashboard county dashboard owning the rounds
   * @param roundNumber 1-indexed round number
   * @param includeAudited include audited ballots
   *
   * @return the list of ballot cards for audit. If the query does not result in
   *         any ballot cards, for instance when the round number is invalid,
   *         the returned list is empty.
   */
  // TODO: includeAudited is unused
  public static List<CastVoteRecord>
      ballotsToAudit(final CountyDashboard countyDashboard,
                     final int roundNumber,
                     final boolean includeAudited) {
    final Round round;

    try {
      // roundNumber is 1-based
      round = countyDashboard.rounds().get(roundNumber - 1);
    } catch (IndexOutOfBoundsException e) {
      return new ArrayList<CastVoteRecord>();
    }

    LOGGER.info(String.format("Ballot cards to audit: "
                              + "[round=%s, round.ballotSequence.size() = %d,"
                              + " round.ballotSequence() = %s]",
                              round, round.ballotSequence().size(),
                              round.ballotSequence()));

    // we already have the list of CVR IDs for the round
    final List<CastVoteRecord> cvrs = CastVoteRecordQueries.get(round.ballotSequence());

    // PERF: Is this a hotspot? We can figure out the audit flag using a single
    // query.
    for (final CastVoteRecord cvr : cvrs) {
      cvr.setAuditFlag(audited(countyDashboard, cvr));
    }

    return cvrs;
  }

  /**
   * @param the_cdb The dashboard.
   * @return true if an audit round is started for the dashboard, false otherwise;
   * an audit round might not be started if there are no driving contests in the
   * county, or if the county needs to audit 0 ballots to meet the risk limit.
   */
  public static ComparisonAudit createAudit(final ContestResult contestResult,
                                            final BigDecimal riskLimit) {
    final ComparisonAudit ca =
      new ComparisonAudit(contestResult, riskLimit, contestResult.getDilutedMargin(),
                          Audit.GAMMA, contestResult.getAuditReason());
    Persistence.save(ca);
    LOGGER.debug(String.format("[createAudit: contestResult=%s, ComparisonAudit=%s]",
                               contestResult, ca));
    return ca;
  }

  /**
   * Do the part of setup for a county dashboard to start their round.
   * - updateRound
   * - updateCVRUnderAudit
   */
  public static boolean startRound(final CountyDashboard cdb,
                                   final Set<ComparisonAudit> audits,
                                   final List<Long> auditSequence,
                                   final List<Long> ballotSequence) {
    LOGGER.info(String.format("Starting a round for %s, drivingContests=%s",
                              cdb.county(), cdb.drivingContestNames()));
    cdb.startRound(ballotSequence.size(), auditSequence.size(),
                   0, ballotSequence, auditSequence);
    // FIXME it appears these two must happen in this order.
    updateRound(cdb, cdb.currentRound());
    updateCVRUnderAudit(cdb);

    // if the round was started there will be ballots to count
    return cdb.ballotsRemainingInCurrentRound() > 0;
  }

  /**
   * Submit an audit CVR for a CVR under audit to the specified county dashboard.
   *
   * @param cdb The dashboard.
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The corresponding audit CVR.
   * @return true if the audit CVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit, or the specified CVR under audit was
   * not in fact under audit.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
  public static boolean submitAuditCVR(final CountyDashboard cdb,
                                       final CastVoteRecord the_cvr_under_audit,
                                       final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;

    final CVRAuditInfo info =
        Persistence.getByID(the_cvr_under_audit.id(), CVRAuditInfo.class);

    if (info == null) {
      LOGGER.warn("attempt to submit ACVR for county " +
                  cdb.id() + ", cvr " +
                  the_cvr_under_audit.id() + " not under audit");
    } else if (checkACVRSanity(the_cvr_under_audit, the_audit_cvr)) {
      LOGGER.trace("[submitAuditCVR: ACVR seems sane]");
      // if the record is the current CVR under audit, or if it hasn't been
      // audited yet, we can just process it
      if (info.acvr() == null) {
        // this audits all instances of the ballot in our current sequence;
        // they might be out of order, but that's OK because we have strong
        // requirements about finishing rounds before looking at results as
        // final and valid
        LOGGER.trace("[submitAuditCVR: ACVR is null, creating]");
        info.setACVR(the_audit_cvr);
        final int new_count = audit(cdb, info, true);
        cdb.addAuditedBallot();
        cdb.setAuditedSampleCount(cdb.auditedSampleCount() + new_count);
      } else {
        // the record has been audited before, so we need to "unaudit" it
        LOGGER.trace("[submitAuditCVR: ACVR is seen, un/reauditing]");
        final int former_count = unaudit(cdb, info);
        info.setACVR(the_audit_cvr);
        final int new_count = audit(cdb, info, true);
        cdb.setAuditedSampleCount(cdb.auditedSampleCount() - former_count + new_count);
      }
      result = true;
    }  else {
      LOGGER.warn("attempt to submit non-corresponding ACVR " +
                  the_audit_cvr.id() + " for county " + cdb.id() +
                  ", cvr " + the_cvr_under_audit.id());
    }
    Persistence.flush();

    LOGGER.trace(String.format("[Before recalc: auditedSampleCount=%d, estimatedSamples=%d, optimisticSamples=%d",
                              cdb.auditedSampleCount(),
                              cdb.estimatedSamplesToAudit(),
                              cdb.optimisticSamplesToAudit()));
    updateCVRUnderAudit(cdb);
    LOGGER.trace(String.format("[After recalc: auditedSampleCount=%d, estimatedSamples=%d, optimisticSamples=%d",
                              cdb.auditedSampleCount(),
                              cdb.estimatedSamplesToAudit(),
                              cdb.optimisticSamplesToAudit()));
    cdb.updateAuditStatus();
    return result;
  }

  /**
   * Computes the estimated total number of samples to audit on the specified
   * county dashboard. This uses the minimum samples to audit calculation,
   * increased by the percentage of discrepancies seen in the audited ballots
   * so far.
   *
   * @param cdb The dashboard.
   */
  public static int estimatedSamplesToAudit(final CountyDashboard cdb) {
    int to_audit = Integer.MIN_VALUE;
    final Set<String> drivingContests = cdb.drivingContestNames();

    // FIXME might look better as a stream().filter().
    for (final ComparisonAudit ca : cdb.comparisonAudits()) { // to_audit = cdb.comparisonAudits.stream()
      final String contestName = ca.contestResult().getContestName(); // strike
      if (drivingContests.contains(contestName)) { // .filter(ca -> drivingContests.contains(ca.contestResult().getContestName()))
        final int bta = ca.estimatedSamplesToAudit(); // .map(ComparisonAudit::estimatedSamplesToAudit)
        to_audit = Math.max(to_audit, bta);           // .max() gets the biggest of all driving contest estimated samples
        LOGGER.debug(String.format("[estimatedSamplesToAudit: "
                                   + "driving contest=%s, bta=%d, to_audit=%d]",
                                   ca.contestResult().getContestName(), bta, to_audit));
      }
    }
    return Math.max(0, to_audit);
  }

  /**
   * Checks to see if the specified CVR has been audited on the specified county
   * dashboard. This check sets the audit flag on the CVR record in memory,
   * so its result can be accessed later without an expensive database hit.
   *
   * @param the_cdb The county dashboard.
   * @param the_cvr The CVR.
   * @return true if the specified CVR has been audited, false otherwise.
   */
  public static boolean audited(final CountyDashboard the_cdb,
                                final CastVoteRecord the_cvr) {
    final CVRAuditInfo info = Persistence.getByID(the_cvr.id(), CVRAuditInfo.class);
    final boolean result;
    if (info == null || info.acvr() == null) {
      result = false;
    } else {
      result = true;
    }
    return result;
  }

  /**
   * Updates a round object with the disagreements and discrepancies
   * that already exist for CVRs in its audit subsequence, creates
   * any CVRAuditInfo objects that don't exist but need to, and
   * increases the multiplicity of any CVRAuditInfo objects that already
   * exist and are duplicated in this round.
   *
   * @param cdb The county dashboard to update.
   * @param round The round to update.
   */
  // FIXME should be private
  public static void updateRound(final CountyDashboard cdb,
                                 final Round round) {
    for (final Long cvrID : new HashSet<>(round.auditSubsequence())) {
      final Map<String, AuditReason> auditReasons = new HashMap<>();
      final Set<AuditReason> discrepancies = new HashSet<>();
      final Set<AuditReason> disagreements = new HashSet<>();
      CVRAuditInfo cvrai = Persistence.getByID(cvrID, CVRAuditInfo.class);

      if (cvrai == null) {
        // create it if it doesn't exist
        cvrai = new CVRAuditInfo(Persistence.getByID(cvrID, CastVoteRecord.class));
        cvrai.setMultiplicity(Collections.frequency(round.auditSubsequence(), cvrID));
        Persistence.saveOrUpdate(cvrai);
      } else if (cvrai.acvr() != null) {
        // update the round statistics as necessary

        for (final ComparisonAudit ca : cdb.comparisonAudits()) {
          auditReasons.put(ca.contestResult().getContestName(), ca.auditReason());
          if (!discrepancies.contains(ca.auditReason()) &&
              ca.computeDiscrepancy(cvrai.cvr(), cvrai.acvr()).isPresent()) {
            discrepancies.add(ca.auditReason());
          }
        }

        for (final CVRContestInfo ci : cvrai.acvr().contestInfo()) {
          final AuditReason reason = auditReasons.get(ci.contest().name());
          if (ci.consensus() == ConsensusValue.NO) {
            disagreements.add(reason);
          }
        }

        final int multiplicity = Collections.frequency(round.auditSubsequence(),
                                                       cvrID);
        for (int i = 0; i < multiplicity; i++) {
          round.addDiscrepancy(discrepancies);
          round.addDisagreement(disagreements);
        }

        cvrai.setMultiplicity(cvrai.multiplicity() + multiplicity);
      }
    }
  }

  /**
   * Audits a CVR/ACVR pair by adding it to all the audits in progress.
   * This also updates the local audit counters, as appropriate.
   *
   * @param cdb The dashboard.
   * @param auditInfo The CVRAuditInfo to audit.
   * @param updateCounters true to update the county dashboard
   * counters, false otherwise; false is used when this ballot
   * has already been audited once.
   * @return the number of times the record was audited.
   */
  @SuppressWarnings({"PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
      "PMD.NPathComplexity"})
  private static int audit(final CountyDashboard cdb,
                           final CVRAuditInfo auditInfo,
                           final boolean updateCounters) {
    final Set<String> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = auditInfo.cvr();
    final CastVoteRecord audit_cvr = auditInfo.acvr();

    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest().name());
      }
    }

    final int audit_count = auditInfo.multiplicity() - auditInfo.counted();

    for (final ComparisonAudit ca : cdb.comparisonAudits()) {

      final OptionalInt discrepancy = ca.computeDiscrepancy(cvr_under_audit, audit_cvr);

      if (discrepancy.isPresent()) {
        for (int i = 0; i < audit_count; i++) {
          ca.recordDiscrepancy(auditInfo, discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }

      // NOTE: this may or may not be correct, we're not sure
      if (contest_disagreements.contains(ca.contestResult().getContestName())) {
        for (int i = 0; i < audit_count; i++) {
          ca.recordDisagreement(auditInfo);
        }
        disagreements.add(ca.auditReason());
      }

      ca.signalSampleAudited(audit_count, cvr_under_audit.id());
      Persistence.saveOrUpdate(ca);
    }

    auditInfo.setDiscrepancy(discrepancies);
    auditInfo.setDisagreement(disagreements);
    auditInfo.setCounted(auditInfo.multiplicity());
    Persistence.saveOrUpdate(auditInfo);

    if (updateCounters) {
      cdb.addDiscrepancy(discrepancies);
      cdb.addDisagreement(disagreements);
    }

    return audit_count;
  }

  /**
   * "Unaudits" a CVR/ACVR pair by removing it from all the audits in
   * progress in the specified county dashboard. This also updates the
   * dashboard's counters as appropriate.
   *
   * @param the_cdb The county dashboard.
   * @param the_info The CVRAuditInfo to unaudit.
   */
  @SuppressWarnings("PMD.NPathComplexity")
  private static int unaudit(final CountyDashboard the_cdb,
                             final CVRAuditInfo the_info) {
    final Set<String> contest_disagreements = new HashSet<>();
    final Set<AuditReason> discrepancies = new HashSet<>();
    final Set<AuditReason> disagreements = new HashSet<>();
    final CastVoteRecord cvr_under_audit = the_info.cvr();
    final CastVoteRecord audit_cvr = the_info.acvr();
    final int result = the_info.counted();

    for (final CVRContestInfo ci : audit_cvr.contestInfo()) {
      if (ci.consensus() == ConsensusValue.NO) {
        contest_disagreements.add(ci.contest().name());
      }
    }

    for (final ComparisonAudit ca : the_cdb.comparisonAudits()) {
      final OptionalInt discrepancy =
          ca.computeDiscrepancy(cvr_under_audit, audit_cvr);
      if (discrepancy.isPresent()) {
        for (int i = 0; i < result; i++) {
          ca.removeDiscrepancy(the_info, discrepancy.getAsInt());
        }
        discrepancies.add(ca.auditReason());
      }
      if (contest_disagreements.contains(ca.contestResult().getContestName())) {
        for (int i = 0; i < result; i++) {
          ca.removeDisagreement(the_info);
        }
        disagreements.add(ca.auditReason());
      }
      ca.signalSampleUnaudited(result, cvr_under_audit.id());
      Persistence.saveOrUpdate(ca);
    }

    the_info.setDisagreement(null);
    the_info.setDiscrepancy(null);
    the_info.setCounted(0);
    Persistence.saveOrUpdate(the_info);

    the_cdb.removeDiscrepancy(discrepancies);
    the_cdb.removeDisagreement(disagreements);

    return result;
  }

  /**
   * Updates the current CVR to audit index of the specified county
   * dashboard to the first CVR after the current CVR under audit that
   * lacks an ACVR. This "audits" all the CVR/ACVR pairs it finds
   * in between, and extends the sequence of ballots to audit if it
   * reaches the end and the audit is not concluded.
   *
   * @param cdb The dashboard.
   */
  public static void updateCVRUnderAudit(final CountyDashboard cdb) {
    // start from where we are in the current round
    final Round round = cdb.currentRound();

    if (round != null) {
      final Set<Long> checked_ids = new HashSet<>();
      int index = round.actualAuditedPrefixLength() - round.startAuditedPrefixLength();

      while (index < round.auditSubsequence().size()) {
        final Long cvr_id = round.auditSubsequence().get(index);
        if (!checked_ids.contains(cvr_id)) {
          checked_ids.add(cvr_id);

          final CVRAuditInfo cai = Persistence.getByID(cvr_id, CVRAuditInfo.class);

          if (cai.acvr() == null) {
            break;              // ok, so this hasn't been audited yet.
          } else {
            final int audit_count = audit(cdb, cai, false);
            cdb.setAuditedSampleCount(cdb.auditedSampleCount() + audit_count);
          }
        }
        index = index + 1;
      }
      // FIXME audited prefix length might not mean the same things that
      // it once meant.
      cdb.setAuditedPrefixLength(index + round.startAuditedPrefixLength());
    }
  }

  /**
   * Checks that the specified CVR and ACVR are an audit pair, and that
   * the specified ACVR is auditor generated.
   *
   * @param the_cvr The CVR.
   * @param the_acvr The ACVR.
   */
  private static boolean checkACVRSanity(final CastVoteRecord the_cvr,
                                         final CastVoteRecord the_acvr) {
    return the_cvr.isAuditPairWith(the_acvr) &&
      (the_acvr.recordType().isAuditorGenerated()
       || the_acvr.recordType().isSystemGenerated())
      ;
  }
}
