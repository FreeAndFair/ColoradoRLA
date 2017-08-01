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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * An elector; has a first name, a last name, and a political party.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "choice")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Elector implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The table of objects that have been created.
   */
  private static final Map<Elector, Elector> CACHE = 
      new HashMap<Elector, Elector>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, Elector> BY_ID =
      new HashMap<Long, Elector>();
  
  /**
   * The current ID number to be used.
   */
  private static long current_id;
 
  /**
   * The database ID of this choice.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  @SuppressWarnings("PMD.ImmutableField")
  private Long my_id = getID();

  /**
   * The first name.
   */
  private String my_first_name;

  /**
   * The last name.
   */
  private String my_last_name;

  /**
   * The political party
   */
  private String my_political_party;
  
  /**
   * Constructs an empty elector, solely for persistence. 
   */
  protected Elector() {
    my_first_name = "";
    my_last_name = "";
    my_political_party = "";
  }
    
  /**
   * Constructs an elector with the specified parameters.
   * 
   * @param the_first_name The first name.
   * @param the_last_name The last name.
   * @param the_political_party The political party.
   */
  protected Elector(final String the_first_name, final String the_last_name,
                    final String the_political_party) {
    my_first_name = the_first_name;
    my_last_name = the_last_name;
    my_political_party = the_political_party;
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
  public static synchronized Elector instance(final String the_first_name, 
                                              final String the_last_name,
                                              final String the_political_party) {
    Elector result = new Elector(the_first_name, the_last_name, the_political_party);
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
  public static synchronized Elector byID(final long the_id) {
    return BY_ID.get(the_id);
  }
  
  /**
   * @return the database ID.
   */
  public long id() {
    return my_id;
  }
  
  /**
   * @return the first name.
   */
  public String firstName() {
    return my_first_name;
  }

  /**
   * @return the last name.
   */
  public String lastName() {
    return my_last_name;
  }
  
  /**
   * @return the political party.
   */
  public String politicalParty() {
    return my_political_party;
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "Elector [first_name=" + my_first_name + ", last_name=" +
           my_last_name + ", political_party=" + my_political_party + "]";
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
    if (the_other instanceof Elector) {
      final Elector other_choice = (Elector) the_other;
      result &= other_choice.firstName().equals(firstName());
      result &= other_choice.lastName().equals(lastName());
      result &= other_choice.politicalParty().equals(politicalParty());
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
