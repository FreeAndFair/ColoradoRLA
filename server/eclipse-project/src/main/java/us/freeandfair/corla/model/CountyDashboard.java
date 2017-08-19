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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * The county dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county_dashboard")
@SuppressWarnings({"PMD.ImmutableField", "PMD.TooManyMethods", "PMD.TooManyFields",
    "PMD.GodClass"})
public class CountyDashboard implements PersistentEntity, Serializable {
  /**
   * The minimum number of members on an audit board.
   */
  public static final int MIN_AUDIT_BOARD_MEMBERS = 2;
  
  /**
   * The empty RLA algorithm constant.
   */
  private static final RLAAlgorithm NO_RLA_ALGORITHM = null;
  
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
   * The county ID.
   */
  @Id
  private Long my_id;
  
  /**
   * The county status of this dashboard.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CountyStatus my_status = CountyStatus.NO_DATA;
  
  /**
   * The RLAAlgorithm of this dashboard. Computed on the fly when
   * necessary.
   */
  private transient RLAAlgorithm my_rla_algorithm;
  
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
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "audit_board_member",
             joinColumns = @JoinColumn(name = "county_dashboard_id", 
                                       referencedColumnName = MY_ID),
             inverseJoinColumns = @JoinColumn(name = "elector_id", 
                                              referencedColumnName = MY_ID))
  private Set<Elector> my_members = new HashSet<>();
  
  /**
   * The ids of the CVRs to audit, in the order they should be audited.
   */
  @ElementCollection(fetch = FetchType.LAZY)
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
  @ElementCollection(fetch = FetchType.LAZY)
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
             fetch = FetchType.LAZY, orphanRemoval = true)
  @OrderColumn(name = INDEX)
  private List<AuditInvestigationReportInfo> my_investigation_reports = 
      new ArrayList<>();
  
  /**
   * The audit interim reports.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "my_dashboard", 
             fetch = FetchType.LAZY, orphanRemoval = true)
  @OrderColumn(name = INDEX)
  private List<IntermediateAuditReportInfo> my_intermediate_reports = 
      new ArrayList<>();
  
  /**
   * The number of ballots audited.
   */
  @Column(nullable = false)
  private Integer my_ballots_audited = 0;
  
  /**
   * The estimated ballots to audit.
   */
  private Integer my_estimated_ballots_to_audit = 0;
  
  /**
   * The index of the current CVR under audit.
   */
  private Integer my_cvr_under_audit;
  
  /**
   * The number of discrepancies found in the audit so far.
   */
  @Column(nullable = false)
  private Integer my_discrepancies = 0;
  
  /**
   * The number of disagreements found in the audit so far.
   */
  @Column(nullable = false)
  private Integer my_disagreements = 0;
  
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
   */
  public CountyDashboard(final Long the_county_id, final CountyStatus the_status) {
    super();
    setID(the_county_id);
    my_status = the_status;
  }
  
  /**
   * @return the database ID for this dashboard, which is the same as
   * its county ID.
   */
  public Long id() {
    return my_id;
  }
  
  /**
   * Sets the database ID for this dashboard. This must be the county ID.
   * 
   * @param the_id The ID. 
   */
  public final void setID(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * @return the county ID for this dashboard.
   */
  public Long countyID() {
    return my_id;
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
  public void setAuditBoardMembers(final Collection<Elector> the_members) {
    my_members.clear();
    my_members.addAll(the_members);
  }
  
  /**
   * Define the CVRs to audit. This also clears the list of submitted
   * audit CVRs.
   * 
   * @param the_cvrs_to_audit A list of the IDs of the CVRs to audit,
   * in the order they should be examined. It may contain duplicates, which
   * are dealt with appropriately when submitting audit CVRs.
   */
  public void setCVRsToAudit(final List<Long> the_cvrs_to_audit) 
      throws IllegalArgumentException {
    if (the_cvrs_to_audit.contains(null)) {
      throw new IllegalArgumentException("null elements in audit cvr list");
    }
    my_cvrs_to_audit = new ArrayList<Long>(the_cvrs_to_audit);
    my_submitted_audit_cvrs.clear();
    my_discrepancies = 0;
    my_disagreements = 0;
    my_rla_algorithm = NO_RLA_ALGORITHM;
    for (int i = 0; i < my_cvrs_to_audit.size(); i++) {
      my_submitted_audit_cvrs.add(Long.MIN_VALUE);
    }
  }

  /**
   * @return the list of CVR IDs to audit.
   */
  public List<Long> cvrsToAudit() {
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
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
  public boolean submitAuditCVR(final CastVoteRecord the_cvr_under_audit, 
                                final CastVoteRecord the_audit_cvr) {
    // performs a sanity check to make sure the CVR under audit and the ACVR
    // are the same card
    boolean result = false;
    
    // a CVR can appear in the list more than once
    final int first_index = my_cvrs_to_audit.indexOf(the_cvr_under_audit.id());
    final int last_index = my_cvrs_to_audit.lastIndexOf(the_cvr_under_audit.id());
    if (first_index >= 0 && 
        the_cvr_under_audit.equals(Persistence.getByID(the_cvr_under_audit.id(), 
                                                       CastVoteRecord.class)) &&
        the_audit_cvr.equals(Persistence.getByID(the_audit_cvr.id(),
                                                 CastVoteRecord.class)) &&
        the_cvr_under_audit.isAuditPairWith(the_audit_cvr) &&
        the_audit_cvr.recordType().isAuditorGenerated()) {
      // the CVRs match!
      result = true;
      boolean increment = false;
      for (int index = first_index; index <= last_index; index++) {
        if (my_cvrs_to_audit.get(index).equals(the_cvr_under_audit.id()) &&
            my_submitted_audit_cvrs.get(index) == Long.MIN_VALUE) {
          // we bump the number of ballots audited as long as this is not a
          // replacement for an already-audited result
          // TODO: do we allow such replacements at all? 
          increment = true;
          my_submitted_audit_cvrs.set(index, the_audit_cvr.id());
        }
      }
      if (increment) {
        my_ballots_audited = my_ballots_audited + 1;
        boolean disagree = false;
        boolean discrepancy = false;
        initializeRLAAlgorithm();
        for (final CVRContestInfo ci : the_audit_cvr.contestInfo()) {
          disagree |= ci.consensus() == ConsensusValue.NO;
          discrepancy |= my_rla_algorithm.discrepancy(the_cvr_under_audit, 
                                                      the_audit_cvr) != 0;
        }
        if (disagree) {
          my_disagreements = my_disagreements + 1;
        }
        if (discrepancy) {
          my_discrepancies = my_discrepancies + 1;
        }
      } else {
        Main.LOGGER.info("ACVR submitted for already-audited CVR ID " + 
                         the_cvr_under_audit.id());
      }
    } 

    updateCVRUnderAudit(first_index);
    updateEstimatedBallotsToAudit();
    
    return result;
  }
  
  /**
   * Updates the current CVR to audit index to the first non-audited CVR
   * at or after the specified index.
   * 
   * @param the_index The index.
   */
  private void updateCVRUnderAudit(final int the_index) {
    int index = the_index;
    my_cvr_under_audit = -1;
    while (index < my_submitted_audit_cvrs.size()) {
      if (my_submitted_audit_cvrs.get(index) == Long.MIN_VALUE) {
        my_cvr_under_audit = index;
        break;
      }
      index = index + 1;
    }
  }
  
  /**
   * Updates the estimated number of ballots to audit.
   */
  private void updateEstimatedBallotsToAudit() {
    if (!my_cvrs_to_audit.isEmpty()) {
      initializeRLAAlgorithm();
      my_estimated_ballots_to_audit = my_rla_algorithm.estimatedBallotsToAudit();
    }
  }
  
  /**
   * @return the list of audit CVRs submitted. The result will contain
   * the value Long.MIN_VALUE for each element in the cvrsToAudit() list 
   * where there has been no audit CVR submitted. Thus, most computations on 
   * this list will use the sublist preceding the first Long.MIN_VALUE value.
   */
  public List<Long> submittedAuditCVRs() {
    return Collections.unmodifiableList(my_submitted_audit_cvrs);
  }
  
  /**
   * Submits an audit investigation report.
   * 
   * @param the_report The audit investigation report.
   */
  public void submitInvestigationReport(final AuditInvestigationReportInfo the_report) {
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
  public void submitIntermediateReport(final IntermediateAuditReportInfo the_report) {
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
  public Long cvrUnderAudit() {
    if (my_cvrs_to_audit == null || my_cvr_under_audit == null ||
        my_cvrs_to_audit.size() <= my_cvr_under_audit) {
      return null;
    } else {
      return my_cvrs_to_audit.get(my_cvr_under_audit);
    }
  }
  
  /**
   * @return the number of ballots audited.
   */
  public Integer ballotsAudited() {
    return my_ballots_audited;
  }
  
  /**
   * @return the number of discrepancies found in the audit so far.
   */
  public Integer discrepancies() {
    return my_discrepancies;
  }
  
  /**
   * @return the number of disagreements found in the audit so far.
   */
  public Integer disagreements() {
    return my_disagreements;
  }
  
  /**
   * @return the estimated number of ballots to audit.
   */
  public Integer estimatedBallotsToAudit() {
    return my_estimated_ballots_to_audit;
  }
  
  /**
   * Initialize the RLAAlgorithm object for this dashboard, if it is uninitialized.
   */
  private void initializeRLAAlgorithm() {
    if (my_rla_algorithm == null && !my_cvrs_to_audit.isEmpty()) {
      my_rla_algorithm = new RLAAlgorithm(this);
    }
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
