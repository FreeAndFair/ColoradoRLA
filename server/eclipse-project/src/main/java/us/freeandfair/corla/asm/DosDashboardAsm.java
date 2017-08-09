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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent;
import us.freeandfair.corla.asm.AsmState.DosDashboardState;
import us.freeandfair.corla.asm.AsmTransitions.DosDashboardTransitions;
import us.freeandfair.corla.util.Pair;

/**
 * The ASM for the Department of State Dashboard.
 * @trace asm.dos_dashboard_next_state
 */
public class DosDashboardAsm extends AbstractAsm {
  /**
   * Create the Department of State Dashboard ASM.
   * @trace asm.dos_asm 
   */
  public DosDashboardAsm() {
    super();
    final Set<AsmState> states = new HashSet<AsmState>();
    for (final AsmState s : DosDashboardState.values()) {
      states.add(s);
    }
    final Set<AsmEvent> events = new HashSet<AsmEvent>();
    for (final AsmEvent e : DosDashboardEvent.values()) {
      events.add(e);
    }
    final Map<Pair<AsmState, AsmEvent>, AsmState> map = 
        new HashMap<Pair<AsmState, AsmEvent>, AsmState>();
    for (final DosDashboardTransitions t : 
        DosDashboardTransitions.values()) {
      map.put(t.my_pair.getFirst(), t.my_pair.getSecond());
    }
    final Set<AsmState> final_states = new HashSet<AsmState>();
    final_states.add(DosDashboardState.AUDIT_RESULTS_PUBLISHED);
    initialize(states, events, map, 
               DosDashboardState.INITIAL_STATE,
               final_states);
  }
}
