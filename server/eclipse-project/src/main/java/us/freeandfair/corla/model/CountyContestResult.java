/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 19, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringSetConverter;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * A class representing the results for a single contest for a single county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Cacheable(true)
@Table(name = "county_contest_result",
       uniqueConstraints = {
         @UniqueConstraint(columnNames = {"county_id", "contest_id"}) },
       indexes = { @Index(name = "idx_ccr_county_contest", 
                          columnList = "county_id, contest_id",
                          unique = true),
                   @Index(name = "idx_ccr_county", columnList = "county_id"),
                   @Index(name = "idx_ccr_contest", columnList = "contest_id") })
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ImmutableField", "PMD.ExcessiveImports",
    "PMD.GodClass"})
public class CountyContestResult implements PersistentEntity, Serializable {
  /**
   * The "my_id" string.
   */
  private static final String MY_ID = "my_id";

  /**
   * The "result_id" string.
   */
  private static final String RESULT_ID = "result_id";
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;
  
  /**
   * The county to which this contest result set belongs. 
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private County my_county;

  /**
   * The contest.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn
  private Contest my_contest;

  /**
   * The winners allowed.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_winners_allowed;
  
  /**
   * The set of contest winners.
   */
  @Column(name = "winners", columnDefinition = "text")
  @Convert(converter = StringSetConverter.class)
  private Set<String> my_winners = new HashSet<>();
  
  /**
   * The set of contest losers.
   */
  @Column(name = "losers", columnDefinition = "text")
  @Convert(converter = StringSetConverter.class)
  private Set<String> my_losers = new HashSet<>();
  
  /**
   * A map from choices to vote totals.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "county_contest_vote_total",
                   joinColumns = @JoinColumn(name = RESULT_ID,
                                             referencedColumnName = MY_ID))
  @MapKeyColumn(name = "choice")
  @Column(name = "vote_total")
  private Map<String, Integer> my_vote_totals = new HashMap<>();
  
  /**
   * The minimum pairwise margin between a winner and a loser.
   */
  private Integer my_min_margin;
  
  /**
   * The maximum pairwise margin between a winner and a loser.
   */
  private Integer my_max_margin;
  
  /**
   * The total number of ballots cast in this county.
   */
  private Integer my_county_ballot_count = 0;
  
  /**
   * The total number of ballots cast in this county that contain this contest.
   */
  private Integer my_contest_ballot_count = 0;
  
  /**
   * Constructs a new empty CountyContestResult (solely for persistence).
   */
  public CountyContestResult() {
    super();
  }
  
