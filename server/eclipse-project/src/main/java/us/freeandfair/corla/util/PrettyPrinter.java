/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Oct 2, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

/**
 * A pretty-printer for various data types, for use in reporting.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
