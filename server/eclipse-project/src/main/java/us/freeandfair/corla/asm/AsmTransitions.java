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

import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmState.AuditBoardDashboardState.*;
import static us.freeandfair.corla.asm.AsmState.DosDashboardState.*;

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
    ONE(new Pair<AsmState, AsmEvent>(DOS_INITIAL_STATE, 
        AUTHENTICATE_STATE_ADMINISTRATOR_EVENT),
        DOS_AUTHENTICATED),
    TWO(new Pair<AsmState, AsmEvent>(DOS_AUTHENTICATED, 
        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT),
        RISK_LIMITS_SET),
    THREE(new Pair<AsmState, AsmEvent>(RISK_LIMITS_SET, 
        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT),
          CONTESTS_TO_AUDIT_IDENTIFIED),
    FOUR(new Pair<AsmState, AsmEvent>(CONTESTS_TO_AUDIT_IDENTIFIED, 
        PUBLIC_SEED_EVENT),
         RANDOM_SEED_PUBLISHED),
    FIVE(new Pair<AsmState, AsmEvent>(RANDOM_SEED_PUBLISHED, 
        PUBLISH_BALLOTS_TO_AUDIT_EVENT),
         BALLOT_ORDER_DEFINED),
    SIX(new Pair<AsmState, AsmEvent>(BALLOT_ORDER_DEFINED, 
        DOS_SKIP_EVENT),
        AUDIT_READY_TO_START),
    // @review kiniry Should this transition just be a DOS_SKIP?
    SEVEN(new Pair<AsmState, AsmEvent>(AUDIT_READY_TO_START, 
        AUDIT_EVENT),
          DOS_AUDIT_ONGOING),
    EIGHT(new Pair<AsmState, AsmEvent>(DOS_AUDIT_ONGOING, 
        AUDIT_EVENT),
          DOS_AUDIT_ONGOING),
    NINE(new Pair<AsmState, AsmEvent>(AUDIT_READY_TO_START, 
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT),
         AUDIT_READY_TO_START),
    TEN(new Pair<AsmState, AsmEvent>(DOS_AUDIT_ONGOING, 
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT),
        DOS_AUDIT_ONGOING),
    ELEVEN(new Pair<AsmState, AsmEvent>(DOS_AUDIT_ONGOING, 
        COUNTY_AUDIT_COMPLETE_EVENT),
           DOS_AUDIT_COMPLETE),
    TWELVE(new Pair<AsmState, AsmEvent>(DOS_AUDIT_COMPLETE, 
        PUBLISH_AUDIT_REPORT_EVENT),
           AUDIT_RESULTS_PUBLISHED),
    REFRESH(new Pair<AsmState, AsmEvent>(DOS_AUDIT_ONGOING, 
        DOS_REFRESH_EVENT),
            DOS_AUDIT_ONGOING);
    
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
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_FILE_TYPE_WRONG),
    MANIFEST_PARSED(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_PARSED),
    MANIFEST_INTERRUPTED(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_TRANSMISSION_INTERRUPTED),
    MANIFEST_LATE(
      new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE, 
               AsmEvent.CountyDashboardEvent.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_EVENT),
        AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_TOO_LATE),
    MANIFEST_RESTART_FROM_HASH_WRONG(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_HASH_WRONG, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE),
    MANIFEST_RESTART_FROM_TYPE_WRONG(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_FILE_TYPE_WRONG, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE),
    MANIFEST_RESTART_FROM_INTERRUPTED(
       new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.COUNTY_UPLOAD_VERIFIED_BALLOT_MANIFEST_DATA_TRANSMISSION_INTERRUPTED, 
                AsmEvent.CountyDashboardEvent.SKIP_EVENT),
         AsmState.CountyDashboardState.AUDIT_BOARD_ESTABLISHED_STATE);
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
    ONE(new Pair<AsmState, AsmEvent>(AUDIT_INITIAL_STATE, 
            AUDIT_SKIP_EVENT),
        AUDIT_IN_PROGRESS_STATE),
    TWO(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
            REPORT_MARKINGS_EVENT),
        AUDIT_IN_PROGRESS_STATE),
    THREE(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
              REPORT_BALLOT_NOT_FOUND_EVENT),
          AUDIT_IN_PROGRESS_STATE),
    FOUR(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
             SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT),
         AUDIT_IN_PROGRESS_STATE),
    FIVE(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
             SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT),
         SIGNOFF_INTERMEDIATE_AUDIT_REPORT_STATE),
    SIX(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
            SUBMIT_AUDIT_REPORT_EVENT),
        SUBMIT_AUDIT_REPORT_STATE),
    SEVEN(new Pair<AsmState, AsmEvent>(SIGNOFF_INTERMEDIATE_AUDIT_REPORT_STATE,
              AUDIT_SKIP_EVENT),
          AUDIT_IN_PROGRESS_STATE),
    EIGHT(new Pair<AsmState, AsmEvent>(AUDIT_IN_PROGRESS_STATE, 
              AUDIT_REFRESH_EVENT),
          AUDIT_IN_PROGRESS_STATE);
    
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
