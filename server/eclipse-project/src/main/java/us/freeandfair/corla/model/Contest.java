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

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * The definition of a contest; comprises a contest name and a set of
 * possible choices.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "contest")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Contest extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The contest name.
   */
  @Column(updatable = false, nullable = false)
  private String my_name;

  /**
   * The contest description.
   */
  @Column(updatable = false, nullable = false)
  private String my_description;
  
  /**
   * The contest choices.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @OrderColumn(name = "index")
  @CollectionTable(name = "contest_choice",
                   joinColumns = @JoinColumn(name = "contest_id", 
                                             referencedColumnName = "my_id"))
  @Column(name = "choice")
  private List<String> my_choice_names;
  
  /**
   * The contest choice descriptions.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "contest_choice_description",
                   joinColumns = @JoinColumn(name = "contest_id", 
                                             referencedColumnName = "my_id"))
  @MapKeyColumn(name = "choice")
  @Column(name = "description")
  private Map<String, String> my_choice_descriptions;
  
  /**
   * The maximum number of votes that can be made in this contest.
   */
  @Column(updatable = false, nullable = false)
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
    my_choice_names = new ArrayList<String>();
    my_choice_descriptions = new HashMap<String, String>();
    for (final Choice c : the_choices) {
      my_choice_names.add(c.name());
      my_choice_descriptions.put(c.name(), c.description());
    }
    my_votes_allowed = the_votes_allowed;
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
    return my_choice_descriptions.containsKey(the_choice);
  }
  
  /**
   * @return the contest choices.
   */
  public List<Choice> choices() {
    final List<Choice> result = new ArrayList<Choice>();
    for (final String name : my_choice_names) {
      result.add(new Choice(name, my_choice_descriptions.get(name)));
    }
    return Collections.unmodifiableList(result);
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
      // note that the next two compare private fields instead of accessors
      // but still maintain the contract on equals/hashCode
      result &= nullableEquals(other_contest.my_choice_names, my_choice_names);
      result &= nullableEquals(other_contest.my_choice_descriptions, my_choice_descriptions);
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
    return toString().hashCode();
  }
}
