/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 4, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.Request;
import spark.Response;

/**
 * A class of helper methods for use with Spark.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class SparkHelper {
  /**
   * Private constructor to prevent instantiation.
   */
  private SparkHelper() {
    // empty
  }
  
  /**
   * Gets the unwrapped raw request from a Spark request, if available;
   * otherwise, gets the output of the_request.raw().
   * This is used primarily to circumvent Spark's caching mechanism
   * to handle large file uploads.
   * 
   * @param the_request The request.
   * @return the raw request, unwrapped if possible.
   */
  public static HttpServletRequest getRaw(final Request the_request) 
      throws IOException {
    HttpServletRequest raw = the_request.raw();
    
    if (raw instanceof ServletRequestWrapper) {
      final ServletRequest sr = ((ServletRequestWrapper) raw).getRequest();
      if (sr instanceof HttpServletRequest) {
        raw = (HttpServletRequest) sr;
      }
    }

    return raw;
  }
  
  /**
   * Gets the unwrapped raw response from a Spark response, if available;
   * otherwise, gets the output of the_response.raw().
   * This is used primarily to circumvent Spark's caching mechanism
   * to handle large file downloads.
   * 
   * @param the_response The response.
   * @return the raw response, unwrapped if possible.
   */
  public static HttpServletResponse getRaw(final Response the_response) 
      throws IOException {
    HttpServletResponse raw = the_response.raw();
    
    if (raw instanceof ServletResponseWrapper) {
      final ServletResponse sr = ((ServletResponseWrapper) raw).getResponse();
      if (sr instanceof HttpServletResponse) {
        raw = (HttpServletResponse) sr;
      }
    }

    return raw;
  }
}
