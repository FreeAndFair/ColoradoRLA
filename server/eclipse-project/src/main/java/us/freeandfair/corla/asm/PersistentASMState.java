/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 11, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * An abstract state machine state, and sufficient information to reconstruct the
 * state machine it belongs to.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "asm_state")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class PersistentASMState extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The class of AbstractStateMachine to which this state belongs, as a String.
   */
  @Column(updatable = false, nullable = false)
  private String my_asm_class;
  
  /**
   * The identifying information for the state machine, if any, as a String.
   */
  @Column(updatable = false)
  private String my_asm_identity;
  
  /**
   * The ASMState class (enum) containing this state, as a String.
   */
  private String my_state_class;
  
  /**
   * The state value, as a String.
   */
  private String my_state_value;
  
  /**
   * Constructs an empty PersistentASMState, solely for persistence.
   */
  protected PersistentASMState() {
    super();
  }
  
  /**
   * Constructs a PersistentASMState with the specified parameters.
   */
  protected PersistentASMState(final String the_asm_class,
                               final String the_asm_identity,
                               final String the_state_class,
                               final String the_state_value) {
    super();
    my_asm_class = the_asm_class;
    my_asm_identity = the_asm_identity;
    my_state_class = the_state_class;
    my_state_value = the_state_value;
  }
  
  /**
   * Obtains a PersistentASMState from an abstract state machine.
   * 
   * @param the_asm The ASM from which to obtain the state.
   * @return The state.
   */
  //@ requires the_asm != null;
  public static PersistentASMState stateFor(final AbstractStateMachine the_asm) {
    final String asm_class = the_asm.getClass().getName();
    // identifying info to be dealt with later
    final ASMState state = the_asm.currentState();
    final String state_class = state.getClass().getName();
    String state_value = null;
    if (state instanceof Enum<?>) {
      final Enum<?> state_enum = (Enum<?>) state;
      state_value = state_enum.name();
    }
    return new PersistentASMState(asm_class, null, state_class, state_value);
  }
  
  /**
   * @return the ASM class.
   */
  public String asmClass() {
    return my_asm_class;
  }
  
  /**
   * @return the ASM identity.
   */
  public String asmIdentity() {
    return my_asm_identity;
  }
  
  /**
   * @return the state class.
   */
  public String stateClass() {
    return my_state_class;
  }
  
  /**
   * @return the state value.
   */
  public String stateValue() {
    return my_state_value;
  }
}
