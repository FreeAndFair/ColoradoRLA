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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.AuditBoard;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.AuditSelection;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.ImportStatus;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response generated on a refresh of the County and Audit Board
 * dashboards.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField",
    "PMD.CyclomaticComplexity", "PMD.TooManyFields"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class CountyDashboardRefreshResponse {
  /**
   * The county ID.
   */
  private final Long my_id;
  
  /**
   * The ASM state.
   */
  private final ASMState my_asm_state;
  
  /**
   * The audit board ASM state.
   */
  private final ASMState my_audit_board_asm_state;
  
  /**
   * The general information.
   * @todo this needs to be connected to something
   */
  private final SortedMap<String, String> my_general_information;
  
  /**
   * The audit board members.
   */
  private final AuditBoard my_audit_board;
  
  /**
   * The ballot manifest file.
   */
  private final UploadedFile my_ballot_manifest_file;
  
  /**
   * The CVR export file.
   */
  private final UploadedFile my_cvr_export_file;
  
  /**
   * The contests on the ballot (by ID).
   */
  private final List<Long> my_contests;
  
  /**
   * The contests under audit, with reasons.
   */
  private final SortedMap<Long, String> my_contests_under_audit;
  
  /**
   * The date and time of the audit. 
   */
  private final Instant my_audit_time;
  
  /**
   * The estimated number of ballots to audit.
   */
  private final Integer my_estimated_ballots_to_audit;
  
  /**
   * The optimistic number of ballots to audit.
   */
  private final Integer my_optimistic_ballots_to_audit;
  
  /**
   * The ballots remaining in the round.
   */
  private final Integer my_ballots_remaining_in_round;
  
  /**
   * The number of ballots represented by the uploaded ballot manifest.
   */
  private final Integer my_ballot_manifest_count;
  
  /**
   * The number of cvrs in the uploaded CVR export.
   */
  private final Integer my_cvr_export_count;
  
  /**
   * The CVR import status.
   */
  private final ImportStatus my_cvr_import_status; 
  
  /**
   * The number of ballots audited.
   */
  private final Integer my_audited_ballot_count;
  
  /**
   * The numbers of discrepancies found, mapped by audit selection.
   */
  private final Map<AuditSelection, Integer> my_discrepancy_count;
  
  /**
   * The number of disagreements found, mapped by audit selection.
   */
  private final Map<AuditSelection, Integer> my_disagreement_count;

  /**
   * The current ballot under audit.
   */
  private final Long my_ballot_under_audit_id;
  
  /**
   * The audited prefix length.
   */
  private final Integer my_audited_prefix_length;
  
  /**
   * The audit rounds.
   */
  private final List<Round> my_rounds;
  
  /** 
   * The current audit round.
   */
  private final Round my_current_round;
  
  /**
   * The audit info.
   */
  private final AuditInfo my_audit_info;
  
  /**
   * Constructs a new CountyDashboardRefreshResponse.
   * 
   * @param the_id The ID.
   * @param the_asm_state The ASM state.
   * @param the_audit_board_asm_state The audit board ASM state.
   * @param the_general_information The general information.
   * @param the_risk_limit The risk limit.
   * @param the_audit_board The current audit board.
   * @param the_ballot_manifest_file The ballot manifest file.
   * @param the_cvr_export_file The CVR export file.
   * @param the_contests The contests.
   * @param the_contests_under_audit The contests under audit, with reasons.
   * @param the_audit_time The audit time.
   * @param the_estimated_ballots_to_audit The estimated ballots to audit.
   * @param the_optimistic_ballots_to_audit The optimistic ballots to audit.
   * @param the_ballots_remaining_in_round The ballots remaining in the 
   * current round.
   * @param the_ballot_manifest_count The number of ballots represented by the
   * uploaded ballot manifest.
   * @param the_cvr_export_count The number of CVRs in the uploaded export file.
   * @param the_cvr_import_status An indication of the status of an ongoing 
   * CVR import.
   * @param the_audited_ballot_count The number of ballots audited.
   * @param the_discrepancy_count The number of discrepencies found, 
   * mapped by audit reason.
   * @param the_disagreement_count The number of disagreements,
   * mapped by audit reason.
   * @param the_ballot_under_audit_id The ID of the CVR under audit.
   * @param the_audited_prefix_length The length of the audited prefix of the
   * ballots to audit list.
   * @param the_rounds The list of audit rounds.
   * @param the_current_round The current audit round.
   * @param the_election_type The election type.
   * @param the_election_date The election date.
   */
  @SuppressWarnings({"PMD.ExcessiveParameterList", "checkstyle:executablestatementcount",
                     "checkstyle:methodlength"})
  protected CountyDashboardRefreshResponse(final Long the_id,
                                           final ASMState the_asm_state,
                                           final ASMState the_audit_board_asm_state,
                                           final SortedMap<String, String> 
                                               the_general_information,
                                           final AuditBoard the_audit_board, 
                                           final UploadedFile the_ballot_manifest_file,
                                           final UploadedFile the_cvr_export_file,
                                           final List<Long> the_contests,
                                           final SortedMap<Long, String> 
                                               the_contests_under_audit,
                                           final Instant the_audit_time,
                                           final Integer the_estimated_ballots_to_audit,
                                           final Integer the_optimistic_ballots_to_audit,
                                           final Integer the_ballots_remaining_in_round,
                                           final Integer the_ballot_manifest_count,
                                           final Integer the_cvr_export_count,
                                           final ImportStatus the_cvr_import_status,
                                           final Integer the_audited_ballot_count,
                                           final Map<AuditSelection, Integer> 
                                               the_discrepancy_count, 
                                           final Map<AuditSelection, Integer> 
                                               the_disagreement_count,
                                           final Long the_ballot_under_audit_id,
                                           final Integer the_audited_prefix_length,
                                           final List<Round> the_rounds,
                                           final Round the_current_round,
                                           final AuditInfo the_audit_info) {
    my_id = the_id;
    my_asm_state = the_asm_state;
    my_audit_board_asm_state = the_audit_board_asm_state;
    my_general_information = the_general_information;
    my_audit_board = the_audit_board;
    my_ballot_manifest_file = the_ballot_manifest_file;
    my_cvr_export_file = the_cvr_export_file;
    my_contests = the_contests;
    my_contests_under_audit = the_contests_under_audit;
    my_audit_time = the_audit_time;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_optimistic_ballots_to_audit = the_optimistic_ballots_to_audit;
    my_ballots_remaining_in_round = the_ballots_remaining_in_round;
    my_ballot_manifest_count = the_ballot_manifest_count;
    my_cvr_export_count = the_cvr_export_count;
    my_cvr_import_status = the_cvr_import_status;
    my_audited_ballot_count = the_audited_ballot_count;
    my_discrepancy_count = the_discrepancy_count;
    my_disagreement_count = the_disagreement_count;
    my_ballot_under_audit_id = the_ballot_under_audit_id;
    my_audited_prefix_length = the_audited_prefix_length;
    my_rounds = the_rounds;
    my_current_round = the_current_round;
    my_audit_info = the_audit_info;
  }
  
  /**
   * Gets the CountyDashboardRefreshResponse for the specified County dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   * response does not exist.
   */
  // this method is essentially a straight line construction of parameters,
  // so we are ignoring the cyclomatic complexity checks for now
  @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
  public static CountyDashboardRefreshResponse 
      createResponse(final CountyDashboard the_dashboard) {
    final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

    if (dosd == null) {
      throw new PersistenceException("unable to read county dashboard state");
    }

    final County county = the_dashboard.county();
    final Long county_id = county.id();

    // general information doesn't exist yet
    final SortedMap<String, String> general_information = new TreeMap<String, String>();
    
    // contests and contests under audit
    final List<Long> contests = new ArrayList<Long>();
    final SortedMap<Long, String> contests_under_audit = new TreeMap<Long, String>();
    if (the_dashboard.cvrFile() != null &&
        the_dashboard.manifestFile() != null) {
      // only add contests if uploads are done
      for (final Contest c : ContestQueries.forCounty(county)) {
        contests.add(c.id());
      }

      for (final ContestToAudit cta : dosd.contestsToAudit()) {
        if (cta.audit() == AuditType.COMPARISON && 
            contests.contains(cta.contest().id())) {
          contests_under_audit.put(cta.contest().id(), cta.reason().toString());
        }
      }
    }
    
    Collections.sort(contests);
    
    // ASM states
    final CountyDashboardASM asm = ASMUtilities.asmFor(CountyDashboardASM.class, 
                                                       county_id.toString());
    final AuditBoardDashboardASM audit_board_asm = 
        ASMUtilities.asmFor(AuditBoardDashboardASM.class, county_id.toString());
    
    // sanitized rounds
    
    final List<Round> rounds = new ArrayList<>();
    for (final Round round : the_dashboard.rounds()) {
      rounds.add(round.withoutSequences());
    }
    Round current_round = the_dashboard.currentRound();
    if (current_round != null) {
      current_round = current_round.withoutSequences();
    }
    
    return new CountyDashboardRefreshResponse(county_id, 
                                              asm.currentState(),
                                              audit_board_asm.currentState(),
                                              general_information,
                                              the_dashboard.currentAuditBoard(),
                                              the_dashboard.manifestFile(),
                                              the_dashboard.cvrFile(),
                                              contests,
                                              contests_under_audit,
                                              the_dashboard.auditTimestamp(),
                                              the_dashboard.estimatedSamplesToAudit(),
                                              the_dashboard.optimisticSamplesToAudit(),
                                              the_dashboard.ballotsRemainingInCurrentRound(),
                                              the_dashboard.ballotsInManifest(),
                                              the_dashboard.cvrsImported(),
                                              the_dashboard.cvrImportStatus(),
                                              the_dashboard.ballotsAudited(),
                                              the_dashboard.discrepancies(),
                                              the_dashboard.disagreements(),
                                              the_dashboard.cvrUnderAudit(),
                                              the_dashboard.auditedPrefixLength(),
                                              rounds,
                                              current_round,
                                              dosd.auditInfo());
  }
  
  /**
   * Gets the abbreviated CountyDashboardRefreshResponse for the specified County 
   * dashboard. The abbreviated response leaves out information about contests,
   * general information, audit board information, and specific ballots to audit.
   * 
   * @param the_dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   * response does not exist.
   */
  // this method is essentially a straight line construction of parameters,
  // so we are ignoring the cyclomatic complexity checks for now
  @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
  public static CountyDashboardRefreshResponse 
      createAbbreviatedResponse(final CountyDashboard the_dashboard) {
    final Long county_id = the_dashboard.id();
    final County county = Persistence.getByID(county_id, County.class);

    if (county == null) {
      throw new PersistenceException("unable to read county dashboard state");
    }

    // ASM states
    final CountyDashboardASM asm = ASMUtilities.asmFor(CountyDashboardASM.class, 
                                                       county_id.toString());
    final AuditBoardDashboardASM audit_board_asm = 
        ASMUtilities.asmFor(AuditBoardDashboardASM.class, county_id.toString());
    
    // sanitized rounds
    
    final List<Round> rounds = new ArrayList<>();
    for (final Round round : the_dashboard.rounds()) {
      rounds.add(round.withoutSequences());
    }
    Round current_round = the_dashboard.currentRound();
    if (current_round != null) {
      current_round = current_round.withoutSequences();
    }
    
    return new CountyDashboardRefreshResponse(county_id, 
                                              asm.currentState(),
                                              audit_board_asm.currentState(),
                                              null,
                                              the_dashboard.currentAuditBoard(),
                                              the_dashboard.manifestFile(),
                                              the_dashboard.cvrFile(),
                                              null,
                                              null,
                                              the_dashboard.auditTimestamp(),
                                              the_dashboard.estimatedSamplesToAudit(),
                                              the_dashboard.optimisticSamplesToAudit(),
                                              the_dashboard.ballotsRemainingInCurrentRound(),
                                              the_dashboard.ballotsInManifest(),
                                              the_dashboard.cvrsImported(),
                                              the_dashboard.cvrImportStatus(),
                                              the_dashboard.ballotsAudited(),
                                              the_dashboard.discrepancies(),
                                              the_dashboard.disagreements(),
                                              null,
                                              the_dashboard.auditedPrefixLength(),
                                              rounds,
                                              current_round,
                                              null);
  }
}
