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
    ONE(new Pair<AsmState, AsmEvent>(AsmState.DosDashboardState.INITIAL_STATE, 
        AsmEvent.DosDashboardEvent.AUTHENTICATE_STATE_ADMINISTRATOR_EVENT),
        AsmState.DosDashboardState.AUTHENTICATED);
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
    ONE(new Pair<AsmState, AsmEvent>(AsmState.CountyDashboardState.INITIAL_STATE, 
        AsmEvent.CountyDashboardEvent.AUTHENTICATE_COUNTY_ADMINISTRATOR),
        AsmState.CountyDashboardState.AUTHENTICATED);
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
    ONE(new Pair<AsmState, AsmEvent>(AsmState.AuditBoardDashboardState.INITIAL_STATE, 
        AsmEvent.AuditBoardDashboardEvent.REPORT_MARKINGS_EVENT),
        AsmState.AuditBoardDashboardState.AUDIT_IN_PROGRESS_STATE);
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
    AuditBoardDashboardTransitions(final Pair<AsmState, AsmEvent> the_pair,
                                   final AsmState the_state) {
      my_pair = new Pair<Pair<AsmState, AsmEvent>, AsmState>(the_pair, the_state);
    }
  }
}
