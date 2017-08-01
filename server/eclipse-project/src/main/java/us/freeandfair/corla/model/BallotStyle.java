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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.gson.ContestsJsonAdapter;

/**
 * A ballot style has an identifier and a list of contests on the ballot.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "ballot_style")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class BallotStyle implements Serializable {  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The table of objects that have been created.
   */
  private static final Map<BallotStyle, BallotStyle> CACHE = 
      new HashMap<BallotStyle, BallotStyle>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, BallotStyle> BY_ID =
      new HashMap<Long, BallotStyle>();
  
  /**
   * The current ID number to be used.
   */
  private static long current_id;
  
  /**
   * The ballot style database ID.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private Long my_id = getID();

  /**
   * The ballot style ID.
   */
  private String my_identifier;
  
  /**
   * The list of contests on a ballot of this style.
   */
  @ManyToMany
  @JsonAdapter(ContestsJsonAdapter.class)
  private List<Contest> my_contests;
  
  /**
   * Constructs an empty ballot style, solely for persistence.
   */
  protected BallotStyle() {
    my_identifier = "";
  }
  
  /**
   * Constructs a new ballot style.
   * 
   * @param the_name The ballot style ID.
   * @param the_contests The list of contests on a ballot of this style.
   */
  protected BallotStyle(final String the_name, final List<Contest> the_contests) {
    my_identifier = the_name;
    my_contests = new ArrayList<Contest>(the_contests);
  }
  
  /**
   * @return the next ID
   */
  private static synchronized long getID() {
    return current_id++;
  }
  
  /**
   * Get all ballot styles that match the specified ID (normally, we would expect
   * there to only be one, but malformed data can cause that not to happen).
   * 
   * @param the_id The ID. If this is null, all ballot styles are returned.
   * @return the requested ballot styles.
   */
  public static synchronized Collection<BallotStyle> getMatching(final String the_id) {
    final Set<BallotStyle> result = new HashSet<BallotStyle>();
    for (final BallotStyle bs : CACHE.keySet()) {
      if (the_id == null || the_id.equals(bs.identifier())) {
        result.add(bs);
      }
    }
    return result;
  }
  
  /**
   * @return all known ballot styles.
   */
  public static synchronized Collection<BallotStyle> getAll() {
    return new HashSet<BallotStyle>(CACHE.keySet());
  }

  /**
   * Returns a ballot style with the specified parameters.
   * 
   * @param the_name The ballot style name.
   * @param the_contests The list of contests on a ballot of this style.
   */
  public static synchronized BallotStyle instance(final String the_name, 
                                                  final List<Contest> the_contests) {
    BallotStyle result = new BallotStyle(the_name, the_contests);
    if (CACHE.containsKey(result)) {
      result = CACHE.get(result);
    } else {
      CACHE.put(result, result);
      BY_ID.put(result.id(), result);
    }
    return result;
  }
  
  /**
   * Returns the ballot style with the specified ID.
   * 
   * @param the_id The ID.
   * @return the ballot style, or null if it doesn't exist.
   */
  public static synchronized BallotStyle byID(final long the_id) {
    return BY_ID.get(the_id);
  }

  /**
   * @return the database ID.
   */
  public long id() {
    return my_id;
  }

  /**
   * @return the ballot style identifier.
   */
  public String identifier() {
    return my_identifier;
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
    return "BallotStyle [identifier=" + my_identifier + ", contests=" +
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
    boolean result = true;
    if (the_other instanceof BallotStyle) {
      final BallotStyle other_style = (BallotStyle) the_other;
      result &= other_style.identifier().equals(identifier());
      result &= other_style.contests().equals(contests());
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
