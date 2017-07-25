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

import java.util.Set;

/**
 * A vote for a single contest.
 * 
 * @author Joey Dodds
 * @version 0.0.1
 */

public class Vote {
  /**
   * A set of zero or more choices made in a single contest
   */
  private final Set<String> my_choices;

  /**
   * @param
   * @return the my_choices
   */
  public Set<String> getMy_choices() {
    assert false;
    //@ assert false;
    return my_choices;
  }

  /**
   * <description> <explanation>
   * 
   * @param
   */
  public Vote(Set<String> the_choices) {
    super();
    my_choices = the_choices;
  }

  /**
   * 
   * @param contest the contest to evaluate this vote in
   * @return true if and only if this is a well formed vote in the provided
   *         contest
   */
  public boolean isWellFormedVoteInContest(Contest contest) {
    return false;
  }

  /**
   * 
   * @param contest the contest to evaluate this vote in
   * @return true if and only if this is an overvote in the provided contest
   */
  public boolean isOvervoteInContest(Contest contest) {
    return false;
  }

  /**
   * 
   * @param contest the contest to evaluate this vote in
   * @return true if and only if this is an undervote in the provided contest
   */
  public boolean isUndervoteInContest(Contest contest) {
    return false;
  }

}
