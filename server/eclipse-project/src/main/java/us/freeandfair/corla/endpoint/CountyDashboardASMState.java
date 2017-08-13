/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.json.ServerASMResponse;

/**
 * An endpoint to provide the state of a county dashboard ASM to the client.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
  public String endpoint(final Request the_request, final Response the_response) {
    // there's really nothing to do here other than get the ASM state, which we
    // conveniently have locally already
    
    okJSON(the_response, 
           Main.GSON.toJson(new ServerASMResponse(my_asm.currentState(), 
                                                  my_asm.enabledUIEvents())));
    return my_endpoint_result;
  }

  /**
   * {@inheritDoc}
   */
  /*@ pure @*/
  @Override
  protected ASMEvent endpointEvent() {
    return CountyDashboardEvent.COUNTY_REFRESH_EVENT;
  }

}
