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

import us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.asm.AsmState.AuditBoardDashboardState;
import us.freeandfair.corla.asm.AsmState.DosDashboardState;
import us.freeandfair.corla.asm.AsmTransitions.AuditBoardDashboardTransitions;
import us.freeandfair.corla.util.Pair;

/**
 * The ASM for the Audit Board Dashboard.
 * @trace asm.dos_dashboard_next_state
 */
public class AuditBoardDashboardAsm extends AbstractAsm {
  /**
   * Create the Audit Board Dashboard ASM.
   * @trace asm.county_dashboard_asm
   */
  public AuditBoardDashboardAsm() {
    super();
    final Set<AsmState> states = new HashSet<AsmState>();
    for (final AsmState s : AuditBoardDashboardState.values()) {
      states.add(s);
    }
    final Set<AsmEvent> events = new HashSet<AsmEvent>();
    for (final AsmEvent e : AuditBoardDashboardEvent.values()) {
      events.add(e);
    }
    final Map<Pair<AsmState, AsmEvent>, AsmState> map = 
        new HashMap<Pair<AsmState, AsmEvent>, AsmState>();
    for (final AuditBoardDashboardTransitions t : 
        AuditBoardDashboardTransitions.values()) {
      map.put(t.my_pair.getFirst(), t.my_pair.getSecond());
    }
    final Set<AsmState> final_states = new HashSet<AsmState>();
    final_states.add(AuditBoardDashboardState.AUDIT_REPORT_SUBMITTED_STATE);
    initialize(states, events, map, 
               AuditBoardDashboardState.AUDIT_INITIAL_STATE,
               final_states);
  }
}
