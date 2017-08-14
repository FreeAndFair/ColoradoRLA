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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import us.freeandfair.corla.comparisonaudit.ComparisonAudit;
// I don't know why Checkstyle wants this blank line here

import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.ContestToAudit.AuditType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;
import us.freeandfair.corla.util.Pair;

/**
 * The top-level class providing the API to drive RLAs for Colorado.
 */
public class RLAAlgorithm {
  /**
   * The RLA comparison audit risk computation algorithm.
   */
  private final ComparisonAudit my_comparison_audit;
  
  /**
   * My county dashboard.
   */
  private final CountyDashboard my_dashboard;
  
  /**
   * Instantiate the RLA algorithm for the RLA Tool.
   * 
   * This constructor assumes that we know the number of CVRs in the
   * election, the risk limit, and the tabulation result.
   */
  public RLAAlgorithm(final CountyDashboard the_dashboard) {
    // count CVRs for contest under audit
    final Long cvr_count = 
        CastVoteRecordQueries.countMatching(the_dashboard.cvrUploadTimestamp(), 
                                            the_dashboard.countyID(),
                                            RecordType.UPLOADED);
    
    // what is the risk limit for this contest?
    final BigDecimal risk_limit = 
        DepartmentOfStateDashboardQueries.get().getRiskLimitForComparisonAudits();
    
    // a map describing all contests in an election that are under audit.
    // as documented in ComparisonAudit:
    // map from contest identifiers to contests, where
    // contests are a pair of the number of winners selected by the
    // contest and a map from candidate identifiers to vote counts
    final Map<String, Pair<Integer, Map<String, Integer>>> contest_map = 
        getContestMap(the_dashboard);
    my_comparison_audit = 
        new ComparisonAudit(cvr_count.intValue(), risk_limit, contest_map);
    my_dashboard = the_dashboard;
  }

  /**
   * Compute the contest map for the specified county dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the map.
   */
  private Map<String, Pair<Integer, Map<String, Integer>>> 
      getContestMap(final CountyDashboard the_dashboard) {
    final Map<String, Pair<Integer, Map<String, Integer>>> result =
        new HashMap<String, Pair<Integer, Map<String, Integer>>>();
    // <contest-name, Pair<number-of-winners, Map<candidate, total>>>
    // first, get all the contests    
    final DepartmentOfStateDashboard dosdb = DepartmentOfStateDashboardQueries.get();
    
    // brute force
    for (final ContestToAudit contest : dosdb.contestsToAudit()) {
      if (contest.audit() != AuditType.COMPARISON) {
        continue;
      }
      final String name = contest.contest().name();
      final Integer num_winners = contest.contest().votesAllowed();
      final Map<String, Integer> vote_totals = new HashMap<String, Integer>();
      for (final Choice choice : contest.contest().choices()) {
        vote_totals.put(choice.name(), 0);
      }
      result.put(name, new Pair<Integer, Map<String, Integer>>(num_winners, vote_totals));
    }
    
    final boolean transaction = Persistence.beginTransaction();
    final Stream<CastVoteRecord> cvrs =
        CastVoteRecordQueries.getMatching(the_dashboard.cvrUploadTimestamp(), 
                                          the_dashboard.countyID(),
                                          RecordType.UPLOADED);

    cvrs.forEach((the_cvr) -> {
      for (final CVRContestInfo contest_info : the_cvr.contestInfo()) {          
        final Pair<Integer, Map<String, Integer>> record = 
            result.get(contest_info.contest().name());
        if (record == null) {
          // this wasn't one of the contests to audit
          continue;
        }
        for (final String choice : record.getSecond().keySet()) {
          if (contest_info.choices().contains(choice)) {
            // we assume no overvotes in uploaded CVRs
            record.getSecond().put(choice, record.getSecond().get(choice) + 1);
          }
        }
      }
      Persistence.currentSession().evict(the_cvr);
    });
    if (transaction) {
      Persistence.commitTransaction();
    }
    
    return result;
  }
  
  /**
   * Compute the order of ballot (cards) for audit within a county.
   * 
   * @param the_seed the seed provided by the Department of State.
   * @param the_cvr_count the total number of CVRs in a given county.
   */
  public Long[] computeBallotOrder(final String the_seed) {
    final boolean with_replacement = true;
    // assuming that CVRs are indexed from 0
    final int minimum = 0;
    // the number of CVRs for the_contest_to_audit
    final Long max_long = 
        CastVoteRecordQueries.countMatching(my_dashboard.cvrUploadTimestamp(), 
                                            my_dashboard.countyID(),
                                            RecordType.UPLOADED);
    final int maximum = max_long.intValue();

    final PseudoRandomNumberGenerator prng = 
        new PseudoRandomNumberGenerator(the_seed, with_replacement,
                                        minimum, maximum);
    final List<Integer> list_of_cvrs_to_audit = 
        prng.getRandomNumbers(minimum, maximum);
    final List<Long> list_of_cvr_ids = 
        CastVoteRecordQueries.idsForMatching(my_dashboard.cvrUploadTimestamp(), 
                                             my_dashboard.countyID(),
                                             RecordType.UPLOADED);
    final List<Long> result = new ArrayList<>();
    for (final int index : list_of_cvrs_to_audit) {
      result.add(list_of_cvr_ids.get(index));
    }
    return result.toArray(new Long[result.size()]);
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
