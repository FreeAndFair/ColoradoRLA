/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.County;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public interface AuthenticationInterface {
  /**
   * The constant for the "admin" property of the current session.
   */
  String ADMIN = "admin";
  
  /**
   * The constant for the "username" request parameter.
   */
  String USERNAME = "username";
  
  /**
   * The constant for the "password" request parameter.
   */
  String PASSWORD = "password";
  
  /**
   * The constant for the "second factor" request parameter.
   */
  String SECOND_FACTOR = "second_factor";
  
  /**
   * The constant used to denote which authentication stage the session is in. 
   * The value of this attribute in the session is either `PASSWORD` (indicating
   * that password authentication is next) or `SECOND_FACTOR` (indicating that
   * second factor authentication is next) or `ADMIN` (indicating that session
   * is authenticated).
   */
  String AUTH_STAGE = "authentication_stage";
  
  /**
   * Set the logger for the authentication subsystem.  This method should
   * be called immediately after construction and before the subsystem is used.
   * @param the_logger the logger to use.
   */
  void setLogger(Logger the_logger);

  /**
   * Set the GSON serialization/deserialization subsystem to use. This method
   * should be called immediately after construction and before the subsystem
   * is used.
   * @param the_gson the GSON subsystem to use.
   */
  void setGSON(Gson the_gson);

  /**
   * Set the DNS name of the authentication server to use for this particular
   * authentication service. 
   * @param the_name the full DNS name of the authentication server.
   */
  void setAuthenticationServerName(String the_name);
  
  /**
   * Authenticate the administrator `the_username` with credentials
   * `the_password` (for traditional authentication) or `the_second_factor`
   * (for two-factor authentication).
   * @trace authentication.authenticate_county_administrator
   * @trace authentication.authenticate_state_administrator
   * @return true iff authentication succeeds.
   * @param the_request The request.
   * @param the_response The response, which is used in the case that a second
   * factor challenge must be sent to the client.
   * @param the_admin_type the type of administrator to attempt to authenticate.
   * @param the_username the username of the person to attempt to authenticate.
   * @param the_password the password for `username`.
   * @param the_second_factor the second factor for `username`.
   */
  //@ requires 0 < the_username.length();
  //@ requires the_password != null || the_second_factor != null;
  boolean authenticateAdministrator(Request the_request,
                                    Response the_response,
                                    AdministratorType the_admin_type,
                                    String the_username,
                                    String the_password,
                                    String the_second_factor);
  
  /**
   * Logout `the_username`.
   * @trace authentication.logout_county_administrator
   * @trace authentication.logout_state_administrator
   * @param the_request The request.
   * @param the_username the user to de-authenticate.
   */
  //@ ensures (* If `the_username` was logged in, now they are not. *);
  void logoutAdministrator(Request the_request,
                           String the_username);

  /**
   * Attempt to authenticate `the_username` using `the_second_factor`.
   * @trace authentication.second_factor_authenticate
   * @return true iff two-factor authentication with credential pair 
   * (username, password) succeeds.
   * @param the_username the username of the person to attempt to authenticate.
   * @param the_request The request.
   * @param the_second_factor the second factor for `username`.
   */
  //@ requires 0 < the_username.length();
  //@ requires the_second_factor != null;
  boolean secondFactorAuthenticate(Request the_request,
                                   String the_username,
                                   String the_second_factor);

  /**
   * Is `the_username` authenticated with a second factor?
   * @trace authenticated.second_factor_authenticated?
   * @param the_request The request.
   * @return true iff `the_username` is second-factor authenticated.
   */
  boolean secondFactorAuthenticated(Request the_request,
                                    String the_username);

  /**
   * @trace authentication.traditional_authenticate
   * @return true iff traditional authentication with credential pair 
   * (username, password) succeeds.
   * @param the_request The request.
   * @param the_response The response, which is used in the case that a second
   * factor challenge must be sent to the client.
   * @param the_username the username of the person to attempt to authenticate.
   * @param the_password the password for `username`.
   */
  //@ requires 0 < the_username.length();
  //@ requires the_password != null;
  boolean traditionalAuthenticate(Request the_request,
                                  Response the_response,
                                  String the_username, 
                                  String the_password);

  /**
   * @trace authentication.traditional_authenticated?
   * @return true iff `the_username` is traditionally authenticated.
   * @param the_username the username of the person to check.
   * @param the_request The request.
   */
  boolean traditionalAuthenticated(Request the_request,
                                   String the_username);
  
  /**
   * @return true iff `the_username` is authenticated with both traditional and
   * two-factor authentication as an administrator of type `the_type`.
   * @param the_request The request.
   * @param the_username the username of the person to check.
   * @param the_type the type of the administrator.
   */
  boolean isAuthenticatedAs(Request the_request,
                            AdministratorType the_type,
                            String the_username);
     
  /**
   * Deauthenticate the currently authenticated user from all systems. 
   * @return true iff `the_username` is deauthenticated.
   * @param the_request The request.
   * @param the_request the request associated with the deauthentication.
   */
  void deauthenticate(Request the_request,
                      String the_username);
      
  /**
   * @trace authentication.traditional_deauthenticate
   * @param the_request The request.
   * @param the_username the user to deauthenticate.
   */
  //@ ensures (* If `the_username` was logged in via traditional authentication, 
  //@            now they are not. *);
  void traditionalDeauthenticate(Request the_request,
                                 String the_username);

  /**
   * @trace authentication.two_factor_deauthenticate
   * @param the_request The request.
   * @param the_username the user to deauthenticate.
   */
  //@ ensures (* If `the_username` was logged in via two-factor authentication, 
  //@            now they are not. *);
  void twoFactorDeauthenticate(Request the_request,
                               String the_username);
  
  /**
   * Gets the authenticated county for a request.
   * 
   * @param the_request The request.
   * @return the authenticated county, or null if this session is not authenticated 
   * as a county administrator.
   */
  County authenticatedCounty(Request the_request);
}
