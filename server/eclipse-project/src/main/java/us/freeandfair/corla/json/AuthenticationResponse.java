/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The response provided by the server after a successful second factor 
 * authentication which indicates what kind of user just authenticated. This 
 * particular response class is general purpose, encoding generic role used by the 
 * CORLA system.
 * @author Joseph R. Kiniry
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, 
                    justification = "Field is read by Gson.")
public class AuthenticationResponse {
  /**
   * The role of the user that just authenticated.
   */
  private final AdministratorType my_role;
  
  /**
   * Create a new response object.
   * @param the_role the role that was just successfully authenticated.
   */
  public AuthenticationResponse(final AdministratorType the_role) {
    my_role = the_role;
  }
}
