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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The contest download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ContestDownload extends AbstractCountyDashboardEndpoint {
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
    return "/contest";
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
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      
      Main.GSON.toJson(Persistence.getAll(Contest.class), bw);
      bw.flush();
      ok(the_response);
    } catch (final IOException e) {
      serverError(the_response, "Unable to stream response");
    }
    return my_endpoint_result;
  }
}
