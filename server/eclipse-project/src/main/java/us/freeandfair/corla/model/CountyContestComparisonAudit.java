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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

import ch.obermuhlner.math.big.BigDecimalMath;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A class representing the state of a single audited contest for
 * a single county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Cacheable(true)
@Table(name = "county_contest_comparison_audit",
       indexes = { @Index(name = "idx_ccca_dashboard", columnList = "dashboard_id") })
@SuppressWarnings({"PMD.ImmutableField", "PMD.CyclomaticComplexity", "PMD.GodClass",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.TooManyFields",
    "PMD.TooManyMethods", "PMD.ExcessiveImports"})
// note: CountyContestComparisionAudit is not serializable because it references
// CountyDashboard, which is not serializable.
public class CountyContestComparisonAudit implements PersistentEntity {
  /**
   * The database stored precision for decimal types.
   */
  public static final int PRECISION = 10;
  
  /**
   * The database stored scale for decimal types.
   */
  public static final int SCALE = 8;
  
  /**
   * Gamma, as presented in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  public static final BigDecimal STARK_GAMMA = BigDecimal.valueOf(1.03905);
  
  /**
   * Gamma, as recommended by Neal McBurnett for use in Colorado.
   */
  public static final BigDecimal COLORADO_GAMMA = BigDecimal.valueOf(1.1);
  
  /**
   * Conservative estimate of error rates for one-vote over- and understatements.
   */
  public static final BigDecimal CONSERVATIVE_ONES_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * Conservative estimate of error rates for two-vote over- and understatements.
   */
  public static final BigDecimal CONSERVATIVE_TWOS_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * Conservative rounding up of 1-vote over/understatements for the initial 
   * estimate of error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_ONES_UP = true;
  
  /**
   * Conservative rounding up of 2-vote over/understatements for the initial 
   * estimate of  error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_TWOS_UP = true;
  
  /**
   * The gamma to use.
   */
  public static final BigDecimal GAMMA = STARK_GAMMA;
  
  /**
   * The initial estimate of error rates for one-vote over- and understatements.
   */
  public static final BigDecimal ONES_RATE = BigDecimal.ZERO;
  
  /**
   * The initial estimate of error rates for two-vote over- and understatements.
   */
  public static final BigDecimal TWOS_RATE = BigDecimal.ZERO;
  
  /**
   * The initial rounding up of 1-vote over/understatements.
   */
  public static final boolean ROUND_ONES_UP = false;
  
  /**
   * The initial rounding up of 2-vote over/understatements.
   */
  public static final boolean ROUND_TWOS_UP = false;
  
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
   * The county dashboard to which this audit state belongs. 
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private CountyDashboard my_dashboard;

  /**
   * The contest to which this audit state belongs.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private Contest my_contest;
  
  /**
   * The contest result for this audit state.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private CountyContestResult my_contest_result;
  
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
  private BigDecimal my_gamma = GAMMA;
  
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
  @CollectionTable(name = "county_contest_comparison_audit_discrepancy",
                   joinColumns = @JoinColumn(name = "county_contest_comparison_audit_id", 
                                             referencedColumnName = "my_id"))
  @MapKeyJoinColumn(name = "cvr_audit_info_id")
  @Column(name = "discrepancy")
  private Map<CVRAuditInfo, Integer> my_discrepancies = new HashMap<>();
  
  /**
   * A map from CVRAuditInfo objects to their discrepancy values for this
   * audited contest.
   */
  @ManyToMany
  @JoinTable(name = "county_contest_comparison_audit_disagreement",
             joinColumns = @JoinColumn(name = "county_contest_comparison_audit_id", 
                                       referencedColumnName = "my_id"),
             inverseJoinColumns = @JoinColumn(name = "cvr_audit_info_id", 
                                              referencedColumnName = "my_id"))
  private Set<CVRAuditInfo> my_disagreements = new HashSet<>();
  
  /**
   * Constructs a new, empty CountyContestAudit (solely for persistence).
   */
  public CountyContestComparisonAudit() {
    super();
  }
  
