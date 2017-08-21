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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The contest by county download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ContestDownloadByCounty extends AbstractEndpoint {
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
    return "/contest/county";
  }

  /**
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    if (validateParameters(the_request)) {
      final Set<Long> county_set = new HashSet<Long>();
      for (final String s : the_request.queryParams()) {
        county_set.add(Long.valueOf(s));
      }
      final Set<Contest> contest_set = 
          ContestQueries.forCounties(county_set);
      if (contest_set == null) {
        serverError(the_response, "Error retrieving records from database");
      } else {
        try {
          final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
          final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

          Main.GSON.toJson(contest_set, bw);
          bw.flush();
          ok(the_response);
        } catch (final IOException e) {
          serverError(the_response, "Unable to stream response");
        }
      }
    } else {
      dataNotFound(the_response, "Invalid county ID specified");
    }
    return my_endpoint_result;
  }
  
  /**
   * Validates the parameters of a request. For this endpoint, 
   * the parameter names must all be integers.
   * 
   * @param the_request The request.
   * @return true if the parameters are valid, false otherwise.
   */
  protected boolean validateParameters(final Request the_request) {
    boolean result = true;
    
    for (final String s : the_request.queryParams()) {
      try {
        Integer.parseInt(s);
      } catch (final NumberFormatException e) {
        result = false;
        break;
      }
    }
    
    return result;
  }
}
