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

import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;

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
   */
  public boolean isAuthenticated(final Request the_request, 
                                 final AdministratorType the_type) {
    boolean result = false;
    
    if (the_request.session().attribute(USER_ID) != null) {
      try {
        final Long user_id = (Long) the_request.session().attribute(USER_ID);
        final Administrator admin = Persistence.getByID(user_id, Administrator.class);
        if (admin != null) {
          result = admin.type() == the_type;
        }
      } catch (final ClassCastException | PersistenceException e) {
        // there's nothing we can do about this except fail the authentication
      }
    }
    
    return result;
  }
}
