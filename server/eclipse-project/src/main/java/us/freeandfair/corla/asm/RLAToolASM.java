/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.ASMState.RLAToolState;

/**
 * The ASM for the whole RLA Tool.
 * @trace asm.rla_tool_asm
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class RLAToolASM extends AbstractStateMachine {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 

  /**
   * The states of this ASM.
   */
  private static final Set<ASMState> STATES = new HashSet<>();
  
  /**
   * The final states of this ASM.
   */
  private static final Set<ASMState> FINAL_STATES = new HashSet<>();
  
  /**
   * The events of this ASM.
   */
  private static final Set<ASMEvent> EVENTS = new HashSet<>();
  
  /**
   * The transition function of this ASM.
   */
  private static final Set<ASMTransition> TRANSITION_FUNCTION =
      new HashSet<>();

  static {
    final AbstractStateMachine[] asms = 
        {new DoSDashboardASM(), 
         new CountyDashboardASM(null), // this will have to be rethought some
         new AuditBoardDashboardASM(null)}; // this too
    
    for (final AbstractStateMachine asm : asms) {
      STATES.addAll(asm.my_states);
      FINAL_STATES.addAll(asm.my_final_states);
      EVENTS.addAll(asm.my_events);
      TRANSITION_FUNCTION.addAll(asm.my_transition_function);
    }
  }
  
  /**
   * Create the RLA Tool ASM.
   */
  public RLAToolASM() {
    super(STATES, EVENTS, TRANSITION_FUNCTION, 
          RLAToolState.RLA_TOOL_INITIAL_STATE, FINAL_STATES,
          null); // there is only one RLA Tool ASM, so no identity passed
  }
}
