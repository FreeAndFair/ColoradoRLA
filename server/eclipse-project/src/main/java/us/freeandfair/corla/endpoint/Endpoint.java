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

/**
 * An interface implemented by all our Spark endpoints.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public interface Endpoint {
  /**
   * The endpoint type for this endpoint.
   */
  enum EndpointType {
    GET, POST, PUT;
  }
  
  /**
   * @return the type of this endpoint.
   */
  EndpointType endpointType();
  
  /**
   * The name of the endpoint implemented by this class.
   * 
   * @return the endpoint name.
   */
  String endpointName();
 
  /**
   * A Spark endpoint.
   *
   * @param the_request The request object.
   * @param the_response The response object.
   * @return the String response.
   */
  String endpoint(Request the_request, Response the_response);
  
  /**
   * The before-filter for this endpoint.
   * 
   * @param the_request The request object.
   * @param the_response The response object.
   */
  void before(Request the_request, Response the_response);
  
  /**
   * The after-filter for this endpoint.
   * 
   * @param the_request The request object.
   * @param the_response The response object.
   */
  void after(Request the_request, Response the_response);
  
  /**
   * @return the required authorization type for this endpoint.
   */
  AuthorizationType requiredAuthorization();
  
  /**
   * The authorization types.
   */
  enum AuthorizationType {
    STATE, COUNTY, EITHER, NONE;
  }
}
