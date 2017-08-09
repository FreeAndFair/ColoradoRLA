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
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.AdministratorQueries;

/**
 * Authentication tasks used by many endpoints.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class Authentication {  
  /**
   * The constant for the "user ID" property of the current session.
   */
  public static final String USER_ID = "user_id";
  
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
  public boolean isAuthenticated(final Request the_request, 
                                 final AdministratorType the_type) {
    boolean result = false;
    final Object user_id_attribute = the_request.session().attribute(USER_ID);
    
    if (user_id_attribute != null) {
      try {
        final Long user_id = (Long) user_id_attribute;
        final Administrator admin = Persistence.getByID(user_id, Administrator.class);
        if (admin != null) {
          result = admin.type() == the_type;
        }
      } catch (final ClassCastException e) {
        // this should never happen since we control what's in the session object,
        // but if it does, we'll clear out that attribute and thereby force another
        // authentication
        the_request.session().removeAttribute(USER_ID);
        Main.LOGGER.error("Invalid user ID type detected in session.");
      } catch (final PersistenceException e) {
        // there's nothing we can do about this except report that the request
        // is unauthenticated
      }
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
  public boolean authenticate(final Request the_request) {
    boolean result = false;
    final String username_param = the_request.queryParams(USERNAME);
    final String password_param = the_request.queryParams(PASSWORD);
    final String second_factor_param = the_request.queryParams(SECOND_FACTOR);
    
    if (username_param != null && password_param != null && second_factor_param != null) {
      final Administrator admin = AdministratorQueries.byUsername(username_param);
      if (admin != null) {
        result = true;
        the_request.session().attribute(USER_ID, admin.id());
      }
    }
    
    return result;
  }
}
