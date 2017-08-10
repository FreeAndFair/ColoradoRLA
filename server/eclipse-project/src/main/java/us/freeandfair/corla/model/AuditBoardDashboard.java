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
   * The county ID of this dashboard.
   */
  @Column(nullable = false, updatable = false)
  private Long my_county_id;
 
  /**
   * The ids of the CVRs to audit.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "audit_board_dashboard_cvr_to_audit",
                   joinColumns = @JoinColumn(name = "audit_board_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @OrderColumn(name = "index")
  @Column(name = "cvr_to_audit")
  private List<Long> my_cvrs_to_audit = new ArrayList<Long>();
  
  /**
   * The index in the list of the current CVR being audited.
   */
  private Integer my_current_cvr_index;
  
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
   * Define the CVRs to audit. This also resets the current CVR to audit
   * to be the first one in the list.
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
    my_current_cvr_index = 0;
  }
  
  /**
   * Submit an aCVR for a CVR under audit.
   * 
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_acvr The corresponding aCVR.
   * @return true if the aCVR is submitted successfully, false if it doesn't
   * correspond to the CVR under audit or if the specified CVR under audit
   * is not the next CVR in sequence.
   */
  //@ require the_cvr_under_audit != null;
  //@ require the_acvr != null;
  public boolean submitAuditCVR(final CastVoteRecord the_cvr_under_audit, 
                                final CastVoteRecord the_acvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card, and that the CVR under audit is the next one in sequence
    
    boolean result = true;
    
    if (my_cvrs_to_audit.get(my_current_cvr_index).equals(the_cvr_under_audit.id())) {
      // compare the CVR and aCVR
      if (/* the_cvr_under_audit.equalsForAudit(the_acvr) && */
          the_cvr_under_audit.recordType() != RecordType.AUDITOR_ENTERED &&
          the_acvr.recordType() == RecordType.AUDITOR_ENTERED) {
        // move on to the next CVR
      } else {
        // something didn't match
        result = false;
      }
    }
    
    return result;
  }
  
  /**
   * @return all the aCVRs that have been submitted to this dashboard in 
   * this round.
   */
  public Set<CastVoteRecord> auditCVRs() {
    final Set<CastVoteRecord> result = 
        new HashSet<CastVoteRecord>(my_cvrs_to_audit.values());
    result.remove(null);
    return result;
  }
}
