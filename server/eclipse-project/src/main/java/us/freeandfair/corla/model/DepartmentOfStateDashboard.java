/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Department of State dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class DepartmentOfStateDashboard {
  /**
   * The county dashboards for all counties (from which status and other
   * information can be gathered).
   */
  private final Map<County, CountyDashboard> my_county_dashboards = 
      new HashMap<County, CountyDashboard>();
  
  /**
   * The contests to be audited and the reasons for auditing.
   */
  private final Map<Contest, AuditReason> my_audit_reasons = 
      new HashMap<Contest, AuditReason>();

  /**
   * The audit stage of the election.
   */
  private AuditStage my_audit_stage = AuditStage.INITIAL;
  
  /**
   * The set of contests to be hand counted.
   */
  private final Set<Contest> my_hand_count_contests = 
      new HashSet<Contest>();
  
  /**
   * The risk limit for comparison audits.
   */
  private BigDecimal my_risk_limit_for_comparison_audits;
  
  /**
   * The random seed.
   */
  private BigInteger my_random_seed;
  
  /**
   * Constructs a new Department of State dashboard with the specified
   * collection of counties. Each is given the initial status
   * CountyStatus.NO_DATA.
   * 
   * @param the_counties The counties.
   */
  public DepartmentOfStateDashboard(final Collection<County> the_counties) {
    for (final County c : the_counties) {
      my_county_dashboards.put(c, new CountyDashboard(c));
    }
  }
  
  /**
   * @return the current stage of the audit.
   */
  public AuditStage auditStage() {
    return my_audit_stage;
  }
  
  /**
   * Get the dashboard for the specified county.
   * 
   * @param the_county The county.
   * @return the dashboard, or null if it does not exist.
   */
  public CountyDashboard dashboardForCounty(final County the_county) {
    return my_county_dashboards.get(the_county);
  }
  
  /**
   * @return the risk limit for comparison audits, or null if none has been set.
   */
  public BigDecimal getRiskLimitForComparisonAudits() {
    return my_risk_limit_for_comparison_audits;
  }
  
  /**
   * Sets the risk limit for comparison audits.
   * 
   * @param the_risk_limit The risk limit.
   */
  //@ requires auditStage() == AuditStage.AUTHENTICATED;
  //@ ensures auditStage() == AuditStage.RISK_LIMITS_SET;
  public void setRiskLimitForComparisonAudits(final BigDecimal the_risk_limit) {
    my_risk_limit_for_comparison_audits = the_risk_limit;
    my_audit_stage = AuditStage.RISK_LIMITS_SET;
  }
  
  /**
   * Select the contests to audit.
   * 
   * @param the_contests A map from the contests to audit to reasons for 
   * auditing them.
   */
  public void selectContestsToAudit(final Map<Contest, AuditReason> the_contests) {
    for (final Contest c : the_contests.keySet()) {
      my_audit_reasons.put(c, the_contests.get(c));
    }
    // TODO this state change doesn't necessarily happen here
    my_audit_stage = AuditStage.CONTESTS_TO_AUDIT_IDENTIFIED;
  }
  
  /**
   * Select a contest for full hand count.
   * 
   * @param the_contest The contest.
   */
  public void selectContestForHandCount(final Contest the_contest) {
    my_hand_count_contests.add(the_contest);
  }
  
  /**
   * Sets the random seed.
   * 
   * @param the_seed The random seed.
   */
  public void setRandomSeed(final BigInteger the_random_seed) {
    my_random_seed = the_random_seed;
  }
  
  /**
   * @return the random seed.
   */
  public BigInteger randomSeed() {
    return my_random_seed;
  }
}
