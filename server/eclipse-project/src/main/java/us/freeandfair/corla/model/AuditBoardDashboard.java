/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.AbstractEntity;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The county dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "audit_board_dashboard")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class AuditBoardDashboard extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 

  /**
   * The county identifier of this dashboard.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_county_id;
 
  /**
   * The ids of the CVRs to audit, in the order they should be audited.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "audit_board_dashboard_cvr_to_audit",
                   joinColumns = @JoinColumn(name = "audit_board_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @OrderColumn(name = "index")
  @Column(name = "cvr_id")
  private List<Long> my_cvrs_to_audit = new ArrayList<>();
  
  /**
   * The ids of the audit CVRs submitted, in the same order as the CVRs
   * to audit. 
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "audit_board_dashboard_submitted_audit_cvr",
                   joinColumns = @JoinColumn(name = "audit_board_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @OrderColumn(name = "index")
  @Column(name = "cvr_id")
  private List<Long> my_submitted_audit_cvrs = new ArrayList<>();
  
  /**
   * The members of the audit board.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "audit_board_member",
                   joinColumns = @JoinColumn(name = "audit_board_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @OrderColumn(name = "index")
  @Column(name = "elector_id")
  private final Set<Elector> my_members = new HashSet<Elector>();
  
  /**
   * Constructs an empty audit board dashboard, solely for persistence.
   */
  public AuditBoardDashboard() {
    super();
  }
  
  /**
   * Constructs an audit board dashboard for the specified county.
   * 
   * @param the_county_id The county identifier.
   */
  public AuditBoardDashboard(final Integer the_county_id) {
    super();
    my_county_id = the_county_id;
  }
  
  /**
   * @return the county ID for this dashboard.
   */
  public Integer countyID() {
    return my_county_id;
  }
  
  /**
   * @return the set of audit board members.
   */
  public Set<Elector> members() {
    return Collections.unmodifiableSet(my_members);
  }
  
  /**
   * Sets the membership of the audit board; this must be the full set
   * of electors on the board, and replaces any other set.
   * 
   * @param the_members The members.
   */
  public void setMembers(final Collection<Elector> the_members) {
    my_members.clear();
    my_members.addAll(the_members);
  }
  
  /**
   * Define the CVRs to audit. This also clears the list of submitted
   * audit CVRs.
   * 
   * @param the_cvrs_to_audit A list of the IDs of the CVRs to audit,
   * in the order they should be examined. It must contain no duplicates.
   * @exception IllegalArgumentException if the list contains duplicates.
   */
  public void setCVRsToAudit(final List<Long> the_cvrs_to_audit) 
      throws IllegalArgumentException {
    final Set<Long> duplicate_check = new HashSet<Long>(the_cvrs_to_audit);
    if (duplicate_check.size() < the_cvrs_to_audit.size()) {
      throw new IllegalArgumentException("duplicate elements in audit cvr list");
    }
    my_cvrs_to_audit = new ArrayList<Long>(the_cvrs_to_audit);
    my_submitted_audit_cvrs.clear();
  }
  
  /**
   * Submit an audit CVR for a CVR under audit.
   * 
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_audit_cvr The corresponding audit CVR.
   * @return true if the audit CVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit, or the specified CVR under audit was
   * not in fact under audit.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  public boolean submitAuditCVR(final CastVoteRecord the_cvr_under_audit, 
                                final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;
    final int index = my_cvrs_to_audit.indexOf(the_cvr_under_audit.id());
    if (index >= 0 && 
        the_cvr_under_audit.equals(Persistence.getByID(the_cvr_under_audit.id(), 
                                                       CastVoteRecord.class)) &&
        the_cvr_under_audit.isAuditPairWith(the_audit_cvr) &&
        the_cvr_under_audit.recordType() != RecordType.AUDITOR_ENTERED) {
      // the CVRs match!
      my_submitted_audit_cvrs.set(index, the_audit_cvr.id());
      result = true;
    } 

    return result;
  }
}
