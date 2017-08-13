/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @created Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.ContestToAudit.AuditType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.CountyDashboard.CountyStatus;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileType;
import us.freeandfair.corla.query.CountyQueries;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;
import us.freeandfair.corla.query.UploadedFileQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response generated on a refresh of the County and Audit Board
 * dashboards.
 * 
 * @author Daniel M. Zimmerman
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField",
                   "PMD.CyclomaticComplexity"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class CountyDashboardRefreshResponse {
  /**
   * The county status.
   */
  private final CountyStatus my_status;
  
  /**
   * The general information.
   * @todo this needs to be connected to something
   */
  private final Map<String, String> my_general_information;
  
  /**
   * The audit board members.
   */
  private final Set<Elector> my_audit_board_members;
  
  /**
   * The ballot manifest digest.
   */
  private final String my_ballot_manifest_digest;
  
  /**
   * The CVR export digest.
   */
  private final String my_cvr_export_digest;
  
  /**
   * The contests on the ballot (by ID).
   */
  private final Set<Long> my_contests;
  
  /**
   * The contests under audit, with reasons.
   */
  private final Map<Long, String> my_contests_under_audit;
  
  /**
   * The date and time of the audit. 
   * @todo connect this to something
   */
  private final Instant my_audit_time;
  
  /**
   * The estimated number of ballots to audit.
   * @todo connect this to something
   */
  private final Integer my_estimated_ballots_to_audit;
  
  /**
   * The number of ballots audited.
   */
  private final Integer my_number_of_ballots_audited;
  
  /**
   * The number of discrepencies found.
   * @todo connect this to something
   */
  private final Integer my_number_of_discrepencies;
  
  /**
   * The number of disagreements found.
   * @todo connect this to something
   */
  private final Integer my_number_of_disagreements;

  /**
   * The list of ballots to audit (by CVR ID).
   */
  private final List<Long> my_ballots_to_audit;

  /**
   * The current ballot under audit.
   */
  private final Long my_ballot_under_audit_id;
  
  /**
   * Constructs a new CountyDashboardRefreshResponse.
   * 
   * @param the_status The status.
   * @param the_general_information The general information.
   * @param the_audit_board_members The audit board members.
   * @param the_ballot_manifest_digest The ballot manifest digest.
   * @param the_cvr_export_digest The CVR export digest.
   * @param the_contests The contests.
   * @param the_contests_under_audit The contests under audit, with reasons.
   * @param the_audit_time The audit time.
   * @param the_estimated_ballots_to_audit The estimated ballots to audit.
   * @param the_number_of_ballots_audited The number of ballots audited.
   * @param the_number_of_discrepencies The number of discrepencies.
   * @param the_number_of_disagreements The number of disagreements.
   * @param the_ballots_to_audit The list of CVRs to audit.
   * @param the_ballot_under_audit The index of the CVR under audit.
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  protected CountyDashboardRefreshResponse(final CountyStatus the_status,
                                           final Map<String, String> the_general_information,
                                           final Set<Elector> the_audit_board_members, 
                                           final String the_ballot_manifest_digest,
                                           final String the_cvr_export_digest,
                                           final Set<Long> the_contests,
                                           final Map<Long, String> the_contests_under_audit,
                                           final Instant the_audit_time,
                                           final Integer the_estimated_ballots_to_audit,
                                           final Integer the_number_of_ballots_audited,
                                           final Integer the_number_of_discrepencies, 
                                           final Integer the_number_of_disagreements,
                                           final List<Long> the_ballots_to_audit,
                                           final Long the_ballot_under_audit) {
    my_status = the_status;
    my_general_information = the_general_information;
    my_audit_board_members = the_audit_board_members;
    my_ballot_manifest_digest = the_ballot_manifest_digest;
    my_cvr_export_digest = the_cvr_export_digest;
    my_contests = the_contests;
    my_contests_under_audit = the_contests_under_audit;
    my_audit_time = the_audit_time;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_number_of_ballots_audited = the_number_of_ballots_audited;
    my_number_of_discrepencies = the_number_of_discrepencies;
    my_number_of_disagreements = the_number_of_disagreements;
    my_ballots_to_audit = the_ballots_to_audit;
    my_ballot_under_audit_id = the_ballot_under_audit;
  }
  
  /**
   * Gets the CountyDashboardRefreshResponse for the specified County dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the response.
   * @exception NullPointerException if necessary information to construct the
   * response does not exist.
   */
  // this method is essentially a straight line construction of parameters,
  // so we are ignoring the cyclomatic complexity checks for now
  @SuppressWarnings({"PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
  public static CountyDashboardRefreshResponse 
      createResponse(final CountyDashboard the_dashboard) {
    final Integer county_id = the_dashboard.countyID();
    final County county = CountyQueries.byID(county_id);
    final DepartmentOfStateDashboard dosd = DepartmentOfStateDashboardQueries.get();

    if (county == null || dosd == null) {
      throw new PersistenceException("unable to read county dashboard state");
    }
    // status = directly from county dashboard
    
    // general information doesn't exist yet
    final Map<String, String> general_information = new HashMap<String, String>();

    // uploaded files (for hashes)
    final UploadedFile manifest_file = 
        UploadedFileQueries.matching(county_id, the_dashboard.manifestUploadTimestamp(), 
                                     FileType.BALLOT_MANIFEST);
    String manifest_digest = null;
    if (manifest_file != null) {
      manifest_digest = manifest_file.hash();
    }
    
    final UploadedFile cvr_file = 
        UploadedFileQueries.matching(county_id, the_dashboard.cvrUploadTimestamp(), 
                                     FileType.CAST_VOTE_RECORD_EXPORT);
    String cvr_digest = null;
    if (cvr_file != null) {
      cvr_digest = cvr_file.hash();
    }
    
    // contests
    final Set<Long> contests = new HashSet<Long>();
    for (final Contest c : county.contests()) {
      contests.add(c.id());
    }
    
    // contests under audit
    final Map<Long, String> contests_under_audit = new HashMap<Long, String>();
    for (final ContestToAudit cta : dosd.contestsToAudit()) {
      if (cta.audit() == AuditType.COMPARISON && 
          contests.contains(cta.contest().id())) {
        contests_under_audit.put(cta.contest().id(), cta.reason().toString());
      }
    }
    
    // audit timestamp = timestamp from dashboard
   
    // estimated ballots to audit = list size from dashboard
    
    // number of ballots audited
    int number_of_ballots_audited = 0;
    for (final Long bid : the_dashboard.submittedAuditCVRs()) {
      if (bid != null) {
        number_of_ballots_audited = number_of_ballots_audited + 1;
      }
    }
    
    // number of discrepencies doesn't exist yet
    final Integer number_of_discrepencies = -1;
    
    // number of disagreements doesn't exist yet
    final Integer number_of_disagreements = -1;

    // list of ballots to audit = list from dashboard
    
    // ballot under audit = from dashboard
    
    return new CountyDashboardRefreshResponse(the_dashboard.status(),
                                              general_information,
                                              the_dashboard.auditBoardMembers(),
                                              manifest_digest,
                                              cvr_digest,
                                              contests,
                                              contests_under_audit,
                                              the_dashboard.auditTimestamp(),
                                              the_dashboard.cvrsToAudit().size(),
                                              number_of_ballots_audited,
                                              number_of_discrepencies,
                                              number_of_disagreements,
                                              the_dashboard.cvrsToAudit(),
                                              the_dashboard.cvrUnderAudit());
  }
}
