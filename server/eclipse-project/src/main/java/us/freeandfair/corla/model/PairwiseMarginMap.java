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
import java.util.Map;
import java.util.OptionalInt;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * A class representing, for a particular county and contest, a map
 * from a choice to the pairwise margins of that choice with all other
 * possible choices.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "pairwise_margin")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class PairwiseMarginMap extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The county to which this contest result set belongs. 
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private CountyContestResult my_result;
  
  /**
   * A map from other choices to margins.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "pairwise_margin_choice_margin",
                   joinColumns = @JoinColumn(name = "pairwise_margin_id", 
                                             referencedColumnName = "my_id"))
  @MapKeyColumn(name = "choice")
  @Column(name = "margin")
  private Map<String, Integer> my_margins;
  
  /**
   * Constructs a new empty PairwiseMarginMap.
   */
  public PairwiseMarginMap() {
    super();
  }
  
  /**
   * Constructs a new empty PairwiseMarginMap for the specified county 
   * contest result.
   * 
   * @param the_result The result.
   */
  public PairwiseMarginMap(final CountyContestResult the_result) {
    super();
    my_result = the_result;
  }
  
  /**
   * Sets the county contest result to which this map belongs.
   * 
   * @param the_result The county contest result.
   */
  public void setCountyContestResult(final CountyContestResult the_result) {
    my_result = the_result;
  }
  
  /**
   * @return the county contest result to which this map belongs.
   */
  public CountyContestResult result() {
    return my_result;
  }
  
  /**
   * Sets the margin for a particular choice.
   * 
   * @param the_choice The choice.
   * @param the_margin The margin.
   */
  public void setMargin(final String the_choice, final Integer the_margin) {
    my_margins.put(the_choice, the_margin);
  }
  
  /**
   * Gets the margin for a particular choice. A negative margin means that
   * the choice specified here has more votes than the "key" choice, and
   * a positive margin means the opposite.
   * 
   * @param the_choice The choice.
   * @return the margin for the choice.
   */
  public OptionalInt margin(final String the_choice) {
    final OptionalInt result;
    final Integer margin = my_margins.get(the_choice);
    
    if (margin == null) {
      result = OptionalInt.empty();
    } else {
      result = OptionalInt.of(margin);
    }

    return result;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "PairwiseMarginMap [id=" + id() + "]";
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
    if (the_other instanceof PairwiseMarginMap) {
      final PairwiseMarginMap other_map = (PairwiseMarginMap) the_other;
      // compare by database ID, since that is the only
      // context in which they can reasonably be compared
      result &= nullableEquals(other_map.id(), id());
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
}
