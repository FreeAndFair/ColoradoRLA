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

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

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
public class County extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The county name.
   */
  @Column(nullable = false, updatable = false, unique = true)
  private String my_name;

  /**
   * The county ID.
   */
  @Column(nullable = false, updatable = false, unique = true)
  private Integer my_identifier;
 
  /**
   * The contests in this county.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "county_contest",
            joinColumns = @JoinColumn(name = "county_id", 
                                      referencedColumnName = "my_identifier"),
            inverseJoinColumns = @JoinColumn(name = "contest_id", 
                                             referencedColumnName = "my_id"))
  private Set<Contest> my_contests;
  
  /**
   * The administrators for this county.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "county_administrator",
             joinColumns = @JoinColumn(name = "county_id", 
                                       referencedColumnName = "my_identifier"),
             inverseJoinColumns = @JoinColumn(name = "administrator_id", 
                                              referencedColumnName = "my_id"))
  private Set<Administrator> my_administrators;

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
   * @param the_contests The contests.
   * @param the_administrators The administrators.
   */
  public County(final String the_name, final Integer the_identifier,
                final Set<Contest> the_contests, 
                final Set<Administrator> the_administrators) {
    super();
    my_name = the_name;
    my_identifier = the_identifier;
    my_contests = the_contests;
    my_administrators = the_administrators;
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
   * @return the contests in this county. The result set is mutable,
   * and can be used to change the persistent county record.
   */
  public Set<Contest> contests() {
    return my_contests;
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
      result &= nullableEquals(other_county.name(), name());
      result &= nullableEquals(other_county.identifier(), identifier());
      result &= nullableEquals(other_county.contests(), contests());
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
