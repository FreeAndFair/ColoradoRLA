/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import org.apache.log4j.Level;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.ServerASMResponse;

/**
 * An endpoint to provide the state of a county dashboard ASM to the client.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CountyDashboardASMState extends AbstractCountyDashboardEndpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/county-asm-state";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Level logLevel() {
    return Level.DEBUG;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    // there's really nothing to do here other than get the ASM state, which we
    // conveniently have locally already
    
    okJSON(the_response, 
           Main.GSON.toJson(new ServerASMResponse(my_asm.get().currentState(), 
                                                  my_asm.get().enabledUIEvents())));
    return my_endpoint_result.get();
  }
}
