/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
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
 * A county involved in an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county")
public class County implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The table of objects that have been created.
   */
  private static final Map<County, County> CACHE = 
      new HashMap<County, County>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, County> BY_ID =
      new HashMap<Long, County>();
  
  /**
   * The current ID number to be used.
   */
  private static long current_id;
 
  /**
   * The database ID of this county.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  @SuppressWarnings("PMD.ImmutableField")
  private long my_id = getID();

  /**
   * The county name.
   */
  private final String my_name;

  /**
   * The county ID.
   */
  private final String my_identifier;
  
  /**
   * Constructs an empty county, solely for persistence. 
   */
  protected County() {
    my_name = "";
    my_identifier = "";
  }
    
  /**
   * Constructs a county with the specified parameters.
   * 
   * @param the_name The county name.
   * @param the_id The county description.
   */
  protected County(final String the_name, final String the_id) {
    my_name = the_name;
    my_identifier = the_id;
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
  public static synchronized County instance(final String the_name, 
                                             final String the_id) {
    County result = new County(the_name, the_id);
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
  public static synchronized County byID(final long the_id) {
    return BY_ID.get(the_id);
  }
  
  /**
   * @return the database ID.
   */
  public long id() {
    return my_id;
  }
  
  /**
   * @return the county name.
   */
  public String name() {
    return my_name;
  }
  
  /**
   * @return the county ID.
   */
  public String identifier() {
    return my_identifier;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "County [name=" + my_name + ", id=" +
           my_identifier + "]";
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
    if (the_other instanceof County) {
      final County other_county = (County) the_other;
      result &= other_county.name().equals(name());
      result &= other_county.identifier().equals(identifier());
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
