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

import java.time.Instant;

/**
 * Submitted election information.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class SubmittedElectionInfo {
  /**
   * The election type.
   */
  private final String my_election_type;
  
  /**
   * The election date (stored as an instant).
   */
  private final Instant my_election_date;
  
  /**
   * Constructs a new SubmittedElectionInfo.
   * 
   * @param the_election_type The election type.
   * @param the_election_date The election date.
   */
  public SubmittedElectionInfo(final String the_election_type,
                               final Instant the_election_date) {
    my_election_type = the_election_type;
    my_election_date = the_election_date;
  }
  
  /**
   * @return the election type.
   */
  public String electionType() {
    return my_election_type;
  }
  
  /**
   * @return the election date (as an instant).
   */
  public Instant electionDate() {
    return my_election_date;
  }
}
