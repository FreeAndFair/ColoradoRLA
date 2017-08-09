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
    AUTHENTICATE_STATE_ADMINISTRATOR_EVENT, // public inbound event
    ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT, // public inbound event
    SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT, // public inbound event
    PUBLIC_SEED_EVENT, // public inbound event
    PUBLISH_BALLOTS_TO_AUDIT_EVENT, // public inbound event
    AUDIT_EVENT, // private internal event
    INDICATE_FULL_HAND_COUNT_CONTEST_EVENT, // public inbound event
    COUNTY_AUDIT_COMPLETE_EVENT, // private internal event
    AUDIT_COMPLETE_EVENT, // private internal event
    PUBLISH_AUDIT_REPORT_EVENT, // public inbound event
    DOS_REFRESH_EVENT, // private internal event
    DOS_SKIP_EVENT // private internal event
  }
  
  /**
   * The County Dashboard's events.
   * @trace asm.county_dashboard_event
   */
  enum CountyDashboardEvent implements AsmEvent {
    AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT, // public inbound event
    ESTABLISH_AUDIT_BOARD_EVENT, // public inbound event
    UPLOAD_BALLOT_MANIFEST_EVENT, // public inbound event
    UPLOAD_CVRS_EVENT, // public inbound event
    START_AUDIT_EVENT, // public inbound event
    COUNTY_REFRESH_EVENT, // private internal event
    COUNTY_SKIP_EVENT // private internal event
  }
  
  /**
   * The Audit Board Dashboard's events.
   * @trace asm.audit_board_dashboard_event
   */
  enum AuditBoardDashboardEvent implements AsmEvent {
    REPORT_MARKINGS_EVENT, // public inbound event
    REPORT_BALLOT_NOT_FOUND_EVENT, // public inbound event
    SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT, // public inbound event
    SUBMIT_AUDIT_REPORT_EVENT, // public inbound event
    SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT, // public inbound event
    AUDIT_SKIP_EVENT, // private internal event
    AUDIT_REFRESH_EVENT // private internal event
  }
}
