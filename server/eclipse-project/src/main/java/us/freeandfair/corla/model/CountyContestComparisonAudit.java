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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * A class representing the state of a single audited contest for
 * a single county.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county_contest_comparison_audit")
@SuppressWarnings({"PMD.ImmutableField", "PMD.CyclomaticComplexity", "PMD.GodClass"})
public class CountyContestComparisonAudit extends AbstractEntity implements Serializable {
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
  public static final BigDecimal CONSERVATIVE_ONE_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * The initial estimate of error rates for two-vote over- and understatements.
   */
  public static final BigDecimal CONSERVATIVE_TWO_RATE = BigDecimal.valueOf(0.01);
  
  /**
   * Rounding up of 1-vote over/understatements for the initial estimate of 
   * error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_UP_ONES = false;
  
  /**
   * Rounding up of 2-vote over/understatements for the initial estimate of 
   * error rates.
   */
  public static final boolean CONSERVATIVE_ROUND_UP_TWOS = true;
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
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
  private BigDecimal my_gamma = COLORADO_GAMMA;
  
  /**
   * The risk limit.
   */
  private BigDecimal my_risk_limit = BigDecimal.ONE;
  
  /**
   * The expected number of ballots remaining to audit.
   */
  private Integer my_ballots_to_audit;
  
  /**
   * The number of two-vote understatements recorded so far.
   */
  private Integer my_two_vote_under = 0;
  
  /**
   * The number of one-vote understatements recorded so far.
   */
  private Integer my_one_vote_under = 0;
  
  /**
   * The number of one-vote overstatements recorded so far.
   */
  private Integer my_one_vote_over = 0;
  
  /**
   * The number of two-vote overstatements recorded so far.
   */
  private Integer my_two_vote_over = 0;
  
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
  public Integer initialBallotsToAudit() {
    // compute the conservative numbers of over/understatements based on 
    // initial estimate of error rate
    return computeBallotsToAuditFromRates(CONSERVATIVE_TWO_RATE.doubleValue(),
                                          CONSERVATIVE_ONE_RATE.doubleValue(),
                                          CONSERVATIVE_ONE_RATE.doubleValue(),
                                          CONSERVATIVE_TWO_RATE.doubleValue(), 
                                          CONSERVATIVE_ROUND_UP_ONES,
                                          CONSERVATIVE_ROUND_UP_TWOS);
  }
  
  /**
   * @return the expected number of ballots to audit.
   */
  public Integer ballotsToAudit() {
    if (my_ballots_to_audit == null && my_contest_result != null) {
      recalculateBallotsToAudit();
    }
    return my_ballots_to_audit;
  }
  
