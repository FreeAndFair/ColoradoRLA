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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A contest choice; has a name and a description.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "choice")
public class Choice implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

//  /**
//   * The database ID of this choice.
//   */
//  @Id
//  @GeneratedValue(strategy = GenerationType.TABLE)
//  private long my_db_id;

  /**
   * The choice name.
   */
  @Id
  private final String my_name;

  /**
   * The choice description.
   */
  @Id
  private final String my_description;
  
  /**
   * Constructs an empty choice, solely for persistence. 
   */
  protected Choice() {
    my_name = "";
    my_description = "";
  }
  
  /**
   * Constructs a choice with the specified parameters.
   * 
   * @param the_name The contest name.
   * @param the_description The contest description.
   */
  public Choice(final String the_name, final String the_description) {
    my_name = the_name;
    my_description = the_description;
  }
  
//  /**
//   * @return the database ID.
//   */
//  public long dbID() {
//    return my_db_id;
//  }
  
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
    boolean result = false;
    if (the_other instanceof Choice) {
      final Choice other_choice = (Choice) the_other;
      result &= other_choice.name().equals(name());
      result &= other_choice.description().equals(description());
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
