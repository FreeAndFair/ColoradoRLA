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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.json.ContestToAuditJsonAdapter;
import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * A class representing a contest to audit or hand count.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "contest_to_audit")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
@JsonAdapter(ContestToAuditJsonAdapter.class)
public class ContestToAudit extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The Department of State dashboard to which this record belongs. 
   */
  @ManyToOne
  @JoinColumn
  private DoSDashboard my_dashboard;

  /**
   * The contest to audit.
   */
  @ManyToOne(optional = false)
  private Contest my_contest;
  
  /**
   * The audit reason.
   */
  @Enumerated(EnumType.STRING)
  @Column(updatable = false)
  private AuditReason my_reason;
  
  /**
   * A value that determines whether to audit or hand count the contest.
   */
  @Enumerated(EnumType.STRING)
  @Column(updatable = false)
  private AuditType my_audit;
  
  /**
   * Constructs an empty ContestToAudit, solely for persistence.
   */
  public ContestToAudit() {
    super();
  }
  
  /**
   * Constructs a new ContestToAudit.
   * 
   * @param the_contest The contest ID.
   * @param the_reason The reason.
   * @param the_audit The audit type.
   */
  public ContestToAudit(final Contest the_contest, final AuditReason the_reason,
                        final AuditType the_audit) {
    super();
    my_contest = the_contest;
    my_reason = the_reason;
    my_audit = the_audit;
  }

  /**
   * Sets the Department of State dashboard that owns this record; this should only 
   * be called by the Department of State dashboard class.
   * 
   * @param the_dashboard The dashboard.
   */
  protected void setDashboard(final DoSDashboard the_dashboard) {
    my_dashboard = the_dashboard;
  }

  /**
   * @return the Department of State dashboard that owns this record.
   */
  public DoSDashboard dashboard() {
    return my_dashboard;
  }
  
  /**
   * @return the contest.
   */
  public Contest contest() {
    return my_contest;
  }

  /**
   * @return the reason.
   */
  public AuditReason reason() {
    return my_reason;
  }

  /**
   * @return the audit type.
   */
  public AuditType audit() {
    return my_audit;
  }
  
  /**
   * @return a String representation of this contest to audit.
   */
  @Override
  public String toString() {
    Long id = null;
    if (my_contest != null) {
      id = my_contest.id();
    }
    return "ContestToAudit [contest=" + id + ", reason=" + 
           my_reason + ", audit_type=" + my_audit + "]";
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
    if (the_other instanceof ContestToAudit) {
      final ContestToAudit other_cta = (ContestToAudit) the_other;
      result &= nullableEquals(other_cta.contest(), contest());
      result &= nullableEquals(other_cta.reason(), reason());
      result &= nullableEquals(other_cta.audit(), audit());
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

  /**
   * The possible audit types.
   */
  public enum AuditType {
    COMPARISON, HAND_COUNT, NONE;
  }
  
  /**
   * The possible audit reasons.
   */
  public enum AuditReason {
    STATE_WIDE_CONTEST,
    COUNTY_WIDE_CONTEST,
    CLOSE_CONTEST,
    GEOGRAPHICAL_SCOPE,
    CONCERN_REGARDING_ACCURACY,
    OPPORTUNISTIC_BENEFITS,
    COUNTY_CLERK_ABILITY;
  }
}
