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
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.persistence.Persistence;
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
    "PMD.CyclomaticComplexity", "PMD.TooManyFields"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class CountyDashboardRefreshResponse {
  /**
   * The county ID.
   */
  private final Long my_id;
  
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
   * The ballot manifest hash.
   */
  private final String my_ballot_manifest_hash;
  
  /**
   * The ballot manifest timestamp.
   */
  private final Instant my_manifest_timestamp;
  
  /**
   * The CVR export hash.
   */
  private final String my_cvr_export_hash;
  
  /**
   * The CVR export timestamp.
   */
  private final Instant my_cvr_timestamp;
  
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
  private final Integer my_audited_ballot_count;
  
  /**
   * The number of discrepancies found.
   */
  private final Integer my_discrepancy_count;
  
  /**
   * The number of disagreements found.
   */
  private final Integer my_disagreement_count;

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
   * @param the_id The ID.
   * @param the_status The status.
   * @param the_general_information The general information.
   * @param the_audit_board_members The audit board members.
   * @param the_ballot_manifest_hash The ballot manifest hash.
   * @param the_cvr_export_hash The CVR export hash.
   * @param the_contests The contests.
   * @param the_contests_under_audit The contests under audit, with reasons.
   * @param the_audit_time The audit time.
   * @param the_estimated_ballots_to_audit The estimated ballots to audit.
   * @param the_audited_ballot_count The number of ballots audited.
   * @param the_discrepancy_count The number of discrepencies.
   * @param the_disagreement_count The number of disagreements.
   * @param the_ballots_to_audit The list of CVRs to audit.
   * @param the_ballot_under_audit The index of the CVR under audit.
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  protected CountyDashboardRefreshResponse(final Long the_id,
                                           final CountyStatus the_status,
                                           final Map<String, String> the_general_information,
                                           final Set<Elector> the_audit_board_members, 
                                           final String the_ballot_manifest_hash,
                                           final Instant the_manifest_timestamp,
                                           final String the_cvr_export_hash,
                                           final Instant the_cvr_timestamp,
                                           final Set<Long> the_contests,
                                           final Map<Long, String> the_contests_under_audit,
                                           final Instant the_audit_time,
                                           final Integer the_estimated_ballots_to_audit,
                                           final Integer the_audited_ballot_count,
                                           final Integer the_discrepancy_count, 
                                           final Integer the_disagreement_count,
                                           final List<Long> the_ballots_to_audit,
                                           final Long the_ballot_under_audit) {
    my_id = the_id;
    my_status = the_status;
    my_general_information = the_general_information;
    my_audit_board_members = the_audit_board_members;
    my_ballot_manifest_hash = the_ballot_manifest_hash;
    my_manifest_timestamp = the_manifest_timestamp;
    my_cvr_export_hash = the_cvr_export_hash;
    my_cvr_timestamp = the_cvr_timestamp;
    my_contests = the_contests;
    my_contests_under_audit = the_contests_under_audit;
    my_audit_time = the_audit_time;
    my_estimated_ballots_to_audit = the_estimated_ballots_to_audit;
    my_audited_ballot_count = the_audited_ballot_count;
    my_discrepancy_count = the_discrepancy_count;
    my_disagreement_count = the_disagreement_count;
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
    final Long county_id = the_dashboard.id();
    final County county = Persistence.getByID(county_id, County.class);
    final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);

    if (county == null || dosd == null) {
      throw new PersistenceException("unable to read county dashboard state");
    }
    
    // general information doesn't exist yet
    final Map<String, String> general_information = new HashMap<String, String>();

    final String manifest_digest = hashForFile(county_id, 
                                               the_dashboard.manifestUploadTimestamp(),
                                               FileStatus.IMPORTED_AS_BALLOT_MANIFEST);
    final String cvr_digest = hashForFile(county_id, 
                                          the_dashboard.cvrUploadTimestamp(), 
                                          FileStatus.IMPORTED_AS_CVR_EXPORT);
    
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
    
    return new CountyDashboardRefreshResponse(county_id, 
                                              the_dashboard.status(),
                                              general_information,
                                              the_dashboard.auditBoardMembers(),
                                              manifest_digest,
                                              the_dashboard.manifestUploadTimestamp(),
                                              cvr_digest,
                                              the_dashboard.cvrUploadTimestamp(),
                                              contests,
                                              contests_under_audit,
                                              the_dashboard.auditTimestamp(),
                                              the_dashboard.estimatedBallotsToAudit(),
                                              the_dashboard.ballotsAudited(),
                                              the_dashboard.discrepancies(),
                                              the_dashboard.disagreements(),
                                              the_dashboard.cvrsToAudit(),
                                              the_dashboard.cvrUnderAudit());
  }
  
  /**
   * Gets the abbreviated CountyDashboardRefreshResponse for the specified County 
   * dashboard. The abbreviated response leaves out information about contests,
   * general information, audit board information, and specific ballots to audit.
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
      createAbbreviatedResponse(final CountyDashboard the_dashboard) {
    final Long county_id = the_dashboard.id();
    final County county = Persistence.getByID(county_id, County.class);

    if (county == null) {
      throw new PersistenceException("unable to read county dashboard state");
    }

    final String manifest_digest = hashForFile(county_id, 
                                               the_dashboard.manifestUploadTimestamp(),
                                               FileStatus.IMPORTED_AS_BALLOT_MANIFEST);
    final String cvr_digest = hashForFile(county_id, 
                                          the_dashboard.cvrUploadTimestamp(), 
                                          FileStatus.IMPORTED_AS_CVR_EXPORT);

    return new CountyDashboardRefreshResponse(county_id, 
                                              the_dashboard.status(),
                                              null,
                                              null,
                                              manifest_digest,
                                              the_dashboard.manifestUploadTimestamp(),
                                              cvr_digest,
                                              the_dashboard.cvrUploadTimestamp(),
                                              null,
                                              null,
                                              the_dashboard.auditTimestamp(),
                                              the_dashboard.estimatedBallotsToAudit(),
                                              the_dashboard.ballotsAudited(),
                                              the_dashboard.discrepancies(),
                                              the_dashboard.disagreements(),
                                              null,
                                              null);
  }
  
  /**
   * Gets the recorded hash for the specified county ID, file timestamp and type.
   * 
   * @param the_id The ID.
   * @param the_timestamp The timestamp.
   * @param the_status The type.
   * @return the hash.
   */
  private static String hashForFile(final Long the_id, 
                                    final Instant the_timestamp, 
                                    final FileStatus the_status) {
    String result = null;
    final UploadedFile file = UploadedFileQueries.matching(the_id, the_timestamp, the_status);
    if (file != null) {
      result = file.hash();
    }
    return result;
  }
}
