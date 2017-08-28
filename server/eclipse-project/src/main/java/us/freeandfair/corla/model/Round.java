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
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Information about an audit round. 
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Embeddable
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Round implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The start time.
   */
  @Column(nullable = false, updatable = false)
  private Instant my_start_time;

  /**
   * The end time.
   */
  private Instant my_end_time;

  /**
   * The expected number of ballots to audit in this round. This 
   * number is based on the audit random sequence, so it may
   * include duplicate ballots.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_expected_count;
  
  /**
   * The actual number of ballots audited in this round. This
   * number is based on the audit random sequence, so it may
   * include duplicate ballots.
   */
  private Integer my_actual_count;
  
  /**
   * The index of the audit random sequence where the round starts.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_start_index;
  
  /**
   * Constructs an empty round, solely for persistence. 
   */
  public Round() {
    super();
  }
    
  /**
   * Constructs a round with the specified parameters.
   * 
   * @param the_start_time The start time.
   * @param the_expected_count The expected number of ballots to audit.
   * @param the_start_index The index of the audit random sequence 
   * where the round starts.
   */
  public Round(final Instant the_start_time,
               final Integer the_expected_count,
               final Integer the_start_index) {
    super();
    my_start_time = the_start_time;
    my_expected_count = the_expected_count;
    my_start_index = the_start_index;
  }
  
  /**
   * @return the start time.
   */
  public Instant startTime() {
    return my_start_time;
  }

  /**
   * @return the end time.
   */
  public Instant endTime() {
    return my_end_time;
  }
  
  /**
   * Sets the end time.
   * 
   * @param the_end_time The end time.
   */
  public void setEndTime(final Instant the_end_time) {
    my_end_time = the_end_time;
  }
  
  /**
   * @return the expected number of ballots to audit.
   */
  public Integer expectedCount() {
    return my_expected_count;
  }
  
  /**
   * @return the actual number of ballots audited.
   */
  public Integer actualCount() {
    return my_actual_count;
  }
  
  /**
   * Sets the actual number of ballots audited.
   * 
   * @param the_actual_count The number of ballots audited; this
   * should reflect the audit random sequence, not physical 
   * ballot cards.
   */
  public void setActualCount(final int the_actual_count) {
    my_actual_count = the_actual_count;
  }
  
  /**
   * @return the index of the audit random sequence where this round
   * starts.
   */
  public Integer startIndex() {
    return my_start_index;
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "Round [start_time=" + my_start_time + ", end_time=" +
           my_end_time + ", expected_count=" + my_expected_count + 
           ", actual_count=" + my_actual_count + ", start_index=" + 
           my_start_index + "]";
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
    if (the_other instanceof Round) {
      final Round other_round = (Round) the_other;
      result &= nullableEquals(other_round.startTime(), startTime());
      result &= nullableEquals(other_round.endTime(), endTime());
      result &= nullableEquals(other_round.expectedCount(), expectedCount());
      result &= nullableEquals(other_round.actualCount(), actualCount());
      result &= nullableEquals(other_round.startIndex(), startIndex());
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
    return nullableHashCode(startTime());
  }
}
