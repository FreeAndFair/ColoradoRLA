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

import us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent;
import us.freeandfair.corla.asm.AsmState.DosDashboardState;
import us.freeandfair.corla.asm.AsmTransitionFunction.DosDashboardTransitionFunction;

/**
 * The ASM for the Department of State Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class DosDashboardAsm extends Asm {
  /**
   * Create the Department of State Dashboard ASM.
   * @trace asm.dos_asm 
   */
  public DosDashboardAsm() {
    super();
    final Set<AsmState> states = new HashSet<>();
    for (final AsmState s : DosDashboardState.values()) {
      states.add(s);
    }
    final Set<AsmEvent> events = new HashSet<>();
    for (final AsmEvent e : DosDashboardEvent.values()) {
      events.add(e);
    }
    final Set<AsmTransition> set = new HashSet<>();
    for (final DosDashboardTransitionFunction t : 
        DosDashboardTransitionFunction.values()) {
      set.add(t.value());
    }
    final Set<AsmState> final_states = new HashSet<AsmState>();
    final_states.add(DosDashboardState.AUDIT_RESULTS_PUBLISHED);
    initialize(states, events, set, 
               DosDashboardState.DOS_INITIAL_STATE,
               final_states);
  }
}
