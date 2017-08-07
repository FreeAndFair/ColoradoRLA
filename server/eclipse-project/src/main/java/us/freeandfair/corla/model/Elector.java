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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.hibernate.AbstractEntity;
import us.freeandfair.corla.util.EqualsHashcodeHelper;

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
public class Elector extends AbstractEntity {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

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
      result &= EqualsHashcodeHelper.nullableEquals(other_choice.firstName(), firstName());
      result &= EqualsHashcodeHelper.nullableEquals(other_choice.lastName(), lastName());
      result &= EqualsHashcodeHelper.nullableEquals(other_choice.politicalParty(), 
                                                    politicalParty());
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
