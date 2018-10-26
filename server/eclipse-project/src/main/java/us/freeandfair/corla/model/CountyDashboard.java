/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.model.ImportStatus.ImportState;

import us.freeandfair.corla.persistence.AuditSelectionIntegerMapConverter;
import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringSetConverter;

/**
 * The county dashboard.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Cacheable(true)
@Table(name = "county_dashboard")
@SuppressWarnings({"PMD.ImmutableField", "PMD.TooManyMethods", "PMD.TooManyFields",
    "PMD.GodClass", "PMD.ExcessiveImports", "checkstyle:methodcount",
    "PMD.ExcessivePublicCount", "PMD.CyclomaticComplexity"})
// note: county dashboard is not serializable because it contains an uploaded file
public class CountyDashboard implements PersistentEntity {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(CountyDashboard.class);

  /**
   * The "text" constant.
   */
  private static final String TEXT = "text";

  /**
   * The minimum number of members on an audit board.
   */
  public static final int MIN_AUDIT_BOARD_MEMBERS = 2;

  /**
   * The minimum number of members on an audit round sign-off.
   */
  public static final int MIN_ROUND_SIGN_OFF_MEMBERS = 2;

  /**
   * The "no content" constant.
   */
  private static final Integer NO_CONTENT = null;

  /**
   * The "index" string.
   */
  private static final String INDEX = "index";

  /**
   * The "my_id" string.
   */
  private static final String MY_ID = "my_id";

  /**
   * The "dashboard_id" string.
   */
  private static final String DASHBOARD_ID = "dashboard_id";

  /**
   * The database ID; this is always the county ID.
   */
  @Id
  private Long my_id;

  /**
   * The county.
   */
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private County my_county;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;

  /**
   * The file containing the most recent set of uploaded CVRs.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn
  private UploadedFile my_cvr_file;

  /**
   * The number of CVRs imported.
   */
  @Column(nullable = false)
  private Integer my_cvrs_imported = 0;

  /**
   * The CVR import status.
   */
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "my_import_state",
                         column = @Column(name = "cvr_import_state")),
      @AttributeOverride(name = "my_error_message",
                         column = @Column(name = "cvr_import_error_message")),
      @AttributeOverride(name = "my_timestamp",
                         column = @Column(name = "cvr_import_timestamp"))
      })
  private ImportStatus my_cvr_import_status =
      new ImportStatus(ImportState.NOT_ATTEMPTED, null);

  /**
   * The timestamp of the most recent uploaded ballot manifest.
   */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn
  private UploadedFile my_manifest_file;

  /**
   * The number of ballots described in the ballot manifest.
   */
  @Column(nullable = false)
  private Integer my_ballots_in_manifest = 0;

  /**
   * The timestamp for the start of the audit.
   */
  private Instant my_audit_timestamp;

  /**
   * The number of audit boards.
   */
  @Column(name="audit_board_count")
  private Integer auditBoardCount;

  /**
   * The audit boards.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @MapKeyColumn(name = INDEX)
  @CollectionTable(name = "audit_board",
                   joinColumns = @JoinColumn(name = DASHBOARD_ID,
                                             referencedColumnName = MY_ID))
  private Map<Integer, AuditBoard> my_audit_boards = new HashMap<>();

  /**
   * The audit rounds.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @OrderColumn(name = INDEX)
  @CollectionTable(name = "round",
                   joinColumns = @JoinColumn(name = DASHBOARD_ID,
                                             referencedColumnName = MY_ID))
  private List<Round> my_rounds = new ArrayList<>();

  /**
   * The current audit round.
   */
  private Integer my_current_round_index;

  /**
   * The set of contests that drive our audits. Strings, not "fancy"
   * Abstract Data Types
   */
  @Column(name = "driving_contests", columnDefinition = TEXT)
  @Convert(converter = StringSetConverter.class)
  private Set<String> drivingContestNames = new HashSet<>();


  /**
   * The audit data.
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "county_dashboard_to_comparison_audit",
             joinColumns = { @JoinColumn(name = DASHBOARD_ID,
                                         referencedColumnName = MY_ID) },
             inverseJoinColumns = { @JoinColumn(name = "comparison_audit_id",
                                                referencedColumnName = MY_ID) })
  private Set<ComparisonAudit> audits = new HashSet<>();

  /**
   * The audit investigation reports.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @OrderColumn(name = INDEX)
  @CollectionTable(name = "audit_investigation_report",
                   joinColumns = @JoinColumn(name = DASHBOARD_ID,
                                             referencedColumnName = MY_ID))
  private List<AuditInvestigationReportInfo> my_investigation_reports =
      new ArrayList<>();

  /**
   * The audit interim reports.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @OrderColumn(name = INDEX)
  @CollectionTable(name = "audit_intermediate_report",
                   joinColumns = @JoinColumn(name = DASHBOARD_ID,
                                             referencedColumnName = MY_ID))
  private List<IntermediateAuditReportInfo> my_intermediate_reports =
      new ArrayList<>();

  /**
   * The number of ballots audited.
   */
  @Column(nullable = false)
  private Integer my_ballots_audited = 0;

  /**
   * The length of the audited prefix of the list of samples to audit;
   * equivalent to the index of the CVR currently under audit.
   */
  private Integer my_audited_prefix_length;

  /**
   * The number of samples that have been audited so far.
   */
  private Integer my_audited_sample_count;

  /**
   * The number of discrepancies found in the audit so far.
   */
  @Column(nullable = false, name = "discrepancies", columnDefinition = "text")
  @Convert(converter = AuditSelectionIntegerMapConverter.class)
  private Map<AuditSelection, Integer> my_discrepancies = new HashMap<>();

  /**
   * The number of disagreements found in the audit so far.
   */
  @Column(nullable = false, name = "disagreements", columnDefinition = "text")
  @Convert(converter = AuditSelectionIntegerMapConverter.class)
  private Map<AuditSelection, Integer> my_disagreements = new HashMap<>();

  /**
   * Constructs an empty county dashboard, solely for persistence.
   */
  public CountyDashboard() {
    super();
  }

  /**
   * Constructs a new county dashboard for the specified county.
   *
   * @param the_county The county.
   */
  public CountyDashboard(final County the_county) {
    super();
    my_county = the_county;
    my_id = the_county.id();
  }

  /**
   * @return the database ID for this dashboard, which is the same as
   * its county ID.
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * Sets the database ID for this dashboard. This operation is unsupported on
   * this class.
   *
   * @param the_id The ID.
   * @exception UnsupportedOperationException always.
   */
  @Override
  public final void setID(final Long the_id) {
    throw new UnsupportedOperationException("setID() not supported on county dashboard");
  }

  /**
   * @return the version for this dashboard.
   */
  @Override
  public Long version() {
    return my_version;
  }

  /**
   * @return the county for this dashboard.
   */
  public County county() {
    return my_county;
  }

  /**
   * @return the CVR file. A return value of null means
   * that no CVRs have been uploaded for this county.
   */
  public UploadedFile cvrFile() {
    return my_cvr_file;
  }

  /**
   * Sets a new CVR file, replacing the previous one.
   *
   * @param the_file The CVR file.
   */
  public void setCVRFile(final UploadedFile the_file) {
    my_cvr_file = the_file;
  }

  /**
   * @return the ballot manifest file. A return value of null means
   * that no ballot manifest has been uploaded for this county.
   */
  public UploadedFile manifestFile() {
    return my_manifest_file;
  }

  /**
   * Sets a new ballot manifest file, replacing the previous one.
   *
   * @param the_file The manifest file.
   */
  public void setManifestFile(final UploadedFile the_file) {
    my_manifest_file = the_file;
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
   * @return the number of audit boards.
   */
  public Integer auditBoardCount() {
    return this.auditBoardCount;
  }

  /**
   * Set the expected number of audit boards.
   *
   * @param count number of audit boards
   */
  public void setAuditBoardCount(final Integer count) {
    this.auditBoardCount = count;
  }

  /**
   * @return the entire list of audit boards.
   */
  public Map<Integer, AuditBoard> auditBoards() {
    return Collections.unmodifiableMap(my_audit_boards);
  }

  /**
   * Signs in the specified audit board as of the present time;
   * the supplied set of electors must be the full set of electors on
   * the board. The previous audit board, if any, is signed out if it
   * had not yet been signed out.
   *
   * @param the_members The members.
   */
  public void signInAuditBoard(final Integer index,
                               final List<Elector> the_members) {
    final AuditBoard currentBoard = my_audit_boards.get(index);
    final AuditBoard newBoard = new AuditBoard(the_members, Instant.now());

    if (currentBoard != null) {
      this.signOutAuditBoard(index);
    }

    my_audit_boards.put(index, newBoard);
  }

  /**
   * Signs out the audit board at index.
   *
   * If no audit board is present at the given index, nothing is changed.
   */
  public void signOutAuditBoard(final Integer index) {
    final AuditBoard currentBoard = my_audit_boards.get(index);

    if (currentBoard != null) {
      currentBoard.setSignOutTime(Instant.now());
      my_audit_boards.remove(index);
    }
  }

  /**
   * Signs out all audit boards.
   */
  public void signOutAllAuditBoards() {
    final Set<Integer> ks = new HashSet<Integer>(my_audit_boards.keySet());

    for (final Integer i : ks) {
      this.signOutAuditBoard(i);
    }
  }

  /**
   * Test if the desired number of audit boards have signed in.
   *
   * Note: Only works properly for indexes less than the current audit board
   * count in case there are orphaned boards outside of the current expected
   * key range, because just counting the number of keys in the audit board map
   * might yield the wrong answer if there are orphaned audit boards.
   *
   * Use signOutAllAuditBoards to properly clear out the data structure holding
   * all audit boards, signing out audit boards as necessary.
   *
   * @return boolean
   */
  public boolean areAuditBoardsSignedIn() {
    boolean result = true;

    for (int i = 0; i < this.auditBoardCount(); i++) {
      if (my_audit_boards.get(i) == null) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * Test if all audit boards are signed out.
   *
   * @return boolean
   */
  public boolean areAuditBoardsSignedOut() {
    boolean result = true;

    for (int i = 0; i < this.auditBoardCount(); i++) {
      if (my_audit_boards.get(i) != null) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * @return all the audit rounds.
   */
  public List<Round> rounds() {
    return Collections.unmodifiableList(my_rounds);
  }

  /**
   * @return the current audit round, or null if no round is in progress.
   */
  public Round currentRound() {
    if (my_current_round_index == null) {
      return null;
    } else {
      return my_rounds.get(my_current_round_index);
    }
  }

  /**
   * Begins a new round with the specified number of ballots to audit
   * and expected achieved prefix length, starting at the specified index
   * in the random audit sequence.
   *
   * @param numberOfBallots The number of ballots in this round
   * @param prefixLength The expected audited prefix length at the round's end.
   * @param startIndex The start index.
   * @param ballotSequence The ballots to audit in the round, in the order
   * in which they should be presented.
   * @param auditSubsequence The audit subsequence for the round.
   * @exception IllegalStateException if a round is currently ongoing.
   */
  public void startRound(final int numberOfBallots,
                         final int prefixLength,
                         final int startIndex,
                         final List<Long> ballotSequence,
                         final List<Long> auditSubsequence) {
    if (my_current_round_index == null) {
      my_current_round_index = my_rounds.size();
    } else {
      throw new IllegalStateException("cannot start a round while one is running");
    }

    // note UI round indexing is from 1, not 0
    final Round round = new Round(my_current_round_index + 1,
                                  Instant.now(),
                                  numberOfBallots,
                                  my_ballots_audited,
                                  prefixLength,
                                  startIndex,
                                  ballotSequence,
                                  auditSubsequence);
    my_rounds.add(round);
  }

  /**
   * Ends the current round.
   *
   * Signs out all audit boards, and performs any bookkeeping necessary to end
   * the round.
   *
   * @exception IllegalStateException if there is no current round.
   */
  public void endRound() {
    if (my_current_round_index == null) {
      throw new IllegalStateException("no round to end");
    } else {
      this.setAuditBoardCount(null);
      this.signOutAllAuditBoards();

      final Round round = my_rounds.get(my_current_round_index);
      round.setEndTime(Instant.now());
      my_current_round_index = NO_CONTENT;
    }
  }

  /**
   * @return the number of ballots remaining in the current round, or 0
   * if there is no current round.
   */
  public int ballotsRemainingInCurrentRound() {
    final int result;

    if (my_current_round_index == null) {
      result = 0;
    } else {

      final Round round = currentRound();

      result = round.ballotSequence().size() - round.actualCount();

      LOGGER.debug(String.format("[ballotsRemainingInCurrentRound:"
                                 + " index=%d, result=%d,"
                                 + " ballotSequence=%s"
                                 + " ballotSequence.size=%d"
                                 + " cdb.auditedSampleCount()=%d]",
                                 my_current_round_index,
                                 result,
                                 round.ballotSequence(),
                                 round.ballotSequence().size(),
                                 this.auditedSampleCount()));
    }
    return result;
  }

  /**
   * @return the set of comparison audits being performed.
   */
  public Set<ComparisonAudit> getAudits() {
    return Collections.unmodifiableSet(audits);
  }


  /**
   * @return the set of comparison audits being performed.
   */
  public Set<ComparisonAudit> comparisonAudits() {
    return Collections.unmodifiableSet(audits);
  }

  /**
   * Sets the comparison audits being performed.
   *
   * @param audits The comparison audits.
   */
  public void setAudits(final Set<ComparisonAudit> audits) {
    this.audits.clear();
    this.audits.addAll(audits);
  }

  /**
   * @return the set of contest names driving the audit.
   */
  public Set<String> drivingContestNames() {
    return Collections.unmodifiableSet(drivingContestNames);
  }

  /**
   * Sets the contests driving the audit.
   *
   * @param the_driving_contests The contests.
   */
  public void setDrivingContestNames(final Set<String> the_driving_contests) {
    drivingContestNames.clear();
    drivingContestNames.addAll(the_driving_contests);
  }

  /**
   * Submits an audit investigation report.
   *
   * @param the_report The audit investigation report.
   */
  public void submitInvestigationReport(final AuditInvestigationReportInfo the_report) {
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
    my_intermediate_reports.add(the_report);
  }

  /**
   * @return the list of submitted audit interim reports.
   */
  public List<IntermediateAuditReportInfo> intermediateReports() {
    return Collections.unmodifiableList(my_intermediate_reports);
  }

  /**
   * Returns the a list of CVR IDs under audit for the assigned audit boards.
   *
   * Delegates the actual calculation to the current Round, if one exists.
   *
   * @return a list of CVR IDs assigned to each audit board, where the list
   *         offset matches the audit board offset.
   */
  public List<Long> cvrsUnderAudit() {
    final Round round = this.currentRound();

    if (round == null) {
      return null;
    }

    return round.cvrsUnderAudit();
  }

  /**
   * @return the number of ballots audited.
   */
  public Integer ballotsAudited() {
    return my_ballots_audited;
  }

  /**
   * Adds an audited ballot. This adds it both to the total and to
   * the current audit round. If no round is ongoing, this method
   * does nothing.
   */
  public void addAuditedBallot() {
    if (my_current_round_index != null) {
      my_ballots_audited = my_ballots_audited + 1;
      my_rounds.get(my_current_round_index).addAuditedBallot();
    }
  }

  /**
   * Removes an audited ballot. This removes it both from the total and
   * from the current audit round, if one is ongoing.
   */
  public void removeAuditedBallot() {
    if (my_current_round_index != null) {
      my_ballots_audited = my_ballots_audited - 1;
      my_rounds.get(my_current_round_index).removeAuditedBallot();
    }
  }

  /**
   * @return the number of CVRs in the CVR import.
   */
  public Integer cvrsImported() {
    return my_cvrs_imported;
  }

  /**
   * Sets the number of CVRs imported.
   *
   * @param the_cvrs_imported The number.
   */
  public void setCVRsImported(final Integer the_cvrs_imported) {
    my_cvrs_imported = the_cvrs_imported;
  }

  /**
   * @return the CVR import status.
   */
  public ImportStatus cvrImportStatus() {
    return my_cvr_import_status;
  }

  /**
   * Sets the CVR import status.
   *
   * @param the_cvr_import_status The new status.
   */
  public void setCVRImportStatus(final ImportStatus the_cvr_import_status) {
    my_cvr_import_status = the_cvr_import_status;
  }

  /**
   * @return the number of ballots described in the ballot manifest.
   */
  public Integer ballotsInManifest() {
    return my_ballots_in_manifest;
  }

  /**
   * Sets the number of ballots described in the ballot manifest.
   *
   * @param the_ballots_in_manifest The number.
   */
  public void setBallotsInManifest(final Integer the_ballots_in_manifest) {
    my_ballots_in_manifest = the_ballots_in_manifest;
  }

  /**
   * @return the numbers of discrepancies found in the audit so far,
   * categorized by contest audit selection.
   */
  public Map<AuditSelection, Integer> discrepancies() {
    return Collections.unmodifiableMap(my_discrepancies);
  }

  /**
   * Adds a discrepancy for the specified audit reasons. This adds it both to the
   * total and to the current audit round, if one is ongoing.
   *
   * @param the_reasons The reasons.
   */
  public void addDiscrepancy(final Set<AuditReason> the_reasons) {
    LOGGER.debug(String.format("[addDiscrepancy for %s County: the_reasons=%s", county().name(), the_reasons));
    final Set<AuditSelection> selections = new HashSet<>();
    for (final AuditReason r : the_reasons) {
      selections.add(r.selection());
    }
    for (final AuditSelection s : selections) {
      my_discrepancies.put(s, my_discrepancies.getOrDefault(s, 0) + 1);
    }
    if (my_current_round_index != null) {
      my_rounds.get(my_current_round_index).addDiscrepancy(the_reasons);
    }
  }

  /**
   * Removes a discrepancy for the specified audit reasons. This removes it
   * both from the total and from the current audit round, if one is ongoing.
   *
   *
   * @param the_reasons The reasons.
   */
  public void removeDiscrepancy(final Set<AuditReason> the_reasons) {
    final Set<AuditSelection> selections = new HashSet<>();
    for (final AuditReason r : the_reasons) {
      selections.add(r.selection());
    }
    for (final AuditSelection s : selections) {
      my_discrepancies.put(s, my_discrepancies.getOrDefault(s, 0) - 1);
    }
    if (my_current_round_index != null) {
      my_rounds.get(my_current_round_index).removeDiscrepancy(the_reasons);
    }
  }


  /**
   * @return the numbers of disagreements found in the audit so far,
   * categorized by contest audit selection.
   */
  public Map<AuditSelection, Integer> disagreements() {
    return my_disagreements;
  }

  /**
   * Adds a disagreement for the specified audit reasons. This adds it both to the
   * total and to the current audit round, if one is ongoing.
   *
   * @param the_reasons The reasons.
   */
  public void addDisagreement(final Set<AuditReason> the_reasons) {
    final Set<AuditSelection> selections = new HashSet<>();
    for (final AuditReason r : the_reasons) {
      selections.add(r.selection());
    }
    for (final AuditSelection s : selections) {
      my_disagreements.put(s, my_disagreements.getOrDefault(s, 0) + 1);
    }
    if (my_current_round_index != null) {
      my_rounds.get(my_current_round_index).addDisagreement(the_reasons);
    }
  }

  /**
   * Removes a disagreement for the specified audit reasons. This removes it
   * both from the total and from the current audit round, if one is ongoing.
   *
   *
   * @param the_reasons The reasons.
   */
  public void removeDisagreement(final Set<AuditReason> the_reasons) {
    final Set<AuditSelection> selections = new HashSet<>();
    for (final AuditReason r : the_reasons) {
      selections.add(r.selection());
    }
    for (final AuditSelection s : selections) {
      my_disagreements.put(s, my_disagreements.getOrDefault(s, 0) - 1);
    }
    if (my_current_round_index != null) {
      my_rounds.get(my_current_round_index).removeDisagreement(the_reasons);
    }
  }

  /**
   *  takes the targeted contests/ComparisonAudits and checks them for
   *  completion/RiskLimitAchieved
   **/
  public Boolean allAuditsComplete() {
    return comparisonAudits().stream()
      // .filter(ca -> ca.isTargeted()) // FIXME This might be better?
      .filter(ca -> ca.auditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .allMatch(ca -> ca.isFinished());
  }

  /**
   * @return the estimated number of samples to audit.
   */
  public Integer estimatedSamplesToAudit() {
    return comparisonAudits().stream()
      .filter(ca -> ca.auditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .map(ca -> ca.estimatedRemaining())
      .mapToInt(Integer::intValue)
      .sum();
  }

  /**
   * @return the optimistic number of samples to audit.
   */
  public Integer optimisticSamplesToAudit() {
    // NOTE: there could be race conditions between audit boards across counties
    final Optional<Integer> maybe = comparisonAudits().stream()
      .filter(ca -> ca.auditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
      .map(ca -> ca.optimisticSamplesToAudit())
      .max(Comparator.naturalOrder());
    // NOTE: we may be asking for this when we don't need to; when there are no
    // audits setup yet
    if (maybe.isPresent()) {
      return maybe.get();
    } else {
      return 0;
    }
  }

  /**
   * @return the length of the audited prefix of the sequence of
   * ballots to audit (i.e., the number of audited ballots that
   * "count").
   */
  public Integer auditedPrefixLength() {
    return my_audited_prefix_length;
  }

  /**
   * Sets the length of the audited prefix of the sequence of
   * ballots to audit. If there is no active round, this method does
   * nothing.
   *
   * @param the_audited_prefix_length The audited prefix length.
   */
  public void setAuditedPrefixLength(final int the_audited_prefix_length) {
    if (my_current_round_index != null) {
      my_audited_prefix_length = the_audited_prefix_length;
      my_rounds.get(my_current_round_index).
          setActualAuditedPrefixLength(the_audited_prefix_length);
    }
  }

  /**
   * @return the number of samples that have been included in the
   * audit calculations so far.
   */
  public Integer auditedSampleCount() {
    return my_audited_sample_count;
  }

  /**
   * Sets the number of samples that have been included in the
   * audit calculations so far.
   *
   * @param the_audited_sample_count The audited sample count.
   */
  public void setAuditedSampleCount(final int the_audited_sample_count) {
    my_audited_sample_count = the_audited_sample_count;
  }

  /**
   * Ends all audits in the county. This changes the status of any audits
   * that have not achieved their risk limit to ENDED.
   *
   * You should not use this lightly, some of the audits might be shared
   * with others!
   */
  public void endAudits() {
    for (final ComparisonAudit ca : audits) {
      ca.endAudit();
    }
  }

  /**
   * End all audits that only belong to this county.
   */
  public List<ComparisonAudit> endSingleCountyAudits() {
    return comparisonAudits().stream()
      .filter(a -> a.isSingleCountyFor(this.county()))
      .map(a -> {
          a.endAudit();
          return a;})
      .collect(Collectors.toList());
  }

  /**
   * Updates the status for all audits in the county. This changes their statuses
   * based on whether they have achieved their risk limits.
   */
  public void updateAuditStatus() {
    for (final ComparisonAudit ca : audits) {
      ca.updateAuditStatus();
    }
  }

  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "CountyDashboard [county=" + id() + "]";
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
    if (the_other instanceof CountyDashboard) {
      final CountyDashboard other_cdb = (CountyDashboard) the_other;
      // there can only be one county dashboard in the system for each
      // ID, so we check their equivalence by ID
      result &= nullableEquals(other_cdb.id(), id());
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
    return id().hashCode();
  }
}
