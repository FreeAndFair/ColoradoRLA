/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 15, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * A filter to enable CORS in our Spark endpoints. This is used as a Spark
 * "afterAfterFilter".
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
public class CORSFilter implements Filter {
  /**
   * The methods property name.
   */
  public static final String METHODS_PROPERTY = "cors.methods";
  
  /**
   * The default methods.
   */
  public static final String DEFAULT_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
  
  /**
   * The origin property name.
   */
  public static final String ORIGIN_PROPERTY = "cors.origin";
  
  /**
   * The default origin.
   */
  public static final String DEFAULT_ORIGIN = "http://localhost:3000";
  
  /**
   * The headers property name.
   */
  public static final String HEADERS_PROPERTY = "cors.headers";
  
  /**
   * The default headers.
   */
  public static final String DEFAULT_HEADERS = 
      "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,";
  
  /**
   * The credentials property name.
   */
  public static final String CREDENTIALS_PROPERTY = "cors.credentials";
  
  /**
   * The default credentials.
   */
  public static final String DEFAULT_CREDENTIALS = "true";
  
  /**
   * The CORS headers for this filter.
   */
  private final Map<String, String> my_cors_headers;
  
  /**
   * Constructs a new CORSFilter with the specified properties.
   * 
   * @param the_properties The properties.
   */
  public CORSFilter(final Properties the_properties) {
    my_cors_headers = corsHeaders(the_properties);
  }
  
  /**
   * Gets CORS headers from a set of properties.
   * 
   * @param the_properties The properties.
   */
  private Map<String, String> corsHeaders(final Properties the_properties) {
    final Map<String, String> result = new HashMap<>();
    
    result.put("Access-Control-Allow-Methods",
               the_properties.getProperty(METHODS_PROPERTY, DEFAULT_METHODS));
    result.put("Access-Control-Allow-Origin", 
               the_properties.getProperty(ORIGIN_PROPERTY, DEFAULT_ORIGIN));
    result.put("Access-Control-Allow-Headers", 
               the_properties.getProperty(HEADERS_PROPERTY, DEFAULT_HEADERS));
    result.put("Access-Control-Allow-Credentials",
               the_properties.getProperty(CREDENTIALS_PROPERTY, DEFAULT_CREDENTIALS));
    
    return result;
  }
  
  /**
   * Handles a request.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  public void handle(final Request the_request, final Response the_response) {
    my_cors_headers.forEach((the_key, the_value) -> { 
      the_response.header(the_key, the_value); 
    });
  }
}
