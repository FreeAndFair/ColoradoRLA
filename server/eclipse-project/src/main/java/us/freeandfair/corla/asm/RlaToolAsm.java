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

import static us.freeandfair.corla.asm.AsmState.RlaToolState.RLA_TOOL_INITIAL_STATE;

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.AsmTransitionFunction.RlaTransitionFunction;

/**
 * The ASM for the whole RLA Tool.
 * @trace asm.rla_tool_asm
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class RlaToolAsm extends Asm {
  /**
   * The ASM for the Department of State Dashboard.
   */
  private final DosDashboardAsm my_dos_asm = new DosDashboardAsm();
  /**
   * The ASM for the County Dashboard.
   */
  private final CountyDashboardAsm my_county_asm = new CountyDashboardAsm();
  /**
   * The ASM for the Audit Board Dashboard.
   */
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
    final Set<AsmTransition> transition_function =
        buildTransitionFunction();
    for (final RlaTransitionFunction t : RlaTransitionFunction.values()) {
      transition_function.add(t.value());
    }
    initialize(states, events, transition_function,
               RLA_TOOL_INITIAL_STATE, final_states);
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
    result.addAll(my_dos_asm.my_events);
    result.addAll(my_county_asm.my_events);
    result.addAll(my_audit_asm.my_events);
    return result;
  }
  
  private Set<AsmTransition> buildTransitionFunction() {
    final Set<AsmTransition> result = new HashSet<>();
    result.addAll(my_dos_asm.my_transition_function);
    result.addAll(my_county_asm.my_transition_function);
    result.addAll(my_audit_asm.my_transition_function);
    return result;
  }

}
