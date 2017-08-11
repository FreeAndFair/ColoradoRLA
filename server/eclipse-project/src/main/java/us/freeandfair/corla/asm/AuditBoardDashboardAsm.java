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

import us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.asm.AsmState.AuditBoardDashboardState;
import us.freeandfair.corla.asm.AsmTransitionFunction.AuditBoardDashboardTransitionFunction;

/**
 * The ASM for the Audit Board Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class AuditBoardDashboardAsm extends Asm {
  /**
   * Create the Audit Board Dashboard ASM.
   * @trace asm.county_dashboard_asm
   */
  public AuditBoardDashboardAsm() {
    super();
    final Set<AsmState> states = new HashSet<>();
    for (final AsmState s : AuditBoardDashboardState.values()) {
      states.add(s);
    }
    final Set<AsmEvent> events = new HashSet<>();
    for (final AsmEvent e : AuditBoardDashboardEvent.values()) {
      events.add(e);
    }
    final Set<AsmTransition> set = new HashSet<>();
    for (final AuditBoardDashboardTransitionFunction t : 
         AuditBoardDashboardTransitionFunction.values()) {
      set.add(t.value());
    }
    final Set<AsmState> final_states = new HashSet<AsmState>();
    final_states.add(AuditBoardDashboardState.AUDIT_REPORT_SUBMITTED_STATE);
    initialize(states, events, set, 
               AuditBoardDashboardState.AUDIT_INITIAL_STATE,
               final_states);
  }
}
