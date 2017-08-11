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

import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;

/**
 * @description A generic Abstract State Machine (ASM).
 * @trace asm.asm
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
@MappedSuperclass
public class Asm {
  /**
   * This ASM's set of states.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<AsmState> my_states;
  
  /**
   * This ASM's initial state.
   */
  @Enumerated(EnumType.STRING)
  protected AsmState my_initial_state;
  
  /**
   * This AMS's final states.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<AsmState> my_final_states;
  
  /**
   * This ASM's set of events.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<AsmEvent> my_events;
  
  /**
   * A map from (state, event) pairs to state.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<AsmTransition> my_transition_function; 
  
  /**
   * The current state of this ASM. Initialized to the initial state provided
   * in the constructor.
   */
  @Enumerated(EnumType.STRING)
  private AsmState my_current_state;
  
  /**
   * Create the ASM for the Colorado RLA Tool. Ownership transfer happens on the
   * passed values.
   * 
   * @param the_states the states of the new ASM.
   * @param the_events the events of the new ASM.
   * @param the_transition_function the transition function of the new ASM. This
   * function, represented as a List of Transition, need only specify the legal
   * transitions. All unspecified transitions are considered erroneous.
   */
  public void initialize(final Set<AsmState> the_states,
                         final Set<AsmEvent> the_events,
                         final Set<AsmTransition> the_transition_function,
                         final AsmState the_initial_state,
                         final Set<AsmState> the_final_states) {
    my_states = the_states;
    my_events = the_events;
    my_transition_function = the_transition_function;
    my_initial_state = the_initial_state;
    my_current_state = the_initial_state;
    my_final_states = the_final_states;
  }
  
  /**
   * @return are we in the initial state?
   */
  public boolean initialState() {
    return my_current_state.equals(my_initial_state);
  }
  
  /**
   * @return are we in the a final state?
   */
  public boolean finalState() {
    return my_final_states.contains(my_current_state);
  }
  
  /**
   * @return the current state of this ASM.
   * @trace asm.current_state
   */
  public AsmState currentState() {
    return my_current_state;
  }
  
  /**
   * @return the transitions of this ASM that are enabled. I.e., which states are
   * reachable from the current state, given any possible event?
   * @trace asm.enabled_events
   */
  public Set<AsmEvent> enabledEvents() {
    final Set<AsmEvent> result = new HashSet<AsmEvent>();
    for (final AsmTransition t : my_transition_function) {
      if (t.startState().equals(my_current_state)) {
        result.add(t.event());
      }
    }
    return result;
  }
  
  /**
   * Transition to the next state of this ASM given the provided event and its
   * current state.
   * @return the next state given the specified event and input.
   * @throws IllegalStateException is this ASM cannot transition given the provided
   * event.
   */
  public AsmState transition(final AsmEvent the_event)
      throws IllegalStateException {
    AsmState result = null;
    for (final AsmTransition t : my_transition_function) {
      if (t.startState().equals(my_current_state) &&
          t.event().equals(the_event)) {
        result = t.endState();
        break;
      }
    }

    if (result == null) {
      throw new IllegalStateException("Illegal transition on ASM: (" + 
                                      my_current_state + ", " + the_event + ")");
    } else {
      return result;
    }
  }
}
