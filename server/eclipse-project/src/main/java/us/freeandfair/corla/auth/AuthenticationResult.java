/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 31, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

import java.io.Serializable;

/**
 * An authentication result from traditional authentication.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class AuthenticationResult {
  /**
   * A flag indicating whether authentication was successful.
   */
  private final boolean my_success;
  
  /**
   * A challenge object that should be sent to the client.
   */
  private final Serializable my_challenge;
  
  /**
   * Constructs a new AuthenticationResult.
   * 
   * @param the_success true if authentication was successful, false otherwise
   * @param the_challenge The challenge to send for second factor authentication.
   */
  public AuthenticationResult(final boolean the_success, final Serializable the_challenge) {
    my_success = the_success;
    my_challenge = the_challenge;
  }
  
  /**
   * @return true if authentication was successful, false otherwise.
   */
  public boolean success() {
    return my_success;
  }
  
  /**
   * @return the second factor challenge, if any.
   */
  public Serializable challenge() {
    return my_challenge;
  }
}
