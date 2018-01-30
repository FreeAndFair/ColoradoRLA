/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Election information.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class AuditInfo implements Serializable {
  /**
   * The database stored precision for decimal types.
   */
  public static final int PRECISION = 10;
  
  /**
   * The database stored scale for decimal types.
   */
  public static final int SCALE = 8;

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The election type.
   */
  private String my_election_type;
  
  /**
   * The election date (stored as an instant).
   */
  private Instant my_election_date;
  
  /**
   * The public meeting date (stored as an instant).
   */
  private Instant my_public_meeting_date;
  
  /**
   * The random seed.
   */
  private String my_seed;
  
  /**
   * The risk limit.
   */
  @Column(precision = PRECISION, scale = SCALE)
  private BigDecimal my_risk_limit;

  /**
   * Constructs an empty ElectionInfo.
   */
  public AuditInfo() {
    // defaults
  }
  
  /**
   * Constructs a new SubmittedElectionInfo.
   * 
   * @param the_election_type The election type.
   * @param the_election_date The election date.
   * @param the_public_meeting_date The public meeting date.
   * @param the_seed The random seed.
   * @param the_risk_limit The risk limit (as a string).
   */
  public AuditInfo(final String the_election_type,
                               final Instant the_election_date,
                               final Instant the_public_meeting_date,
                               final String the_seed,
                               final BigDecimal the_risk_limit) {
    my_election_type = the_election_type;
    my_election_date = the_election_date;
    my_public_meeting_date = the_public_meeting_date;
    my_seed = the_seed;
    my_risk_limit = the_risk_limit;
  }
  
  /**
   * @return the election type.
   */
  public String electionType() {
    return my_election_type;
  }
  
  /**
   * @return a capitalized string for the election type.
   */
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public String capitalizedElectionType() {
    final String result;
    
    if (my_election_type == null) {
      result = null; 
    } else if (my_election_type.length() > 1) {
      result = my_election_type.substring(0, 1).toUpperCase(Locale.getDefault()) +
               my_election_type.substring(1).toLowerCase(Locale.getDefault());
    } else {
      result = my_election_type.toUpperCase(Locale.getDefault());
    }
    
    return result;
  }
  
  /**
   * @return the election date (as an instant).
   */
  public Instant electionDate() {
    return my_election_date;
  }
  
  /**
   * @return the public meeting date (as an instant).
   */
  public Instant publicMeetingDate() {
    return my_public_meeting_date;
  }
  
  /**
   * @return the random seed.
   */
  public String seed() {
    return my_seed;
  }
  
  /**
   * @return the risk limit.
   */
  public BigDecimal riskLimit() {
    return my_risk_limit;
  }
  
  /**
   * Updates this ElectionInfo with information from the specified one.
   * Any non-null fields in the specified ElectionInfo replace the 
   * corresponding fields of this ElectionInfo; any null fields in the 
   * specified ElectionInfo are ignored. It is not possible to nullify 
   * a field of an ElectionInfo once it has been set, only to replace 
   * it with a new non-null value.
   * 
   * @param the_other_info The other ElectionInfo.
   */
  public void updateFrom(final AuditInfo the_other_info) {
    if (the_other_info.my_election_type != null) {
      my_election_type = the_other_info.my_election_type;
    }
    if (the_other_info.my_election_date != null) {
      my_election_date = the_other_info.my_election_date;
    }
    if (the_other_info.my_public_meeting_date != null) {
      my_public_meeting_date = the_other_info.my_public_meeting_date;
    }
    if (the_other_info.my_seed != null) {
      my_seed = the_other_info.my_seed;
    }
    if (the_other_info.my_risk_limit != null) {
      my_risk_limit = the_other_info.my_risk_limit;
    }
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
    if (the_other instanceof AuditInfo) {
      final AuditInfo other_info = (AuditInfo) the_other;
      result &= nullableEquals(other_info.electionType(), electionType());
      result &= nullableEquals(other_info.electionDate(), electionDate());
      result &= nullableEquals(other_info.publicMeetingDate(), publicMeetingDate());
      result &= nullableEquals(other_info.seed(), seed());
      result &= nullableEquals(other_info.riskLimit(), riskLimit());
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
    return nullableHashCode(seed());
  }
}
