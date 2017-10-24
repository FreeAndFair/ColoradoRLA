/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 4, 2017
 * @copyright 2017 Colorado Department of State
 * @license GNU Affero General Public License v3 with Classpath Exception
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A class of helper methods for dealing with files.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class ExponentialBackoffHelper {
  /**
   * Private constructor to prevent instantiation.
   */
  private ExponentialBackoffHelper() {
    // empty
  }
  
  /**
   * Calculates a delay, in milliseconds, to sleep before retrying a transaction.
   * This is done using a relatively standard exponential backoff and the specified
   * unit delay.
   * 
   * @param the_retries The number of retries so far.
   */
  public static long exponentialBackoff(final int the_retries, final long the_unit_delay) {
    final double exponentiated = Math.pow(2,  the_retries);
    long multiplier = 1;
    
    if (Double.isNaN(exponentiated)) {
      multiplier = 1;
    } else {
      final long max_delay_factor = Math.max(1, Math.round(exponentiated));
      multiplier = ThreadLocalRandom.current().nextLong(max_delay_factor) + 1;
    }
    
    return multiplier * the_unit_delay;
  }
}