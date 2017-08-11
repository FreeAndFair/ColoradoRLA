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
import java.util.Map;
import java.util.Set;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.UiToAsmEventRelation.UiEvent;
import us.freeandfair.corla.util.Pair;

/**
 * @description A generic Abstract State Machine (ASM).
 * @trace asm.asm
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class Asm {
  /**
   * This ASM's set of states.
   */
  protected Set<AsmState> my_states;
  
  /**
   * This ASM's initial state.
   */
  protected AsmState my_initial_state;
  
  /**
   * This AMS's final states.
   */
  protected Set<AsmState> my_final_states;
  
  /**
   * This ASM's set of transitions.
   */
  protected Set<AsmEvent> my_transitions;
  
  /**
   * A map from (state, event) pairs to state.
   */
  protected Map<Pair<AsmState, AsmEvent>, AsmState> my_transition_function; 
  
  /**
   * The relation between UI events and Asm transitions.
   */
  protected final UiToAsmEventRelation my_ui_to_asm_relation = 
      new UiToAsmEventRelation();
  
  /**
   * The current state of this ASM. Initialized to the initial state provided
   * in the constructor.
   */
  private AsmState my_current_state;

  /**
   * Create the ASM for the Colorado RLA Tool. Ownership transfer happens on the
   * passed values.
   * 
   * @param the_states the states of the new ASM.
   * @param the_transitions the transitions of the new ASM.
   * @param the_transition_function the transition function of the new ASM. This
   * function, represented as a Map, need only specify those transitions that are
   * legal. All missing transitions are considered erroneous.
   */
  public void initialize(final Set<AsmState> the_states,
                         final Set<AsmEvent> the_transitions,
                         final Map<Pair<AsmState, AsmEvent>, AsmState> 
                           the_transition_function,
                         final AsmState the_initial_state,
                         final Set<AsmState> the_final_states) {
    my_states = the_states;
    my_transitions = the_transitions;
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
   * @return the relation between UI events and ASM transitions.
   */
  public UiToAsmEventRelation uiAsmRelation() {
    return my_ui_to_asm_relation;
  }

  /**
   * @return the UI events enabled in this ASM.  I.e., which UI events correspond
   * to those states reachable from the current state?
   */
  public Set<UiEvent> enabledUiEvents() {
    final Set<AsmEvent> asm_events_enabled = enabledAsmEvents();
    final Set<UiEvent> result = new HashSet<UiEvent>();
    // For each enabled ASM event, look up which UI events it corresponds to.
    for (final AsmEvent e : asm_events_enabled) {
      result.addAll(Main.asm().uiAsmRelation().leftArrow(e));
    }
    return result;
  }
  
  /**
   * @return the transitions of this ASM that are enabled. I.e., which states are
   * reachable from the current state, given any possible event?
   * @trace asm.enabled_events
   */
  public Set<AsmEvent> enabledAsmEvents() {
    final Set<AsmEvent> result = new HashSet<AsmEvent>();
    for (final Pair<AsmState, AsmEvent> p : my_transition_function.keySet()) {
      if (p.getFirst().equals(my_current_state)) {
        result.add(p.getSecond());
      }
    }
    return result;
  }
  
  /**
   * Transition to the next state of this ASM given the provided transition and
   * its current state.
   * @param the_transition the transition that is triggered.
   * @return the new current state of the ASM after the transition.
   * @throws IllegalStateException if this ASM cannot take a step given the provided
   * transition.
   */
  public AsmState stepTransition(final AsmTransitions the_transition)
      throws IllegalStateException {
    // If we are in the right state then transition to the new state.
    if (my_current_state.equals(the_transition.value().getFirst().getFirst())) {
      my_current_state = the_transition.value().getSecond();
      Main.LOGGER.info("ASM transition succeeded: " + the_transition); 
    } else {
      Main.LOGGER.error("ASM transition " + the_transition + 
                        " failed from state " + my_current_state); 
      throw new IllegalStateException("Attempted to transition from " +
          my_current_state + " using transition " + the_transition);
    }
    return my_current_state;
  }
  
  /**
   * Transition to the next state of this ASM given the provided event and its
   * current state.
   * @param the_event the event that is to trigger a transition.
   * @return the new current state of the ASM after the event.
   * @throws IllegalStateException is this ASM cannot transition given the provided
   * event.
   */
  public AsmState stepEvent(final AsmEvent the_event)
      throws IllegalStateException {
    final Pair<AsmState, AsmEvent> pair = 
        new Pair<AsmState, AsmEvent>(my_current_state, the_event);
    if (!my_transition_function.containsKey(pair)) {
      throw new IllegalStateException("Illegal transition on ASM: (" + 
        my_current_state + ", " + the_event + ")");
    }
    return my_transition_function.get(pair);
  }
}
