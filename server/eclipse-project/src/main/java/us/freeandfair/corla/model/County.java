/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A county involved in an audit.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Immutable // this is a Hibernate-specific annotation, but there is no JPA alternative
@Cacheable(true)
@Table(name = "county")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class County implements PersistentEntity, Serializable {  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The database, and county, ID.
   */
  @Id
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;
  
  /**
   * The county name.
   */
  @Column(nullable = false, updatable = false, unique = true)
  private String my_name;

  /**
   * Constructs an empty county, solely for persistence. 
   */
  public County() {
    super();
  }
    
  /**
   * Constructs a county with the specified parameters.
   * 
   * @param the_name The county name.
   * @param the_identifier The county ID.
   * @param the_administrators The administrators.
   */
  public County(final String the_name, final Long the_identifier) {
    super();
    my_name = the_name;
    my_id = the_identifier;
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
  @Override
  public Long id() {
    return my_id;
  }
  
  /**
   * @return the version for this county.
   */
  @Override
  public Long version() {
    return my_version;
  }
  
  /**
   * Sets the ID of this county.
   * 
   * @param the_id The ID.
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "County [name=" + my_name + ", id=" +
           my_id + "]";
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
      result &= nullableEquals(other_county.name(), name());
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
    return nullableHashCode(name());
  }
  
  
  /**
   * A comparator to sort County objects alphabetically by county name.
   */
  @SuppressWarnings("PMD.AtLeastOneConstructor")
  public static class NameComparator 
      implements Serializable, Comparator<County> {
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1;
    
    /**
     * Orders two County objects lexicographically by county name.
     * 
     * @param the_first The first response.
     * @param the_second The second response.
     * @return a positive, negative, or 0 value as the first response is
     * greater than, equal to, or less than the second, respectively.
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    public int compare(final County the_first, 
                       final County the_second) {
      final int result;
      if (the_first == null && the_second == null) {
        result = 0;
      } else if (the_first == null || the_first.name() == null) {
        result = -1;
      } else if (the_second == null || the_second.name() == null) {
        result = 1;
      } else {
        result = the_first.name().compareTo(the_second.name());
      }
      return result;
    }
  }
}
