/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
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

import us.freeandfair.corla.hibernate.EntityOperations;
import us.freeandfair.corla.hibernate.Persistence;

/**
 * A county involved in an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(updatable = false, nullable = false)
  private Long my_id;

  /**
   * The county name.
   */
  @Column(nullable = false, updatable = false)
  private String my_name;

  /**
   * The county ID.
   */
  @Column(nullable = false, updatable = false)
  private String my_identifier;
  
  /**
   * Constructs an empty county, solely for persistence. 
   */
  protected County() {
    // default values
  }
    
  /**
   * Constructs a county with the specified parameters.
   * 
   * @param the_name The county name.
   * @param the_id The county ID.
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
   * Returns a county with the specified parameters.
   * 
   * @param the_name The county name.
   * @param the_description The county ID.
   */
  public static synchronized County instance(final String the_name, 
                                             final String the_id) {
    County result = 
        EntityOperations.matchingEntity(new County(the_name, the_id), 
                                   County.class);

    if (!Persistence.isEnabled()) {
      // cache ourselves because persistence is not enabled
      if (CACHE.containsKey(result)) {
        result = CACHE.get(result);
      } else {
        result.my_id = getID();
        CACHE.put(result, result);
        BY_ID.put(result.id(), result);
      }
    }
    
    return result;
  }
  
  /**
   * Returns the county with the specified ID.
   * 
   * @param the_id The ID.
   * @return the county, or null if it doesn't exist.
   */
  public static synchronized County byID(final long the_id) {
    final County result;
    
    if (Persistence.isEnabled()) {
      result = EntityOperations.entityByID(the_id, County.class);
    } else {
      result = BY_ID.get(the_id);
    }
    
    return result;
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
