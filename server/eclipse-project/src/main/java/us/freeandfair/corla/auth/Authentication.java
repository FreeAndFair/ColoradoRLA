/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

import spark.Request;

import us.freeandfair.corla.json.SubmittedCredentials;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.County;

/**
 * Authentication tasks used by many endpoints.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
public final class Authentication {  
  /**
   * The constant for the "admin" property of the current session.
   */
  public static final String ADMIN = "admin";
  
  /**
   * The constant for the "username" request parameter.
   */
  public static final String USERNAME = "username";
  
  /**
   * The constant for the "password" request parameter.
   */
  public static final String PASSWORD = "password";
  
  /**
   * The constant for the "second factor" request parameter.
   */
  public static final String SECOND_FACTOR = "second_factor";
  
  /**
   * Private constructor to prevent instantiation.
   */
  private Authentication() {
    // do nothing
  }
  
  /**
   * Checks to see whether the specified request is authenticated as
   * the specified administrator type.
   * 
   * @param the_request The request.
   * @param the_type The administrator type.
   * @return true if the request is authenticated, false otherwise.
   */
  public static boolean isAuthenticatedAs(final Request the_request, 
                                          final AdministratorType the_type) {
    assert false;
    return false;
  }
  
  /**
   * Attempts to authenticate using the data in the specified request.
   * 
   * @param the_request The request.
   * @return true if authentication is successful, false otherwise.
   */
  // TODO: eventually this will use the password and second factor in the request
  // to do real authentication; right now, it authenticates anybody whose username
  // actually exists in the database
  public static boolean authenticate(final Request the_request) {
    assert false;
    return false;
  }
  
  /**
   * Attempts to authenticate using a submitted username/password object.
   * 
   * @param the_auth_info The username/password object.
   * @return true if authentication is successful, false otherwise.
   */
  public static boolean authenticate(final Request the_request, 
                                     final SubmittedCredentials the_auth_info) {
    assert false;
    return false;
  }
  
  /**
   * Attempts to authenticate a particular type of administrator using the data
   * in the specified request.
   * 
   * @param the_request The request.
   * @param the_auth_info The username/password object.
   * @param the_type The type of administrator to attempt to authenticate as.
   * @return true if authentication is successful (including that the type of
   * the administrator matches the specified type), false otherwise.
   */
  public static boolean authenticateAs(final Request the_request, 
                                       final AdministratorType the_type) {
    assert false;
    return false;
  }
  
  /**
   * Attempts to authenticate a particular type of administrator using the data
   * in the specified request.
   * 
   * @param the_request The request.
   * @param the_auth_info The username/password object.
   * @param the_type The type of administrator to attempt to authenticate as.
   * @return true if authentication is successful (including that the type of
   * the administrator matches the specified type), false otherwise.
   */
  public static boolean authenticateAs(final Request the_request, 
                                       final SubmittedCredentials the_info,
                                       final AdministratorType the_type) {
    assert false;
    return false;
  }
  
  /**
   * Gets the authenticated county for a request.
   * 
   * @param the_request The request.
   * @return the authenticated county, or null if this session
   * is not authenticated as a county administrator.
   */
  public static County authenticatedCounty(final Request the_request) {
    assert false;
    return null;
  }
  
  /**
   * Gets the authenticated administrator for a request.
   * 
   * @param the_request The request.
   * @return the authenticated administrator, or null if this
   * session is not authenticated.
   */
  public static Administrator authenticatedAdministrator(final Request the_request) {
    assert false;
    return null;
//    return (Administrator) the_request.session().attribute(ADMIN);
  }
  
  /**
   * Unauthenticates the session associated with a request.
   * 
   * @param the_request The request.
   */
  public static void unauthenticate(final Request the_request) {
    assert false;
//    the_request.session().removeAttribute(ADMIN);
//    Main.LOGGER.info("session is now unauthenticated");
  }
}
