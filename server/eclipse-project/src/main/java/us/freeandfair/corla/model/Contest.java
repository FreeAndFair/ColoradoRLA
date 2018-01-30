/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
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
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.json.ContestJsonAdapter;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * The definition of a contest; comprises a contest name and a set of
 * possible choices.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Cacheable(true)
@Table(name = "contest",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name", "county_id", "description", "votes_allowed"}) },
       indexes = { @Index(name = "idx_contest_name", columnList = "name"),
                   @Index(name = "idx_contest_name_county_description_votes_allowed", 
                          columnList = "name, county_id, description, votes_allowed") })
@JsonAdapter(ContestJsonAdapter.class)
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
   * The county to which this contest result set belongs. 
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn  
  private County my_county;
  
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
   * The maximum number of winners in this contest.
   */
  @Column(name = "winners_allowed", updatable = false, nullable = false)
  private Integer my_winners_allowed;
  
  /**
   * The import sequence number.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_sequence_number;

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
   * @param the_county The county for this contest.
   * @param the_description The contest description.
   * @param the_choices The set of contest choices.
   * @param the_votes_allowed The maximum number of votes that can
   * be made in this contest.
   * @param the_winners_allowed The maximum number of winners for
   * this contest.
   * @param the_sequence_number The sequence number.
   */
  //@ requires 1 <= the_votes_allowed;
  //@ requires the_votes_allowed <= the_choices.size();
  public Contest(final String the_name, final County the_county, 
                 final String the_description, final List<Choice> the_choices, 
                 final int the_votes_allowed, final int the_winners_allowed,
                 final int the_sequence_number)  {
    super();
    my_name = the_name;
    my_county = the_county;
    my_description = the_description;
    my_choices.addAll(the_choices);
    my_votes_allowed = the_votes_allowed;
    my_winners_allowed = the_winners_allowed;
    my_sequence_number = the_sequence_number;
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
   * @return the county ID.
   */
  public County county() {
    return my_county;
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
  public Integer votesAllowed() {
    return my_votes_allowed;
  }

  /**
   * @return the maximum number of winners in this contest.
   */
  public Integer winnersAllowed() {
    return my_winners_allowed;
  }
  
  /**
   * @return the sequence number of this contest.
   */
  public Integer sequenceNumber() {
    return my_sequence_number;
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
      result &= nullableEquals(other_contest.county(), county());
      result &= nullableEquals(other_contest.description(), description());
      result &= nullableEquals(other_contest.choices(), choices());
      result &= nullableEquals(other_contest.votesAllowed(), votesAllowed());
      result &= nullableEquals(other_contest.sequenceNumber(), sequenceNumber());
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
