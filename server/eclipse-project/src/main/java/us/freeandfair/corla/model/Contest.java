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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * The definition of a contest; comprises a contest name and a set of
 * possible choices.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Immutable // this is a Hibernate-specific annotation, but there is no JPA alternative
@Cacheable(true)
@Table(name = "contest",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name", "description", "votes_allowed"}) },
       indexes = { @Index(name = "idx_contest_name", columnList = "name"),
                   @Index(name = "idx_contest_name_description_votes_allowed", 
                          columnList = "name, description, votes_allowed") })
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Contest implements PersistentEntity, Serializable {
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
   * The contest name.
   */
  @Column(name = "name", updatable = false, nullable = false)
  private String my_name;

  /**
   * The contest description.
   */
  @Column(name = "description", updatable = false, nullable = false)
  private String my_description;
  
  /**
   * The contest choices.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @OrderColumn(name = "index")
  @CollectionTable(name = "contest_choice",
                   joinColumns = @JoinColumn(name = "contest_id", 
                                             referencedColumnName = "my_id"))
  private List<Choice> my_choices = new ArrayList<>();
  
  /**
   * The maximum number of votes that can be made in this contest.
   */
  @Column(name = "votes_allowed", updatable = false, nullable = false)
  private Integer my_votes_allowed;
  
  /**
   * Constructs an empty contest, solely for persistence.
   */
  public Contest() {
    super();
    // default values for everything
  }
  
  /**
   * Constructs a contest with the specified parameters.
   * 
   * @param the_name The contest name.
   * @param the_description The contest description.
   * @param the_choices The set of contest choices.
   * @param the_votes_allowed The maximum number of votes that can
   * be made in this contest.
   */
  //@ requires 1 <= the_votes_allowed;
  //@ requires the_votes_allowed <= the_choices.size();
  public Contest(final String the_name, final String the_description, 
                 final List<Choice> the_choices, final int the_votes_allowed)  {
    super();
    my_name = the_name;
    my_description = the_description;
    my_choices.addAll(the_choices);
    my_votes_allowed = the_votes_allowed;
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
   * @return the contest name.
   */
  public String name() {
    return my_name;
  }
  
  /**
   * @return the contest description.
   */
  public String description() {
    return my_description;
  }
  
  /**
   * Checks to see if the specified choice is valid for this contest.
   * 
   * @param the_choice The choice.
   * @return true if the choice is valid, false otherwise.
   */
  public boolean isValidChoice(final String the_choice) {
    for (final Choice c : my_choices) {
      if (c.name().equals(the_choice)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @return the contest choices.
   */
  public List<Choice> choices() {
    return Collections.unmodifiableList(my_choices);
  }
  
  /**
   * @return the maximum number of votes that can be made in this contest.
   */
  public int votesAllowed() {
    return my_votes_allowed;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "Contest [name=" + my_name + ", description=" +
           my_description + ", choices=" + choices() + 
           ", votes_allowed=" + my_votes_allowed + "]";
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
    if (the_other instanceof Contest) {
      final Contest other_contest = (Contest) the_other;
      result &= nullableEquals(other_contest.name(), name());
      result &= nullableEquals(other_contest.description(), description());
      result &= nullableEquals(other_contest.choices(), choices());
      result &= nullableEquals(other_contest.votesAllowed(), votesAllowed());
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
    return nullableHashCode(name().hashCode());
  }
}
