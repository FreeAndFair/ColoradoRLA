/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @design In the formal model this concept is currently called "option".
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * A contest choice; has a name and a description.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Embeddable
public class Choice implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The choice name.
   */
  private String my_name;

  /**
   * The choice description.
   */
  private String my_description;
  
  /**
   * Constructs a choice with default values, solely for persistence.
   */
  public Choice() {
    // defaults
  }
  
  /**
   * Constructs a choice with the specified parameters.
   * 
   * @param the_name The choice name.
   * @param the_description The choice description.
   */
  public Choice(final String the_name, final String the_description) {
    my_name = the_name;
    my_description = the_description;
  }
  
  /**
   * @return the name.
   */
  public String name() {
    return my_name;
  }
  
  /**
   * @return the description.
   */
  public String description() {
    return my_description;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "Choice [name=" + my_name + ", description=" +
           my_description + "]";
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
    if (the_other instanceof Choice) {
      final Choice other_choice = (Choice) the_other;
      result &= nullableEquals(other_choice.name(), name());
      result &= nullableEquals(other_choice.description(), description());
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
