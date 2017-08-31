/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 31, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

/**
 * The stages through which authentication passes. Basically a small state machine
 * for two-factor authentication. The authentication subsystem begins in the
 * NO_AUTHENTICATION state, if traditional authenticate succeeds it moves to
 * the TRADITIONALLY_AUTHENTICATED state, and then if second factor authentication
 * succeeds it moves to the SECOND_FACTOR_AUTHENTICATED state.  The current
 * state of the state machine is encoded in the HTTP session's 
 * AuthenticationInterface.AUTH_STAGE attribute.
 * 
 * @author Joseph R. Kiniry
 * @version 0.0.1
 */
public enum AuthenticationStage {
  NO_AUTHENTICATION,
  TRADITIONALLY_AUTHENTICATED,
  SECOND_FACTOR_AUTHENTICATED
}
