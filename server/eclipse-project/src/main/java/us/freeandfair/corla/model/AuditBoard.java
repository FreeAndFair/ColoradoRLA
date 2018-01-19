/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import us.freeandfair.corla.persistence.ElectorListConverter;

/**
 * An audit board. Contains a set of electors, a timestamp when the board
 * signed in, and a timestamp when the board signed out.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class AuditBoard implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * The audit board members.
   */
  @Column(name = "members", columnDefinition = "text")
  @Convert(converter = ElectorListConverter.class)
  private List<Elector> my_members = new ArrayList<>();
  
  /**
   * The time at which the audit board signed in.
   */
  @Column(nullable = false, updatable = false)
  private Instant my_sign_in_time;
  
  /**
   * The time at which the audit board signed out.
   */
  private Instant my_sign_out_time;
  
  /**
   * Constructs a new, empty audit board, solely for persistence.
   */
  public AuditBoard() {
    // defaults
  }
  
  /**
   * Constructs a new audit board.
   * 
   * @param the_members The set of Electors on the board.
   * @param the_sign_in_time The sign in time of the board.
   */
  public AuditBoard(final List<Elector> the_members, final Instant the_sign_in_time) {
    my_members.addAll(the_members);
    my_sign_in_time = the_sign_in_time;
  }
  
  /**
   * @return the audit board members.
   */
  public List<Elector> members() {
    return Collections.unmodifiableList(my_members);
  }
  
  /**
   * @return the sign in time.
   */
  public Instant signInTime() {
    return my_sign_in_time;
  }
  
  /**
   * @return the sign out time.
   */
  public Instant signOutTime() {
    return my_sign_out_time;
  }
  
  /**
   * Sets the sign out time.
   * 
   * @param the_sign_out_time The time.
   */
  public void setSignOutTime(final Instant the_sign_out_time) {
    my_sign_out_time = the_sign_out_time;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "AuditBoard [members=" + my_members + ", sign_in_time=" +
           my_sign_in_time + ", sign_out_time=" + my_sign_out_time + "]";
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
    if (the_other instanceof AuditBoard) {
      final AuditBoard other_board = (AuditBoard) the_other;
      result &= nullableEquals(other_board.members(), members());
      result &= nullableEquals(other_board.signInTime(), signInTime());
      result &= nullableEquals(other_board.signOutTime(), signOutTime());
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
    return nullableHashCode(signInTime());
  } 
}
