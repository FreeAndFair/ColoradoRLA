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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.hibernate.AbstractEntity;

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
public class County extends AbstractEntity {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The county name.
   */
  @Column(nullable = false, updatable = false)
  private String my_name;

  /**
   * The county ID.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_identifier;
  
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
   * @param the_id The county ID.
   */
  public County(final String the_name, final Integer the_id) {
    super();
    my_name = the_name;
    my_identifier = the_id;
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
  public Integer identifier() {
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
