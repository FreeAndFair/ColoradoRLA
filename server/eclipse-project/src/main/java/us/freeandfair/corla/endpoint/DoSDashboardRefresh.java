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

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.DOS_REFRESH_EVENT;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.json.DoSDashboardRefreshResponse;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for refreshing the Department of State dashboard status.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
// endpoints don't need constructors
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class DoSDashboardRefresh extends AbstractDoSDashboardEndpoint {
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
    return "/dos-dashboard";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return DOS_REFRESH_EVENT;
  }
  
  /**
   * Provides information about the DoS dashboard.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final String refresh = 
          Main.GSON.toJson(DoSDashboardRefreshResponse.createResponse
                           (DepartmentOfStateDashboardQueries.get()));
      ok(the_response, refresh);
    } catch (final PersistenceException e) {
      serverError(the_response, "could not obtain dashboard state");
    }
    return my_endpoint_result;
  }

  /**
   * This endpoint requires STATE authorization.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }
}
