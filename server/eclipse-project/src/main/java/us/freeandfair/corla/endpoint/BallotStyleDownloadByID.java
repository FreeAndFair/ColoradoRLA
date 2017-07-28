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

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotStyle;

/**
 * The ballot style by ID download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotStyleDownloadByID implements Endpoint {
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
    return "/ballot-style/id/:id";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    String result = null;
    try {
      final BallotStyle b = BallotStyle.byID(Long.parseLong(the_request.params(":id")));
      if (b != null) {
        result = Main.GSON.toJson(b);
      }
    } catch (final NumberFormatException e) {
      // ignore
    }
    if (result == null) {
      the_response.status(HttpStatus.BAD_REQUEST_400);
      result = "Not OK";
    }
    return result;
  }
}
