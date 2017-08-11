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

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent;
import us.freeandfair.corla.json.ServerASMResponse;
import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The endpoint for authenticating a state administrator.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuthenticateStateAdministrator implements Endpoint {
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
    int status = HttpStatus.OK_200;
    String result = "";
    
    if (!Authentication.authenticateAs(the_request, AdministratorType.STATE)) {
      status = HttpStatus.UNAUTHORIZED_401;
      result = "Authentication failed";
    }
    
    // Take the transition triggered by this successful authentication.
    Main.asm().stepEvent(DoSDashboardEvent.AUTHENTICATE_STATE_ADMINISTRATOR_EVENT);

    // Build the ASM server response.
    final ServerASMResponse asm_response =
        new ServerASMResponse(Main.asm().currentState(),
                              Main.asm().enabledUIEvents());
    the_response.body(Main.GSON.toJson(asm_response));
    
    the_response.status(status);
    return result;
  }
}
