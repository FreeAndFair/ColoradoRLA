/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.*;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.*;
import static us.freeandfair.corla.asm.ASMState.DoSDashboardState.*;

/**
 * The generic idea of an ASM transition function.
 * @trace asm.asm_transition_function
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.AvoidDuplicateLiterals"})
public interface ASMTransitionFunction {
  /**
   * The Department of State Dashboard's transition function.
   * @trace asm.dos_dashboard_next_state
   */
  enum DoSDashboardTransitionFunction implements ASMTransitionFunction {
    A(new ASMTransition(DOS_INITIAL_STATE, 
                        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
                        RISK_LIMITS_SET)),
    B(new ASMTransition(RISK_LIMITS_SET, 
                        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
                        CONTESTS_TO_AUDIT_IDENTIFIED)),
    C(new ASMTransition(CONTESTS_TO_AUDIT_IDENTIFIED, 
                        PUBLISH_AUDIT_DATA_EVENT,
                        DATA_TO_AUDIT_PUBLISHED)),
    D(new ASMTransition(DATA_TO_AUDIT_PUBLISHED, 
                        PUBLIC_SEED_EVENT,
                        RANDOM_SEED_PUBLISHED)),
    E(new ASMTransition(RANDOM_SEED_PUBLISHED, 
                        PUBLISH_BALLOTS_TO_AUDIT_EVENT,
                        DOS_AUDIT_ONGOING)),
    F(new ASMTransition(DOS_AUDIT_ONGOING, 
                        AUDIT_EVENT,
                        DOS_AUDIT_ONGOING)),
    G(new ASMTransition(DOS_AUDIT_ONGOING, 
                        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
                        DOS_AUDIT_ONGOING)),
    H(new ASMTransition(DOS_AUDIT_ONGOING, 
                        COUNTY_AUDIT_COMPLETE_EVENT,
                        DOS_AUDIT_ONGOING)),
    I(new ASMTransition(DOS_AUDIT_ONGOING,
                        AUDIT_COMPLETE_EVENT,
                        DOS_AUDIT_COMPLETE)),
    J(new ASMTransition(DOS_AUDIT_COMPLETE, 
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
                        ESTABLISH_AUDIT_BOARD_EVENT,
                        AUDIT_BOARD_OK)),
    B(new ASMTransition(COUNTY_INITIAL_STATE,
                        UPLOAD_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_OK)),
    C(new ASMTransition(COUNTY_INITIAL_STATE,
                        UPLOAD_CVRS_EVENT,
                        CVRS_OK)),
    D(new ASMTransition(COUNTY_INITIAL_STATE,
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    E(new ASMTransition(AUDIT_BOARD_OK, 
                        UPLOAD_BALLOT_MANIFEST_EVENT,
                        AUDIT_BOARD_AND_BALLOT_MANIFEST_OK)),
    F(new ASMTransition(AUDIT_BOARD_OK,
                        UPLOAD_CVRS_EVENT,
                        AUDIT_BOARD_AND_CVRS_OK)),
    G(new ASMTransition(AUDIT_BOARD_OK, 
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    H(new ASMTransition(BALLOT_MANIFEST_OK,
                        ESTABLISH_AUDIT_BOARD_EVENT,
                        AUDIT_BOARD_AND_BALLOT_MANIFEST_OK)),
    I(new ASMTransition(BALLOT_MANIFEST_OK, 
                        UPLOAD_CVRS_EVENT,
                        BALLOT_MANIFEST_AND_CVRS_OK)),
    J(new ASMTransition(BALLOT_MANIFEST_OK, 
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    K(new ASMTransition(CVRS_OK,
                        ESTABLISH_AUDIT_BOARD_EVENT,
                        AUDIT_BOARD_AND_CVRS_OK)),
    L(new ASMTransition(CVRS_OK,
                        UPLOAD_BALLOT_MANIFEST_EVENT,
                        BALLOT_MANIFEST_AND_CVRS_OK)),
    M(new ASMTransition(CVRS_OK,
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    N(new ASMTransition(AUDIT_BOARD_AND_BALLOT_MANIFEST_OK,
                        UPLOAD_CVRS_EVENT,
                        AUDIT_BOARD_BALLOT_MANIFEST_AND_CVRS_OK)),
    O(new ASMTransition(AUDIT_BOARD_AND_BALLOT_MANIFEST_OK,
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    P(new ASMTransition(AUDIT_BOARD_AND_CVRS_OK,
                        UPLOAD_BALLOT_MANIFEST_EVENT,
                        AUDIT_BOARD_BALLOT_MANIFEST_AND_CVRS_OK)),
    Q(new ASMTransition(AUDIT_BOARD_AND_CVRS_OK,
                        COUNTY_START_AUDIT_EVENT,
                        DEADLINE_MISSED)),
    R(new ASMTransition(AUDIT_BOARD_BALLOT_MANIFEST_AND_CVRS_OK, 
                        COUNTY_START_AUDIT_EVENT,
                        COUNTY_AUDIT_UNDERWAY)),
    S(new ASMTransition(COUNTY_AUDIT_UNDERWAY, 
                        COUNTY_AUDIT_COMPLETE_EVENT,
                        COUNTY_AUDIT_COMPLETE));

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
    A(new ASMTransition(AUDIT_INITIAL_STATE, 
                        AUDIT_BOARD_START_AUDIT_EVENT,
                        AUDIT_IN_PROGRESS)),
    B(new ASMTransition(AUDIT_IN_PROGRESS, 
                        REPORT_MARKINGS_EVENT,
                        AUDIT_IN_PROGRESS)),
    C(new ASMTransition(AUDIT_IN_PROGRESS,
                        REPORT_BALLOT_NOT_FOUND_EVENT,
                        AUDIT_IN_PROGRESS)),
    D(new ASMTransition(AUDIT_IN_PROGRESS,
                        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
                        AUDIT_IN_PROGRESS)),
    E(new ASMTransition(AUDIT_IN_PROGRESS,
                        SUBMIT_AUDIT_REPORT_EVENT,
                        AUDIT_IN_PROGRESS)),
    F(new ASMTransition(AUDIT_IN_PROGRESS,
                        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
                        AUDIT_IN_PROGRESS)),
    G(new ASMTransition(AUDIT_IN_PROGRESS, 
                        SUBMIT_AUDIT_REPORT_EVENT,
                        AUDIT_REPORT_SUBMITTED));
    
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
    AuditBoardDashboardTransitionFunction(final ASMTransition the_transition) {
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
   * @return the transition of this transition function element.
   */
  ASMTransition value();
}
