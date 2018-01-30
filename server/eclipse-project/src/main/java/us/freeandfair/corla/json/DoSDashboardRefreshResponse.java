/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestComparisonAuditQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response generated on a refresh of the DoS dashboard.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
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
  private final SortedMap<Long, AuditReason> my_audited_contests;
  
  /**
   * A map from audited contests to estimated ballots left to audit.
   */
  private final SortedMap<Long, Integer> my_estimated_ballots_to_audit;
  
  /**
   * A map from audited contests to optimistic ballots left to audit.
   */
  private final SortedMap<Long, Integer> my_optimistic_ballots_to_audit;
  
  /**
   * A map from audited contests to discrepancy count maps.
   */
  private final SortedMap<Long, Map<Integer, Integer>> my_discrepancy_count;
  
  /**
   * A map from county IDs to county status.
   */
  private final SortedMap<Long, CountyDashboardRefreshResponse> my_county_status;
  
  /**
   * A set of contests selected for full hand count.
   */
  private final List<Long> my_hand_count_contests;
  
  /**
   * The audit info.
   */
  private final AuditInfo my_audit_info;
  
  /**
   * The audit reasons for the contests under audit.
   */
  private final SortedMap<Long, AuditReason> my_audit_reasons;
  
  /**
   * The audit types for the contests under audit.
   */
  private final SortedMap<Long, AuditType> my_audit_types;
  
  /**
   * Constructs a new DosDashboardRefreshResponse.
   * 
   * @param the_asm_state The ASM state.
   * @param the_audited_contests The audited contests.
   * @param the_estimated_ballots_to_audit The estimated ballots to audit, 
   * by contest.
   * @param the_optimistic_ballots_to_audit The optimistic ballots to audit, 
   * by contest.
   * @param the_discrepancy_count The discrepancy count for each discrepancy type,
   * by contest.
   * @param the_county_status The county statuses.
   * @param the_hand_count_contests The hand count contests.
   * @param the_audit_info The election info.
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  protected DoSDashboardRefreshResponse(final ASMState the_asm_state,
                                        final SortedMap<Long, AuditReason> 
                                           the_audited_contests,
                                        final SortedMap<Long, Integer> 
                                           the_estimated_ballots_to_audit,
                                        final SortedMap<Long, Integer>
                                           the_optimistic_ballots_to_audit,
                                        final SortedMap<Long, Map<Integer, Integer>>
                                           the_discrepancy_counts,
                                        final SortedMap<Long, CountyDashboardRefreshResponse> 
                                           the_county_status,
                                        final List<Long> the_hand_count_contests,
                                        final AuditInfo the_audit_info,
                                        final SortedMap<Long, AuditReason> the_audit_reasons,
                                        final SortedMap<Long, AuditType> the_audit_types) {
    my_asm_state = the_asm_state;
    my_audited_contests = the_audited_contests;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_optimistic_ballots_to_audit = the_optimistic_ballots_to_audit;
    my_discrepancy_count = the_discrepancy_counts;
    my_county_status = the_county_status;
    my_hand_count_contests = the_hand_count_contests;
    my_audit_info = the_audit_info;
    my_audit_reasons = the_audit_reasons;
    my_audit_types = the_audit_types;
  }
  
  /**
   * Gets the DoSDashboardRefreshResponse for the specified DoS dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   * response does not exist.
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public static DoSDashboardRefreshResponse 
      createResponse(final DoSDashboard the_dashboard) {
    // construct the various audit info from the contests to audit in the dashboard
    final SortedMap<Long, AuditReason> audited_contests = 
        new TreeMap<Long, AuditReason>();
    final SortedMap<Long, Integer> estimated_ballots_to_audit = new TreeMap<Long, Integer>();
    final SortedMap<Long, Integer> optimistic_ballots_to_audit = new TreeMap<Long, Integer>();
    final SortedMap<Long, Map<Integer, Integer>> discrepancy_count = new TreeMap<>();
    final List<Long> hand_count_contests = new ArrayList<Long>();
    final SortedMap<Long, AuditReason> audit_reasons = 
        new TreeMap<Long, AuditReason>();
    final SortedMap<Long, AuditType> audit_types = 
        new TreeMap<Long, AuditType>();
    
    for (final ContestToAudit cta : the_dashboard.contestsToAudit()) {
      if (cta.audit() != AuditType.NONE) {
        audit_reasons.put(cta.contest().id(), cta.reason());
        audit_types.put(cta.contest().id(), cta.audit());
      }
      switch (cta.audit()) {
        case COMPARISON:
          final Map<Integer, Integer> discrepancy = new HashMap<>();
          int optimistic = Integer.MIN_VALUE;
          int estimated = Integer.MIN_VALUE;
          audited_contests.put(cta.contest().id(), cta.reason());
          for (final CountyContestComparisonAudit ccca : 
               CountyContestComparisonAuditQueries.matching(cta.contest())) {
            optimistic = 
                Math.max(optimistic, 
                         Math.max(0, ccca.optimisticSamplesToAudit() - 
                                     ccca.dashboard().auditedPrefixLength()));
            estimated = 
                Math.max(estimated, 
                         Math.max(0, ccca.estimatedSamplesToAudit() - 
                                     ccca.dashboard().auditedPrefixLength()));
            
            // possible discrepancy types range from -2 to 2 inclusive,
            // and we provide them all in the refresh response
            for (int i = -2; i <= 2; i++) {
              if (discrepancy.get(i) == null) {
                discrepancy.put(i,  0);
              }
              discrepancy.put(i, discrepancy.get(i) + ccca.discrepancyCount(i));
            }
          }
          estimated_ballots_to_audit.put(cta.contest().id(), optimistic);
          optimistic_ballots_to_audit.put(cta.contest().id(), estimated);
          discrepancy_count.put(cta.contest().id(), discrepancy);
          break;
          
        case HAND_COUNT:
          // we list these separately for some reason
          hand_count_contests.add(cta.contest().id());
          break;
          
        default:
      }
    }
    
    Collections.sort(hand_count_contests);
    
    // status
    final DoSDashboardASM asm = 
        ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);

    
    return new DoSDashboardRefreshResponse(asm.currentState(),
                                           audited_contests,
                                           estimated_ballots_to_audit,
                                           optimistic_ballots_to_audit,
                                           discrepancy_count,
                                           countyStatusMap(),
                                           hand_count_contests,
                                           the_dashboard.auditInfo(),
                                           audit_reasons,
                                           audit_types);
  }
  
  /**
   * Gets the county statuses for all counties in the database.
   * 
   * @return a map from county identifiers to statuses.
   */
  private static SortedMap<Long, CountyDashboardRefreshResponse> countyStatusMap() {
    final SortedMap<Long, CountyDashboardRefreshResponse> status_map = 
        new TreeMap<Long, CountyDashboardRefreshResponse>();
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
