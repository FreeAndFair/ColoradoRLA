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

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A county involved in an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
   * The "my_id" string.
   */
  private static final String MY_ID = "my_id";
  
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
   * The administrators for this county.
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "county_administrator",
             joinColumns = @JoinColumn(name = "county_id", 
                                       referencedColumnName = MY_ID),
             inverseJoinColumns = @JoinColumn(name = "administrator_id", 
                                              referencedColumnName = MY_ID))
  private Set<Administrator> my_administrators = new HashSet<>();

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
  public County(final String the_name, final Long the_identifier,
                final Set<Administrator> the_administrators) {
    super();
    my_name = the_name;
    my_id = the_identifier;
    my_administrators.addAll(the_administrators);
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
   * @return the administrators for this county. The result set is
   * mutable, and can be used to change the persistent county record.
   */
  public Set<Administrator> administrators() {
    return my_administrators;
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
      result &= nullableEquals(other_county.administrators(), administrators());
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
}
