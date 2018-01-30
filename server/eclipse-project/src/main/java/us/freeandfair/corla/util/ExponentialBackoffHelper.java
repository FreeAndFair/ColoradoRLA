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

import java.util.Random;

/**
 * A class of helper methods for dealing with files.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class ExponentialBackoffHelper {
  /**
   * The random number generator.
   */
  private static final Random RANDOM = new Random();
  
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
      final int max_delay_factor = 
          (int) Math.max(1, Math.min(Integer.MAX_VALUE, Math.round(exponentiated)));
      multiplier = RANDOM.nextInt(max_delay_factor) + 1;
    }
    
    return multiplier * the_unit_delay;
  }
}
