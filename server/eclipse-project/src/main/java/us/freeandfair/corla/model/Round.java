/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import us.freeandfair.corla.persistence.AuditSelectionIntegerMapConverter;
import us.freeandfair.corla.persistence.ElectorListConverter;
import us.freeandfair.corla.persistence.LongListConverter;

/**
 * Information about an audit round. 
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
@SuppressWarnings({"PMD.ImmutableField", "PMD.TooManyMethods"})
public class Round implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The "text" constant.
   */
  private static final String TEXT = "text";
  
  /**
   * The round number.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_number;
  
  /**
   * The start time.
   */
  @Column(nullable = false, updatable = false)
  private Instant my_start_time;

  /**
   * The end time.
   */
  private Instant my_end_time;

  /**
   * The expected number of ballots to audit in this round. 
   */
  @Column(nullable = false, updatable = false)
  private Integer my_expected_count;
  
  /**
   * The actual number of ballots audited in this round. 
   */
  @Column(nullable = false)
  private Integer my_actual_count;
  
  /**
   * The audited prefix length expected to be achieved by the end of
   * this round.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_expected_audited_prefix_length;
  
  /**
   * The audited prefix length actually achieved by the end of this round.
   */
  @Column
  private Integer my_actual_audited_prefix_length;
  
  /**
   * The index of the audit random sequence where the round starts.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_start_audited_prefix_length;
  
  /**
   * The number of previously-audited ballots when the round starts.
   */
  @Column(nullable = false, updatable = false)
  private Integer my_previous_ballots_audited;
  
  /**
   * The sequence of CVR IDs for ballots to audit in this round, 
   * in the order they are to be presented.
   */
  @Column(nullable = false, updatable = false,
          name = "ballot_sequence", columnDefinition = TEXT)
  @Convert(converter = LongListConverter.class)
  private List<Long> my_ballot_sequence;
  
  /**
   * The CVR IDs for the audit subsequence to audit in this
   * round, in audit sequence order. 
   */
  @Column(nullable = false, updatable = false,
          name = "audit_subsequence", columnDefinition = TEXT)
  @Convert(converter = LongListConverter.class)
  private List<Long> my_audit_subsequence;
  
  /**
   * The number of discrepancies found in the audit so far.
   */
  @Column(nullable = false, name = "discrepancies", columnDefinition = TEXT)
  @Convert(converter = AuditSelectionIntegerMapConverter.class)
  private Map<AuditSelection, Integer> my_discrepancies = new HashMap<>();
  
  /**
   * The number of disagreements found in the audit so far.
   */
  @Column(nullable = false, name = "disagreements", columnDefinition = TEXT)
  @Convert(converter = AuditSelectionIntegerMapConverter.class)
  private Map<AuditSelection, Integer> my_disagreements = new HashMap<>();
  
  /**
   * The signatories for round sign-off
   */
  @Column(name = "signatories", columnDefinition = TEXT)
  @Convert(converter = ElectorListConverter.class)
  private List<Elector> my_signatories = new ArrayList<>();

  /**
   * Constructs an empty round, solely for persistence. 
   */
  public Round() {
    super();
  }
    
  /**
   * Constructs a round with the specified parameters.
   * 
   * @param the_number The round number.
   * @param the_start_time The start time.
   * @param the_expected_count The expected number of ballots to audit.
   * @param the_previous_ballots_audited The number of ballots audited when the 
   * round starts.
   * @param the_expected_audited_prefix_length The audit random sequence index 
   * where the round is expected to end.
   * @param the_start_audited_prefix_length The index of the audit random sequence 
   * where the round starts.
   * @param the_ballot_sequence The sequence of ballots to audit in this round.
   * @param the_audit_subsequence The subsequence of the audit sequence for 
   * this round.
   */
  public Round(final Integer the_number,
               final Instant the_start_time,
               final Integer the_expected_count,
               final Integer the_previous_ballots_audited,
               final Integer the_expected_audited_prefix_length,
               final Integer the_start_audited_prefix_length,
               final List<Long> the_ballot_sequence,
               final List<Long> the_audit_subsequence) {
    super();
    my_number = the_number;
    my_start_time = the_start_time;
    my_expected_count = the_expected_count;
    my_expected_audited_prefix_length = the_expected_audited_prefix_length;
    my_actual_count = 0;
    my_start_audited_prefix_length = the_start_audited_prefix_length;
    my_actual_audited_prefix_length = the_start_audited_prefix_length;
    my_previous_ballots_audited = the_previous_ballots_audited;
    my_ballot_sequence = the_ballot_sequence;
    my_audit_subsequence = the_audit_subsequence;
  }
  
  /**
   * @return the round number.
   */
  public Integer number() {
    return my_number;
  }
  
  /**
   * @return the start time.
   */
  public Instant startTime() {
    return my_start_time;
  }

  /**
   * @return the end time.
   */
  public Instant endTime() {
    return my_end_time;
  }
  
  /**
   * Sets the end time.
   * 
   * @param the_end_time The end time.
   */
  public void setEndTime(final Instant the_end_time) {
    my_end_time = the_end_time;
  }
  
  /**
   * @return the expected number of ballots to audit.
   */
  public Integer expectedCount() {
    return my_expected_count;
  }
  
  /**
   * @return the actual number of ballots audited.
   */
  public Integer actualCount() {
    return my_actual_count;
  }
  
  /**
   * Sets the actual number of ballots audited.
   * 
   * @param the_actual_count The count.
   */
  public void setActualCount(final Integer the_actual_count) {
    my_actual_count = the_actual_count;
  }
  
  /**
   * @return the number of ballots audited prior to this round.
   */
  public Integer previousBallotsAudited() {
    return my_previous_ballots_audited;
  }
  
  /**
   * @return the expected audit sequence prefix length to be 
   * achieved by the end of this round.
   */
  public Integer expectedAuditedPrefixLength() {
    return my_expected_audited_prefix_length;
  }
  
  /**
   * @return the audit sequence prefix length achieved.
   */
  public Integer actualAuditedPrefixLength() {
    return my_actual_audited_prefix_length;
  }
  
  /**
   * Sets the audit prefix sequence length achieved.
   * 
   * @param the_audited_prefix_length The prefix length achieved.
   */
  public void setActualAuditedPrefixLength(final int the_audited_prefix_length) {
    my_actual_audited_prefix_length = the_audited_prefix_length;
  }
  
  /**
   * @return the ballot sequence for this round.
   */
  public List<Long> ballotSequence() {
    return my_ballot_sequence;
  }
  
  /**
   * @return the audit subsequence for this round.
   */
  public List<Long> auditSubsequence() {
    return my_audit_subsequence;
  }
  
  /**
   * Adds an audited ballot.
   */
  public void addAuditedBallot() {
    my_actual_count = my_actual_count + 1;
  }
  
  /**
   * Removes an audited ballot.
   */
  public void removeAuditedBallot() {
    my_actual_count = my_actual_count - 1;
  }
  
  /**
   * @return the index of the audit random sequence where this round
   * starts.
   */
  public Integer startAuditedPrefixLength() {
    return my_start_audited_prefix_length;
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
    final Set<AuditSelection> selections = new HashSet<>();
    for (final AuditReason r : the_reasons) {
      selections.add(r.selection());
    }
    for (final AuditSelection s : selections) {
      my_discrepancies.put(s, my_discrepancies.getOrDefault(s, 0) + 1);
    }
  }
  
  /**
   * Removes a discrepancy for the specified audit reasons. This removes it 
   * both from the total and from the current audit round, if one is ongoing.
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
  }
  
  /**
   * @return the numbers of disagreements found in the audit so far,
   * categorized by contest audit reason.
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
  }
  
  /**
   * Removes a disagreement for the specified audit reasons. This removes it 
   * both from the total and from the current audit round, if one is ongoing.
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
  }
  
  /**
   * @return the signatories.
   */
  public List<Elector> signatories() {
    return Collections.unmodifiableList(my_signatories);
  }
  
  /**
   * Sets the signatories.
   */
  public void setSignatories(final List<Elector> the_signatories) {
    my_signatories.clear();
    my_signatories.addAll(the_signatories);
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "Round [start_time=" + my_start_time + ", end_time=" +
           my_end_time + ", expected_count=" + my_expected_count + 
           ", actual_count=" + my_actual_count + ", start_index=" + 
           my_start_audited_prefix_length + ", discrepancies=" + my_discrepancies + 
           "disagreements=" + my_disagreements + ", signatories=" +
           my_signatories + "]";
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
    if (the_other instanceof Round) {
      final Round other_round = (Round) the_other;
      result &= nullableEquals(other_round.startTime(), startTime());
      result &= nullableEquals(other_round.endTime(), endTime());
      result &= nullableEquals(other_round.expectedCount(), expectedCount());
      result &= nullableEquals(other_round.actualCount(), actualCount());
      result &= nullableEquals(other_round.startAuditedPrefixLength(), 
                               startAuditedPrefixLength());
      result &= nullableEquals(other_round.discrepancies(), discrepancies());
      result &= nullableEquals(other_round.disagreements(), disagreements());
      result &= nullableEquals(other_round.signatories(), signatories());
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
    return nullableHashCode(startTime());
  }
  
  /**
   * @return a version of this Round with no ballot/cvr sequences
   */
  public Round withoutSequences() {
    final Round result =
        new Round(my_number, my_start_time, my_expected_count, my_previous_ballots_audited,
                  my_expected_audited_prefix_length, my_start_audited_prefix_length,
                  null, null);
    result.my_actual_count = my_actual_count;
    result.my_actual_audited_prefix_length = my_actual_audited_prefix_length;
    result.my_discrepancies = my_discrepancies;
    result.my_disagreements = my_disagreements;
    result.my_signatories = my_signatories;
    result.my_end_time = my_end_time;
    
    return result;
  }
}
