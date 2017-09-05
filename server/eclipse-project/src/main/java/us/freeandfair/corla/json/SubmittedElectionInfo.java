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
   * The public meeting date (stored as an instant).
   */
  private final Instant my_public_meeting_date;
  
  /**
   * The random seed.
   */
  private final String my_seed;
  
  /**
   * The risk limit (stored as a string).
   */
  private final String my_risk_limit;
  
  /**
   * Constructs a new SubmittedElectionInfo.
   * 
   * @param the_election_type The election type.
   * @param the_election_date The election date.
   * @param the_public_meeting_date The public meeting date.
   * @param the_seed The random seed.
   * @param the_risk_limit The risk limit (as a string).
   */
  public SubmittedElectionInfo(final String the_election_type,
                               final Instant the_election_date,
                               final Instant the_public_meeting_date,
                               final String the_seed,
                               final String the_risk_limit) {
    my_election_type = the_election_type;
    my_election_date = the_election_date;
    my_public_meeting_date = the_public_meeting_date;
    my_seed = the_seed;
    my_risk_limit = the_risk_limit;
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
  
  /**
   * @return the public meeting date (as an instant).
   */
  public Instant publicMeetingDate() {
    return my_public_meeting_date;
  }
  
  /**
   * @return the random seed.
   */
  public String seed() {
    return my_seed;
  }
  
  /**
   * @return the risk limit (as a string).
   */
  public String riskLimit() {
    return my_risk_limit;
  }
}