  /**
   * Constructs a new CountyContestResult for the specified county ID and
   * contest.
   * 
   * @param the_county The county.
   * @param the_contest The contest.
   */
  public CountyContestResult(final County the_county, final Contest the_contest) {
    super();
    my_county = the_county;
    my_contest = the_contest;
    my_winners_allowed = the_contest.winnersAllowed();
    for (final Choice c : the_contest.choices()) {
      if (!c.fictitious()) {
        my_vote_totals.put(c.name(), 0);
      }
    }
  }
 
  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
  }
  
  /**
   * @return the county for this CountyContestResult.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the contest for this CountyContestResult.
   */
  public Contest contest() {
    return my_contest;
  }
  
  /**
   * @return the winners for thie CountyContestResult.
   */
  public Set<String> winners() {
    return Collections.unmodifiableSet(my_winners);
  }
  
  /**
   * @return the losers for this CountyContestResult.
   */
  public Set<String> losers() {
    return Collections.unmodifiableSet(my_losers);
  }
  
  /**
   * @return a map from choices to vote totals.
   */
  public Map<String, Integer> voteTotals() {
    return Collections.unmodifiableMap(my_vote_totals);
  }
  
  /**
   * @return a list of the choices in descending order by number of votes
   * received.
   */
  public List<String> rankedChoices() {
    final List<String> result = new ArrayList<String>();
    
    final SortedMap<Integer, List<String>> sorted_totals = 
        new TreeMap<Integer, List<String>>(new ReverseIntegerComparator());
    for (final Entry<String, Integer> e : my_vote_totals.entrySet()) {
      final List<String> list = sorted_totals.get(e.getValue());
      if (list == null) {
        sorted_totals.put(e.getValue(), new ArrayList<>());
      }
      sorted_totals.get(e.getValue()).add(e.getKey());
    }
    
    final Iterator<Entry<Integer, List<String>>> iterator = 
        sorted_totals.entrySet().iterator();
    while (iterator.hasNext()) {
      final Entry<Integer, List<String>> entry = iterator.next();
      result.addAll(entry.getValue());
    }
    return result;
  }
  
  /**
   * Compute the pairwise margin between the specified choices.
   * If the first choice has more votes than the second, the
   * result will be positive; if the second choie has more 
   * votes than the first, the result will be negative; if they
   * have the same number of votes, the result will be 0.
   * 
   * @param the_first_choice The first choice.
   * @param the_second_choice The second choice.
   * @return the pairwise margin between the two choices, as
   * an OptionalInt (empty if the margin cannot be calculated).
   */
  public OptionalInt pairwiseMargin(final String the_first_choice,
                                    final String the_second_choice) {
    final Integer first_votes = my_vote_totals.get(the_first_choice);
    final Integer second_votes = my_vote_totals.get(the_second_choice);
    final OptionalInt result;
    
    if (first_votes == null || second_votes == null) {
      result = OptionalInt.empty();
    } else {
      result = OptionalInt.of(first_votes - second_votes);
    }
    
    return result;
  }
  
  /**
   * Computes the margin between the specified choice and the next choice. 
   * If the specified choice is the last choice, or is not a valid choice,
   * the margin is empty. 
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  public OptionalInt marginToNearestLoser(final String the_choice) {
    final OptionalInt result;
    final List<String> choices = rankedChoices();
    int index = choices.indexOf(the_choice);
    
    if (index < 0 || index == choices.size() - 1) {
      result = OptionalInt.empty();
    } else {
      // find the nearest loser
      String loser = "";
      index = index + 1;
      while (index < choices.size() && !losers().contains(loser)) {
        loser = choices.get(index);
        index = index + 1;
      }
      if (losers().contains(loser)) {
        result = OptionalInt.of(voteTotals().get(the_choice) - 
                                voteTotals().get(loser));
      } else {
        // there was no nearest loser, maybe there are only winners
        result = OptionalInt.empty();
      }
    }
    
    return result;
  }
  
  /**
   * Computes the diluted margin between the specified choice and the nearest
   * loser. If the specified choice is the last choice or is not a valid 
   * choice, or the margin is undefined, the result is null.
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  public BigDecimal countyDilutedMarginToNearestLoser(final String the_choice) {
    BigDecimal result = null;
    final OptionalInt margin = marginToNearestLoser(the_choice);
    
    if (margin.isPresent() && my_county_ballot_count > 0) {
      result = BigDecimal.valueOf(margin.getAsInt()).
                   divide(BigDecimal.valueOf(my_county_ballot_count), 
                          MathContext.DECIMAL128);
    }
    
    return result;
  }
  
  /**
   * Computes the diluted margin between the specified choice and the nearest
   * loser. If the specified choice is the last choice or is not a valid 
   * choice, or the margin is undefined, the result is null.
   * 
   * @param the_choice The choice.
   * @return the margin.
   */
  public BigDecimal contestDilutedMarginToNearestLoser(final String the_choice) {
    BigDecimal result = null;
    final OptionalInt margin = marginToNearestLoser(the_choice);
    
    if (margin.isPresent() && my_contest_ballot_count > 0) {
      result = BigDecimal.valueOf(margin.getAsInt()).
                   divide(BigDecimal.valueOf(my_contest_ballot_count), 
                          MathContext.DECIMAL128);
    }
    
    return result;
  }
  
  /**
   * @return the number of winners allowed in this contest.
   */
  public Integer winnersAllowed() {
    return my_winners_allowed;
  }
  
  /**
   * @return the number of ballots cast in this county that include this contest.
   */
  public Integer contestBallotCount() {
    return my_contest_ballot_count;
  }
  
  /**
   * @return the number of ballots cast in this county.
   */
  public Integer countyBallotCount() {
    return my_county_ballot_count;
  }
  
  /**
   * @return the maximum margin between a winner and a loser.
   */
  public Integer maxMargin() {
    return my_max_margin;
  }
  
  /**
   * @return the minimum margin between a winner and a loser.
   */
  public Integer minMargin() {
    return my_min_margin;
  }
  
  /**
   * @return the county diluted margin for this contest, defined as the
   * minimum margin divided by the number of ballots cast in the county.
   * @exception IllegalStateException if no ballots have been counted.
   */
  public BigDecimal countyDilutedMargin() {
    BigDecimal result;
    if (my_county_ballot_count > 0) {
      result = BigDecimal.valueOf(my_min_margin).
               divide(BigDecimal.valueOf(my_county_ballot_count), 
                      MathContext.DECIMAL128);
      if (my_losers.isEmpty()) {
        // if we only have winners, there is no margin
        result = BigDecimal.ONE;
      }
      
      // TODO: how do we handle a tie?
    } else {
      throw new IllegalStateException("attempted to calculate diluted margin with no ballots");
    }
    
    return result;
  }
  
  /**
   * @return the diluted margin for this contest, defined as the
   * minimum margin divided by the number of ballots cast in this county
   * that contain this contest.
   * @exception IllegalStateException if no ballots have been counted.
   */
  public BigDecimal contestDilutedMargin() {
    BigDecimal result;
    if (my_contest_ballot_count > 0) {
      result = BigDecimal.valueOf(my_min_margin).
               divide(BigDecimal.valueOf(my_contest_ballot_count), 
                      MathContext.DECIMAL128);
      if (my_losers.isEmpty()) {
        // if we only have winners, there is no margin
        result = BigDecimal.ONE;
      }
      
      // TODO: how do we handle a tie?
    } else {
      throw new IllegalStateException("attempted to calculate diluted margin with no ballots");
    }
    
    return result;
  }
  
  /**
   * Reset the vote totals and all related data in this CountyContestResult.
   */
  public void reset() {
    my_winners.clear();
    my_losers.clear();
    for (final String s : my_vote_totals.keySet()) {
      my_vote_totals.put(s, 0);
    }
    updateResults();
  }
  
  /**
   * Update the vote totals using the data from the specified CVR.
   * 
   * @param the_cvr The CVR.
   */
  public void addCVR(final CastVoteRecord the_cvr) {
    final CVRContestInfo ci = the_cvr.contestInfoForContest(my_contest);
    if (ci != null) {
      for (final String s : ci.choices()) {
        my_vote_totals.put(s, my_vote_totals.get(s) + 1);
      }
      my_contest_ballot_count = Integer.valueOf(my_contest_ballot_count + 1);
    }
    my_county_ballot_count = Integer.valueOf(my_county_ballot_count + 1);
  }
  
  /**
   * Updates the stored results.
   */
  public void updateResults() {
    // first, sort the vote totals
    final SortedMap<Integer, List<String>> sorted_totals = 
        new TreeMap<Integer, List<String>>(new ReverseIntegerComparator());
    for (final Entry<String, Integer> e : my_vote_totals.entrySet()) {
      final List<String> list = sorted_totals.get(e.getValue());
      if (list == null) {
        sorted_totals.put(e.getValue(), new ArrayList<>());
      }
      sorted_totals.get(e.getValue()).add(e.getKey());
    }
    // next, get the winners and losers
    final Iterator<Entry<Integer, List<String>>> vote_total_iterator = 
        sorted_totals.entrySet().iterator();
    Entry<Integer, List<String>> entry = null;
    while (vote_total_iterator.hasNext() && my_winners.size() < my_winners_allowed) {
      entry = vote_total_iterator.next();
      final List<String> choices = entry.getValue();
      if (choices.size() + my_winners.size() <= my_winners_allowed) {
        my_winners.addAll(choices);
      } else {
        // we are arbitrarily making the first choices in the list "winners" and
        // the last choices in the list "losers", but since it's a tie, it really
        // doesn't matter
        final int to_add = my_winners_allowed - my_winners.size();
        my_winners.addAll(choices.subList(0, to_add));
        my_losers.addAll(choices.subList(to_add, choices.size()));
      }
    }
    while (vote_total_iterator.hasNext()) {
      // all the other choices count as losers
      my_losers.addAll(vote_total_iterator.next().getValue());
    }
    
    calculateMargins();
  }
  
  /**
   * Calculates all the pairwise margins using the vote totals.
   */
  private void calculateMargins() {
    my_min_margin = Integer.MAX_VALUE;
    my_max_margin = Integer.MIN_VALUE;
    for (final String w : my_winners) {
      if (my_losers.isEmpty()) {
        my_min_margin = 0;
        my_max_margin = 0;
      } else {
        for (final String l : my_losers) {
          final int margin = my_vote_totals.get(w) - my_vote_totals.get(l);
          my_min_margin = Math.min(my_min_margin, margin);
          my_max_margin = Math.max(my_max_margin, margin);
        }
      }
    }
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "CountyContestResult [id=" + id() + "]";
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
    if (the_other instanceof CountyContestResult) {
      final CountyContestResult other_result = (CountyContestResult) the_other;
      // compare by database ID, since that is the only
      // context in which they can reasonably be compared
      result &= nullableEquals(other_result.id(), id());
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
  
  /**
   * A reverse integer comparator, for sorting lists of integers in reverse.
   */
  @SuppressFBWarnings("RV_NEGATING_RESULT_OF_COMPARETO")
  public static class ReverseIntegerComparator 
      implements Comparator<Integer>, Serializable {
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    
   /**
     * Compares two integers. Returns the negation of the regular integer 
     * comparator result.
     * 
     * @param the_first The first integer.
     * @param the_second The second integer.
     */
    public int compare(final Integer the_first, final Integer the_second) {
      return -(the_first.compareTo(the_second));
    }
  }
}
