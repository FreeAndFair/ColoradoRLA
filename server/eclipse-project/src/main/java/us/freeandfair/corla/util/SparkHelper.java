/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 4, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import spark.Request;

/**
 * A class of helper methods for use with Spark.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
  public static HttpServletRequest getRawRequest(final Request the_request) 
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
}
