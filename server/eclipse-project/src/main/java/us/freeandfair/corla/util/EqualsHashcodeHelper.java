/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 1, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

/**
 * A utility class with useful methods for building equals and hashCode
 * methods.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class EqualsHashcodeHelper {
  /**
   * Private constructor to prevent instantiation.
   */
  private EqualsHashcodeHelper() {
    // empty
  }
  
  /**
   * Compares two objects, which can be null, for equivalence. The
   * correctness of this method depends on the correctness of the
   * objects' equals() methods.
   * 
   * @param the_first The first object.
   * @param the_second The second object.
   * @return true if the objects (or nulls) are equivalent, false otherwise.
   */
  public static boolean nullableEquals(final Object the_first, 
                                       final Object the_second) {
    boolean result = false;
    if (the_first == null) {
      result = the_second == null;
    } else {
      result = the_first.equals(the_second);
    }
    return result;
  }
  
  /**
   * Computes a hash code for an object, which can be null. The
   * correctness of this method depends on the correctness of the
   * object's hashCode() method.
   * 
   * @param the_object The object.
   * @return the hash code.
   */
  public static int nullableHashCode(final Object the_object) {
    int result = 0;
    if (the_object != null) {
      result = the_object.hashCode();
    }
    return result;
  }
}
