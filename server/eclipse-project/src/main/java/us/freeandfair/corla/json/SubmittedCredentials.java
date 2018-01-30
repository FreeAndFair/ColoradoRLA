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
 * A submitted set of credentials, usually either a (username, password)
 * pair or a (username, second factor) pair.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
public class SubmittedCredentials {
  /**
   * The username.
   */
  private final String my_username;

  /**
   * The password.
   */
  private final String my_password;
  
  /**
   * The two-factor authentication information.
   */
  private final String my_second_factor;
  
  //@ private invariant my_password != null || my_second_factor != null;
  
  /**
   * Constructs a new instance of this class. Note that most two-factor
   * authentication systems preclude the ability to authenticate both factors
   * simultaneously, since that often opens up a replay attack.  Thus, in most
   * use cases, either `the_password` or `the_second_factor` is non-null.
   * 
   * @param the_username The username.
   * @param the_password The password.
   * @param the_second_factor The second factor.
   */
  public SubmittedCredentials(final String the_username,
                              final String the_password,
                              final String the_second_factor) {
    my_username = the_username;
    my_password = the_password;
    my_second_factor = the_second_factor;
  }
  
  /**
   * @return the username.
   */
  public String username() {
    return my_username;
  }
  
  /**
   * @return the password.
   */
  public String password() {
    return my_password;
  }
  
  /**
   * @return the second factor.
   */
  public String secondFactor() {
    return my_second_factor;
  }
}
