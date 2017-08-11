/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * A single transition of an abstract state machine.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@Entity
@Table(name = "asm_transition")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class AsmTransition extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The start state for this transition.
   */
  @Enumerated(EnumType.STRING)
  private AsmState my_start_state;
  
  /**
   * The event for this transition.
   */
  @Enumerated(EnumType.STRING)
  private AsmEvent my_event;
  
  /**
   * The end state for this transition.
   */
  @Enumerated(EnumType.STRING)
  private AsmState my_end_state;
  
  /**
   * Constructs an empty AsmTransition, solely for persistence.
   */
  public AsmTransition() {
    super();
  }
  
  /**
   * Constructs an AsmTransition with the specified start state,
   * event, and end state.
   * 
   * @param the_start_state The start state.
   * @param the_event The event.
   * @param the_end_state The end state.
   */
  public AsmTransition(final AsmState the_start_state,
                       final AsmEvent the_event,
                       final AsmState the_end_state) {
    super();
    my_start_state = the_start_state;
    my_event = the_event;
    my_end_state = the_end_state;
  }
  
  /**
   * @return the start state.
   */
  public AsmState startState() {
    return my_start_state;
  }
  
  /**
   * @return the event.
   */
  public AsmEvent event() {
    return my_event;
  }
  
  /**
   * @return the end state.
   */
  public AsmState endState() {
    return my_end_state;
  }
  
  /**
   * @return a String representation of this AsmTransition
   */
  @Override
  public String toString() {
    return "AsmTransition [start=" + my_start_state + 
           ", event=" + my_event + ", end=" + 
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
    if (the_other instanceof AsmTransition) {
      final AsmTransition other_transition = (AsmTransition) the_other;
      result &= nullableEquals(other_transition.startState(), startState());
      result &= nullableEquals(other_transition.event(), event());
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
    return toString().hashCode();
  }
}
