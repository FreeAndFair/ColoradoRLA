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

import java.util.List;

import us.freeandfair.corla.util.Pair;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class Contest {
  private final List<Pair<String, String>> my_names_descriptions;

  /**
   * <description> <explanation>
   * 
   * @param
   * @return the names_descriptions
   */
  public List<Pair<String, String>> get_names_descriptions() {
    assert false;
    // @ assert false;
    return my_names_descriptions;
  }

  /**
   * <description> <explanation>
   * 
   * @param A list of pairs of first the names, and then the descriptions of the
   *          contest
   */
  public Contest(List<Pair<String, String>> the_names_descriptions) {
    super();
    this.my_names_descriptions = the_names_descriptions;
  }
}
