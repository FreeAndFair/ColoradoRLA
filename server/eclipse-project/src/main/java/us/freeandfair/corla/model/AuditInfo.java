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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.MapKey;

import us.freeandfair.corla.persistence.CountyCanonicalContestsMapConverter;

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
   * The mapping of county name to a set of contest names within each
   * county.
   */
  @Convert(converter = CountyCanonicalContestsMapConverter.class)
  @Column(name = "canonical_contests", columnDefinition = "text")
  @MapKey(name = "my_id")
  private Map<String, Set<String>>
    canonicalContests = new TreeMap<String, Set<String>>();

  /**
   * Constructs an empty AuditInfo using defaults
   */
  public AuditInfo() {
    // defaults
  }

  /**
   * Constructs a new AuditInfo.
   *
   * @param electionType The election type.
   * @param electionDate The election date.
   * @param publicMeetingDate The public meeting date.
   * @param seed The random seed.
   * @param riskLimit The risk limit
   */
  public AuditInfo(final String electionType,
                   final Instant electionDate,
                   final Instant publicMeetingDate,
                   final String seed,
                   final BigDecimal riskLimit) {
    my_election_type = electionType;
    my_election_date = electionDate;
    my_public_meeting_date = publicMeetingDate;
    my_seed = seed;
    my_risk_limit = riskLimit;
  }

  /**
   * Constructs a new AuditInfo with a collection of canonical contests.
   *
   * @param electionType The election type.
   * @param electionDate The election date.
   * @param publicMeetingDate The public meeting date.
   * @param seed The random seed.
   * @param riskLimit The risk limit
   * @param contests The map of canonical contest names for counties
   */
  public AuditInfo(final String electionType,
                   final Instant electionDate,
                   final Instant publicMeetingDate,
                   final String seed,
                   final BigDecimal riskLimit,
                   final Map<String, Set<String>> contests) {
    my_election_type = electionType;
    my_election_date = electionDate;
    my_public_meeting_date = publicMeetingDate;
    my_seed = seed;
    my_risk_limit = riskLimit;
    this.canonicalContests = contests;
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
   * @return some kind of canonical contests thingy
   */

  public Map<String, Set<String>> canonicalContests() {
    return this.canonicalContests;
  }

  /**
   * setter of county-contest mapping
   * @param m The map you want to set
   */
  public void setCanonicalContests(final Map<String, Set<String>> m) {
    this.canonicalContests = m;
  }

  /**
   * Updates this AuditInfo with information from the specified one.
   * Any non-null fields in the specified AuditInfo replace the
   * corresponding fields of this AuditInfo; any null fields in the
   * specified AuditInfo are ignored. It is not possible to nullify
   * a field of an AuditInfo once it has been set, only to replace
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

    if (the_other_info.canonicalContests != null) {
      canonicalContests = the_other_info.canonicalContests;
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

  /**
   * a good practice
   */
  @Override
  public String toString() {
    return
      "AuditInfo[canonicalContests=" + canonicalContests() +
      ", electionType=" + electionType() +
      ", electionDate=" + electionDate() +
      ", publicMeetingDate=" + publicMeetingDate() +
      ", seed=" + seed() +
      ", riskLimit" + riskLimit();
  }
}
