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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * A class representing a contest to audit or hand count.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "cvr_audit_info")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class CVRAuditInfo extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The county dashboard to which this record belongs. 
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private CountyDashboard my_dashboard;

  /**
   * The CVR to audit.
   */
  @Column(updatable = false)
  private Long my_cvr_id;
  
  /**
   * The submitted audit CVR.
   */
  private Long my_acvr_id;

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
   * @param the_cvr_id The ID of the CVR to audit.
   */
  public CVRAuditInfo(final CountyDashboard the_dashboard,
                      final Long the_cvr_id) {
    super();
    my_dashboard = the_dashboard;
    my_cvr_id = the_cvr_id;
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
  public Long cvrID() {
    return my_cvr_id;
  }

  /**
   * @return the submitted audit CVR..
   */
  public Long acvrID() {
    return my_acvr_id;
  }

  /**
   * Sets the submitted audit CVR for this record.
   * 
   * @param the_acvr_id The audit CVR ID.
   */
  public void setACVRID(final Long the_acvr_id) {
    my_acvr_id = the_acvr_id;
  }
  
  /**
   * @return a String representation of this contest to audit.
   */
  @Override
  public String toString() {
    return "CVRAuditInfo [dashboard=" + my_dashboard.id() + 
           ", cvr=" + my_cvr_id + ", acvr=" + my_acvr_id + "]";
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
      result &= nullableEquals(other_info.cvrID(), cvrID());
      result &= nullableEquals(other_info.acvrID(), acvrID());
      result &= nullableEquals(other_info.dashboard().id(), dashboard().id());
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
