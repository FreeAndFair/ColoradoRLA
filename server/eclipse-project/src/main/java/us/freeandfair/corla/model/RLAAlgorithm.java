/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.freeandfair.corla.comparisonaudit.ComparisonAudit;

import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.util.Pair;

/**
 * The top-level class providing the API to drive RLAs for Colorado.
 */
public class RLAAlgorithm {
  /**
   * The RLA comparison audit risk computation algorithm.
   */
  private ComparisonAudit my_comparison_audit;
  
  /**
   * Instantiate the RLA algorithm for the RLA Tool.
   * 
   * This constructor assumes that we know the number of CVRs in the
   * election, the risk limit, and the tabulation result.
   */
  public RLAAlgorithm() {
    // how many cvrs are there for the contest under audit?
    final int cvr_count = 0;
    // what is the risk limit for this contest?
    final BigDecimal risk_limit = BigDecimal.ZERO;
    // a map describing all contests in an election that are under audit.
    // as documented in ComparisonAudit:
    // map from contest identifiers to contests, where
    // contests are a pair of the number of winners selected by the
    // contest and a map from candidate identifiers to vote counts
    final Map<String, Pair<Integer, Map<String, Integer>>> contest = 
        new HashMap<String, Pair<Integer, Map<String, Integer>>>();
    my_comparison_audit = 
        new ComparisonAudit(cvr_count, risk_limit, contest);
  }

  /**
   * Compute the order of ballot (cards) for audit within a county.
   * 
   * @param the_seed the seed provided by the Department of State.
   * @param the_cvr_count the total number of CVRs in a given county.
   */
  public int[] computeBallotOrder(final String the_seed,
                                  final int the_cvr_count) {
    final boolean with_replacement = true;
    // assuming that CVRs are indexed from 0
    final int minimum = 0;
    // the number of CVRs for the_contest_to_audit
    final int maximum = the_cvr_count; 
    final PseudoRandomNumberGenerator prng = 
        new PseudoRandomNumberGenerator(the_seed, with_replacement,
                                        minimum, maximum);
    final List<Integer> list_of_cvrs_to_audit = 
        prng.getRandomNumbers(minimum, maximum);
    return list_of_cvrs_to_audit.stream().mapToInt(i -> i).toArray();
  }
  
  /**
   * Compute the estimated number of ballots to audit, given the ballots
   * under audit described in the constructor.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public int estimatedInitialSampleSize() {
    return my_comparison_audit.nminfromrates(0.01, 0.01, 0.01, 0.01, true, true);
  }

  /**
   * Compute the total estimated number of ballots to audit given
   * that the audit has commenced and auditors have submitted audit
   * CVRs. 
   */
  public int estimatedBallotsToAudit() {
    // determine the prefix of ballots in the audit ballot order that
    // have CVRs returned
    
    // for each of the following computations, we must conservatively 
    // assume that all future un-audited ballots in the sample are
    // two-vote overstatements. @todo kiniry Check with Neal on this.
    
    // compute the observed rate rate of one-vote overstatements
    final double r1 = 0.0; 
    // compute the observed rate rate of two-vote overstatements
    final double r2 = 0.0; 
    // compute the observed rate rate of one-vote understatements
    final double s1 = 0.0; 
    // compute the observed rate rate of one-vote understatements
    final double s2 = 0.0;
        
    return my_comparison_audit.nminfromrates(r1, r2, s1, s2, true, true);
  }
}
