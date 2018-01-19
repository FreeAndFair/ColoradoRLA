/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.util.SetCreator;

/**
 * A single transition of an abstract state machine.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class ASMTransition implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The start state for this transition.
   */
  private final Set<ASMState> my_start_states = new HashSet<ASMState>();
  
  /**
   * The set of events for this transition.
   */
  private final Set<ASMEvent> my_events = new HashSet<ASMEvent>();
  
  /**
   * The end state for this transition.
   */
  private final ASMState my_end_state;
  
  /**
   * Constructs an ASMTransition with the specified start state,
   * event, and end state.
   * 
   * @param the_start_state The start state.
   * @param the_event The event.
   * @param the_end_state The end state.
   */
  public ASMTransition(final ASMState the_start_state,
                       final ASMEvent the_event,
                       final ASMState the_end_state) {
    this(SetCreator.setOf(the_start_state), 
         SetCreator.setOf(the_event),
         the_end_state);
  }
  
  /**
   * Constructs an ASMTransition with the specified set of start states,
   * event, and end state.
   * 
   * @param the_start_states The start states.
   * @param the_event The event.
   * @param the_end_state The end state.
   */
  public ASMTransition(final Set<ASMState> the_start_states,
                       final ASMEvent the_event,
                       final ASMState the_end_state) {
    this(the_start_states, 
         SetCreator.setOf(the_event),
         the_end_state);
  }
  
  /**
   * Constructs an ASMTransition with the specified start state,
   * set of events, and end state.
   * 
   * @param the_start_state The start state.
   * @param the_events The events.
   * @param the_end_state The end state.
   */
  public ASMTransition(final ASMState the_start_state,
                       final Set<ASMEvent> the_events,
                       final ASMState the_end_state) {
    this(SetCreator.setOf(the_start_state), 
         the_events,
         the_end_state);
  }
  
  /**
   * Constructs an ASMTransition with the specified start states,
   * set of events, and end state.
   * 
   * @param the_start_states The start states.
   * @param the_events The events.
   * @param the_end_state The end state.
   */
  public ASMTransition(final Set<ASMState> the_start_states,
                       final Set<ASMEvent> the_events,
                       final ASMState the_end_state) {
    my_start_states.addAll(the_start_states);
    my_events.addAll(the_events);
    my_end_state = the_end_state;
  }
  
  /**
   * @return the start state.
   */
  public Set<ASMState> startStates() {
    return my_start_states;
  }
  
  /**
   * @return the events.
   */
  public Set<ASMEvent> events() {
    return my_events;
  }
  
  /**
   * @return the end state.
   */
  public ASMState endState() {
    return my_end_state;
  }
  
  /**
   * @return a String representation of this ASMTransition
   */
  @Override
  public String toString() {
    return "ASMTransition [start=" + my_start_states + 
           ", events=" + my_events + ", end=" + 
           my_end_state + "]";
  }
  
  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof ASMTransition) {
      final ASMTransition other_transition = (ASMTransition) the_other;
      result &= nullableEquals(other_transition.startStates(), startStates());
      result &= nullableEquals(other_transition.events(), events());
      result &= nullableEquals(other_transition.endState(), endState());
    } else {
      result = false;
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return startStates().hashCode();
  }
}
