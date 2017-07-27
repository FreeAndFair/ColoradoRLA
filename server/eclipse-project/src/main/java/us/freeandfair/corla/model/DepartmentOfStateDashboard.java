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
   * The audit stages of the contests.
   */
  private final Map<Contest, AuditStage> my_audit_stages =
      new HashMap<Contest, AuditStage>();
  
  /**
   * The set contests to be hand counted.
   */
  private final Set<Contest> my_hand_count_contests = 
      new HashSet<Contest>();
  
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
   * Get the dashboard for the specified county.
   * 
   * @param the_county The county.
   * @return the dashboard, or null if it does not exist.
   */
  public CountyDashboard dashboardForCounty(final County the_county) {
    return my_county_dashboards.get(the_county);
  }
}
