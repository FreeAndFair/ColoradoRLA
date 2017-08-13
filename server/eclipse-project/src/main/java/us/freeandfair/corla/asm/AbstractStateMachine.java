/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.freeandfair.corla.Main;

/**
 * @description A generic Abstract State Machine (ASM).
 * @trace asm.asm
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
public abstract class AbstractStateMachine implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * This ASM's set of states.
   */
  protected final Set<ASMState> my_states;
  
  /**
   * This ASM's initial state.
   */
  protected final ASMState my_initial_state;
  
  /**
   * This AMS's final states.
   */
  protected final Set<ASMState> my_final_states;
  
  /**
   * This ASM's set of events.
   */
  protected final Set<ASMEvent> my_events;
  
  /**
   * A map from (state, event) pairs to state.
   */
  protected final Set<ASMTransition> my_transition_function; 
  
  /**
   * The relation between UI events and ASM transitions.
   */
  protected final UIToASMEventRelation my_ui_to_asm_relation = 
      new UIToASMEventRelation();
  
  /**
   * The current state of this ASM. Initialized to the initial state
   * provided in the constructor.
   */
  protected ASMState my_current_state;
  
  /**
   * The identity of this ASM. The structure of this string is child
   * class implementation dependent.
   */
  protected String my_identity;
  
  /**
   * Constructs an ASM. This constructor takes ownership of all the 
   * Collections passed to it.
   * 
   * @param the_states the states of the new ASM.
   * @param the_events the events of the new ASM.
   * @param the_transition_function the transition function of the new
   * ASM. This function, represented as a set of ASMTransitionFunction
   * elements, need only specify legal transitions; all unspecified
   * transitions are considered illegal.
   * @param the_initial_state The initial state of the new ASM.
   * @param the_final_states The final states of the new ASM.
   * @param the_identity The identity of the new ASM.
   */
  public AbstractStateMachine(final Set<ASMState> the_states,
                              final Set<ASMEvent> the_events,
                              final Set<ASMTransition> the_transition_function,
                              final ASMState the_initial_state,
                              final Set<ASMState> the_final_states,
                              final String the_identity) {
    my_states = the_states;
    my_events = the_events;
    my_transition_function = the_transition_function;
    my_initial_state = the_initial_state;
    my_current_state = the_initial_state;
    my_final_states = the_final_states;
    my_identity = the_identity;
  }
  
  /**
   * @return are we in the initial state?
   */
  public boolean isInInitialState() {
    return my_current_state.equals(my_initial_state);
  }
  
  /**
   * @return are we in a final state?
   */
  public boolean isInFinalState() {
    return my_final_states.contains(my_current_state);
  }
  
  /**
   * @return the current state of this ASM.
   * @trace asm.current_state
   */
  public ASMState currentState() {
    return my_current_state;
  }
  
  /**
   * Sets the current state. This method ignores any constraints
   * imposed by the current state, and should only be used as part of
   * reconstructing an ASM at a particular state.
   * 
   * @param the_state The new state.
   */
  protected void setCurrentState(final ASMState the_state) {
    my_current_state = the_state;
  }
  
  /**
   * @return the ASM's identity, or null if this ASM is a singleton.
   */
  public String identity() {
    return my_identity;
  }
  
  /**
   * Sets the ASM's identity. This method should only be used as part
   * of reconstructing an ASM at a particular state.
   * 
   * @param the_identity The new identity.
   */
  protected void setIdentity(final String the_identity) {
    my_identity = the_identity;
  }
  
  /**
   * @return the UI events enabled in this ASM.  I.e., which UI events
   * correspond to those states reachable from the current state?
   */
  public Set<UIEvent> enabledUIEvents() {
    final Set<ASMEvent> asm_events_enabled = enabledASMEvents();
    final Set<UIEvent> result = new HashSet<UIEvent>();
    // For each enabled ASM event, look up which UI events it corresponds to.
    for (final ASMEvent e : asm_events_enabled) {
      result.addAll(my_ui_to_asm_relation.leftArrow(e));
    }
    return result;
  }
  
  /**
   * @return the transitions of this ASM that are enabled. I.e., which
   * states are reachable from the current state, given any possible
   * event?
   * @trace asm.enabled_events
   */
  public Set<ASMEvent> enabledASMEvents() {
    final Set<ASMEvent> result = new HashSet<ASMEvent>();
    // skips and refreshes are always permitted
    result.add(DOS_SKIP_EVENT);
    result.add(DOS_REFRESH_EVENT);
    result.add(COUNTY_SKIP_EVENT);
    result.add(COUNTY_REFRESH_EVENT);
    result.add(AUDIT_SKIP_EVENT);
    result.add(AUDIT_REFRESH_EVENT);
    for (final ASMTransition t : my_transition_function) {
      if (t.startState().equals(my_current_state)) {
        result.add(t.event());
      }
    }
    return result;
  }
  
  /**
   * Transition to the next state of this ASM given the provided
   * transition and its current state.
   * @param the_transition the transition that is triggered.
   * @return the new current state of the ASM after the transition.
   * @throws IllegalStateException if this ASM cannot take a step
   * given the provided transition.
   */
  public ASMState stepTransition(final ASMTransition the_transition)
      throws IllegalStateException {
    // If we are in the right state then transition to the new state.
    if (my_current_state.equals(the_transition.startState())) {
      my_current_state = the_transition.endState();
      Main.LOGGER.info("ASM transition succeeded: " + the_transition); 
    } else {
      Main.LOGGER.error("ASM transition " + the_transition + 
                        " failed from state " + my_current_state); 
      throw new IllegalStateException("Attempted to transition from " +
                                      my_current_state + " using transition " + 
                                      the_transition);
    }
    return my_current_state;
  }
  
  /**
   * Transition to the next state of this ASM given the provided event
   * and its current state.
   * @return the next state given the specified event and input.
   * @throws IllegalStateException is this ASM cannot transition given
   * the provided event.
   */
  public ASMState stepEvent(final ASMEvent the_event)
      throws IllegalStateException {
    // refresh and skip events are always permitted
    if (the_event == DOS_REFRESH_EVENT ||
        the_event == DOS_SKIP_EVENT ||
        the_event == COUNTY_REFRESH_EVENT || 
        the_event == COUNTY_SKIP_EVENT ||
        the_event == AUDIT_REFRESH_EVENT || 
        the_event == AUDIT_SKIP_EVENT) {
      return currentState();
    }
                
    ASMState result = null;
    for (final ASMTransition t : my_transition_function) {
      if (t.startState().equals(my_current_state) &&
          t.event().equals(the_event)) {
        result = t.endState();
        break;
      }
    }
    if (result == null) {
      Main.LOGGER.error("ASM event " + the_event + 
                        " failed from state " + my_current_state); 
      throw new IllegalStateException("Illegal transition on ASM: (" + 
                                      my_current_state + ", " + the_event + ")");
    } else {
      Main.LOGGER.info("ASM event " + the_event + " caused transition to " + 
                       my_current_state); 
      return result;
    }
  }
  
  /**
   * Converts a list of ASMTransitionFunctions to a set of
   * ASMTransitions.
   * 
   * @param the set of ASMTransitionFunctions.
   * @return the set of ASMTransitions for the specified list of 
   * ASMTransitionFunctions.
   */
  public static Set<ASMTransition>
      transitionsFor(final List<ASMTransitionFunction> the_list) {
    final Set<ASMTransition> result = new HashSet<ASMTransition>();
    for (final ASMTransitionFunction atf : the_list) {
      result.add(atf.value());
    }
    return result;
  }
  
  /**
   * @return a String representation of this ASM.
   */
  public String toString() {
    return getClass().getName() + ", identity=" + my_identity + 
           ", current_state=" + my_current_state;
  }
}
