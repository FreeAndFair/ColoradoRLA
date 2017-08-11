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

import us.freeandfair.corla.asm.ASMEvent.DosDashboardEvent;
import us.freeandfair.corla.asm.ASMState.DosDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.DosDashboardTransitionFunction;

/**
 * The ASM for the Department of State Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class DoSDashboardASM extends ASM {
  /**
   * Create the Department of State Dashboard ASM.
   * @trace asm.dos_asm 
   */
  public DoSDashboardASM() {
    super();
    final Set<ASMState> states = new HashSet<>();
    for (final ASMState s : DosDashboardState.values()) {
      states.add(s);
    }
    final Set<ASMEvent> events = new HashSet<>();
    for (final ASMEvent e : DosDashboardEvent.values()) {
      events.add(e);
    }
    final Set<ASMTransition> set = new HashSet<>();
    for (final DosDashboardTransitionFunction t : 
        DosDashboardTransitionFunction.values()) {
      set.add(t.value());
    }
    final Set<ASMState> final_states = new HashSet<ASMState>();
    final_states.add(DosDashboardState.AUDIT_RESULTS_PUBLISHED);
    initialize(states, events, set, 
               DosDashboardState.DOS_INITIAL_STATE,
               final_states);
  }
}
