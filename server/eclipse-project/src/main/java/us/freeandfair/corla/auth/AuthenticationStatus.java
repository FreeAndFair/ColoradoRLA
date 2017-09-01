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

package us.freeandfair.corla.auth;

import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The response provided by the server after a successful second factor 
 * authentication which indicates what kind of user just authenticated. This 
 * particular response class is general purpose, encoding generic role used by the 
 * CORLA system.
 * @author Joseph R. Kiniry
 * @author Daniel M. Zimmerman
 */
public class AuthenticationStatus {
  /**
   * The role of the user that just authenticated.
   */
  private final AdministratorType my_role;
  
  /**
   * The authentication stage.
   */
  private final AuthenticationStage my_stage;
  
  /**
   * Create a new response object.
   * @param the_role the role that was just successfully authenticated.
   */
  public AuthenticationStatus(final AdministratorType the_role,
                              final AuthenticationStage the_stage) {
    my_role = the_role;
    my_stage = the_stage;
  }
  
  /**
   * @return the role.
   */
  public AdministratorType role() {
    return my_role;
  }
  
  /**
   * @return the stage.
   */
  public AuthenticationStage stage() {
    return my_stage;
  }
}
