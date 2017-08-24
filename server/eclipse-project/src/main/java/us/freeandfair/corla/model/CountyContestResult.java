/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 19, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import us.freeandfair.corla.persistence.AbstractEntity;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * A class representing the results for a single contest for a single county.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "county_contest_result",
       uniqueConstraints = {
         @UniqueConstraint(columnNames = {"county_id", "contest_id"})
       })

//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class CountyContestResult extends AbstractEntity implements Serializable {
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
   * The votes allowed.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_votes_allowed;
  
  /**
   * The set of contest winners.
   */
  @ElementCollection
  @CollectionTable(name = "county_contest_winners",
                   joinColumns = @JoinColumn(name = RESULT_ID, 
                                             referencedColumnName = MY_ID))
  @Column(name = "winner")
  private Set<String> my_winners = new HashSet<String>();
  
  /**
   * The set of contest losers.
   */
  @ElementCollection
  @CollectionTable(name = "county_contest_losers",
                   joinColumns = @JoinColumn(name = RESULT_ID,
                                             referencedColumnName = MY_ID))
  @Column(name = "loser")
  private Set<String> my_losers = new HashSet<String>();
  
  /**
   * A map from choices to vote totals.
   */
  @ElementCollection
  @CollectionTable(name = "county_contest_vote_total",
                   joinColumns = @JoinColumn(name = RESULT_ID,
                                             referencedColumnName = MY_ID))
  @MapKeyColumn(name = "choice")
  @Column(name = "vote_total")
  private Map<String, Integer> my_vote_totals = 
      new HashMap<String, Integer>();
  
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
    my_votes_allowed = the_contest.votesAllowed();
    for (final Choice c : the_contest.choices()) {
      my_vote_totals.put(c.name(), 0);
    }
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
   * @return the number of votes allowed in this contest.
   */
  public Integer votesAllowed() {
    return my_votes_allowed;
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
  public BigDecimal dilutedMarginCounty() {
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
    final SortedMap<Integer, String> sorted_totals = 
        new TreeMap<Integer, String>(new ReverseIntegerComparator());
    for (final Entry<String, Integer> e : my_vote_totals.entrySet()) {
      sorted_totals.put(e.getValue(), e.getKey());
    }
    // next, get the winners and losers
    // TODO: this probably needs work to deal properly with ties in 
    // races where only one winner is allowed
    // TODO: this needs to be revised once we save number of winners in addition
    // to votes allowed
    final Iterator<Entry<Integer, String>> iterator = 
        sorted_totals.entrySet().iterator();
    Entry<Integer, String> entry = null;
    int votes = 0;
    while (iterator.hasNext() && my_winners.size() < my_votes_allowed) {
      entry = iterator.next();
      votes = entry.getKey();
      my_winners.add(entry.getValue());
    }
    while (iterator.hasNext()) {
      // all the other choices that have the same number of votes as the last
      // winner count as winners
      entry = iterator.next();
      if (entry.getKey() == votes) {
        my_winners.add(entry.getValue());
      } else {
        my_losers.add(entry.getValue());
      }
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
      for (final String l : my_losers) {
        final int margin = my_vote_totals.get(w) - my_vote_totals.get(l);
        my_min_margin = Math.min(my_min_margin, margin);
        my_max_margin = Math.max(my_max_margin, margin);
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
    return toString().hashCode();
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
