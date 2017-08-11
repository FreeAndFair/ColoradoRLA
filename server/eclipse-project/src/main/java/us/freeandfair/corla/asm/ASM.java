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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * @description A generic Abstract State Machine (ASM).
 * @trace asm.asm
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@Entity
@Table(name = "abstract_state_machine")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asm_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ASM")
public class ASM extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * This ASM's set of states.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<ASMState> my_states = new HashSet<>();
  
  /**
   * This ASM's initial state.
   */
  @Enumerated(EnumType.STRING)
  protected ASMState my_initial_state;
  
  /**
   * This AMS's final states.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<ASMState> my_final_states = new HashSet<>();
  
  /**
   * This ASM's set of events.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<ASMEvent> my_events = new HashSet<>();
  
  /**
   * A map from (state, event) pairs to state.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  protected Set<ASMTransition> my_transition_function = new HashSet<>(); 
  
  /**
   * The current state of this ASM. Initialized to the initial state provided
   * in the constructor.
   */
  @Enumerated(EnumType.STRING)
  private ASMState my_current_state;
  
  /**
   * Constructs a new ASM with default values, solely for persistence.
   */
  public ASM() {
    super();
  }
  
  /**
   * Constructs an ASM.
   * 
   * @param the_states the states of the new ASM.
   * @param the_events the events of the new ASM.
   * @param the_transition_function the transition function of the new ASM. This
   * function, represented as a set of ASMTransitionFunction elements, need only
   * specify legal transitions; all unspecified transitions are considered illegal.
   */
  public ASM(final Set<ASMState> the_states,
             final Set<ASMEvent> the_events,
             final Set<ASMTransitionFunction> the_transition_function,
             final ASMState the_initial_state,
             final Set<ASMState> the_final_states) {
    super();
    my_states.addAll(the_states);
    my_events.addAll(the_events);
    for (final ASMTransitionFunction atf : the_transition_function) {
      my_transition_function.add(atf.value());
    }
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
  public ASMState currentState() {
    return my_current_state;
  }
  
  /**
   * @return the transitions of this ASM that are enabled. I.e., which states are
   * reachable from the current state, given any possible event?
   * @trace asm.enabled_events
   */
  public Set<ASMEvent> enabledEvents() {
    final Set<ASMEvent> result = new HashSet<ASMEvent>();
    for (final ASMTransition t : my_transition_function) {
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
  public ASMState transition(final ASMEvent the_event)
      throws IllegalStateException {
    ASMState result = null;
    for (final ASMTransition t : my_transition_function) {
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
