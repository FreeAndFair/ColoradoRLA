/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

/**
 * The events of the Abstract State Machine (ASM) of the Colorado RLA Tool.
 * @trace asm.asm_event
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
public interface ASMEvent extends Event {
  /**
   * The Department of State Dashboard's events.
   * @trace asm.department_of_state_dashboard_event
   */
  enum DoSDashboardEvent implements ASMEvent {
    PARTIAL_AUDIT_INFO_EVENT, // public inbound event
    COMPLETE_AUDIT_INFO_EVENT, // public inbound event
    DOS_START_ROUND_EVENT, // public inbound event
    DOS_ROUND_COMPLETE_EVENT, // private internal event
    AUDIT_EVENT, // private internal event
    DOS_COUNTY_AUDIT_COMPLETE_EVENT, // private internal event
    DOS_AUDIT_COMPLETE_EVENT, // private internal event
    PUBLISH_AUDIT_REPORT_EVENT // public inbound event
  }

  /**
   * The County Dashboard's events.
   * @trace asm.county_dashboard_event
   */
  enum CountyDashboardEvent implements ASMEvent {
    IMPORT_BALLOT_MANIFEST_EVENT, // public inbound event
    IMPORT_CVRS_EVENT, // public inbound event
    CVR_IMPORT_SUCCESS_EVENT, // private internal event
    CVR_IMPORT_FAILURE_EVENT, // private internal event
    COUNTY_START_AUDIT_EVENT, // private internal event
    COUNTY_AUDIT_COMPLETE_EVENT // private internal event
  }

  /**
   * The Audit Board Dashboard's events.
   * @trace asm.audit_board_dashboard_event
   */
  enum AuditBoardDashboardEvent implements ASMEvent {
    COUNTY_DEADLINE_MISSED_EVENT, // private internal event
    NO_CONTESTS_TO_AUDIT_EVENT, // private internal event
    REPORT_MARKINGS_EVENT, // public inbound event
    REPORT_BALLOT_NOT_FOUND_EVENT, // public inbound event
    SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT, // public inbound event
    SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT, // public inbound event
    SIGN_OUT_AUDIT_BOARD_EVENT, // public inbound event
    SIGN_IN_AUDIT_BOARD_EVENT, // public inbound event
    ROUND_START_EVENT, // private internal event
    ROUND_COMPLETE_EVENT, // private internal event
    ROUND_SIGN_OFF_EVENT, // public inbound event
    RISK_LIMIT_ACHIEVED_EVENT, // private internal event
    ABORT_AUDIT_EVENT, // public inbound event
    BALLOTS_EXHAUSTED_EVENT // private internal event
  }
}
