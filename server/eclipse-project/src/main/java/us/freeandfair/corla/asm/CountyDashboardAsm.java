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

import us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.AsmState.CountyDashboardState;
import us.freeandfair.corla.asm.AsmTransitions.CountyDashboardTransitions;
import us.freeandfair.corla.util.Pair;

/**
 * The ASM for the County Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class CountyDashboardAsm extends Asm {
  /**
   * Create the County Dashboard ASM.
   * @trace asm.county_dashboard_asm
   */
  public CountyDashboardAsm() {
    super();
    final Set<AsmState> states = new HashSet<AsmState>();
    for (final AsmState s : CountyDashboardState.values()) {
      states.add(s);
    }
    final Set<AsmEvent> events = new HashSet<AsmEvent>();
    for (final AsmEvent e : CountyDashboardEvent.values()) {
      events.add(e);
    }
    final Map<Pair<AsmState, AsmEvent>, AsmState> map = 
        new HashMap<Pair<AsmState, AsmEvent>, AsmState>();
    for (final CountyDashboardTransitions t : 
        CountyDashboardTransitions.values()) {
      map.put(t.value().getFirst(), t.value().getSecond());
    }
    final Set<AsmState> final_states = new HashSet<AsmState>();
    final_states.add(CountyDashboardState.UPLOAD_BALLOT_MANIFEST_TOO_LATE);
    final_states.add(CountyDashboardState.UPLOAD_CVRS_TOO_LATE);
    final_states.add(CountyDashboardState.COUNTY_AUDIT_COMPLETE);
    initialize(states, events, map, 
               CountyDashboardState.COUNTY_INITIAL_STATE,
               final_states);
  } 
}
