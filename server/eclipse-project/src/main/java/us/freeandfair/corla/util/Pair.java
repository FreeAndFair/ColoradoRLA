/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

/**
 * A pair of objects, potentially of different types.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class Pair<A, B> {
  /**
   * The first object.
   */
  private final A my_first;
  
  /**
   * The second object.
   */
  private final B my_second;

  /**
   * Constructs a new pair from two objects.
   * 
   * @param the_first The first object.
   * @param the_second The second object.
   */
  public Pair(final A the_first, final B the_second) {
    my_first = the_first;
    my_second = the_second;
  }

  /**
   * Statically constructs a new pair from two objects.
   * 
   * @param the_first The first object.
   * @param the_second The second object.
   */
  public static <A, B> Pair<A, B> make(final A the_first, final B the_second) {
    return new Pair<A, B>(the_first, the_second);
  }
  
  /**
   * @return the first object in this pair.
   */
  public A first() {
    return my_first;
  }

  /**
   * @return the second object in this pair.
   */
  public B second() {
    return my_second;
  }
  
  /**
   * Compares this pair with another for equivalence.
   * 
   * @param the_other The other pair.
   * @return true if the two pairs are equivalent, false otherwise.
   */
  public boolean equals(final Object the_other) {
    final boolean result;
    
    if (the_other instanceof Pair) {
      final Pair<?, ?> other_pair = (Pair<?, ?>) the_other;
      result = nullableEquals(other_pair.first(), first()) &&
               nullableEquals(other_pair.second(), second());
    } else {
      result = false;
    }
    
    return result;
  }
  
  /**
   * @return a hash code for this pair.
   */
  public int hashCode() {
    return nullableHashCode(first()) + 7 * nullableHashCode(second());
  }
}
