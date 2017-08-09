/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

/**
 * The events of the Abstract State Machine (ASM) of the Colorado RLA Tool.
 * @trace asm.asm_event
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public interface AsmEvent {
  /**
   * The Department of State Dashboard's events.
   * @trace asm.department_of_state_dashboard_event
   */
  enum DosDashboardEvent implements AsmEvent {
    AUTHENTICATE_STATE_ADMINISTRATOR_EVENT,
    ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
    SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
    PUBLIC_SEED_EVENT,
    PUBLISH_BALLOTS_TO_AUDIT_EVENT,
    AUDIT_EVENT,
    INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
    COUNTY_AUDIT_COMPLETE_EVENT,
    AUDIT_COMPLETE_EVENT,
    PUBLISH_AUDIT_REPORT_EVENT,
    DOS_REFRESH_EVENT,
    DOS_SKIP_EVENT
  }
  
  /**
   * The County Dashboard's events.
   * @trace asm.county_dashboard_event
   */
  enum CountyDashboardEvent implements AsmEvent {
    AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT,
    ESTABLISH_AUDIT_BOARD_EVENT,
    COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT,
    UPLOAD_VERIFIED_CVRS_EVENT,
    START_AUDIT_EVENT,
    REFRESH_EVENT,
    SKIP_EVENT
  }
  
  /**
   * The Audit Board Dashboard's events.
   * @trace asm.audit_board_dashboard_event
   */
  enum AuditBoardDashboardEvent implements AsmEvent {
    REPORT_MARKINGS_EVENT,
    REPORT_BALLOT_NOT_FOUND_EVENT,
    SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
    SUBMIT_AUDIT_REPORT_EVENT,
    SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
    AUDIT_SKIP_EVENT,
    AUDIT_REFRESH_EVENT
  }
}
