/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.report;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;

/**
 * All the data required for a county audit report.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class CountyReport {
  /**
   * The county for which this report was generated.
   */
  private final County my_county;
  
  /**
   * The date and time this report was generated.
   */
  private final Instant my_timestamp;
  
  /**
   * The lists of CVR imprinted IDs to audit for each round.
   */
  private final Map<Integer, List<String>> my_cvrs_to_audit_by_round;
  
  /**
   * The "ballot verification report" for each round (we still don't know
   * what this means).
   */
  private final Map<Integer, String> my_ballot_verification_reports_by_round;
  
  /**
   * The contests driving the audit, and their results.
   */
  private final Set<CountyContestResult> my_driving_contest_results;
  
  /**
   * The data for each audit round.
   */
  private final List<Round> my_rounds;
  
  /**
   * Initialize a county report for the specified county, timestamped
   * with the current time.
   * 
   * @param the_county The county.
   */
  public CountyReport(final County the_county) {
    this(the_county, Instant.now());
  }
  /**
   * Initialize a county report object for the specified county, with the
   * specified timestamp.
   * 
   * @param the_county The county.
   * @param the_timestamp The timestamp.
   */
  public CountyReport(final County the_county, final Instant the_timestamp) {
    my_county = the_county;
    my_timestamp = the_timestamp;
    my_driving_contest_results = CountyContestResultQueries.forCounty(my_county);
    final CountyDashboard cdb = 
        Persistence.getByID(my_county.id(), CountyDashboard.class);
    my_rounds = cdb.rounds();
    my_cvrs_to_audit_by_round = new HashMap<>();
    my_ballot_verification_reports_by_round = new HashMap<>();
    for (final Round r : my_rounds) {
      final List<CastVoteRecord> cvrs_to_audit = 
          ComparisonAuditController.computeBallotOrder(cdb, r.number());
      cvrs_to_audit.sort(new CastVoteRecord.BallotOrderComparator());
      final List<String> cvr_ids_to_audit = new ArrayList<>();
      for (final CastVoteRecord cvr : cvrs_to_audit) {
        cvr_ids_to_audit.add(cvr.imprintedID());
      }
      my_cvrs_to_audit_by_round.put(r.number(), cvr_ids_to_audit);
      my_ballot_verification_reports_by_round.put(r.number(), "TBD");
    }
  }
  
  /**
   * @return the county for this report.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the timestamp for this report.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the CVRs imprinted IDs to audit by round map for this report.
   */
  public Map<Integer, List<String>> cvrsToAuditByRound() {
    return Collections.unmodifiableMap(my_cvrs_to_audit_by_round);
  }
  
  /**
   * @return the ballot verification reports by round map for this report.
   */
  public Map<Integer, String> ballotVerificationReportsByRound() {
    return Collections.unmodifiableMap(my_ballot_verification_reports_by_round);
  }
  
  /**
   * @return the driving contest results for this report.
   */
  public Set<CountyContestResult> drivingContestResults() {
    return Collections.unmodifiableSet(my_driving_contest_results);
  }
  
  /**
   * @return the list of rounds for this report.
   */
  public List<Round> rounds() {
    return Collections.unmodifiableList(my_rounds);
  }
}