  /**
   * Constructs a CountyContestAudit for the specified dashboard, contest result,
   * risk limit, and audit reason.
   * 
   * @param the_dashboard The dashboard.
   * @param the_contest_result The contest result.
   * @param the_risk_limit The risk limit.
   * @param the_audit_reason The audit reason.
   */
  public CountyContestComparisonAudit(final CountyDashboard the_dashboard,
                                      final CountyContestResult the_contest_result,
                                      final BigDecimal the_risk_limit,
                                      final AuditReason the_audit_reason) {
    super();
    my_dashboard = the_dashboard;
    my_contest_result = the_contest_result;
    my_contest = my_contest_result.contest();
    my_risk_limit = the_risk_limit;
    my_audit_reason = the_audit_reason;
    if (the_contest_result.countyDilutedMargin().equals(BigDecimal.ZERO)) {
      // the county diluted margin is 0, so this contest is not auditable
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
   * @return the county dashboard associated with this audit.
   */
  public CountyDashboard dashboard() {
    return my_dashboard;
  }
  
  /**
   * @return the contest associated with this audit.
   */
  public Contest contest() {
    return my_contest;
  }
  
  /**
   * @return the contest result associated with this audit.
   */
  public CountyContestResult contestResult() {
    return my_contest_result;
  }
  
  /**
   * @return the gamma associated with this audit.
   */
  public BigDecimal gamma() {
    return my_gamma;
  }
  
  /**
   * @return the risk limit associated with this audit.
   */
  public BigDecimal riskLimit() {
    return my_risk_limit;
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
      final BigDecimal audited_samples = BigDecimal.valueOf(my_dashboard.auditedSampleCount());
      final BigDecimal overstatements = 
          BigDecimal.valueOf(my_one_vote_over_count + my_two_vote_over_count);
      final BigDecimal fudge_factor;
      if (audited_samples.equals(BigDecimal.ZERO)) {
        fudge_factor = BigDecimal.ONE;
      } else {
        fudge_factor =
            BigDecimal.ONE.add(overstatements.divide(audited_samples, MathContext.DECIMAL128));
      }
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
  @SuppressWarnings({"checkstyle:magicnumber", "PMD.AvoidDuplicateLiterals"})
  private BigDecimal computeOptimisticSamplesToAudit(final int the_two_under,
                                                     final int the_one_under,
                                                     final int the_one_over,
                                                     final int the_two_over) {
    final BigDecimal result;
    if (my_audit_status == AuditStatus.NOT_AUDITABLE) {
      // the contest is not auditable, so return the number of ballots in the county
      // (for lack of a better number)
      result = BigDecimal.valueOf(my_contest_result.countyBallotCount());
    } else {
      final BigDecimal invgamma = BigDecimal.ONE.divide(my_gamma, MathContext.DECIMAL128);
      final BigDecimal twogamma = BigDecimal.valueOf(2).multiply(my_gamma);
      final BigDecimal invtwogamma = 
          BigDecimal.ONE.divide(twogamma, MathContext.DECIMAL128);
      final BigDecimal two_under_bd = BigDecimal.valueOf(the_two_under);
      final BigDecimal one_under_bd = BigDecimal.valueOf(the_one_under);
      final BigDecimal one_over_bd = BigDecimal.valueOf(the_one_over);
      final BigDecimal two_over_bd = BigDecimal.valueOf(the_two_over);

      final BigDecimal over_under_sum = 
          two_under_bd.add(one_under_bd).add(one_over_bd).add(two_over_bd);
      final BigDecimal two_under = 
          two_under_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invgamma), 
                                                   MathContext.DECIMAL128));
      final BigDecimal one_under =
          one_under_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invtwogamma), 
                                                   MathContext.DECIMAL128));
      final BigDecimal one_over = 
          one_over_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invtwogamma), 
                                                  MathContext.DECIMAL128));
      final BigDecimal two_over =
          two_over_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invgamma),
                                                  MathContext.DECIMAL128));
      final BigDecimal numerator =
          twogamma.negate().
          multiply(BigDecimalMath.log(my_risk_limit, MathContext.DECIMAL128).
                   add(two_under.add(one_under).add(one_over).add(two_over)));
      final BigDecimal ceil =
          numerator.divide(my_contest_result.countyDilutedMargin(),
                           MathContext.DECIMAL128).setScale(0, RoundingMode.CEILING);
      result = ceil.max(over_under_sum);
    }
    
    Main.LOGGER.info("estimate for contest " + contest().name() + 
                     ", diluted margin " + contestResult().countyDilutedMargin() + 
                     ": " + result);
    return result;
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
   * @param the_cvr The CVR.
   * @param the_acvr The ACVR.
   * @return an optional int that is present if there is a discrepancy and absent
   * otherwise.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public OptionalInt computeDiscrepancy(final CastVoteRecord the_cvr, 
                                        final CastVoteRecord the_acvr) {
    OptionalInt result = OptionalInt.empty();
    final CVRContestInfo cvr_info = 
        the_cvr.contestInfoForContest(my_contest_result.contest());
    final CVRContestInfo acvr_info =
        the_acvr.contestInfoForContest(my_contest_result.contest());

    if (the_acvr.recordType() == RecordType.PHANTOM_BALLOT) {
      result = OptionalInt.of(computePhantomBallotDiscrepancy(cvr_info));
    } else if (the_cvr.recordType() == RecordType.PHANTOM_RECORD){
      // similar to the phantom ballot, we use the worst case scenario, a 2-vote
      // overstatement, except here, we don't have a CVR to check anything on.
      Main.LOGGER.info("computeDiscrepancy two-overstatement for cvr id: " + the_cvr.id());
      result = OptionalInt.of(2);
    }
    else if (cvr_info != null && acvr_info != null) {
      if (acvr_info.consensus() == ConsensusValue.NO) {
        // a lack of consensus for this contest is treated
        // identically to a phantom ballot
        result = OptionalInt.of(computePhantomBallotDiscrepancy(cvr_info));
      } else {
        result = computeAuditedBallotDiscrepancy(cvr_info, acvr_info);
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
    if (the_acvr_info.choices().size() <= my_contest.votesAllowed()) {
      acvr_choices.addAll(the_acvr_info.choices());
    } // else overvote so don't count the votes
    
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
    
    for (final String winner : my_contest_result.winners()) {
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
      if (my_contest_result.losers().isEmpty()) {
        // if there are no losers, we'll just negate this number - even though in 
        // real life, we wouldn't be auditing the contest at all
        raw_result = Math.max(raw_result, -winner_change);
      } else {
        for (final String loser : my_contest_result.losers()) {
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
                                      contest().name());
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
      result = 2;
    } else {
      // this contest does appear in the CVR, so we can actually check
      final Set<String> winner_votes = new HashSet<>(the_info.choices());    
      winner_votes.removeAll(my_contest_result.losers());
      if (winner_votes.isEmpty()) {
        result = 1;
      } else { 
        result = 2;
      }
    }
    
    return result;
  }
}
