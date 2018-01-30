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
 * The states of the Abstract State Machine (ASM) of the Colorado RLA Tool.
 * @trace asm.asm_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
public interface ASMState {
  /**
   * The Department of State Dashboard's states.
   * @trace asm.department_of_state_dashboard_state
   */
  enum DoSDashboardState implements ASMState {
    DOS_INITIAL_STATE,
    PARTIAL_AUDIT_INFO_SET,
    COMPLETE_AUDIT_INFO_SET,
    RANDOM_SEED_PUBLISHED,
    DOS_AUDIT_ONGOING,
    DOS_ROUND_COMPLETE,
    DOS_AUDIT_COMPLETE,
    AUDIT_RESULTS_PUBLISHED
  }
  
  /**
   * The County Dashboard's states.
   * @trace asm.county_dashboard_state
   */
  enum CountyDashboardState implements ASMState {
    COUNTY_INITIAL_STATE,
    BALLOT_MANIFEST_OK,
    CVRS_IMPORTING,
    CVRS_OK,
    BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING,
    BALLOT_MANIFEST_AND_CVRS_OK,
    COUNTY_AUDIT_UNDERWAY,
    COUNTY_AUDIT_COMPLETE,
    DEADLINE_MISSED
  }
  
  /**
   * The Audit Board Dashboard's states.
   * @trace asm.audit_board_dashboard_state
   */
  enum AuditBoardDashboardState implements ASMState {
    AUDIT_INITIAL_STATE,
    WAITING_FOR_ROUND_START,
    WAITING_FOR_ROUND_START_NO_AUDIT_BOARD,
    ROUND_IN_PROGRESS,
    ROUND_IN_PROGRESS_NO_AUDIT_BOARD,
    WAITING_FOR_ROUND_SIGN_OFF,
    WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD,
    AUDIT_COMPLETE,
    UNABLE_TO_AUDIT,
    AUDIT_ABORTED
  }
}
