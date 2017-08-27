/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A class representing a contest to audit or hand count.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Cacheable(true)
@Table(name = "cvr_audit_info",
       indexes = { @Index(name = "idx_cvrai_cvr", columnList = "cvr_id"),
                   @Index(name = "idx_cvrai_dashboard_cvr", 
                          columnList = "dashboard_id, cvr_id") })
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class CVRAuditInfo implements PersistentEntity, Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;
  
  /**
   * The county dashboard to which this record belongs. 
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private CountyDashboard my_dashboard;

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
   * A flag indicating whether this CVRAuditInfo has been considered
   * in the audit calculations.
   */
  @Column(nullable = false)
  private Boolean my_counted = false;
  
  /**
   * Constructs an empty CVRAuditInfo, solely for persistence.
   */
  public CVRAuditInfo() {
    super();
  }
  
  /**
   * Constructs a new CVRAuditInfo for the specified dashboard and
   * CVR to audit.
   * 
   * @param the_dashboard The dashboard this record belongs to.
   * @param the_cvr The CVR to audit.
   */
  public CVRAuditInfo(final CountyDashboard the_dashboard,
                      final CastVoteRecord the_cvr) {
    super();
    my_dashboard = the_dashboard;
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
   * @return the county dashboard that owns this record.
   */
  public CountyDashboard dashboard() {
    return my_dashboard;
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
   * @return true if this record has been counted in its audits,
   * false otherwise.
   */
  public boolean counted() { 
    return my_counted;
  }
  
  /**
   * Sets the flag that indicates whether this record has been
   * counted in its audits.
   * 
   * @param the_counted The new flag value.
   */
  public void setCounted(final boolean the_counted) {
    my_counted = the_counted;
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
    return "CVRAuditInfo [dashboard=" + my_dashboard.id() + 
           ", cvr=" + cvr + ", acvr=" + acvr + "]";
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
}
