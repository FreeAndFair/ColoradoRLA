/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 15, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
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
 * @version 1.0.0
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
   * The filter to run after this one, if any.
   */
  private final Filter my_filter;
  
  /**
   * Constructs a new CORSFilter with the specified properties.
   * 
   * @param the_properties The properties.
   * @param the_filter The filter to run after this one, if any.
   */
  public CORSFilter(final Properties the_properties, final Filter the_filter) {
    my_cors_headers = corsHeaders(the_properties);
    my_filter = the_filter;
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
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  public void handle(final Request the_request, final Response the_response) 
      throws Exception {
    my_cors_headers.forEach((the_key, the_value) -> { 
      the_response.header(the_key, the_value); 
    });
    my_filter.handle(the_request, the_response);
  }
}
