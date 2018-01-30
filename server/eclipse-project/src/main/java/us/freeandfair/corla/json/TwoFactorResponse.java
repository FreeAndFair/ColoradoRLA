/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response provided by the server during the first phase of two factor
 * authentication to indicate the challenge for the second factor to the user.
 * This particular response class is general purpose, encoding a generic 
 * string challenge used by most two-factor authentication systems. The client
 * UI will simply print the challenge with little adornment.
 * 
 * @trace authentication.two_factor_challenge
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, 
                    justification = "Field is read by Gson.")
public class TwoFactorResponse {
  /**
   * The challenge issues by the two-factor authentication system.
   */
  private final String my_challenge;
  
  /**
   * Create a new response object.
   * @param the_challenge the two-factor challenge.
   */
  public TwoFactorResponse(final String the_challenge) {
    my_challenge = the_challenge;
  }
}
