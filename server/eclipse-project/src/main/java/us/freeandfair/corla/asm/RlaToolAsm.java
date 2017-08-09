/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
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

import us.freeandfair.corla.asm.AsmState.DosDashboardState;
import us.freeandfair.corla.util.Pair;

/**
 * The ASM for the whole RLA Tool.
 * @trace asm.rla_tool_asm
 */
public class RlaToolAsm extends AbstractAsm {
  private final DosDashboardAsm my_dos_asm = new DosDashboardAsm();
  private final CountyDashboardAsm my_county_asm = new CountyDashboardAsm();
  private final AuditBoardDashboardAsm my_audit_asm = new AuditBoardDashboardAsm();

  /**
   * Create the RLA Tool ASM.
   */
  public RlaToolAsm() {
    super();
    // Collect all states, events, and transition function pairs of
    // component ASMs.
    final Set<AsmState> states = buildStates();
    final Set<AsmState> final_states = buildFinalStates();
    final Set<AsmEvent> events = buildTransitions();
    final Map<Pair<AsmState, AsmEvent>, AsmState> transition_function =
        buildTransitionFunction();
    final AsmState intial_state = my_dos_asm.my_initial_state;
    // Add the necessary transitions to move between the component ASMs.
    // @todo kiniry Add missing events and transitions.
  }
  
  private Set<AsmState> buildStates() {
    final Set<AsmState> result = new HashSet<AsmState>();
    result.addAll(my_dos_asm.my_states);
    result.addAll(my_county_asm.my_states);
    result.addAll(my_audit_asm.my_states);
    return result;
  }
  
  private Set<AsmState> buildFinalStates() {
    final Set<AsmState> result = new HashSet<AsmState>();
    result.addAll(my_dos_asm.my_final_states);
    result.addAll(my_county_asm.my_final_states);
    result.addAll(my_audit_asm.my_final_states);
    return result;
  }
  
  private Set<AsmEvent> buildTransitions() {
    final Set<AsmEvent> result = new HashSet<AsmEvent>();
    result.addAll(my_dos_asm.my_transitions);
    result.addAll(my_county_asm.my_transitions);
    result.addAll(my_audit_asm.my_transitions);
    return result;
  }
  
  private Map<Pair<AsmState, AsmEvent>, AsmState> buildTransitionFunction() {
    final Map<Pair<AsmState, AsmEvent>, AsmState> result = 
        new HashMap<Pair<AsmState, AsmEvent>, AsmState>();
    result.putAll(my_dos_asm.my_transition_function);
    result.putAll(my_county_asm.my_transition_function);
    result.putAll(my_audit_asm.my_transition_function);
    return result;
  }

}
