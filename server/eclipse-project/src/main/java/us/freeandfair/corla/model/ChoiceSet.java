/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 26, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * A set of choices. This is necessary for Hibernate to automatically persist
 * our object model, since it can't deal with nested collections.
 */
@Entity(name = "ChoiceSet")
public class ChoiceSet {
  /**
   * The database ID of this choice set.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long my_db_id; 
  
  /**
   * The set encapsulated by this ChoiceSet.
   */
  @ManyToMany
  @Cascade({CascadeType.MERGE})
  private final Set<Choice> my_set;
  
  /**
   * Constructs a new ChoiceSet.
   */
  public ChoiceSet() {
    my_set = new HashSet<Choice>();
  }
  
  /**
   * @return the database ID.
   */
  public long dbID() {
    return my_db_id;
  }
  
  /**
   * Tests to see if the specified choice is in this set.
   * 
   * @param the_choice The choice to test.
   * @return true if the specified choice is in this set, false otherwise.
   */
  public boolean contains(final Choice the_choice) {
    return my_set.contains(the_choice);
  }
  
  /**
   * Adds the specified choice to this set.
   * 
   * @param the_choice The choice to add.
   * @return true if the set was changed, false otherwise.
   */
  public boolean add(final Choice the_choice) {
    return my_set.add(the_choice);
  }
  
  /**
   * Removes the specified choice from this set.
   * 
   * @param the_choice The choice to remove.
   * @return true if the set was changed, false otherwise.
   */
  public boolean remove(final Choice the_choice) {
    return my_set.remove(the_choice);
  }
  
  /** 
   * Empties this set.
   */
  public void clear() {
    my_set.clear();
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "ChoiceSet [" + my_set  + "]";
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
    if (the_other instanceof ChoiceSet) {
      final ChoiceSet other_cs = (ChoiceSet) the_other;
      result &= other_cs.my_set.equals(my_set);
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  public int hashCode() {
    return my_set.hashCode();
  }
}
