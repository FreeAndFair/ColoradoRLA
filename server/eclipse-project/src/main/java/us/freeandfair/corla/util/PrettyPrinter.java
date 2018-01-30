/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Oct 2, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

/**
 * A pretty-printer for various data types, for use in reporting.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class PrettyPrinter {
  /**
   * Private constructor to prevent instantiation.
   */
  private PrettyPrinter() {
    // do nothing
  }
  
  /**
   * Pretty-prints a Boolean as "Yes" or "No". 
   * 
   * @param the_boolean The Boolean.
   * @return "Yes" if the_boolean is true, "No" if the_boolean is false.
   */
  public static String booleanYesNo(final boolean the_boolean) {
    final String result;
    
    if (the_boolean) {
      result = "Yes";
    } else {
      result = "No";
    }
    
    return result;
  }
}
