/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.model.AuditStage;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.ContestToAudit.AuditReason;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.Pair;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response generated on a refresh of the DoS dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings({"URF_UNREAD_FIELD",
// Justification: Field is read by Gson.
    "SF_SWITCH_NO_DEFAULT"})
// Justification: False positive; there is a default case.

public class DoSDashboardRefreshResponse {
  /**
   * The audit stage.
   */
  private final AuditStage my_audit_stage;
  
  /**
   * The risk limit.
   */
  private final BigDecimal my_risk_limit;
  
  /**
   * A map from audited contests to audit reasons.
   */
  private final Map<Long, AuditReason> my_audited_contests;
  
  /**
   * A map from county IDs to county status.
   */
  private final Map<Long, CountyDashboardRefreshResponse> my_county_status;
  
  /**
   * The random seed.
   */
  private final String my_random_seed;
  
  /**
   * A set of contests selected for full hand count.
   */
  private final Set<Long> my_hand_count_contests;
  
  /**
   * Constructs a new DosDashboardRefreshResponse.
   * 
   * @param the_audit_stage The audit stage.
   * @param the_risk_limit The risk limit.
   * @param the_audited_contests The audited contests.
   * @param the_county_status The county statuses.
   * @param the_random_seed The random seed.
   * @param the_hand_count_contests The hand count contests.
   */
  protected DoSDashboardRefreshResponse(final AuditStage the_audit_stage,
                                        final BigDecimal the_risk_limit,
                                        final Map<Long, AuditReason> the_audited_contests,
                                        final Map<Long, CountyDashboardRefreshResponse> 
                                           the_county_status,
                                        final String the_random_seed,
                                        final Set<Long> the_hand_count_contests) {
    my_audit_stage = the_audit_stage;
    my_risk_limit = the_risk_limit;
    my_audited_contests = the_audited_contests;
    my_county_status = the_county_status;
    my_random_seed = the_random_seed;
    my_hand_count_contests = the_hand_count_contests;
  }
  
  /**
   * Gets the DoSDashboardRefreshResponse for the specified DoS dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   * response does not exist.
   */
  public static DoSDashboardRefreshResponse 
      createResponse(final DoSDashboard the_dashboard) {
    // construct the audit/hand count info from the contests to audit in the dashboard
    final Pair<Map<Long, AuditReason>, Set<Long>> info =
        contestInfo(the_dashboard.contestsToAudit());
    
    return new DoSDashboardRefreshResponse(the_dashboard.auditStage(),
                                           the_dashboard.riskLimitForComparisonAudits(),
                                           info.getFirst(),
                                           countyStatusMap(),
                                           the_dashboard.randomSeed(),
                                           info.getSecond());
  }
  
  /**
   * Converts the dashboard's contests to audit into collections suitable for the
   * response. 
   * 
   * @param the_set The set of contests to audit.
   * @return the audit/hand count info from a set of contests to audit as a pair
   * of collections.
   */
  private static Pair<Map<Long, AuditReason>, Set<Long>> 
      contestInfo(final Set<ContestToAudit> the_set) {
    final Map<Long, AuditReason> audited_contests = 
        new HashMap<Long, AuditReason>();
    final Set<Long> hand_count_contests = new HashSet<Long>();
    
    for (final ContestToAudit cta : the_set) {
      switch (cta.audit()) {
        case COMPARISON:
          audited_contests.put(cta.contest().id(), cta.reason());
          break;
          
        case HAND_COUNT:
          hand_count_contests.add(cta.contest().id());
          break;
          
        default:
      }
    }
    return new Pair<Map<Long, AuditReason>, Set<Long>>
    (audited_contests, hand_count_contests);
  }
  
  /**
   * Gets the county statuses for all counties in the database.
   * 
   * @return a map from county identifiers to statuses.
   */
  private static Map<Long, CountyDashboardRefreshResponse> countyStatusMap() {
    final Map<Long, CountyDashboardRefreshResponse> status_map = 
        new HashMap<Long, CountyDashboardRefreshResponse>();
    final List<County> counties = Persistence.getAll(County.class);
    
    for (final County c : counties) {
      final CountyDashboard db = Persistence.getByID(c.id(), CountyDashboard.class);
      if (db == null) {
        throw new PersistenceException("unable to read county dashboard state.");
      } else {   
        status_map.put(db.id(), 
                       CountyDashboardRefreshResponse.createAbbreviatedResponse(db));
      }
    }
    
    return status_map;
  }
}
