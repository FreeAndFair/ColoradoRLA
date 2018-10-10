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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.*;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.*;

import us.freeandfair.corla.util.SetCreator;

/**
 * The generic idea of an ASM transition function.
 * @trace asm.asm_transition_function
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidDuplicateLiterals"})
public interface ASMTransitionFunction {
  /**
   * The Department of State Dashboard's transition function.
   * @trace asm.dos_dashboard_next_state
   */
  enum DoSDashboardTransitionFunction implements ASMTransitionFunction {
    A(new ASMTransition(SetCreator.setOf(DOS_INITIAL_STATE,
                                         PARTIAL_AUDIT_INFO_SET,
                                         COMPLETE_AUDIT_INFO_SET),
                        PARTIAL_AUDIT_INFO_EVENT,
                        PARTIAL_AUDIT_INFO_SET)),
    B(new ASMTransition(SetCreator.setOf(DOS_INITIAL_STATE,
                                         PARTIAL_AUDIT_INFO_SET,
                                         COMPLETE_AUDIT_INFO_SET),
                        COMPLETE_AUDIT_INFO_EVENT,
                        COMPLETE_AUDIT_INFO_SET)),
    D(new ASMTransition(COMPLETE_AUDIT_INFO_SET,
                         DOS_START_ROUND_EVENT,
                         DOS_AUDIT_ONGOING)),
    E(new ASMTransition(DOS_AUDIT_ONGOING,
                        SetCreator.setOf(AUDIT_EVENT,
                                         DOS_COUNTY_AUDIT_COMPLETE_EVENT,
                                         DOS_START_ROUND_EVENT),
                        DOS_AUDIT_ONGOING)),
    F(new ASMTransition(DOS_AUDIT_ONGOING,
                        DOS_ROUND_COMPLETE_EVENT,
                        DOS_ROUND_COMPLETE)),
    G(new ASMTransition(DOS_ROUND_COMPLETE,
                        DOS_START_ROUND_EVENT,
                        DOS_AUDIT_ONGOING)),
    H(new ASMTransition(SetCreator.setOf(DOS_AUDIT_ONGOING,
                                         DOS_ROUND_COMPLETE),
                        DOS_AUDIT_COMPLETE_EVENT,
                        DOS_AUDIT_COMPLETE)),
    I(new ASMTransition(DOS_AUDIT_COMPLETE,
                        PUBLISH_AUDIT_REPORT_EVENT,
                        AUDIT_RESULTS_PUBLISHED));

    /**
     * A single transition.
     */
    @SuppressWarnings("PMD.ConstantsInInterface")
    private final transient ASMTransition my_transition;

    /**
     * Create a transition.
     * @param the_pair the (current state, event) pair.
     * @param the_state the state transitioned to when the pair is witnessed.
     */
    DoSDashboardTransitionFunction(final ASMTransition the_transition) {
      my_transition = the_transition;
    }

    /**
     * @return the pair encoding this enumeration.
     */
    public ASMTransition value() {
      return my_transition;
    }
  }

  /**
   * The County Board Dashboard's transition function.
   * @trace asm.county_dashboard_next_state
   */
  enum CountyDashboardTransitionFunction implements ASMTransitionFunction {
    A(new ASMTransition(COUNTY_INITIAL_STATE,
                        IMPORT_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_OK)),
    B(new ASMTransition(COUNTY_INITIAL_STATE,
                        IMPORT_CVRS_EVENT,
                        CVRS_IMPORTING)),
    C(new ASMTransition(CVRS_IMPORTING,
                        CVR_IMPORT_SUCCESS_EVENT,
                        CVRS_OK)),
    D(new ASMTransition(CVRS_IMPORTING,
                        CVR_IMPORT_FAILURE_EVENT,
                        COUNTY_INITIAL_STATE)),
    E(new ASMTransition(BALLOT_MANIFEST_OK,
                        IMPORT_CVRS_EVENT,
                        BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING)),
    F(new ASMTransition(BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING,
                        CVR_IMPORT_SUCCESS_EVENT,
                        BALLOT_MANIFEST_AND_CVRS_OK)),
    G(new ASMTransition(BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING,
                        CVR_IMPORT_FAILURE_EVENT,
                        BALLOT_MANIFEST_OK)),
    H(new ASMTransition(BALLOT_MANIFEST_OK,
                        IMPORT_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_OK)),
    I(new ASMTransition(CVRS_OK,
                        IMPORT_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_AND_CVRS_OK)),
    J(new ASMTransition(CVRS_OK,
                        IMPORT_CVRS_EVENT,
                        CVRS_IMPORTING)),
    K(new ASMTransition(BALLOT_MANIFEST_AND_CVRS_OK,
                        IMPORT_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_AND_CVRS_OK)),
    L(new ASMTransition(BALLOT_MANIFEST_AND_CVRS_OK,
                        IMPORT_CVRS_EVENT,
                        BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING)),
    M(new ASMTransition(BALLOT_MANIFEST_AND_CVRS_OK,
                        COUNTY_START_AUDIT_EVENT,
                        COUNTY_AUDIT_UNDERWAY)),
    N(new ASMTransition(COUNTY_AUDIT_UNDERWAY,
                        COUNTY_AUDIT_COMPLETE_EVENT,
                        COUNTY_AUDIT_COMPLETE)),
    O(new ASMTransition(SetCreator.setOf(COUNTY_INITIAL_STATE,
                                         BALLOT_MANIFEST_OK,
                                         CVRS_OK,
                                         CVRS_IMPORTING,
                                         BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING),
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED));

    /**
     * A single transition.
     */
    @SuppressWarnings("PMD.ConstantsInInterface")
    private final transient ASMTransition my_transition;

    /**
     * Create a transition.
     * @param the_pair the (current state, event) pair.
     * @param the_state the state transitioned to when the pair is witnessed.
     */
    CountyDashboardTransitionFunction(final ASMTransition the_transition) {
      my_transition = the_transition;
    }

    /**
     * @return the pair encoding this enumeration.
     */
    public ASMTransition value() {
      return my_transition;
    }
  }

  /**
   * The Audit Board Dashboard's transition function.
   * @trace asm.audit_board_dashboard_next_state
   */
  enum AuditBoardDashboardTransitionFunction implements ASMTransitionFunction {
    A(new ASMTransition(SetCreator.setOf(AUDIT_INITIAL_STATE,
                                         WAITING_FOR_ROUND_START_NO_AUDIT_BOARD),
                        ROUND_START_EVENT,
                        ROUND_IN_PROGRESS_NO_AUDIT_BOARD)),
    B(new ASMTransition(SetCreator.setOf(AUDIT_INITIAL_STATE,
                                         WAITING_FOR_ROUND_START_NO_AUDIT_BOARD),
                        SIGN_IN_AUDIT_BOARD_EVENT,
                        WAITING_FOR_ROUND_START)),
    C(new ASMTransition(AUDIT_INITIAL_STATE,
                        SetCreator.setOf(NO_CONTESTS_TO_AUDIT_EVENT,
                                         RISK_LIMIT_ACHIEVED_EVENT),
                        AUDIT_COMPLETE)),
    D(new ASMTransition(AUDIT_INITIAL_STATE,
                        COUNTY_DEADLINE_MISSED_EVENT,
                        UNABLE_TO_AUDIT)),
    F(new ASMTransition(WAITING_FOR_ROUND_START,
                        ROUND_START_EVENT,
                        ROUND_IN_PROGRESS)),
    G(new ASMTransition(WAITING_FOR_ROUND_START,
                        SIGN_OUT_AUDIT_BOARD_EVENT,
                        WAITING_FOR_ROUND_START_NO_AUDIT_BOARD)),
    H(new ASMTransition(WAITING_FOR_ROUND_START,
                        RISK_LIMIT_ACHIEVED_EVENT,
                        AUDIT_COMPLETE)),
    M(new ASMTransition(ROUND_IN_PROGRESS,
                        SetCreator.setOf(REPORT_MARKINGS_EVENT,
                                         REPORT_BALLOT_NOT_FOUND_EVENT,
                                         SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT),
                        ROUND_IN_PROGRESS)),
    N(new ASMTransition(ROUND_IN_PROGRESS,
                        SIGN_OUT_AUDIT_BOARD_EVENT,
                        ROUND_IN_PROGRESS_NO_AUDIT_BOARD)),
    O(new ASMTransition(ROUND_IN_PROGRESS,
                        ROUND_COMPLETE_EVENT,
                        WAITING_FOR_ROUND_SIGN_OFF)),

    // this can happen if there are no ballots to audit in the first round
    O1(new ASMTransition(AUDIT_INITIAL_STATE,
                        ROUND_COMPLETE_EVENT,
                        WAITING_FOR_ROUND_START)),

    // this can happen if there are no ballots to audit in subsequent rounds
    O2(new ASMTransition(WAITING_FOR_ROUND_START,
                         ROUND_COMPLETE_EVENT,
                         WAITING_FOR_ROUND_START)),

    // this can happen if there are no ballots for an audit board
    O3(new ASMTransition(WAITING_FOR_ROUND_START,
                         ROUND_SIGN_OFF_EVENT,
                         WAITING_FOR_ROUND_START)),


    /* We probably want this transition eventually, but not for CDOS
    EARLY(new ASMTransition(ROUND_IN_PROGRESS,
                            RISK_LIMIT_ACHIEVED_EVENT,
                            AUDIT_COMPLETE)), */
    P(new ASMTransition(ROUND_IN_PROGRESS_NO_AUDIT_BOARD,
                        SIGN_IN_AUDIT_BOARD_EVENT,
                        ROUND_IN_PROGRESS)),
    Q(new ASMTransition(WAITING_FOR_ROUND_SIGN_OFF,
                        SIGN_OUT_AUDIT_BOARD_EVENT,
                        WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD)),
    R(new ASMTransition(WAITING_FOR_ROUND_SIGN_OFF,
                        ROUND_SIGN_OFF_EVENT,
                        WAITING_FOR_ROUND_START)),
    S(new ASMTransition(WAITING_FOR_ROUND_SIGN_OFF,
                        SetCreator.setOf(RISK_LIMIT_ACHIEVED_EVENT,
                                         BALLOTS_EXHAUSTED_EVENT),
                        AUDIT_COMPLETE)),
    T(new ASMTransition(WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD,
                        SIGN_IN_AUDIT_BOARD_EVENT,
                        WAITING_FOR_ROUND_SIGN_OFF)),
    U(new ASMTransition(SetCreator.setOf(AUDIT_INITIAL_STATE,
                                         WAITING_FOR_ROUND_START,
                                         WAITING_FOR_ROUND_START_NO_AUDIT_BOARD,
                                         ROUND_IN_PROGRESS,
                                         ROUND_IN_PROGRESS_NO_AUDIT_BOARD,
                                         WAITING_FOR_ROUND_SIGN_OFF,
                                         WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD),
                        ABORT_AUDIT_EVENT,
                        AUDIT_ABORTED));

    /**
     * A single transition.
     */
    @SuppressWarnings("PMD.ConstantsInInterface")
    private final transient ASMTransition my_transition;

    /**
     * Create a transition.
     * @param the_transition the transition.
     */
    AuditBoardDashboardTransitionFunction(final ASMTransition the_transition) {
      my_transition = the_transition;
    }

    /**
     * @return the transition.
     */
    public ASMTransition value() {
      return my_transition;
    }
  }

  /**
   * @return the value of this transition function element.
   */
  ASMTransition value();
}
