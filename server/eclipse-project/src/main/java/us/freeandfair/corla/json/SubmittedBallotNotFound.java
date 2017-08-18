/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

/**
 * A submitted ballot not found ID.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class SubmittedBallotNotFound {
  /**
   * The id.
   */
  private final Long my_id;
  
  /**
   * Constructs a new SubmittedBallotNotFound.
   * 
   * @param the_id The id.
   */
  public SubmittedBallotNotFound(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * @return the id.
   */
  public Long id() {
    return my_id;
  }
}
