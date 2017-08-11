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

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.AuditBoardDashboardTransitionFunction;

/**
 * The ASM for the Audit Board Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class AuditBoardDashboardASM extends ASM {
  /**
   * Create the Audit Board Dashboard ASM.
   * @trace asm.county_dashboard_asm
   */
  public AuditBoardDashboardASM() {
    super();
    final Set<ASMState> states = new HashSet<>();
    for (final ASMState s : AuditBoardDashboardState.values()) {
      states.add(s);
    }
    final Set<ASMEvent> events = new HashSet<>();
    for (final ASMEvent e : AuditBoardDashboardEvent.values()) {
      events.add(e);
    }
    final Set<ASMTransition> set = new HashSet<>();
    for (final AuditBoardDashboardTransitionFunction t : 
         AuditBoardDashboardTransitionFunction.values()) {
      set.add(t.value());
    }
    final Set<ASMState> final_states = new HashSet<ASMState>();
    final_states.add(AuditBoardDashboardState.AUDIT_REPORT_SUBMITTED_STATE);
    initialize(states, events, set, 
               AuditBoardDashboardState.AUDIT_INITIAL_STATE,
               final_states);
  }
}
