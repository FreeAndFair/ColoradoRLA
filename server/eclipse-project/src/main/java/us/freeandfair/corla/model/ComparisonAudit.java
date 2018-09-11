/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 19, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import us.freeandfair.corla.math.Audit;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A class representing the state of a single audited contest for
 * across multiple counties
 *
 */
@Entity
@Cacheable(true)
@Table(name = "comparison_audit")

public class ComparisonAudit implements PersistentEntity {
  /**
   * The database stored precision for decimal types.
   */
  public static final int PRECISION = 10;

  /**
   * The database stored scale for decimal types.
   */
  public static final int SCALE = 8;

  @ManyToMany()
  @JoinTable(name = "counties_to_comparison_audits",
             joinColumns = { @JoinColumn(name = "comparison_audit_id") },
             inverseJoinColumns = { @JoinColumn(name = "county_dashboard_id") })
  private final Set<CountyDashboard> countyDashboards = new HashSet<>();

  /**
   * set the set of counties
   */
  public boolean addCountyDashboards(final Set<CountyDashboard> cs) {
    return this.countyDashboards.addAll(cs);
  }

  /**
   * @return the counties related to this contestresult.
   */
  public Set<CountyDashboard> getCountyDashboards() {
    return Collections.unmodifiableSet(this.countyDashboards);
  }

