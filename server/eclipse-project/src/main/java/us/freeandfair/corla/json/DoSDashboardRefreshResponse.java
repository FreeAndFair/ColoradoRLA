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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestComparisonAuditQueries;
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
   * The ASM state.
   */
  private final ASMState my_asm_state;
  
  /**
   * A map from audited contests to audit reasons.
   */
  private final Map<Long, AuditReason> my_audited_contests;
  
  /**
   * A map from audited contests to estimated ballots left to audit.
   */
  private final Map<Long, Integer> my_estimated_ballots_to_audit;
  
  /**
   * A map from audited contests to optimistic ballots left to audit.
   */
  private final Map<Long, Integer> my_optimistic_ballots_to_audit;
  
  /**
   * A map from county IDs to county status.
   */
  private final Map<Long, CountyDashboardRefreshResponse> my_county_status;
  
  /**
   * A set of contests selected for full hand count.
   */
  private final Set<Long> my_hand_count_contests;
  
  /**
   * The audit info.
   */
  private final AuditInfo my_audit_info;
  
  /**
   * Constructs a new DosDashboardRefreshResponse.
   * 
   * @param the_asm_state The ASM state.
   * @param the_audited_contests The audited contests.
   * @param the_county_status The county statuses.
   * @param the_hand_count_contests The hand count contests.
   * @param the_audit_info The election info.
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  protected DoSDashboardRefreshResponse(final ASMState the_asm_state,
                                        final Map<Long, AuditReason> the_audited_contests,
                                        final Map<Long, Integer> 
                                           the_estimated_ballots_to_audit,
                                        final Map<Long, Integer>
                                           the_optimistic_ballots_to_audit,
                                        final Map<Long, CountyDashboardRefreshResponse> 
                                           the_county_status,
                                        final Set<Long> the_hand_count_contests,
                                        final AuditInfo the_audit_info) {
    my_asm_state = the_asm_state;
    my_audited_contests = the_audited_contests;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_optimistic_ballots_to_audit = the_optimistic_ballots_to_audit;
    my_county_status = the_county_status;
    my_hand_count_contests = the_hand_count_contests;
    my_audit_info = the_audit_info;
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
    // construct the various audit info from the contests to audit in the dashboard
    final Map<Long, AuditReason> audited_contests = 
        new HashMap<Long, AuditReason>();
    final Map<Long, Integer> estimated_ballots_to_audit = new HashMap<Long, Integer>();
    final Map<Long, Integer> optimistic_ballots_to_audit = new HashMap<Long, Integer>();
    final Set<Long> hand_count_contests = new HashSet<Long>();
    
    for (final ContestToAudit cta : the_dashboard.contestsToAudit()) {
      switch (cta.audit()) {
        case COMPARISON:
          audited_contests.put(cta.contest().id(), cta.reason());
          final Pair<Integer, Integer> estimates = ballotsToAudit(cta.contest());
          estimated_ballots_to_audit.put(cta.contest().id(), estimates.first());
          optimistic_ballots_to_audit.put(cta.contest().id(), estimates.second());
          break;
          
        case HAND_COUNT:
          hand_count_contests.add(cta.contest().id());
          break;
          
        default:
      }
    }
    // status
    final DoSDashboardASM asm = 
        ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);

    
    return new DoSDashboardRefreshResponse(asm.currentState(),
                                           audited_contests,
                                           estimated_ballots_to_audit,
                                           optimistic_ballots_to_audit,
                                           countyStatusMap(),
                                           hand_count_contests,
                                           the_dashboard.auditInfo());
  }
  
  /**
   * Gets the estimated and optimistic ballots to audit for a contest under audit.
   * 
   * @param the_contest The contest
   * @return a pair <estimated, optimistic> of numbers of ballots to audit.
   */
  private static Pair<Integer, Integer> ballotsToAudit(final Contest the_contest) {
    int optimistic = Integer.MIN_VALUE;
    int estimated = Integer.MIN_VALUE;
    for (final CountyContestComparisonAudit ccca : 
         CountyContestComparisonAuditQueries.matching(the_contest)) {
      optimistic = 
          Math.max(optimistic, 
                   Math.max(0, ccca.optimisticBallotsToAudit() - 
                               ccca.dashboard().auditedPrefixLength()));
      estimated = 
          Math.max(estimated, 
                   Math.max(0, ccca.estimatedBallotsToAudit() - 
                               ccca.dashboard().auditedPrefixLength()));    
    }
    return new Pair<Integer, Integer>(Math.max(0, estimated), Math.max(0, optimistic));
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
