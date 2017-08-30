/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.json.SubmittedUsernamePassword;
import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The endpoint for authenticating a state administrator.
 * 
 * @author Daniel M Zimmerman
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuthenticateStateAdministrator extends AbstractEndpoint {
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
  protected Class<DoSDashboardASM> asmClass() {
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
    return "/auth-state-admin";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return null;
  }
  
  /**
   * @param the_request the ignored request.
   * @return null because the DoS dashboard is a singleton.
   */
  @Override
  // this method is definitely not empty, but PMD thinks it is
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
  protected String asmIdentity(final Request the_request) {
    return null;
  }

  /**
   * Attempts to authenticate a state administrator; if the authentication is
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
    if (Authentication.authenticateAs(the_request, AdministratorType.STATE)) {
      ok(the_response, "Authenticated");
    } else {
      try {
        final SubmittedUsernamePassword auth_info = 
            Main.GSON.fromJson(the_request.body(), SubmittedUsernamePassword.class);
        if (auth_info != null &&
            Authentication.authenticateAs(the_request, auth_info, 
                                          AdministratorType.STATE)) {
          ok(the_response, "Authenticated");
        } else {
          unauthorized(the_response, "Authentication failed");
        }
      } catch (final JsonParseException e) {
        unauthorized(the_response, "Authentication failed");
      }
    }
    return my_endpoint_result.get();
  }
}
