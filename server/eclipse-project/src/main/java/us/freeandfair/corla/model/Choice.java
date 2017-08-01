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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Choice implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The table of objects that have been created.
   */
  private static final Map<Choice, Choice> CACHE = 
      new HashMap<Choice, Choice>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, Choice> BY_ID =
      new HashMap<Long, Choice>();
  
  /**
   * The current ID number to be used.
   */
  private static long current_id;
 
  /**
   * The database ID of this choice.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private Long my_id = getID();

  /**
   * The choice name.
   */
  private String my_name;

  /**
   * The choice description.
   */
  private String my_description;
  
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
   * @param the_name The choice name.
   * @param the_description The choice description.
   */
  protected Choice(final String the_name, final String the_description) {
    my_name = the_name;
    my_description = the_description;
  }
  
  /**
   * @return the next ID
   */
  private static synchronized long getID() {
    return current_id++;
  }

  /**
   * Returns a choice with the specified parameters.
   * 
   * @param the_name The choice name.
   * @param the_description The choice description.
   */
  public static synchronized Choice instance(final String the_name, 
                                             final String the_description) {
    Choice result = new Choice(the_name, the_description);
    if (CACHE.containsKey(result)) {
      result = CACHE.get(result);
    } else {
      CACHE.put(result, result);
      BY_ID.put(result.id(), result);
    }
    return result;
  }
  
  /**
   * Returns the choice with the specified ID.
   * 
   * @param the_id The ID.
   * @return the choice, or null if it doesn't exist.
   */
  public static synchronized Choice byID(final long the_id) {
    return BY_ID.get(the_id);
  }
  
  /**
   * @return the database ID.
   */
  public long id() {
    return my_id;
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
      result &= other_choice.name().equals(name());
      result &= other_choice.description().equals(description());
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
