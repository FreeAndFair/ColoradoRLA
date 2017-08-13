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

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_SKIP_EVENT;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.Administrator.AdministratorType;

/**
 * The endpoint for authenticating a county administrator.
 * 
 * @author Daniel M Zimmerman
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuthenticateCountyAdministrator extends AbstractEndpoint {
  /**
   * @return no authorization is required for this endpoints.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.NONE;
  }
  
  /**
   * @return this endpoint uses the Department of State ASM.
   */
  @Override
  protected Class<CountyDashboardASM> asmClass() {
    return CountyDashboardASM.class;
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
    return "/auth-county-admin";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return COUNTY_SKIP_EVENT;
  }
  
  /**
   * Gets the ASM identity for the specified request.
   * 
   * @param the_request The request.
   * @return the county ID of the authenticated county.
   */
  @Override
  protected String asmIdentity(final Request the_request) {
    return String.valueOf(Authentication.
                          authenticatedCounty(the_request).identifier());
  }

  /**
   * Attempts to authenticate a county administrator; if the authentication is
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
    ok(the_response, "Authenticated");
    if (!Authentication.authenticateAs(the_request, AdministratorType.COUNTY)) {
      unauthorized(the_response, "Authentication failed");
    }
    return my_endpoint_result;
  }
}
