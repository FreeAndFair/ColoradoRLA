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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The county dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county_dashboard")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class CountyDashboard extends AbstractEntity implements Serializable {   
  /**
   * The minimum number of members on an audit board.
   */
  public static final int MIN_AUDIT_BOARD_MEMBERS = 2;
  
  /**
   * The "index" string.
   */
  private static final String INDEX = "index";

  /**
   * The "my_id" string.
   */
  private static final String MY_ID = "my_id";
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 

  /**
   * The county ID of this dashboard.
   */
  private Integer my_county_id;
  
  /**
   * The county status of this dashboard.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CountyStatus my_status = CountyStatus.NO_DATA;
  
  /**
   * The timestamp of the most recent set of uploaded CVRs.
   * 
   * @design This is how we are currently linking the dashboard with the most
   * recently uploaded set of CVRs. Each uploaded file is assigned a
   * timestamp, and it seems safe to assume that the same county won't
   * upload two CVR files in the same nanosecond. This is therefore far more
   * efficient that maintaining a list of all the uploaded CVRs in the database,
   * since the query to obtain them when needed is straightforward.
   */
  private Instant my_cvr_upload_timestamp;
  
  /**
   * The timestamp of the most recent uploaded ballot manifest. 
   * 
   * @design This is how we are currently linking the dashboard with the most
   * recently uploaded ballot manifest file. Each uploaded file is assigned a
   * timestamp, and it seems safe to assume that the same county won't
   * upload two ballot manifest files in the same nanosecond. This is therefore 
   * far more efficient than maintaining a list of all the uploaded ballot
   * manifests in the database, since the query to obtain them when needed is 
   * straightforward.
   */
  private Instant my_manifest_upload_timestamp;
  
  /**
   * The timestamp for the start of the audit.
   */
  private Instant my_audit_timestamp; 
  
  /**
   * The members of the audit board.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "audit_board_member",
             joinColumns = @JoinColumn(name = "county_dashboard_id", 
                                       referencedColumnName = MY_ID),
             inverseJoinColumns = @JoinColumn(name = "elector_id", 
                                              referencedColumnName = MY_ID))
  private Set<Elector> my_members = new HashSet<>();
  
  /**
   * The ids of the CVRs to audit, in the order they should be audited.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "county_dashboard_cvr_to_audit",
                   joinColumns = @JoinColumn(name = "county_dashboard_id", 
                                             referencedColumnName = MY_ID))
  @OrderColumn(name = INDEX)
  @Column(name = "cvr_id")
  private List<Long> my_cvrs_to_audit = new ArrayList<>();
  
  /**
   * The ids of the audit CVRs submitted, in the same order as the CVRs
   * to audit. 
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "county_dashboard_submitted_audit_cvr",
                   joinColumns = @JoinColumn(name = "county_dashboard_id", 
                                             referencedColumnName = MY_ID))
  @OrderColumn(name = INDEX)
  @Column(name = "cvr_id")
  private List<Long> my_submitted_audit_cvrs = new ArrayList<>();
  
  /**
   * The audit investigation reports.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "my_dashboard", 
             fetch = FetchType.EAGER, orphanRemoval = true)
  @OrderColumn(name = INDEX)
  private List<AuditInvestigationReportInfo> my_investigation_reports = 
      new ArrayList<>();
  
  /**
   * The audit interim reports.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "my_dashboard", 
             fetch = FetchType.EAGER, orphanRemoval = true)
  @OrderColumn(name = INDEX)
  private List<IntermediateAuditReportInfo> my_intermediate_reports = 
      new ArrayList<>();
  
  /**
   * The number of ballots audited.
   */
  @Column(name = "ballots_audited", nullable = false)
  private Integer my_number_of_ballots_audited = 0;
  
  /**
   * The number of discrepancies found in the audit so far.
   */
  @Column(name = "discrepancies", nullable = false)
  private Integer my_number_of_discrepancies = 0;
  
  /**
   * The number of disagreements found in the audit so far.
   */
  @Column(name = "disagreements", nullable = false)
  private Integer my_number_of_disagreements = 0;
  
  /**
   * Constructs an empty county dashboard, solely for persistence.
   */
  public CountyDashboard() {
    super();
  }
  
  /**
   * Constructs a new county dashboard with the specified parameters.
   * 
   * @param the_county_id The county ID.
   * @param the_status The status.
   * @param the_cvr_upload_timestamp The CVR upload timestamp.
   */
  public CountyDashboard(final Integer the_county_id, final CountyStatus the_status,
                         final Instant the_cvr_upload_timestamp) {
    super();
    my_county_id = the_county_id;
    my_status = the_status;
    my_cvr_upload_timestamp = the_cvr_upload_timestamp;
  }
  
  /**
   * @return the county ID for this dashboard.
   */
  public Integer countyID() {
    return my_county_id;
  }
  
  /**
   * @return the status for this dashboard.
   */
  public CountyStatus status() {
    return my_status;
  }

  /**
   * Sets the dashboard status.
   * 
   * @param the_status The new status.
   */
  public void setStatus(final CountyStatus the_status) {
    my_status = the_status;
  }
  
  /**
   * @return the CVR upload timestamp. A return value of null means
   * that no CVRs have been uploaded for this county.
   */
  public Instant cvrUploadTimestamp() {
    return my_cvr_upload_timestamp;
  }
  
  /**
   * Sets a new CVR upload timestamp, replacing the previous one.
   * 
   * @param the_timestamp The new upload timestamp.
   */
  public void setCVRUploadTimestamp(final Instant the_timestamp) {
    my_cvr_upload_timestamp = the_timestamp;
  }  
  
  /**
   * @return the ballot manifest upload timestamp. A return value of null means
   * that no ballot manifest has been uploaded for this county.
   */
  public Instant manifestUploadTimestamp() {
    return my_manifest_upload_timestamp;
  }
  
  /**
   * Sets a new CVR upload timestamp, replacing the previous one.
   * 
   * @param the_timestamp The new upload timestamp.
   */
  public void setManifestUploadTimestamp(final Instant the_timestamp) {
    my_manifest_upload_timestamp = the_timestamp;
  }  
  
  /**
   * @return the audit timestamp. A return value of null means
   * that no audit has been started.
   */
  public Instant auditTimestamp() {
    return my_audit_timestamp;
  }
  
  /**
   * Sets a new audit timestamp, replacing the previous one.
   * 
   * @param the_timestamp The new audit timestamp.
   */
  public void setAuditTimestamp(final Instant the_timestamp) {
    my_audit_timestamp = the_timestamp;
  }  

  /**
   * @return the set of audit board members.
   */
  public Set<Elector> auditBoardMembers() {
    return Collections.unmodifiableSet(my_members);
  }
  
  /**
   * Sets the membership of the audit board; this must be the full set
   * of electors on the board, and replaces any other set.
   * 
   * @param the_members The members.
   */
  public synchronized void setAuditBoardMembers(final Collection<Elector> the_members) {
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
  public synchronized void setCVRsToAudit(final List<Long> the_cvrs_to_audit) 
      throws IllegalArgumentException {
    final Set<Long> duplicate_check = new HashSet<Long>(the_cvrs_to_audit);
    if (duplicate_check.size() < the_cvrs_to_audit.size()) {
      throw new IllegalArgumentException("duplicate elements in audit cvr list");
    }
    if (the_cvrs_to_audit.contains(null)) {
      throw new IllegalArgumentException("null elements in audit cvr list");
    }
    my_cvrs_to_audit = new ArrayList<Long>(the_cvrs_to_audit);
    my_submitted_audit_cvrs.clear();
    for (int i = 0; i < my_cvrs_to_audit.size(); i++) {
      my_submitted_audit_cvrs.add(null);
    }
  }

  /**
   * @return the list of CVR IDs to audit.
   */
  public synchronized List<Long> cvrsToAudit() {
    return Collections.unmodifiableList(my_cvrs_to_audit);
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
  public synchronized boolean submitAuditCVR(final CastVoteRecord the_cvr_under_audit, 
                                             final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;
    final int index = my_cvrs_to_audit.indexOf(the_cvr_under_audit.id());
    if (index >= 0 && 
        the_cvr_under_audit.equals(Persistence.getByID(the_cvr_under_audit.id(), 
                                                       CastVoteRecord.class)) &&
        the_cvr_under_audit.isAuditPairWith(the_audit_cvr) &&
        the_cvr_under_audit.recordType().isAuditorGenerated()) {
      // the CVRs match!
      if (my_submitted_audit_cvrs.get(index) == null) {
        // we bump the number of ballots audited as long as this is not a
        // replacement for an already-audited result
        // TODO: do we allow such replacements at all? 
        my_number_of_ballots_audited = my_number_of_ballots_audited + 1;
      }
      my_submitted_audit_cvrs.set(index, the_audit_cvr.id());
      result = true;
    } 

    return result;
  }
  
  /**
   * @return the list of audit CVRs submitted. The result will contain
   * a null value for each element in the cvrsToAudit() list where there
   * has been no audit CVR submitted. Thus, most computations on this list
   * will use the sublist preceding the first null value.
   */
  public List<Long> submittedAuditCVRs() {
    return Collections.unmodifiableList(my_submitted_audit_cvrs);
  }
  
  /**
   * Submits an audit investigation report.
   * 
   * @param the_report The audit investigation report.
   */
  public synchronized void 
      submitInvestigationReport(final AuditInvestigationReportInfo the_report) {
    the_report.setDashboard(this);
    my_investigation_reports.add(the_report);
  }
  
  /**
   * @return the list of submitted audit investigation reports.
   */
  public List<AuditInvestigationReportInfo> investigationReports() {
    return Collections.unmodifiableList(my_investigation_reports);
  }
  
  /**
   * Submits an audit investigation report.
   * 
   * @param the_report The audit investigation report.
   */
  public synchronized void 
      submitIntermediateReport(final IntermediateAuditReportInfo the_report) {
    the_report.setDashboard(this);
    my_intermediate_reports.add(the_report);
  }
  
  /**
   * @return the list of submitted audit interim reports.
   */
  public List<IntermediateAuditReportInfo> intermediateReports() {
    return Collections.unmodifiableList(my_intermediate_reports);
  }
  
  /**
   * @return the current CVR under audit. This is the first entry in the list 
   * of CVRs to audit that has no corresponding ACVR. Returns null if there is 
   * no next CVR to audit.
   */
  public synchronized Long cvrUnderAudit() {
    for (int i = 0; i < my_submitted_audit_cvrs.size(); i++) {
      if (my_submitted_audit_cvrs.get(i) == null) {
        return my_cvrs_to_audit.get(i);
      }
    }
    return null;
  }
  
  /**
   * @return the number of ballots audited.
   */
  public Integer numberOfBallotsAudited() {
    return my_number_of_ballots_audited;
  }
  
  /**
   * @return the number of discrepancies found in the audit so far.
   */
  public Integer numberOfDiscrepancies() {
    return my_number_of_discrepancies;
  }
  
  /**
   * @return the number of disagreements found in the audit so far.
   */
  public Integer numberOfDisagreements() {
    return my_number_of_disagreements;
  }
  
  /**
   * The possible statuses for a county in an audit.
   */
  public enum CountyStatus {
    NO_DATA,
    CVRS_UPLOADED_SUCCESSFULLY,
    ERROR_IN_UPLOADED_DATA;
  }
}
