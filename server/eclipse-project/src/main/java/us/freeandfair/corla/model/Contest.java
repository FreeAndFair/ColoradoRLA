/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 25, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.Collections;
import java.util.Set;

/**
 * The definition of a contest; comprises a contest name and a set of possible
 * choices.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class Contest {
  /**
   * The contest name.
   */
  private final String my_name;

  /**
   * The contest description.
   */
  private final String my_description;
  
  /**
   * The set of contest choices.
   */
  private final Set<Choice> my_choices;
  
  /**
   * Constructs a contest with the specified parameters.
   * 
   * @param the_name The contest name.
   * @param the_description The contest description.
   * @param the_choices The set of contest choices.
   */
  public Contest(final String the_name, final String the_description, 
                 final Set<Choice> the_choices) {
    super();
    my_name = the_name;
    my_description = the_description;
    my_choices = the_choices;
  }
  
  /**
   * @return the contest name.
   */
  public String name() {
    return my_name;
  }
  
  /**
   * @return the contest description.
   */
  public String description() {
    return my_description;
  }
  
  /**
   * @return the contest choices.
   */
  public Set<Choice> choices() {
    return Collections.unmodifiableSet(my_choices);
  }
}
