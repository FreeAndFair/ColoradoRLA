/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

/**
 * A pair of objects, potentially of different types.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
}