  /**
   * @return the expected number of ballots remaining to audit.
   * This is the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  private void recalculateBallotsToAudit() {
    my_ballots_to_audit = computeBallotsToAudit(my_two_vote_under, 
                                                my_one_vote_under,
                                                my_one_vote_over,
                                                my_two_vote_over);
  }
  
  /**
   * Computes the expected number of ballots remaining to audit given the
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
  private Integer computeBallotsToAudit(final double the_two_under,
                                        final double the_one_under,
                                        final double the_one_over,
                                        final double the_two_over) {
    final double gamma_double = my_gamma.doubleValue();
    return (int)
        (Math.max(my_one_vote_over + my_two_vote_over +
                  my_one_vote_under + my_two_vote_under,
                  Math.ceil(-2 * gamma_double *
                            (Math.log(my_risk_limit.doubleValue()) +
                                the_two_under * Math.log(1 + 1 / gamma_double) +
                                the_one_under * Math.log(1 + 1 / (2 * gamma_double)) +
                                the_one_over * Math.log(1 - 1 / (2 * gamma_double)) +
                                the_two_over * Math.log(1 - 1 / gamma_double)) /
                            my_contest_result.dilutedMarginCounty().doubleValue())));
  }
  
  /**
   * Computes the expected number of ballots remaining to audit given the
   * specified rates of over- and understatements.
   * 
   * @param the_two_under_rate The rate of two-vote understatements.
   * @param the_one_under_rate The rate of one-vote understatements.
   * @param the_one_over_rate The rate of one-vote overstatements.
   * @param the_two_over_rate The rate of two-vote overstatements.
   * @param the_round_ones true to always round the number of one-
   * vote over- and understatements up, false otherwise
   * @param the_round_twos true to always round the number of twox-
   * vote over- and understatements up, false otherwise
   */
  @SuppressWarnings("checkstyle:magicnumber")
  private Integer computeBallotsToAuditFromRates(final double the_two_under_rate,
                                                 final double the_one_under_rate,
                                                 final double the_one_over_rate,
                                                 final double the_two_over_rate,
                                                 final boolean the_round_ones,
                                                 final boolean the_round_twos) {
    final double gamma_double = my_gamma.doubleValue();
    double bta = -2 * gamma_double * Math.log(my_risk_limit.doubleValue()) /
                (my_contest_result.dilutedMarginCounty().doubleValue() + 2 * gamma_double *
                    (the_two_under_rate * Math.log(1 + 1 / gamma_double) +
                     the_one_under_rate * Math.log(1 + 1 / (2 * gamma_double)) +
                     the_one_over_rate * Math.log(1 - 1 / (2 * gamma_double)) +
                     the_two_over_rate * Math.log(1 - 1 / gamma_double)));

    double two_under;
    double one_under;
    double one_over;
    double two_over;
 
    final int loop_bound = 3;
    for (int i = 0; i < loop_bound; i++) {
      if (the_round_ones) {
        one_under = Math.ceil(the_one_under_rate * bta);
        one_over = Math.ceil(the_one_over_rate * bta);
      } else {
        one_under = Math.round(the_one_under_rate * bta);
        one_over = Math.round(the_one_over_rate * bta);
      }
      if (the_round_twos) {
        two_under = Math.ceil(the_two_under_rate * bta);
        two_over = Math.ceil(the_two_over_rate * bta);
      } else {
        two_under = Math.round(the_two_under_rate * bta);
        two_over = Math.round(the_two_over_rate * bta);
      }
      bta = computeBallotsToAudit(two_under, one_under, one_over, two_over);
    }
    return (int) bta;
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
        break;
       
      case -1:
        my_one_vote_under = my_one_vote_under + 1;
        break;
        
      case 0:
        break;
        
      case 1: 
        my_one_vote_over = my_one_vote_over + 1;
        break;
        
      case 2:
        my_two_vote_over = my_two_vote_over + 1;
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
        break;

      case -1:
        my_one_vote_under = my_one_vote_under - 1;
        break;

      case 0:
        break;

      case 1: 
        my_one_vote_over = my_one_vote_over - 1;
        break;

      case 2:
        my_two_vote_over = my_two_vote_over - 1;
        break;

      default:
        throw new IllegalArgumentException("invalid over or understatement: " + the_statement);
    }
  }
  
  /**
   * Adds the over/understatement represented by the CVR/ACVR pair stored in the
   * specified CVRAuditInfo.
   * 
   * @param the_info The CVRAuditInfo.
   */
  public void addCVRAuditPair(final CVRAuditInfo the_info) {
    recordDiscrepancy(computeDiscrepancy(the_info));
    recalculateBallotsToAudit();
  }
  
  /**
   * Removes the over/understatement represented by the CVR/ACVR pair stored in 
   * the specified CVRAuditInfo.
   * 
   * @param the_info The CVRAuditInfo.
   */
  public void removeCVRAuditPair(final CVRAuditInfo the_info) {
    removeDiscrepancy(computeDiscrepancy(the_info));
    recalculateBallotsToAudit();
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
      // this is a very quick calculation, and may not work correctly 
      // for elections with multiple winners, but will err on the side of being
      // pessimistic

      // if the ACVR is a phantom ballot, we need to assume that it was a vote
      // for all the losers; so if any winners had votes on the original CVR 
      // it's a 2-vote overstatement, otherwise a 1-vote overstatement
      
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

    if (!winner_gains.isEmpty() && loser_losses.isEmpty()) {
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
    
    winner_votes.removeAll(my_contest_result.losers());
    if (winner_votes.isEmpty()) {
      result = 1;
    } else { 
      result = 2;
    }
    
    return result;
  }
}
