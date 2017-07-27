/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * A ballot style has an identifier and a list of contests on the ballot.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "ballot_style")
public class BallotStyle implements Serializable {
//
//  /**
//   * The ballot style database ID.
//   */
//  @Id
//  @GeneratedValue(strategy = GenerationType.TABLE)
//  private long my_db_id;
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The ballot style ID.
   */
  @Id
  private final String my_id;
  
  /**
   * The list of contests on a ballot of this style.
   */
  @ManyToMany
  @Cascade({CascadeType.SAVE_UPDATE})
  private final List<Contest> my_contests;
  
  /**
   * Constructs an empty ballot style, solely for persistence.
   */
  protected BallotStyle() {
    my_id = "";
    my_contests = null;
  }
  
  /**
   * Constructs a new ballot style.
   * 
   * @param the_id The ballot style ID.
   * @param the_contests The list of contests on a ballot of this style.
   */
  public BallotStyle(final String the_id, final List<Contest> the_contests) {
    my_id = the_id;
    // TODO: clone to make immutable
    my_contests = the_contests;
  }
  
//  /**
//   * @return the database ID.
//   */
//  public long dbID() {
//    return my_db_id;
//  }

  /**
   * @return the ballot style ID.
   */
  public String id() {
    return my_id;
  }
  
  /**
   * @return the contests on a ballot of this style.
   */
  public List<Contest> contests() {
    return Collections.unmodifiableList(my_contests);
  }

  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "BallotStyle [id=" + my_id + ", contests=" +
           my_contests + "]";
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
    if (the_other instanceof BallotStyle) {
      final BallotStyle other_style = (BallotStyle) the_other;
      result &= other_style.id().equals(id());
      result &= other_style.contests().equals(contests());
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
