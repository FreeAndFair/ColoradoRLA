/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * A contest choice; has a name and a description.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class Choice {
  /**
   * The choice name.
   */
  private final String my_name;

  /**
   * The choice description.
   */
  private final String my_description;
  
  /**
   * Constructs a choice with the specified parameters.
   * 
   * @param the_name The contest name.
   * @param the_description The contest description.
   */
  public Choice(final String the_name, final String the_description) {
    super();
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
    return "Contest [name=" + my_name + ", description=" +
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
    boolean result = false;
    if (the_other != null && getClass().equals(the_other.getClass())) {
      final Choice other_choice = (Choice) the_other;
      result &= other_choice.name().equals(name());
      result &= other_choice.description().equals(description());
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  public int hashCode() {
    return toString().hashCode();
  }
}
