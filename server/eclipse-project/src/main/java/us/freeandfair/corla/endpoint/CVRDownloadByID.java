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

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_SKIP_EVENT;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The CVR by ID download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CVRDownloadByID extends AbstractCountyDashboardEndpoint {
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
    return "/cvr/id/:id";
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return COUNTY_SKIP_EVENT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    String result = "";
    try {
      final CastVoteRecord c = 
          Persistence.getByID(Long.parseLong(the_request.params(":id")),
                              CastVoteRecord.class);
      if (c != null) {
        result = Main.GSON.toJson(c);
      }
    } catch (final NumberFormatException e) {
      invariantViolation(the_response, "Bad CVR ID");
    }
    if (result == null) {
      dataNotFound(the_response, "CVR not found");
    }
    return my_endpoint_result;
  }
}
