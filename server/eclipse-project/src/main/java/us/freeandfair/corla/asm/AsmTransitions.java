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

import us.freeandfair.corla.util.Pair;

/**
 * The generic idea of a set of ASM transitions.
 * @trace asm.asm_transition_function
 */
public interface AsmTransitions {
  /**
   * The Department of State Dashboard's transition function.
   * @trace asm.dos_dashboard_next_state
   */
  enum DosDashboardTransitions implements AsmTransitions {
    AUTHENTICATE(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.INITIAL_STATE, 
      AsmEvent.DosDashboardEvent.AUTHENTICATE_STATE_ADMINISTRATOR_EVENT),
      AsmState.DosDashboardState.AUTHENTICATED),
    SET_RISK_LIMIT(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUTHENTICATED, 
      AsmEvent.DosDashboardEvent.ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT),
      AsmState.DosDashboardState.RISK_LIMITS_SET),
    CONTESTS_SELECTED(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.RISK_LIMITS_SET, 
      AsmEvent.DosDashboardEvent.SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT),
      AsmState.DosDashboardState.CONTESTS_TO_AUDIT_IDENTIFIED),
    RANDOM_SEED(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.CONTESTS_TO_AUDIT_IDENTIFIED, 
      AsmEvent.DosDashboardEvent.PUBLIC_SEED_EVENT),
      AsmState.DosDashboardState.RANDOM_SEED_PUBLISHED),
    PUBLISH_BALLOTS_TO_AUDIT(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.RANDOM_SEED_PUBLISHED, 
      AsmEvent.DosDashboardEvent.PUBLISH_BALLOTS_TO_AUDIT_EVENT),
      AsmState.DosDashboardState.BALLOT_ORDER_DEFINED),
    AUDIT_READY(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.BALLOT_ORDER_DEFINED, 
      AsmEvent.DosDashboardEvent.SKIP_EVENT),
      AsmState.DosDashboardState.AUDIT_READY_TO_START),
    AUDIT_START(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_READY_TO_START, 
      AsmEvent.DosDashboardEvent.AUDIT_EVENT),
      AsmState.DosDashboardState.AUDIT_ONGOING),
    AUDIT_CONTINUES(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_ONGOING, 
      AsmEvent.DosDashboardEvent.AUDIT_EVENT),
      AsmState.DosDashboardState.AUDIT_ONGOING),
    HAND_CONTEST_INIT(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_READY_TO_START, 
      AsmEvent.DosDashboardEvent.INDICATE_FULL_HAND_COUNT_CONTEST_EVENT),
      AsmState.DosDashboardState.AUDIT_READY_TO_START),
    HAND_CONTEST_CONT(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_ONGOING, 
      AsmEvent.DosDashboardEvent.INDICATE_FULL_HAND_COUNT_CONTEST_EVENT),
      AsmState.DosDashboardState.AUDIT_ONGOING),
    COUNTY_COMPLETE(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_ONGOING, 
      AsmEvent.DosDashboardEvent.COUNTY_AUDIT_COMPLETE_EVENT),
      AsmState.DosDashboardState.AUDIT_ONGOING),
    AUDIT_COMPLETE(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_COMPLETE, 
      AsmEvent.DosDashboardEvent.PUBLISH_AUDIT_REPORT_EVENT),
      AsmState.DosDashboardState.AUDIT_RESULTS_PUBLISHED),
    REFRESH(
      new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.AUDIT_ONGOING, 
      AsmEvent.DosDashboardEvent.REFRESH_EVENT),
      AsmState.DosDashboardState.AUDIT_ONGOING);
    
    /**
     * The pair holding a single transition.
     */
    protected final Pair<Pair<AsmState, AsmEvent>, AsmState> my_pair;
    
    /**
     * Create a transition.
     * @param the_pair the (current state, event) pair.
     * @param the_state the state transitioned to when the pair is witnessed.
     */
    DosDashboardTransitions(final Pair<AsmState, AsmEvent> the_pair,
                            final AsmState the_state) {
      my_pair = new Pair<Pair<AsmState, AsmEvent>, AsmState>(the_pair, the_state);
    }
  }
  
  /**
   * The County Board Dashboard's transition function.
   * @trace asm.county_dashboard_next_state
   */
  enum CountyDashboardTransitions implements AsmTransitions {
    AUTHENTICATE(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.INITIAL_STATE, 
               AsmEvent.CountyDashboardEvent.AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT),
        AsmState.CountyDashboardState.AUTHENTICATED),
    ESTABLISH_AUDIT_BOARD(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUTHENTICATED, 
               AsmEvent.CountyDashboardEvent.ESTABLISH_AUDIT_BOARD_EVENT),
        AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE),
    MANIFEST_SUCESSFUL(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_CHECKING_HASH),
    MANIFEST_CHECKING(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_CHECKING_HASH, 
               AsmEvent.CountyDashboardEvent.SKIP_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_UPLOAD_SUCESSFUL),
    MANIFEST_VERIFIED(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_UPLOAD_SUCESSFUL, 
               AsmEvent.CountyDashboardEvent.SKIP_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_HASH_VERIFIED),
    MANIFEST_WRONG(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_UPLOAD_SUCESSFUL, 
               AsmEvent.CountyDashboardEvent.SKIP_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_HASH_WRONG),
    MANIFEST_TYPE(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_FILE_TYPE_WRONG),
    MANIFEST_PARSED(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_PARSED),
    MANIFEST_INTERRUPTED(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_TRANSMISSION_INTERRUPTED),
    MANIFEST_LATE(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_TOO_LATE),
    MANIFEST_RESTART_FROM_HASH_WRONG(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_HASH_WRONG, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE),
    MANIFEST_RESTART_FROM_TYPE_WRONG(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_FILE_TYPE_WRONG, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE),
    MANIFEST_RESTART_FROM_INTERRUPTED(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_TRANSMISSION_INTERRUPTED, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.ESTABLISH_AUDIT_BOARD_STATE);
    // @todo kiniry Add all remaining transitions.
    
    /**
     * The pair holding a single transition.
     */
    protected final Pair<Pair<AsmState, AsmEvent>, AsmState> my_pair;
    
    /**
     * Create a transition.
     * @param the_pair the (current state, event) pair.
     * @param the_state the state transitioned to when the pair is witnessed.
     */
    CountyDashboardTransitions(final Pair<AsmState, AsmEvent> the_pair,
                               final AsmState the_state) {
      my_pair = new Pair<Pair<AsmState, AsmEvent>, AsmState>(the_pair, the_state);
    }
  }
  
  /**
   * The Audit Board Dashboard's transition function.
   * @trace asm.audit_board_dashboard_next_state
   */
  enum AuditBoardDashboardTransitions implements AsmTransitions {
    START(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.INITIAL_STATE, 
        AsmEvent.AuditBoardDashboardEvent.SKIP_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE),
    REPORT_MARKING(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.REPORT_MARKINGS_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE),
    REPORT_BNF(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.REPORT_BALLOT_NOT_FOUND_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE),
    SUBMIT_AIR(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE),
    SUBMIT_AR(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_REPORT_EVENT),
        AsmState.AuditBoardDashboardState.SUBMIT_AUDIT_REPORT_STATE),
    SUBMIT_IAR(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT),
        AsmState.AuditBoardDashboardState.SIGNOFF_INTERMEDIATE_AUDIT_REPORT_STATE),
    RESTART(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.SIGNOFF_INTERMEDIATE_AUDIT_REPORT_STATE,
        AsmEvent.AuditBoardDashboardEvent.SKIP_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE),
    REFRESH(
        new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE, 
        AsmEvent.AuditBoardDashboardEvent.REFRESH_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE);
    
    /**
     * The pair holding a single transition.
     */
    protected final Pair<Pair<AsmState, AsmEvent>, AsmState> my_pair;
    
    /**
     * Create a transition.
     * @param the_pair the (current state, event) pair.
     * @param the_state the state transitioned to when the pair is witnessed.
     */
    AuditBoardDashboardTransitions(final Pair<AsmState, AsmEvent> the_pair,
                                   final AsmState the_state) {
      my_pair = new Pair<Pair<AsmState, AsmEvent>, AsmState>(the_pair, the_state);
    }
  }
}
