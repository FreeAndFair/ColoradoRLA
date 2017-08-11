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

import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.CountyDashboardTransitionFunction;

/**
 * The ASM for the County Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class CountyDashboardASM extends ASM {
  /**
   * Create the County Dashboard ASM.
   * @trace asm.county_dashboard_asm
   */
  public CountyDashboardASM() {
    super();
    final Set<ASMState> states = new HashSet<>();
    for (final ASMState s : CountyDashboardState.values()) {
      states.add(s);
    }
    final Set<ASMEvent> events = new HashSet<>();
    for (final ASMEvent e : CountyDashboardEvent.values()) {
      events.add(e);
    }
    final Set<ASMTransition> set = new HashSet<>();
    for (final CountyDashboardTransitionFunction t : 
         CountyDashboardTransitionFunction.values()) {
      set.add(t.value());
    }
    final Set<ASMState> final_states = new HashSet<ASMState>();
    final_states.add(CountyDashboardState.UPLOAD_BALLOT_MANIFEST_TOO_LATE);
    final_states.add(CountyDashboardState.UPLOAD_CVRS_TOO_LATE);
    final_states.add(CountyDashboardState.COUNTY_AUDIT_COMPLETE);
    initialize(states, events, set, 
               CountyDashboardState.COUNTY_INITIAL_STATE,
               final_states);
  } 
}
