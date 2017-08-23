/*
 * Free & Fair Colorado RLA System
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

/**
 * The states of the Abstract State Machine (ASM) of the Colorado RLA Tool.
 * @trace asm.asm_state
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public interface ASMState {
  /**
   * The Department of State Dashboard's states.
   * @trace asm.department_of_state_dashboard_state
   */
  enum DoSDashboardState implements ASMState {
    DOS_INITIAL_STATE,
    DOS_AUTHENTICATED,
    RISK_LIMITS_SET,
    CONTESTS_TO_AUDIT_IDENTIFIED,
    DATA_TO_AUDIT_PUBLISHED,
    RANDOM_SEED_PUBLISHED,
    BALLOT_ORDER_DEFINED,
    AUDIT_READY_TO_START,
    DOS_AUDIT_ONGOING,
    DOS_AUDIT_COMPLETE,
    AUDIT_RESULTS_PUBLISHED
  }
  
  /**
   * The County Dashboard's states.
   * @trace asm.county_dashboard_state
   */
  enum CountyDashboardState implements ASMState {
    COUNTY_INITIAL_STATE,
    COUNTY_AUTHENTICATED,
    AUDIT_BOARD_OK,
    BALLOT_MANIFEST_OK,
    CVRS_OK,
    AUDIT_BOARD_AND_BALLOT_MANIFEST_OK,
    AUDIT_BOARD_AND_CVRS_OK,
    BALLOT_MANIFEST_AND_CVRS_OK,
    AUDIT_BOARD_BALLOT_MANIFEST_AND_CVRS_OK,
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
    AUDIT_IN_PROGRESS,
    AUDIT_REPORT_SUBMITTED
  }
}
