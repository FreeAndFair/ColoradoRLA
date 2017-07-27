/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 25, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * The definition of a contest; comprises a contest name and a set of possible
 * choices.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "contest")
public class Contest implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The database ID of this contest.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long my_db_id;
  
  /**
   * The contest name.
   */
  private final String my_name;

  /**
   * The contest description.
   */
  private final String my_description;
  
  /**
   * The set of contest choices.
   */
  @OneToMany
  @Cascade({CascadeType.MERGE})
  private final List<Choice> my_choices;
  
  /**
   * The maximum number of votes that can be made in this contest.
   */
  private final int my_votes_allowed;
  
  /**
   * Constructs an empty contest, solely for persistence.
   */
  protected Contest() {
    my_name = "";
    my_description = "";
    my_choices = null;
    my_votes_allowed = 0;
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
  public Contest(final String the_name, final String the_description, 
                 final List<Choice> the_choices, final int the_votes_allowed) {
    super();
    my_name = the_name;
    my_description = the_description;
    my_choices = the_choices;
    my_votes_allowed = the_votes_allowed;
  }
  
  /**
   * @return the database ID.
   */
  public long dbID() {
    return my_db_id;
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
           my_description + ", choices=" + my_choices + 
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
    boolean result = false;
    if (the_other instanceof Contest) {
      final Contest other_contest = (Contest) the_other;
      result &= other_contest.name().equals(name());
      result &= other_contest.description().equals(description());
      result &= other_contest.choices().equals(choices());
      result &= other_contest.votesAllowed() == votesAllowed();
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