  /**
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;

  /**
   * The contest result for this audit state.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private ContestResult my_contest_result;

  /**
   * The reason for this audit.
   */
  @Column(updatable = false, nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditReason my_audit_reason;

  /**
   * The status of this audit.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditStatus my_audit_status = AuditStatus.NOT_STARTED;

  /**
   * The gamma.
   */
  @Column(updatable = false, nullable = false,
          precision = PRECISION, scale = SCALE)
          private BigDecimal my_gamma = Audit.GAMMA;

  /**
   * The diluted margin
   */
  @Column(updatable = false, nullable = false,
          precision = PRECISION, scale = SCALE)
          private BigDecimal diluted_margin = BigDecimal.ONE;

  /**
   * The risk limit.
   */
  @Column(updatable = false, nullable = false,
          precision = PRECISION, scale = SCALE)
          private BigDecimal my_risk_limit = BigDecimal.ONE;

  /**
   * The number of samples audited.
   */
  @Column(nullable = false)
  private Integer my_audited_sample_count = 0;

  /**
   * The number of samples to audit overall assuming no further overstatements.
   */
  @Column(nullable = false)
  private Integer my_optimistic_samples_to_audit = 0;

  /**
   * The expected number of samples to audit overall assuming overstatements
   * continue at the current rate.
   */
  @Column(nullable = false)
  private Integer my_estimated_samples_to_audit = 0;

  /**
   * The number of two-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_under_count = 0;

  /**
   * The number of one-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_under_count = 0;

  /**
   * The number of one-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_over_count = 0;

  /**
   * The number of two-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_over_count = 0;

  /**
   * The number of discrepancies recorded so far that are neither
   * understatements nor overstatements.
   */
  @Column(nullable = false)
  private Integer my_other_count = 0;

  /**
   * The number of disagreements.
   */
  @Column(nullable = false)
  private Integer my_disagreement_count = 0;

  /**
   * A flag that indicates whether the optimistic ballots to audit
   * estimate needs to be recalculated.
   */
  @Column(nullable = false)
  private Boolean my_optimistic_recalculate_needed = true;

  /**
   * A flag that indicates whether the non-optimistic ballots to
   * audit estimate needs to be recalculated
   */
  @Column(nullable = false)
  private Boolean my_estimated_recalculate_needed = true;

  /**
   * A map from CVRAuditInfo objects to their discrepancy values for this
   * audited contest.
   */
  @ElementCollection
  @CollectionTable(name = "contest_comparison_audit_discrepancy",
                   joinColumns = @JoinColumn(name = "contest_comparison_audit_id",
                                             referencedColumnName = "my_id"))
  @MapKeyJoinColumn(name = "cvr_audit_info_id")
  @Column(name = "discrepancy")
  private Map<CVRAuditInfo, Integer> my_discrepancies = new HashMap<>();

  /**
   * A map from CVRAuditInfo objects to their discrepancy values for this
   * audited contest.
   */
  @ManyToMany
  @JoinTable(name = "contest_comparison_audit_disagreement",
             joinColumns = @JoinColumn(name = "contest_comparison_audit_id",
                                       referencedColumnName = "my_id"),
             inverseJoinColumns = @JoinColumn(name = "cvr_audit_info_id",
                                              referencedColumnName = "my_id"))
  private Set<CVRAuditInfo> my_disagreements = new HashSet<>();

  /**
   * Constructs a new, empty ComparisonAudit (solely for persistence).
   */
  public ComparisonAudit() {
    super();
  }

  /**
   * Constructs a ComparisonAudit for the given params
   *
   * @param contestResult The contest result.
   * @param riskLimit The risk limit.
   * @param dilutedMargin moo
   * @param gamma γ
   * @param auditReason The audit reason.
   */
  public ComparisonAudit(final ContestResult contestResult,
                         final BigDecimal riskLimit,
                         final BigDecimal dilutedMargin,
                         final BigDecimal gamma,
                         final AuditReason auditReason) {
    super();
    my_contest_result = contestResult;
    my_risk_limit = riskLimit;
    this.diluted_margin = dilutedMargin;
    my_gamma = gamma;
    my_audit_reason = auditReason;

    if (contestResult.getDilutedMargin().equals(BigDecimal.ZERO)) {
      // the diluted margin is 0, so this contest is not auditable
      my_audit_status = AuditStatus.NOT_AUDITABLE;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
  }

  /**
   * @return the contest result associated with this audit.
   */
  public ContestResult contestResult() {
    return my_contest_result;
  }

  /**
   * @return the gamma associated with this audit.
   */
  public BigDecimal getGamma() {
    return my_gamma;
  }

  /**
   * @return the risk limit associated with this audit.
   */
  public BigDecimal getRiskLimit() {
    return my_risk_limit;
  }

  /**
   * @return the risk limit associated with this audit.
   */
  public BigDecimal getDilutedMargin() {
    return this.diluted_margin;
  }

  /**
   * @return the audit reason associated with this audit.
   */
  public AuditReason auditReason() {
    return my_audit_reason;
  }

  /**
   * @return the audit status associated with this audit.
   */
  public AuditStatus auditStatus() {
    return my_audit_status;
  }

  /**
   * Updates the audit status based on the current risk limit. If the audit
   * has already been ended or the contest is not auditable, this method has
   * no effect on its status.
   */
  public void updateAuditStatus() {
    if (my_audit_status == AuditStatus.ENDED ||
        my_audit_status == AuditStatus.NOT_AUDITABLE) {
      return;
    }

    if (my_optimistic_samples_to_audit - my_audited_sample_count <= 0) {
      my_audit_status = AuditStatus.RISK_LIMIT_ACHIEVED;
    } else {
      // risk limit has not been achieved
      // note that it _is_ possible to go from RISK_LIMIT_ACHIEVED to
      // IN_PROGRESS if a sample or set of samples is "unaudited"
      my_audit_status = AuditStatus.IN_PROGRESS;
    }
  }

  /**
   * Ends this audit; if the audit has already reached its risk limit,
   * or the contest is not auditable, this call has no effect on its status.
   */
  public void endAudit() {
    if (my_audit_status != AuditStatus.RISK_LIMIT_ACHIEVED &&
        my_audit_status != AuditStatus.NOT_AUDITABLE) {
      my_audit_status = AuditStatus.ENDED;
    }
  }

  /**
   * @return the initial expected number of samples to audit.
   */
  @SuppressWarnings({"checkstyle:magicnumber", "PMD.AvoidDuplicateLiterals"})
  public int initialSamplesToAudit() {
    return computeOptimisticSamplesToAudit(0, 0, 0, 0).
      setScale(0, RoundingMode.CEILING).intValue();
  }

  /**
   * @return the expected overall number of ballots to audit, assuming no
   * further overstatements occur.
   */
  public Integer optimisticSamplesToAudit() {
    if (my_optimistic_recalculate_needed) {
      recalculateSamplesToAudit();
    }

    return my_optimistic_samples_to_audit;
  }

  /**
   * @return the expected overall number of ballots to audit, assuming
   * overstatements continue to occur at the current rate.
   */
  public Integer estimatedSamplesToAudit() {
    if (my_estimated_recalculate_needed) {
      recalculateSamplesToAudit();
    }
    return my_estimated_samples_to_audit;
  }


  /**
   * FIXME Does the wrong thing
   * TODO Do the right thing
   *
   * FIXME We have to figure out how to keep track of the audit
   * subsequence for a statewide contest. Right now, everything about
   * the auditedPrefixLength is compartmentalized in a CountyDashboard.
   * The dashboard has a subsequence from when we sliced up a larger
   * sequence of CVR ids by county, but we need to find a way to
   * replicate that view at a higher level.
   */
  public Integer auditedPrefixLength() {
    return 0;
  }

  public BigDecimal auditedSamples() {
    // FIXME Originally, we were doing a county level audit. Now, we
    // need to think about many counties, so we need to find out how
    // many samples we've audited across all of the counties that
    // aprticipate.

    // return BigDecimal.valueOf(my_dashboard.auditedSampleCount());
    return BigDecimal.ZERO;
  }

  public BigDecimal overstatements() {
    // FIXME collect the number of 1&2 vote overstatements across
    // participating counties.

    // return BigDecimal.valueOf(my_one_vote_over_count + my_two_vote_over_count);
    return BigDecimal.ZERO;
  }

  /**
   * Recalculates the overall numbers of ballots to audit.
   */
  private void recalculateSamplesToAudit() {
    if (my_optimistic_recalculate_needed) {
      final BigDecimal optimistic = computeOptimisticSamplesToAudit(my_two_vote_under_count,
                                                                    my_one_vote_under_count,
                                                                    my_one_vote_over_count,
                                                                    my_two_vote_over_count);
      my_optimistic_samples_to_audit = optimistic.intValue();
      my_optimistic_recalculate_needed = false;
    }

    if (my_one_vote_over_count + my_two_vote_over_count == 0) {
      my_estimated_samples_to_audit = my_optimistic_samples_to_audit;
    } else {
      // compute the "fudge factor" for the estimate
      // FIXME extract-fn:
      // fudge_factor = ComparisonAuditController.scalingFactor(auditedSamples(), overstatements());
      final BigDecimal audited_samples = auditedSamples();
      final BigDecimal overstatements = overstatements();
      final BigDecimal fudge_factor;

      if (audited_samples.equals(BigDecimal.ZERO)) {
        fudge_factor = BigDecimal.ONE;
      } else {
        fudge_factor =
          BigDecimal.ONE.add(overstatements.divide(audited_samples, MathContext.DECIMAL128));
      }
      // extract-fn

      final BigDecimal estimated =
        BigDecimal.valueOf(my_optimistic_samples_to_audit).multiply(fudge_factor);
      my_estimated_samples_to_audit = estimated.setScale(0, RoundingMode.CEILING).intValue();
    }
    my_estimated_recalculate_needed = false;
  }

  /**
   * Computes the expected number of ballots to audit overall given the
   * specified numbers of over- and understatements.
   *
   * @param the_two_under The two-vote understatements.
   * @param the_one_under The one-vote understatements.
   * @param the_one_over The one-vote overstatements.
   * @param the_two_over The two-vote overstatements.
   *
   * @return the expected number of ballots remaining to audit.
   * This is the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  private BigDecimal computeOptimisticSamplesToAudit(final int twoUnder,
                                                     final int oneUnder,
                                                     final int oneOver,
                                                     final int twoOver) {
    return Audit.optimistic(getRiskLimit(), getDilutedMargin(), getGamma(),
                            twoUnder, oneUnder, oneOver, twoOver);
  }

  /**
   * Signals that a sample has been audited. This ensures that estimates
   * are recalculated correctly and states are updated.
   *
   * @param the_count The count of samples that have been audited simultaneously
   * (for duplicates).
   */
  public void signalSampleAudited(final int the_count) {
    my_estimated_recalculate_needed = true;
    my_audited_sample_count = my_audited_sample_count + the_count;

    if (my_audit_status != AuditStatus.ENDED &&
        my_audit_status != AuditStatus.NOT_AUDITABLE) {
      my_audit_status = AuditStatus.IN_PROGRESS;
    }
  }

  /**
   * Signals that a sample has been unaudited. This ensures that estimates
   * are recalculated correctly and states are updated.
   *
   * @param the_count The count of samples that have been unaudited simultaneously
   * (for duplicates).
   */
  public void signalSampleUnaudited(final int the_count) {
    my_estimated_recalculate_needed = true;
    my_audited_sample_count = my_audited_sample_count - the_count;

    if (my_audit_status != AuditStatus.ENDED &&
        my_audit_status != AuditStatus.NOT_AUDITABLE) {
      my_audit_status = AuditStatus.IN_PROGRESS;
    }
  }

  /**
   * Records a disagreement with the specified CVRAuditInfo.
   *
   * @param the_record The CVRAuditInfo record that generated the disagreement.
   */
  public void recordDisagreement(final CVRAuditInfo the_record) {
    my_disagreements.add(the_record);
    my_disagreement_count = my_disagreement_count + 1;
  }

  /**
   * Removes a disagreement with the specified CVRAuditInfo.
   *
   * @param the_record The CVRAuditInfo record that generated the disagreement.
   */
  public void removeDisagreement(final CVRAuditInfo the_record) {
    my_disagreements.remove(the_record);
    my_disagreement_count = my_disagreement_count - 1;
  }

  /**
   * @return the disagreement count.
   */
  public int disagreementCount() {
    return my_disagreement_count;
  }

  /**
   * Records the specified discrepancy (the valid range is -2 .. 2: -2 and -1 are
   * understatements, 0 is a discrepancy that doesn't affect the RLA calculations,
   * and 1 and 2 are overstatements).
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @param the_type The type of discrepancy to add.
   * @exception IllegalArgumentException if an invalid discrepancy type is
   * specified.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public void recordDiscrepancy(final CVRAuditInfo the_record,
                                final int the_type) {
    // we never trigger an estimated recalculate here; it is
    // triggered by signalBallotAudited() regardless of whether there is
    // a discrepancy or not
    switch (the_type) {
    case -2:
      my_two_vote_under_count = my_two_vote_under_count + 1;
      my_optimistic_recalculate_needed = true;
      break;

    case -1:
      my_one_vote_under_count = my_one_vote_under_count + 1;
      my_optimistic_recalculate_needed = true;
      break;

    case 0:
      my_other_count = my_other_count + 1;
      // no optimistic recalculate needed
      break;

    case 1:
      my_one_vote_over_count = my_one_vote_over_count + 1;
      my_optimistic_recalculate_needed = true;
      break;

    case 2:
      my_two_vote_over_count = my_two_vote_over_count + 1;
      my_optimistic_recalculate_needed = true;
      break;

    default:
      throw new IllegalArgumentException("invalid discrepancy type: " + the_type);
    }

    my_discrepancies.put(the_record, the_type);
  }

  /**
   * Removes the specified over/understatement (the valid range is -2 .. 2:
   * -2 and -1 are understatements, 0 is a discrepancy that doesn't affect the
   * RLA calculations, and 1 and 2 are overstatements). This is typically done
   * when a new interpretation is submitted for a ballot that had already been
   * interpreted.
   *
   * @param the_record The CVRAuditInfo record that generated the discrepancy.
   * @param the_type The type of discrepancy to remove.
   * @exception IllegalArgumentException if an invalid discrepancy type is
   * specified.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public void removeDiscrepancy(final CVRAuditInfo the_record, final int the_type) {
    // we never trigger an estimated recalculate here; it is
    // triggered by signalBallotAudited() regardless of whether there is
    // a discrepancy or not
    switch (the_type) {
    case -2:
      my_two_vote_under_count = my_two_vote_under_count - 1;
      my_optimistic_recalculate_needed = true;
      break;

    case -1:
      my_one_vote_under_count = my_one_vote_under_count - 1;
      my_optimistic_recalculate_needed = true;
      break;

    case 0:
      my_other_count = my_other_count - 1;
      // no recalculate needed
      break;

    case 1:
      my_one_vote_over_count = my_one_vote_over_count - 1;
      my_optimistic_recalculate_needed = true;
      break;

    case 2:
      my_two_vote_over_count = my_two_vote_over_count - 1;
      my_optimistic_recalculate_needed = true;
      break;

    default:
      throw new IllegalArgumentException("invalid discrepancy type: " + the_type);
    }

    my_discrepancies.remove(the_record);
  }

  /**
   * Returns the count of the specified type of discrepancy. -2 and -1 represent
   * understatements, 0 represents a discrepancy that doesn't affect the RLA
   * calculations, and 1 and 2 represent overstatements.
   *
   * @param the_type The type of discrepancy.
   * @exception IllegalArgumentException if an invalid discrepancy type is
   * specified.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public int discrepancyCount(final int the_type) {
    final int result;

    switch (the_type) {
    case -2:
      result = my_two_vote_under_count;
      break;

    case -1:
      result = my_one_vote_under_count;
      break;

    case 0:
      result = my_other_count;
      break;

    case 1:
      result = my_one_vote_over_count;
      break;

    case 2:
      result = my_two_vote_over_count;
      break;

    default:
      throw new IllegalArgumentException("invalid discrepancy type: " + the_type);
    }

    return result;
  }

  /**
   * Computes the over/understatement represented by the CVR/ACVR pair stored in
   * the specified CVRAuditInfo. This method returns an optional int that, if
   * present, indicates a discrepancy. There are 5 possible types of
   * discrepancy: -1 and -2 indicate 1- and 2-vote understatements; 1 and 2
   * indicate 1- and 2- vote overstatements; and 0 indicates a discrepancy that
   * does not count as either an under- or overstatement for the RLA algorithm,
   * but nonetheless indicates a difference between ballot interpretations.
   *
   * @param the_info The CVRAuditInfo.
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  public OptionalInt computeDiscrepancy(final CVRAuditInfo the_info) {
    if (the_info.acvr() == null || the_info.cvr() == null) {
      throw new IllegalArgumentException("null CVR or ACVR in pair " + the_info);
    } else {
      return computeDiscrepancy(the_info.cvr(), the_info.acvr());
    }
  }

  /**
   * Computes the over/understatement represented by the specified CVR and ACVR.
   * This method returns an optional int that, if present, indicates a discrepancy.
   * There are 5 possible types of discrepancy: -1 and -2 indicate 1- and 2-vote
   * understatements; 1 and 2 indicate 1- and 2- vote overstatements; and 0
   * indicates a discrepancy that does not count as either an under- or
   * overstatement for the RLA algorithm, but nonetheless indicates a difference
   * between ballot interpretations.
   *
   * @param cvr The CVR that the machine saw
   * @param auditedCVR The ACVR that the human audit board saw
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  // FIXME Should we point to the ContestResult instead?
  public OptionalInt computeDiscrepancy(final CastVoteRecord cvr,
                                        final CastVoteRecord auditedCVR) {
    OptionalInt result = OptionalInt.empty();

    // FIXME this needs to get this stuff from the ContestResult
    // - a CastVoteRecord belongs to a county.
    // - a CVRContestInfo belongs to a Contest, which belongs to a county.
    // - should we change the CVRContestInfo to belong to a ContestResult instead?
    //
    // The CVRContestInfo has teh list of choices. we need this for
    // winners and loser of the contest......BUT the ContestResult also
    // has a set of winners and losers, which is now the MOST ACCURATE
    // version of this, since we're now out of the county context...
    final Optional<CVRContestInfo> cvr_info = cvr.contestInfoForContestResult(my_contest_result);
    final Optional<CVRContestInfo> acvr_info = auditedCVR.contestInfoForContestResult(my_contest_result);

    if (auditedCVR.recordType() == RecordType.PHANTOM_BALLOT) {
      // FIXME I think we can pass the set of ContestResult
      // winners/losers to computePhantomBallotDiscrepancy
      result = OptionalInt.of(computePhantomBallotDiscrepancy(cvr_info, my_contest_result));
    } else if (cvr_info.isPresent() && acvr_info.isPresent()) {
      if (acvr_info.get().consensus() == ConsensusValue.NO) {
        // a lack of consensus for this contest is treated
        // identically to a phantom ballot
        result = OptionalInt.of(computePhantomBallotDiscrepancy(cvr_info, my_contest_result));
      } else {
        result = computeAuditedBallotDiscrepancy(cvr_info.get(), acvr_info.get());
      }
    }

    return result;
  }

  /**
   * Computes the discrepancy between two ballots. This method returns an optional
   * int that, if present, indicates a discrepancy. There are 5 possible types of
   * discrepancy: -1 and -2 indicate 1- and 2-vote understatements; 1 and 2 indicate
   * 1- and 2- vote overstatements; and 0 indicates a discrepancy that does not
   * count as either an under- or overstatement for the RLA algorithm, but
   * nonetheless indicates a difference between ballot interpretations.
   *
   * @param the_cvr_info The CVR info.
   * @param the_acvr_info The ACVR info.
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  @SuppressWarnings({"PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity",
        "PMD.NPathComplexity", "PMD.ExcessiveMethodLength",
        "checkstyle:methodlength"})

  private OptionalInt computeAuditedBallotDiscrepancy(final CVRContestInfo the_cvr_info,
                                                      final CVRContestInfo the_acvr_info) {
    // check for overvotes
    final Set<String> acvr_choices = new HashSet<>();
    // TODO: validate choices().size()
    // if (the_acvr_info.choices().size() <= my_contest_result.votesAllowed()) {
    acvr_choices.addAll(the_acvr_info.choices());
    // } // else overvote so don't count the votes

    // avoid linear searches on CVR choices
    final Set<String> cvr_choices = new HashSet<>(the_cvr_info.choices());

    // if the choices in the CVR and ACVR are identical now, we can simply return the
    // fact that there's no discrepancy
    if (cvr_choices.equals(acvr_choices)) {
      return OptionalInt.empty();
    }

    // we want to get the maximum pairwise update delta, because that's the "worst"
    // change in a pairwise margin, and the discrepancy we record; we start with
    // Integer.MIN_VALUE so our maximization algorithm works. it is also the case
    // that _every_ pairwise margin must be increased for an understatement to be
    // reported

    int raw_result = Integer.MIN_VALUE;

    boolean possible_understatement = true;
    // FIXME my_contest_result is global to this object. I'd rather it
    // be an argument to this function.
    for (final String winner : my_contest_result.getWinners()) {
      final int winner_change;
      if (!cvr_choices.contains(winner) && acvr_choices.contains(winner)) {
        // this winner gained a vote
        winner_change = 1;
      } else if (cvr_choices.contains(winner) && !acvr_choices.contains(winner)) {
        // this winner lost a vote
        winner_change = -1;
      } else {
        // this winner's votes didn't change
        winner_change = 0;
        }
      if (my_contest_result.getLosers().isEmpty()) {
        // if there are no losers, we'll just negate this number - even though in
        // real life, we wouldn't be auditing the contest at all
        raw_result = Math.max(raw_result, -winner_change);
      } else {
        for (final String loser : my_contest_result.getLosers()) {
          final int loser_change;
          if (!cvr_choices.contains(loser) && acvr_choices.contains(loser)) {
            // this loser gained a vote
            loser_change = 1;
          } else if (cvr_choices.contains(loser) && !acvr_choices.contains(loser)) {
            // this loser lost a vote
            loser_change = -1;
          } else {
            // this loser's votes didn't change
            loser_change = 0;
          }
          // the discrepancy is the loser change minus the winner change (i.e., if this
          // loser lost a vote (-1) and this winner gained a vote (1), that's a 2-vote
          // understatement (-1 - 1 = -2). Overstatements are worse than understatements,
          // as far as the audit is concerned, so we keep the highest discrepancy
          final int discrepancy = loser_change - winner_change;

          // taking the max here does not cause a loss of information even if the
          // discrepancy is 0; if the discrepancy is 0 we can no longer report an
          // understatement, and we still know there was a discrepancy because we
          // didn't short circuit earlier
          raw_result = Math.max(raw_result, discrepancy);

          // if this discrepancy indicates a narrowing of, or no change in, this pairwise
          // margin, then an understatement is no longer possible because that would require
          // widening _every_ pairwise margin
          if (discrepancy >= 0) {
            possible_understatement = false;
          }
        }
      }
    }

    if (raw_result == Integer.MIN_VALUE) {
      // this should only be possible if something went horribly wrong (like the contest
      // has no winners)
      throw new IllegalStateException("unable to compute discrepancy in contest " +
                                      contestResult().getContestName());
    }

    final OptionalInt result;

    if (possible_understatement) {
      // we return the raw result unmodified
      result = OptionalInt.of(raw_result);
    } else {
      // we return the raw result with a floor of 0, because we can't report an
      // understatement
      result = OptionalInt.of(Math.max(0, raw_result));
    }

    return result;
  }

  /**
   * Computes the discrepancy between a phantom ballot and the specified
   * CVRContestInfo.
   * @return The number of discrepancies
   */
  private Integer computePhantomBallotDiscrepancy(final Optional<CVRContestInfo> cvrInfo,
                                                  final ContestResult contestResult) {
    int result = 2;
    // the second predicate means "no contest winners had votes on the
    // original CVR"
    final Set<String> winner_votes = new HashSet<>(cvrInfo.get().choices());
    winner_votes.removeAll(contestResult.getLosers());
    if (winner_votes.isEmpty()) {
      result = 1;
    }
    return result;
  }

  /**
   * Computes the discrepancy between a phantom ballot and the specified
   * CVRContestInfo.
   *
   * @param the_info The CVRContestInfo.
   * @return the discrepancy.
   */
  private Integer computePhantomBallotDiscrepancy(final CVRContestInfo the_info) {
    final int result;

    // if the ACVR is a phantom ballot, we need to assume that it was a vote
    // for all the losers; so if any winners had votes on the original CVR
    // it's a 2-vote overstatement, otherwise a 1-vote overstatement

    if (the_info == null) {
      // this contest doesn't appear in the CVR, so we assume the worst
      // FIXME I think this is the same as "this CVR has no winners?"
      result = 2;
    } else {
      // this contest does appear in the CVR, so we can actually check
      final Set<String> winner_votes = new HashSet<>(the_info.choices());
      // FIXME pass my_contest_result as a function argument
      winner_votes.removeAll(my_contest_result.getLosers());
      if (winner_votes.isEmpty()) {
        result = 1;
      } else {
        result = 2;
      }
    }

    return result;
  }
}
