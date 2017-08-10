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
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
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
      the_request.session().removeAttribute(ADMIN);
      Main.LOGGER.error("Invalid admin type detected in session.");
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
    boolean result = false;
    final String username_param = the_request.queryParams(USERNAME);
    final String password_param = the_request.queryParams(PASSWORD);
    final String second_factor_param = the_request.queryParams(SECOND_FACTOR);
    
    if (username_param != null && password_param != null && second_factor_param != null) {
      try {
        final Administrator admin = AdministratorQueries.byUsername(username_param);
        if (admin != null) {
          admin.updateLastLoginTime();
          Persistence.saveOrUpdate(admin);
          result = true;
          the_request.session().attribute(ADMIN, admin);
          Main.LOGGER.info("Authentication succeeded for user " + username_param + 
                           " of type " + admin.type());
        }
      } catch (final PersistenceException e) {
        // there's nothing we can really do here other than saying that the
        // authentication failed; it's also possible we failed to update the last
        // login time, but that's not critical
      }
    }
    
    if (!result) {
      // a failed authentication attempt removes any existing session authentication 
      the_request.session().removeAttribute(ADMIN);
      Main.LOGGER.info("Authentication failed for user " + username_param);
    }
    
    return result;
  }
  
  /**
   * Attempts to authenticate a particular type of administrator using the data
   * in the specified request.
   * 
   * @param the_request The request.
   * @param the_type The type of administrator to attempt to authenticate as.
   * @return true if authentication is successful (including that the type of
   * the administrator matches the specified type), false otherwise.
   */
  public static boolean authenticateAs(final Request the_request, 
                                       final AdministratorType the_type) {
    boolean result = authenticate(the_request);
    
    if (result) {
      final Object admin_attribute = the_request.session().attribute(ADMIN);
      if (admin_attribute instanceof Administrator) {
        final Administrator admin = (Administrator) admin_attribute;
        if (admin.type() != the_type) {
          // remove the session authentication
          result = false;
          the_request.session().removeAttribute(ADMIN);
          Main.LOGGER.info("User " + admin.username() + " was not of expected " +
                           "type " + the_type);
        }
      }
    }
    
    return result;
  }
}
