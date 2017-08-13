/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

/**
 * A submitted username and password.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class SubmittedUsernamePassword {
  /**
   * The username.
   */
  private final String my_username;

  /**
   * The password.
   */
  private final String my_password;
  
  /**
   * Constructs a new SubmittedUsernamePassword.
   * 
   * @param the_username The username.
   * @param the_password The password.
   */
  public SubmittedUsernamePassword(final String the_username,
                                   final String the_password) {
    my_username = the_username;
    my_password = the_password;
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
}
