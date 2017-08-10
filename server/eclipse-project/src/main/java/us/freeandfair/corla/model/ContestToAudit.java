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

import java.io.Serializable;

import javax.persistence.Entity;
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
@Table(name = "contest_to_audit")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
// @JsonAdapter(ContestToAuditJsonAdapter.class)
public class ContestToAudit extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The Department of State dashboard to which this record belongs. 
   */
  @ManyToOne(optional = false)
  @JoinColumn
  private DepartmentOfStateDashboard my_dashboard;

  /**
   * The contest to audit.
   */
  private Contest my_contest;
  
  /**
   * The audit reason.
   */
  private AuditReason my_reason;
  
  /**
   * A value that determines whether to audit or hand count the contest.
   */
  private AuditType my_audit_type;
  
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
   * @param the_audit_type The audit type.
   */
  public ContestToAudit(final Contest the_contest, final AuditReason the_reason,
                        final AuditType the_audit_type) {
    super();
    my_contest = the_contest;
    my_reason = the_reason;
    my_audit_type = the_audit_type;
  }

  /**
   * Sets the Department of State dashboard that owns this record; this should only 
   * be called by the Department of State dashboard class.
   * 
   * @param the_dashboard The dashboard.
   */
  protected void setDashboard(final DepartmentOfStateDashboard the_dashboard) {
    my_dashboard = the_dashboard;
  }

  /**
   * @return the Department of State dashboard that owns this record.
   */
  public DepartmentOfStateDashboard dashboard() {
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
  public AuditType auditType() {
    return my_audit_type;
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
