/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The contest by ID endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ContestDownloadByID extends AbstractEndpoint implements Endpoint {
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
    return "/contest/id/:id";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    String result = "";
    try {
      final Contest c = 
          Persistence.getByID(Long.parseLong(the_request.params(":id")),
                              Contest.class);
      if (c != null) {
        result = Main.GSON.toJson(c);
      }
    } catch (final NumberFormatException e) {
      invariantViolation(the_response, "Bad contest ID");
    }
    if (result == null) {
      dataNotFound(the_response, "Contest not found");
    }
    return my_endpoint_result;
  }
}
