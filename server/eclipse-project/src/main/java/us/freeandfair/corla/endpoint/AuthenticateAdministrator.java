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

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.auth.AuthenticationInterface;
import us.freeandfair.corla.json.SubmittedCredentials;
import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The endpoint for authenticating an administrator.
 * 
 * @author Daniel M Zimmerman
 * @author Joseph R. Kiniry
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuthenticateAdministrator extends AbstractEndpoint {
  /**
   * @return no authorization is required for this endpoint.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.NONE;
  }
  
  /**
   * @return this endpoint does not use an ASM.
   */
  @Override
  protected Class<AbstractStateMachine> asmClass() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/auth-admin";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return null;
  }
  
  /**
   * Gets the ASM identity for the specified request.
   * 
   * @param the_request The request.
   * @return the county ID of the authenticated county.
   */
  @Override
  protected String asmIdentity(final Request the_request) {
    return null;
  }

  /**
   * Attempts to authenticate an administrator; if the authentication is
   * successful, authentication data is added to the session.
   * 
   * Session query parameters: <tt>username</tt>, <tt>password</tt>, 
   * <tt>second_factor</tt>
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      // Check for JSON credentials in the request.
      final SubmittedCredentials auth_info = 
          Main.GSON.fromJson(the_request.body(), SubmittedCredentials.class);
      final String username;
      final String password;
      final String second_factor;
      // If there wasn't a JSON request, are there HTTP parameters?
      if (auth_info == null) {
        username = the_request.queryParams(AuthenticationInterface.USERNAME);
        password = the_request.queryParams(AuthenticationInterface.PASSWORD);
        second_factor = the_request.queryParams(AuthenticationInterface.SECOND_FACTOR);
      } else {
        username = auth_info.username();
        password = auth_info.password();
        second_factor = auth_info.secondFactor();
      }
      if (Main.authentication().
          isAuthenticated(the_request, username)) {
        ok(the_response, "Already authenticated");
      } else {
        if (Main.authentication().
            authenticateAdministrator(the_request, the_response,
                                      username, password, second_factor)) {
          ok(the_response, "Authenticated (partial or fully)");
        } else {
          unauthorized(the_response, "Authentication failed");
        }
      }
    } catch (final JsonParseException e) {
      unauthorized(the_response, "Authentication failed");
    }
    return my_endpoint_result.get();
  }
}
