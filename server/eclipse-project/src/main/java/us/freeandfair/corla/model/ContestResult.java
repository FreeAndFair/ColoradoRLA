package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.StringSetConverter;

/**
 * A class representing the results for a contest across counties.
 * A roll-up  of CountyContestResults
 */
@Entity
@Cacheable(true)
@Table(name = "contest_result",
       uniqueConstraints = {
         @UniqueConstraint(columnNames = {"contest_name"}) },
       indexes = { @Index(name = "idx_cr_contest",
                          columnList = "contest_name",
                          unique = true)})
public class ContestResult implements PersistentEntity, Serializable {

  /**
   * The "id" string.
   */
  private static final String ID = "id";

  /**
   * The "result_id" string.
   */
  private static final String RESULT_ID = "result_id";

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long version;

  /**
   * The set of contest winners.
   */
  @Column(name = "winners", columnDefinition = "text")
  @Convert(converter = StringSetConverter.class)
  private final Set<String> winners = new HashSet<>();

  /**
   * The set of contest losers.
   */
  @Column(name = "losers", columnDefinition = "text")
  @Convert(converter = StringSetConverter.class)
  private final Set<String> losers = new HashSet<>();

  /**
   * A map from choices to vote totals.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "contest_vote_total",
                   joinColumns = @JoinColumn(name = RESULT_ID,
                                             referencedColumnName = ID))
  @MapKeyColumn(name = "choice")
  @Column(name = "vote_total")
  private  Map<String, Integer> vote_totals = new HashMap<>();

  /**
   * A ContestResult has many counties - supporting auditing multi-county
   * contests. Counties have many ContestResults (and many Contests)
   **/
  @ManyToMany()
  @JoinTable(name = "counties_to_contest_results",
             joinColumns = { @JoinColumn(name = "contest_result_id") },
             inverseJoinColumns = { @JoinColumn(name = "county_id") })
  private final Set<County> counties = new HashSet<>();


  /**
   *  A ContestResult has many Contests through "contests_to_contest_results"
   *  Contests are many in the db because each county has their own, just 'cause
   */
  @OneToMany()
  @JoinTable(name = "contests_to_contest_results",
             joinColumns = { @JoinColumn(name = "contest_result_id") },
             inverseJoinColumns = { @JoinColumn(name = "contest_id") })
  private final Set<Contest> contests = new HashSet<>();

  /**
   * The contest name.
   */
  @Column(name = "contest_name", nullable = false)
  private String contestName;

  /**
   * Constructs a new empty ContestResult (solely for persistence).
   */
  public ContestResult() {
    super();
  }

  /**
   * Constructs a new ContestResult for the specified county. The countyName is
   * what links Contests together as well as ContestResults.
   *
   * @param the_contest The contest.
   */
  public ContestResult(final String contestName) {
    super();
    this.contestName = contestName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    this.id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return version;
  }

  /**
   * @return the contest name.
   */
  public String getContestName() {
    return this.contestName;
  }

  /**
   * @return the counties related to this contestresult.
   */
  public Set<County> getCounties() {
    return Collections.unmodifiableSet(this.counties);
  }

  /**
   * @return the contests related to this ContestResult
   * (should be contests that have the same name as this.contestName)
   **/
  public Set<Contest> getContests() {
    return Collections.unmodifiableSet(this.contests);
  }

  /**
   * @return the winners for thie ContestResult.
   */
  public Set<String> getWinners() {
    return Collections.unmodifiableSet(this.winners);
  }

  /**
   * @return the losers for this ContestResult.
   */
  public Set<String> getLosers() {
    return Collections.unmodifiableSet(this.losers);
  }

  /**
   * @return a map from choices to vote totals.
   */
  public Map<String, Integer> getVoteTotals() {
    return Collections.unmodifiableMap(this.vote_totals);
  }

  /**
   * @param voteTotals a map from choices to vote totals.
   */
  public void setVoteTotals(final Map<String, Integer> voteTotals) {
    this.vote_totals = voteTotals;
  }

  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "ContestResult [id=" + id() + " contestName=" + getContestName() + "]";
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
    if (the_other instanceof ContestResult) {
      final ContestResult other_result = (ContestResult) the_other;
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
}
