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

package us.freeandfair.corla.endpoint;

import javax.persistence.PersistenceException;

import spark.Request;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.SubmittedUsernamePassword;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AdministratorQueries;

/**
 * Authentication tasks used by many endpoints.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
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
    boolean result = false;
    final Object admin_attribute = the_request.session().attribute(ADMIN);
    
    if (admin_attribute instanceof Administrator) {
      final Administrator admin = (Administrator) admin_attribute;
      result = admin.type() == the_type;
      the_request.session().attribute(ADMIN, admin);
    } else if (admin_attribute != null) {
      // this should never happen since we control what's in the session object,
      // but if it does, we'll clear out that attribute and thereby force another
      // authentication
      Main.LOGGER.error("Invalid admin type detected in session.");
      unauthenticate(the_request);
    }
    
    return result;
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
    final String username_param = the_request.queryParams(USERNAME);
    final String password_param = the_request.queryParams(PASSWORD);
    return authenticate(the_request,
                        new SubmittedUsernamePassword(username_param, 
                                                      password_param));
  }
  
  /**
   * Attempts to authenticate using a submitted username/password object.
   * 
   * @param the_auth_info The username/password object.
   * @return true if authentication is successful, false otherwise.
   */
  public static boolean authenticate(final Request the_request, 
                                     final SubmittedUsernamePassword the_auth_info) {
    boolean result = false;
    if (the_auth_info.username() != null && 
        the_auth_info.username().trim().length() > 0 &&
        the_auth_info.password() != null) {
      try {
        final Administrator admin = 
            AdministratorQueries.byUsername(the_auth_info.username());
        if (admin != null) { // TODO: password check!
          admin.updateLastLoginTime();
          Persistence.saveOrUpdate(admin);
          result = true;
          the_request.session().attribute(ADMIN, admin);
          Main.LOGGER.info("Authentication succeeded for user " + 
                           the_auth_info.username() + 
                           " of type " + admin.type());
        } 
      } catch (final PersistenceException e) {
        // there's nothing we can really do here other than saying that the
        // authentication failed; it's also possible we failed to update the last
        // login time, but that's not critical
        unauthenticate(the_request);
      }
    } else {
      Main.LOGGER.info("invalid username or password specified");
    }
    
    if (!result) {
      // a failed authentication attempt removes any existing session authentication 
      unauthenticate(the_request);
      Main.LOGGER.info("Authentication failed for user " + the_auth_info.username());
    }
    
    return result;
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
    final String username_param = the_request.queryParams(USERNAME);
    final String password_param = the_request.queryParams(PASSWORD);
    return authenticateAs(the_request,
                          new SubmittedUsernamePassword(username_param, 
                                                        password_param),
                          the_type);
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
                                       final SubmittedUsernamePassword the_info,
                                       final AdministratorType the_type) {
    boolean result = authenticate(the_request, the_info);
    
    if (result) {
      final Object admin_attribute = the_request.session().attribute(ADMIN);
      if (admin_attribute instanceof Administrator) {
        final Administrator admin = (Administrator) admin_attribute;
        if (admin.type() != the_type) {
          // remove the session authentication
          result = false;
          Main.LOGGER.info("User " + admin.username() + " was not of expected " +
                           "type " + the_type);
          unauthenticate(the_request);
        }
      }
    }
    
    return result;
  }
  
  /**
   * Gets the authenticated county for a request.
   * 
   * @param the_request The request.
   * @return the authenticated county, or null if this session
   * is not authenticated as a county administrator.
   */
  public static County authenticatedCounty(final Request the_request) {
    County result = null;
    
    if (isAuthenticatedAs(the_request, AdministratorType.COUNTY)) {
      final Administrator admin = authenticatedAdministrator(the_request);
      if (admin != null) {
        result = admin.county();
      }
    }
    
    return result;
  }
  
  /**
   * Gets the authenticated administrator for a request.
   * 
   * @param the_request The request.
   * @return the authenticated administrator, or null if this
   * session is not authenticated.
   */
  public static Administrator authenticatedAdministrator(final Request the_request) {
    return (Administrator) the_request.session().attribute(ADMIN);
  }
  
  /**
   * Unauthenticates the session associated with a request.
   * 
   * @param the_request The request.
   */
  public static void unauthenticate(final Request the_request) {
    the_request.session().removeAttribute(ADMIN);
    Main.LOGGER.info("session is now unauthenticated");
  }
}
