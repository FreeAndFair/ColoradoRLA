/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 19, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ch.obermuhlner.math.big.BigDecimalMath;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A class representing the state of a single audited contest for
 * a single county.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Cacheable(true)
@Table(name = "county_contest_comparison_audit",
       indexes = { @Index(name = "idx_ccca_dashboard", columnList = "dashboard_id") })

@SuppressWarnings({"PMD.ImmutableField", "PMD.CyclomaticComplexity", "PMD.GodClass"})
public class CountyContestComparisonAudit implements PersistentEntity, Serializable {
  /**
   * The database stored precision for decimal types.
   */
  public static final int PRECISION = 6;
  
  /**
   * The database stored scale for decimal types.
   */
  public static final int SCALE = 4;
  
  /**
   * Gamma, as presented in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  public static final BigDecimal GENTLE_GAMMA = BigDecimal.valueOf(1.03905);

  /**
   * Gamma, as recommended by Neal McBurnett for use in Colorado.
   */
  public static final BigDecimal COLORADO_GAMMA = BigDecimal.valueOf(1.1);

  /**
   * The initial estimate of error rates for one-vote over- and understatements.
   */
  public static final BigDecimal CONSERVATIVE_ONES_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * The initial estimate of error rates for two-vote over- and understatements.
   */
  public static final BigDecimal CONSERVATIVE_TWOS_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * Rounding up of 1-vote over/understatements for the initial estimate of 
   * error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_ONES_UP = true;
  
  /**
   * Rounding up of 2-vote over/understatements for the initial estimate of 
   * error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_TWOS_UP = true;
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
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
   * The gamma.
   */
  @Column(updatable = false, nullable = false, 
          precision = PRECISION, scale = SCALE)
  private BigDecimal my_gamma = COLORADO_GAMMA;
  
  /**
   * The risk limit.
   */
  @Column(updatable = false, nullable = false, 
          precision = PRECISION, scale = SCALE)
  private BigDecimal my_risk_limit = BigDecimal.ONE;
  
  /**
   * The expected number of ballots remaining to audit.
   */
  @Column(nullable = false)
  private Integer my_ballots_to_audit = 0;
  
  /**
   * The number of two-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_under = 0;
  
  /**
   * The number of one-vote understatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_under = 0;
  
  /**
   * The number of one-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_one_vote_over = 0;
  
  /**
   * The number of two-vote overstatements recorded so far.
   */
  @Column(nullable = false)
  private Integer my_two_vote_over = 0;
  
  /**
   * A flag that indicates whether the ballots to audit need to be 
   * recalculated.
   */
  @Column(nullable = false)
  private Boolean my_recalculate_needed = true;
  
  /**
   * Constructs a new, empty CountyContestAudit (solely for persistence).
   */
  public CountyContestComparisonAudit() {
    super();
  }
  
