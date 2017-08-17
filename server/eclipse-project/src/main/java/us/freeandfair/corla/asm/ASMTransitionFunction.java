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
    ONE(new ASMTransition(DOS_INITIAL_STATE, 
                          ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
                          RISK_LIMITS_SET)),
    /*
    // right now, PUBLISH_AUDIT_DATA_EVENT never happens, so we jump
    // straight to DATA_TO_AUDIT_PUBLISHED
     
    TWO(new ASMTransition(RISK_LIMITS_SET, 
                          SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
                          CONTESTS_TO_AUDIT_IDENTIFIED)),
    THREE(new ASMTransition(CONTESTS_TO_AUDIT_IDENTIFIED, 
                            PUBLISH_AUDIT_DATA_EVENT,
                            DATA_TO_AUDIT_PUBLISHED)),
    */
    TWOTHREE(new ASMTransition(RISK_LIMITS_SET,
                               SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
                               DATA_TO_AUDIT_PUBLISHED)),
    FOUR(new ASMTransition(DATA_TO_AUDIT_PUBLISHED, 
                           PUBLIC_SEED_EVENT,
                           RANDOM_SEED_PUBLISHED)),
    FIVE(new ASMTransition(RANDOM_SEED_PUBLISHED, 
                           PUBLISH_BALLOTS_TO_AUDIT_EVENT,
                           DOS_AUDIT_ONGOING)),
    SIX(new ASMTransition(DOS_AUDIT_ONGOING, 
                          AUDIT_EVENT,
                          DOS_AUDIT_ONGOING)),
    SEVEN(new ASMTransition(DOS_AUDIT_ONGOING, 
                            INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
                            DOS_AUDIT_ONGOING)),
    EIGHT(new ASMTransition(DOS_AUDIT_ONGOING, 
                            COUNTY_AUDIT_COMPLETE_EVENT,
                            DOS_AUDIT_ONGOING)),
    NINE(new ASMTransition(DOS_AUDIT_ONGOING,
                           AUDIT_COMPLETE_EVENT,
                           DOS_AUDIT_COMPLETE)),
    TEN(new ASMTransition(DOS_AUDIT_COMPLETE, 
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
    ONE(new ASMTransition(COUNTY_INITIAL_STATE, 
                          ESTABLISH_AUDIT_BOARD_EVENT,
                          AUDIT_BOARD_ESTABLISHED_STATE)),
    TWO(new ASMTransition(AUDIT_BOARD_ESTABLISHED_STATE, 
                          UPLOAD_BALLOT_MANIFEST_EVENT,
                          UPLOAD_BALLOT_MANIFEST_SUCCESSFUL)),
    THREE(new ASMTransition(AUDIT_BOARD_ESTABLISHED_STATE, 
                            DEADLINE_MISSED_EVENT,
                            UPLOAD_BALLOT_MANIFEST_TOO_LATE)),
    FOUR(new ASMTransition(UPLOAD_BALLOT_MANIFEST_SUCCESSFUL, 
                           UPLOAD_CVRS_EVENT,
                           UPLOAD_CVRS_SUCCESSFUL)),
    FIVE(new ASMTransition(UPLOAD_BALLOT_MANIFEST_SUCCESSFUL, 
                           DEADLINE_MISSED_EVENT,
                           UPLOAD_CVRS_TOO_LATE)),
    SIX(new ASMTransition(UPLOAD_CVRS_SUCCESSFUL, 
                          COUNTY_START_AUDIT_EVENT,
                          COUNTY_AUDIT_UNDERWAY)),
    SEVEN(new ASMTransition(COUNTY_AUDIT_UNDERWAY, 
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
    ONE(new ASMTransition(AUDIT_INITIAL_STATE, 
                          AUDIT_BOARD_START_AUDIT_EVENT,
                          AUDIT_IN_PROGRESS_STATE)),
    TWO(new ASMTransition(AUDIT_IN_PROGRESS_STATE, 
                          REPORT_MARKINGS_EVENT,
                          AUDIT_IN_PROGRESS_STATE)),
    THREE(new ASMTransition(AUDIT_IN_PROGRESS_STATE,
                            REPORT_BALLOT_NOT_FOUND_EVENT,
                            AUDIT_IN_PROGRESS_STATE)),
    FOUR(new ASMTransition(AUDIT_IN_PROGRESS_STATE,
                           SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
                           AUDIT_IN_PROGRESS_STATE)),
    FIVE(new ASMTransition(AUDIT_IN_PROGRESS_STATE,
                           SUBMIT_AUDIT_REPORT_EVENT,
                           AUDIT_IN_PROGRESS_STATE)),
    SIX(new ASMTransition(AUDIT_IN_PROGRESS_STATE,
                          SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
                          AUDIT_IN_PROGRESS_STATE)),
    SEVEN(new ASMTransition(AUDIT_IN_PROGRESS_STATE, 
                            SUBMIT_AUDIT_REPORT_EVENT,
                            AUDIT_REPORT_SUBMITTED_STATE));    
    
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
