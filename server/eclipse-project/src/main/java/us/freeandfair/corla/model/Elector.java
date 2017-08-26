/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * An elector; has a first name, a last name, and a political party.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Immutable // this is a Hibernate-specific annotation, but there is no JPA alternative
@Cacheable(true)
@Table(name = "elector")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Elector implements PersistentEntity, Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;
  
  /**
   * The first name.
   */
  @Column(nullable = false, updatable = false)
  private String my_first_name;

  /**
   * The last name.
   */
  @Column(nullable = false, updatable = false)
  private String my_last_name;

  /**
   * The political party
   */
  @Column(nullable = false, updatable = false)
  private String my_political_party;
  
  /**
   * Constructs an empty elector, solely for persistence. 
   */
  public Elector() {
    super();
  }
    
  /**
   * Constructs an elector with the specified parameters.
   * 
   * @param the_first_name The first name.
   * @param the_last_name The last name.
   * @param the_political_party The political party.
   */
  public Elector(final String the_first_name,
                 final String the_last_name,
                 final String the_political_party) {
    super();
    my_first_name = the_first_name;
    my_last_name = the_last_name;
    my_political_party = the_political_party;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
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
      final Elector other_elector = (Elector) the_other;
      result &= nullableEquals(other_elector.firstName(), firstName());
      result &= nullableEquals(other_elector.lastName(), lastName());
      result &= nullableEquals(other_elector.politicalParty(), politicalParty());
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
    return nullableHashCode(lastName());
  }
}