  /**
   * Constructs a CountyContestAudit for the specified dashboard, contest result,
   * and risk limit.
   * 
   * @param the_dashboard The dashboard.
   * @param the_contest_result The contest result.
   * @param the_risk_limit The risk limit.
   */
  public CountyContestComparisonAudit(final CountyDashboard the_dashboard,
                                      final CountyContestResult the_contest_result,
                                      final BigDecimal the_risk_limit) {
    super();
    my_dashboard = the_dashboard;
    my_contest_result = the_contest_result;
    my_contest = my_contest_result.contest();
    my_risk_limit = the_risk_limit;
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
   * @return the initial (conservative) expected number of ballots to audit.
   */
  @SuppressWarnings({"checkstyle:magicnumber", "PMD.AvoidDuplicateLiterals"})
  public int initialBallotsToAudit() {
    // compute the conservative numbers of over/understatements based on 
    // initial estimate of error rate
    return computeBallotsToAuditFromRates(CONSERVATIVE_TWOS_RATE,
                                          CONSERVATIVE_ONES_RATE,
                                          CONSERVATIVE_ONES_RATE,
                                          CONSERVATIVE_TWOS_RATE, 
                                          CONSERVATIVE_ROUND_ONES_UP,
                                          CONSERVATIVE_ROUND_TWOS_UP).intValue();
  }
  
  /**
   * @return the expected overall number of ballots to audit, assuming no 
   * further discrepancies occur.
   */
  public Integer ballotsToAudit() {
    if (my_recalculate_needed) {
      recalculateBallotsToAudit();
      my_recalculate_needed = false;
    }
    return my_ballots_to_audit;
  }
  
  /**
   * @return the expected number of ballots remaining to audit, assuming 
   * over- and understatement rates continue as they currently are.
   */
  public int expectedBallotsRemainingToAudit() {
    return computeBallotsToAuditFromProgress(my_two_vote_under, my_one_vote_under,
                                             my_one_vote_over, my_two_vote_over,
                                             my_dashboard.auditedPrefixLength());
  }
  
  /**
   * Recalculates the overall number of ballots to audit.
   */
  private void recalculateBallotsToAudit() {
    my_ballots_to_audit = computeBallotsToAudit(my_two_vote_under, 
                                                my_one_vote_under,
                                                my_one_vote_over,
                                                my_two_vote_over).intValue();
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
  private BigDecimal computeBallotsToAudit(final int the_two_under,
                                           final int the_one_under,
                                           final int the_one_over,
                                           final int the_two_over) {
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
    final BigDecimal result = ceil.max(over_under_sum);

    Main.LOGGER.info("estimate for contest " + contest().name() + 
                     ", diluted margin " + contestResult().countyDilutedMargin() + 
                     ": " + result);
    return result;
  }
  
  /**
   * Computes the expected number of ballots remaining to audit given the specified
   * number of over- and understatements and the number of ballots audited so far, 
   * assuming that the over- and understatement rate remains the same.
   * 
   * @param the_two_under The number of two-vote understatements.
   * @param the_one_under The one-vote understatements.
   * @param the_one_over The one-vote overstatements.
   * @param the_two_over The two-vote overstatements.
   * @param the_number_audited The number of ballots audited so far.
   * 
   * @return the expected number of ballots remaining to audit.
   * This is calculated from the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  private int computeBallotsToAuditFromProgress(final int the_two_under,
                                                final int the_one_under,
                                                final int the_one_over,
                                                final int the_two_over,
                                                final int the_number_audited) {
    // compute the current over- and understatement rates
    final BigDecimal number_audited = BigDecimal.valueOf(the_number_audited);
    final BigDecimal two_under_rate = 
        BigDecimal.valueOf(the_two_under).divide(number_audited, MathContext.DECIMAL128);
    final BigDecimal one_under_rate =
        BigDecimal.valueOf(the_one_under).divide(number_audited, MathContext.DECIMAL128);
    final BigDecimal one_over_rate =
        BigDecimal.valueOf(the_one_over).divide(number_audited, MathContext.DECIMAL128);
    final BigDecimal two_over_rate =
        BigDecimal.valueOf(the_two_over).divide(number_audited, MathContext.DECIMAL128);
    
    final BigDecimal bta = 
        computeBallotsToAuditFromRates(two_under_rate, one_under_rate, 
                                       one_over_rate, two_over_rate,
                                       CONSERVATIVE_ROUND_ONES_UP, CONSERVATIVE_ROUND_TWOS_UP);
    final BigDecimal remaining = bta.subtract(BigDecimal.valueOf(the_number_audited));
    return remaining.setScale(0, RoundingMode.CEILING).intValue();
  }
  
  /**
   * Computes the expected number of ballots to audit overall given the
   * specified rates of over- and understatements.
   * 
   * @param the_two_under_rate The rate of two-vote understatements.
   * @param the_one_under_rate The rate of one-vote understatements.
   * @param the_one_over_rate The rate of one-vote overstatements.
   * @param the_two_over_rate The rate of two-vote overstatements.
   * @param the_round_ones_up true to always round the number of one-
   * vote over- and understatements up, false otherwise
   * @param the_round_twos_up true to always round the number of twox-
   * vote over- and understatements up, false otherwise
   */
  @SuppressWarnings("checkstyle:magicnumber")
  private BigDecimal computeBallotsToAuditFromRates(final BigDecimal the_two_under_rate,
                                                    final BigDecimal the_one_under_rate,
                                                    final BigDecimal the_one_over_rate,
                                                    final BigDecimal the_two_over_rate,
                                                    final boolean the_round_ones_up,
                                                    final boolean the_round_twos_up) {
    final BigDecimal invgamma = BigDecimal.ONE.divide(my_gamma, MathContext.DECIMAL128);
    final BigDecimal twogamma = 
        BigDecimal.valueOf(2).multiply(my_gamma);
    final BigDecimal invtwogamma = BigDecimal.ONE.divide(twogamma, MathContext.DECIMAL128);
    
    final BigDecimal two_under_initial = 
        the_two_under_rate.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invgamma), 
                                                       MathContext.DECIMAL128));
    final BigDecimal one_under_initial =
        the_one_under_rate.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invtwogamma), 
                                                       MathContext.DECIMAL128));
    final BigDecimal one_over_initial = 
        the_one_over_rate.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invtwogamma), 
                                                      MathContext.DECIMAL128));
    final BigDecimal two_over_initial =
        the_two_over_rate.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invgamma),
                                                      MathContext.DECIMAL128));
    final BigDecimal sum = 
        two_under_initial.add(one_under_initial).add(one_over_initial).add(two_over_initial);
    final BigDecimal denominator =
        my_contest_result.countyDilutedMargin().add(twogamma.multiply(sum));
    final BigDecimal numerator = 
        twogamma.negate().
        multiply(BigDecimalMath.log(my_risk_limit, MathContext.DECIMAL128));
    BigDecimal result = numerator.divide(denominator, MathContext.DECIMAL128);
    
    final BigDecimal two_under;
    final BigDecimal one_under;
    final BigDecimal one_over;
    final BigDecimal two_over;
 
    if (the_round_ones_up) {
      one_under = 
          the_one_under_rate.multiply(result).setScale(0, RoundingMode.CEILING);
      one_over = 
          the_one_over_rate.multiply(result).setScale(0, RoundingMode.CEILING);
    } else {
      one_under = the_one_under_rate.multiply(result).round(MathContext.DECIMAL128);
      one_over = the_one_over_rate.multiply(result).round(MathContext.DECIMAL128);
    }
    if (the_round_twos_up) {
      two_under = 
          the_two_under_rate.multiply(result).setScale(0, RoundingMode.CEILING);
      two_over = 
          the_two_over_rate.multiply(result).setScale(0, RoundingMode.CEILING);
    } else {
      two_under = the_two_under_rate.multiply(result).round(MathContext.DECIMAL128);
      two_over = the_two_over_rate.multiply(result).round(MathContext.DECIMAL128);  
    }
    Main.LOGGER.info("expected numbers u1=" + one_under + "/" + one_under.intValue() + 
                     ", u2=" + two_under + "/" + two_under.intValue() + 
                     ", o1=" + one_over + "/" + one_over.intValue() + 
                     ", o2=" + two_over + "/" + two_over.intValue());
    result = computeBallotsToAudit(two_under.intValue(), one_under.intValue(), 
                                   one_over.intValue(), two_over.intValue());

    Main.LOGGER.info("initial estimate for contest " + contest().name() + 
                     ", diluted margin " + contestResult().countyDilutedMargin() + 
                     ": " + result);
    return result;
  }
  
  /**
   * Records the specified over/understatement (the valid range is -2 .. 2).
   * 
   * @param the_statement The over/understatement to record.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public void recordDiscrepancy(final int the_statement) {
    switch (the_statement) {
      case -2: 
        my_two_vote_under = my_two_vote_under + 1;
        my_recalculate_needed = true;
        break;
       
      case -1:
        my_one_vote_under = my_one_vote_under + 1;
        my_recalculate_needed = true;
        break;
        
      case 0:
        break;
        
      case 1: 
        my_one_vote_over = my_one_vote_over + 1;
        my_recalculate_needed = true;
        break;
        
      case 2:
        my_two_vote_over = my_two_vote_over + 1;
        my_recalculate_needed = true;
        break;
        
      default:
        throw new IllegalArgumentException("invalid over or understatement: " + the_statement);
    }
  }
    
  /**
   * Removes the specified over/understatement (the valid range is -2 .. 2).
   * This is typically done when a new interpretation is submitted for a ballot
   * that had already been interpreted.
   * 
   * @param the_statement The over/understatement to remove.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public void removeDiscrepancy(final int the_statement) {
    switch (the_statement) {
      case -2: 
        my_two_vote_under = my_two_vote_under - 1;
        my_recalculate_needed = true;
        break;

      case -1:
        my_one_vote_under = my_one_vote_under - 1;
        my_recalculate_needed = true;
        break;

      case 0:
        break;

      case 1: 
        my_one_vote_over = my_one_vote_over - 1;
        my_recalculate_needed = true;
        break;

      case 2:
        my_two_vote_over = my_two_vote_over - 1;
        my_recalculate_needed = true;
        break;

      default:
        throw new IllegalArgumentException("invalid over or understatement: " + the_statement);
    }
  }
  
  /**
   * Computes the over/understatement represented by the CVR/ACVR pair
   * stored in the specified CVRAuditInfo.
   * 
   * @param the_info The CVRAuditInfo.
   * @return the over/understatement; valid values are -2 .. 2.
   */
  public Integer computeDiscrepancy(final CVRAuditInfo the_info) {
    if (the_info.acvr() == null || the_info.cvr() == null) {
      throw new IllegalArgumentException("null CVR or ACVR in pair " + the_info);
    } else {
      return computeDiscrepancy(the_info.cvr(), the_info.acvr());
    }
  }

  /**
   * Computes the over/understatement represented by the specified
   * CVR and ACVR.
   * 
   * @param the_cvr The CVR.
   * @param the_acvr The ACVR.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public Integer computeDiscrepancy(final CastVoteRecord the_cvr, 
                                    final CastVoteRecord the_acvr) {
    int result = 0;
    final CVRContestInfo cvr_info = 
        the_cvr.contestInfoForContest(my_contest_result.contest());
    final CVRContestInfo acvr_info =
        the_acvr.contestInfoForContest(my_contest_result.contest());

    if (cvr_info != null && acvr_info != null) {
      if (the_acvr.recordType() == RecordType.PHANTOM_BALLOT) {
        result = computePhantomBallotDiscrepancy(cvr_info);
      } else {
        result = computeAuditedBallotDiscrepancy(cvr_info, acvr_info);
      }
    }
    
    return result;
  }
  
  /**
   * Computes the discrepancy between two ballots.
   * 
   * @param the_cvr_info The CVR info.
   * @param the_acvr_info The ACVR info.
   * @return the discrepancy.
   */
  @SuppressWarnings({"checkstyle:magicnumber", "PMD.ConfusingTernary"})
  private Integer computeAuditedBallotDiscrepancy(final CVRContestInfo the_cvr_info,
                                                  final CVRContestInfo the_acvr_info) {
    int result = 0;
    
    // this is a very quick calculation, and may not work correctly 
    // for elections with multiple winners, but will err on the side of being
    // pessimistic
    
    // check for overvotes
    final Set<String> acvr_choices = new HashSet<>();
    if (the_acvr_info.choices().size() <= my_contest_result.votesAllowed()) {
      acvr_choices.addAll(the_acvr_info.choices());
    } // else overvote so don't count the votes
    
    // find the candidates who gained and lost votes in the ACVR

    final Set<String> gained_votes = new HashSet<>(acvr_choices);
    gained_votes.removeAll(the_cvr_info.choices());
    final Set<String> winner_gains = new HashSet<>(gained_votes);
    winner_gains.removeAll(my_contest_result.losers());
    final Set<String> loser_gains = new HashSet<>(gained_votes);
    loser_gains.removeAll(my_contest_result.winners());

    final Set<String> lost_votes = new HashSet<>(the_cvr_info.choices());
    lost_votes.removeAll(acvr_choices);
    final Set<String> winner_losses = new HashSet<>(lost_votes);
    winner_losses.removeAll(my_contest_result.losers());
    final Set<String> loser_losses = new HashSet<>(lost_votes);
    loser_losses.removeAll(my_contest_result.winners());

    // now, we have several cases:
    
    if (gained_votes.isEmpty() && lost_votes.isEmpty()) {
      // the CVR and ACVR have identical choices and we
      // can skip the rest of this
      result = 0;
    } else if (!winner_gains.isEmpty() && loser_losses.isEmpty()) {
      // if only winners gained votes and no losers lost votes, 
      // it's a 1-vote understatement       
      result = -1;
    } else if (!winner_gains.isEmpty() && !loser_losses.isEmpty()) {        
      // if only winners gained votes and any losers lost votes, 
      // it's a 2-vote understatement       
      result = -2;
    } else if (!winner_losses.isEmpty() && loser_gains.isEmpty()) {
      // if any winners lost votes and no losers gained votes, 
      // it's a 1-vote overstatement
      result = 1;
    } else if (!winner_losses.isEmpty() && !loser_gains.isEmpty()) {
      // if any winners lost votes and any losers gained votes,
      // it's a 2-vote overstatement
      result = 2;
    } else if (loser_gains.isEmpty()) {
      // no votes changed for winners, and no losers gained votes,
      // so losers must have lost votes; if _all_ the losers lost
      // votes, it'd be a 1-vote understatement
      if (loser_losses.equals(my_contest_result.losers())) {
        result = -1;
      }
    } else {
      // no votes changed for winners, and losers gained votes, 
      // so it's a 1-vote overstatement
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
    final Set<String> winner_votes = new HashSet<>(the_info.choices());

    // if the ACVR is a phantom ballot, we need to assume that it was a vote
    // for all the losers; so if any winners had votes on the original CVR 
    // it's a 2-vote overstatement, otherwise a 1-vote overstatement
    
    winner_votes.removeAll(my_contest_result.losers());
    if (winner_votes.isEmpty()) {
      result = 1;
    } else { 
      result = 2;
    }
    
    return result;
  }
}
