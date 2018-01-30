/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

/**
 * A submitted ballot not found ID.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
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
