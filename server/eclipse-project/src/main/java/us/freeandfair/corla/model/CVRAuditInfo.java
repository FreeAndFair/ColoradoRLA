/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.AuditReasonSetConverter;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A class representing a contest to audit or hand count.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Cacheable(true)
@Table(name = "cvr_audit_info")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
// note: CVRAuditInfo is not serializable because it references CountyDashboard,
// which is not serializable
@SuppressWarnings("PMD.ImmutableField")
public class CVRAuditInfo implements Comparable<CVRAuditInfo>,
                                     PersistentEntity {
  /**
   * The ID number. This is always the same as the CVR ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  private Long my_id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;

  /**
   * The CVR to audit.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private CastVoteRecord my_cvr;

  /**
   * The submitted audit CVR.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private CastVoteRecord my_acvr;

  /**
   * The number of times this CVRAuditInfo appears in the audit
   * sequence.
   */
  @Column(nullable = false)
  private Integer my_multiplicity = 1;

  /**
   * The number of times this CVRAuditInfo has been considered
   * in the audit calculations.
   */
  @Column(nullable = false)
  private Integer my_counted = 0;

  /**
   * The number of discrepancies found in the audit so far.
   */
  @Column(nullable = false, name = "discrepancy", columnDefinition = "text")
  @Convert(converter = AuditReasonSetConverter.class)
  private Set<AuditReason> my_discrepancy = new HashSet<>();

  /**
   * The number of disagreements found in the audit so far.
   */
  @Column(nullable = false, name = "disagreement", columnDefinition = "text")
  @Convert(converter = AuditReasonSetConverter.class)
  private Set<AuditReason> my_disagreement = new HashSet<>();

  /**
   * Constructs an empty CVRAuditInfo, solely for persistence.
   */
  public CVRAuditInfo() {
    super();
  }

  /**
   * Constructs a new CVRAuditInfo for the specified CVR to audit.
   *
   * @param the_cvr The CVR to audit.
   */
  public CVRAuditInfo(final CastVoteRecord the_cvr) {
    super();
    my_id = the_cvr.id();
    my_cvr = the_cvr;
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
   * @return the CVR to audit.
   */
  public CastVoteRecord cvr() {
    return my_cvr;
  }

  /**
   * @return the submitted audit CVR..
   */
  public CastVoteRecord acvr() {
    return my_acvr;
  }

  /**
   * Sets the submitted audit CVR for this record.
   *
   * @param the_acvr The audit CVR.
   */
  public void setACVR(final CastVoteRecord the_acvr) {
    my_acvr = the_acvr;
  }

  /**
   * @return the number of times this record appears in the audit
   * sequence.
   */
  public int multiplicity() {
    return my_multiplicity;
  }

  /**
   * Sets the number of times this record appears in the audit sequence.
   *
   * @param the_multiplicity The new value.
   */
  public void setMultiplicity(final int the_multiplicity) {
    my_multiplicity = the_multiplicity;
  }

  /**
   * @return the number of times this record has been counted in
   * the audit calculations.
   */
  public int counted() {
    return my_counted;
  }

  /**
   * Sets the number of times this record has been counted in the
   * audit calculations.
   *
   * @param the_counted The new value.
   */
  public void setCounted(final int the_counted) {
    my_counted = the_counted;
  }

  /**
   * @return a map from audit reason to whether this record was marked
   * as a discrepancy in a contest audited for that reason.
   */
  public Set<AuditReason> discrepancy() {
    return Collections.unmodifiableSet(my_discrepancy);
  }

  /**
   * Sets the audit reasons for which the record is marked as a discrepancy.
   *
   * @param the_reasons The reasons.
   */
  public void setDiscrepancy(final Set<AuditReason> the_reasons) {
    my_discrepancy.clear();
    if (the_reasons != null) {
      my_discrepancy.addAll(the_reasons);
    }
  }

  /**
   * @return a map from audit reason to whether this record was marked
   * as a disagreement in a contest audited for that reason.
   */
  public Set<AuditReason> disagreement() {
    return Collections.unmodifiableSet(my_disagreement);
  }

  /**
   * Sets the audit reasons for which the record is marked as a disagreement.
   *
   * @param the_reasons The reasons.
   */
  public void setDisagreement(final Set<AuditReason> the_reasons) {
    my_disagreement.clear();
    if (the_reasons != null) {
      my_disagreement.addAll(the_reasons);
    }
  }

  /**
   * @return a String representation of this contest to audit.
   */
  @Override
  public String toString() {
    final String cvr;
    final String acvr;
    if (my_cvr == null) {
      cvr = "null";
    } else {
      cvr = my_cvr.id().toString();
    }
    if (my_acvr == null) {
      acvr = "null";
    } else {
      acvr = my_acvr.id().toString();
    }
    return "CVRAuditInfo [cvr=" + cvr + ", acvr=" + acvr + "]";
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
    if (id() != null && the_other instanceof CVRAuditInfo) {
      final CVRAuditInfo other_info = (CVRAuditInfo) the_other;
      // we compare by database ID
      result &= nullableEquals(other_info.id(), id());
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
    if (id() == null) {
      return 0;
    } else {
      return id().hashCode();
    }
  }

  /**
   * Compares this CVRAuditInfo to another.
   *
   * Uses the underlying CVR to provide the sorting behavior.
   *
   * @return int
   */
  @Override
  public int compareTo(final CVRAuditInfo other) {
    return this.cvr().compareTo(other.cvr());
  }
}
