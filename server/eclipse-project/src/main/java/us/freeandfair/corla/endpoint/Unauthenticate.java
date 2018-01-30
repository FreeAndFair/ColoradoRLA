/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.AbstractStateMachine;

/**
 * The endpoint for unauthenticating an administrator.
 *  
 * @author Daniel M Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class Unauthenticate extends AbstractEndpoint {
  /**
   * @return no authorization is required for this endpoints.
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
    return "/unauthenticate";
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
   * Unauthenticates the session.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    Main.authentication().deauthenticate(the_request);
    ok(the_response, "Unauthenticated");
    return my_endpoint_result.get();
  }
}
