/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 1, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

/**
 * A utility class with useful methods for building equals and hashCode
 * methods.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
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
    if (the_first == null) {
      return the_second == null;
    } else {
      return the_first.equals(the_second);
    }
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
    if (the_object == null) {
      return 0;
    } else {
      return the_object.hashCode();
    }
  }
}
