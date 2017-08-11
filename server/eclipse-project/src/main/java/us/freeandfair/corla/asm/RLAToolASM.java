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

import static us.freeandfair.corla.asm.ASMState.RlaToolState.RLA_TOOL_INITIAL_STATE;

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.ASMTransitionFunction.RlaTransitionFunction;

/**
 * The ASM for the whole RLA Tool.
 * @trace asm.rla_tool_asm
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class RLAToolASM extends ASM {
  /**
   * The ASM for the Department of State Dashboard.
   */
  private final DoSDashboardASM my_dos_asm = new DoSDashboardASM();
  /**
   * The ASM for the County Dashboard.
   */
  private final CountyDashboardASM my_county_asm = new CountyDashboardASM();
  /**
   * The ASM for the Audit Board Dashboard.
   */
  private final AuditBoardDashboardASM my_audit_asm = new AuditBoardDashboardASM();

  /**
   * Create the RLA Tool ASM.
   */
  public RLAToolASM() {
    super();
    // Collect all states, events, and transition function pairs of
    // component ASMs.
    final Set<ASMState> states = buildStates();
    final Set<ASMState> final_states = buildFinalStates();
    final Set<ASMEvent> events = buildTransitions();
    final Set<ASMTransition> transition_function =
        buildTransitionFunction();
    for (final RlaTransitionFunction t : RlaTransitionFunction.values()) {
      transition_function.add(t.value());
    }
    initialize(states, events, transition_function,
               RLA_TOOL_INITIAL_STATE, final_states);
  }
  
  private Set<ASMState> buildStates() {
    final Set<ASMState> result = new HashSet<ASMState>();
    result.addAll(my_dos_asm.my_states);
    result.addAll(my_county_asm.my_states);
    result.addAll(my_audit_asm.my_states);
    return result;
  }
  
  private Set<ASMState> buildFinalStates() {
    final Set<ASMState> result = new HashSet<ASMState>();
    result.addAll(my_dos_asm.my_final_states);
    result.addAll(my_county_asm.my_final_states);
    result.addAll(my_audit_asm.my_final_states);
    return result;
  }
  
  private Set<ASMEvent> buildTransitions() {
    final Set<ASMEvent> result = new HashSet<ASMEvent>();
    result.addAll(my_dos_asm.my_events);
    result.addAll(my_county_asm.my_events);
    result.addAll(my_audit_asm.my_events);
    return result;
  }
  
  private Set<ASMTransition> buildTransitionFunction() {
    final Set<ASMTransition> result = new HashSet<>();
    result.addAll(my_dos_asm.my_transition_function);
    result.addAll(my_county_asm.my_transition_function);
    result.addAll(my_audit_asm.my_transition_function);
    return result;
  }

}
