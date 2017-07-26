/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 25, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import us.freeandfair.corla.util.Pair;

/**
 * A pseudo-random number generator based on Philip Stark's pseudo-random number
 * generator found at
 * <a href="https://www.stat.berkeley.edu/~stark/Java/Html/sha256Rand.htm">
 * https://www.stat.berkeley.edu/~stark/Java/Html/sha256Rand.htm</a>.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class PseudoRandomNumberGenerator {

  /**
   * The message digest we will use for generating hashes
   */
  private MessageDigest my_sha256_digest;

  /**
   * The random numbers generated so far
   */
  private final List<Integer> my_random_numbers;

  /**
   * The current number to use for generation
   */
  private int my_count;

  /**
   * True if we should "replace" drawn numbers once they are drawn. True allows
   * repeats
   */
  private final boolean my_with_replacement;

  /**
   * The seed given
   */
  private final String my_seed;

  /**
   * The minimum value to generate
   */
  private final int my_minimum;

  /**
   * The maximum value to generate
   */
  private final int my_maximum;

  /**
   * The maximum index that can be generated without replacement
   */
  private final int my_maximum_index;

  /**
   * 
   * @param the_seed The seed to generate random numbers from
   * @param the_with_replacement True if duplicates can be generated
   * @param the_minimum The minimum value to generate
   * @param the_maximum The maximum value to generate
   */
  public PseudoRandomNumberGenerator(final String the_seed, final boolean the_with_replacement,
                                     final int the_minimum, final int the_maximum) {
    try {
      my_sha256_digest = MessageDigest.getInstance("SHA-256");
    } catch (final NoSuchAlgorithmException e) {
      assert false;
      e.printStackTrace();
    }
    my_random_numbers = new LinkedList<Integer>();
    my_with_replacement = the_with_replacement;
    my_seed = the_seed;
    assert the_minimum < the_maximum;
    my_minimum = the_minimum;
    my_maximum = the_maximum;

    my_maximum_index = my_maximum - my_minimum + 1;

  }

  /**
   * 
   * <description> <explanation>
   * 
   * @param the_from the "index" of the first random number generator to give
   * @param the_to the "index" of the final random number to give
   * 
   * @return A list containing the_from - the_to + 1 random numbers
   */
  public List<Integer> getRandomNumbers(final int the_from, final int the_to) {
    assert my_with_replacement || the_to <= my_maximum_index;
    if (the_to > my_random_numbers.size()) {
      extendList(the_to);
    }

    return my_random_numbers.subList(the_from, the_to);
  }

  private void extendList(final int the_length) {
    while (my_random_numbers.size() < the_length) {
      generateNext();
    }
  }

  /**
   * 
   * Attempt to generate the next random number. This will either extend the
   * list of randoms in length or leave it the same. It will always advance the
   * count
   * 
   * @param
   */
  public void generateNext() {
    my_count++;
    assert my_with_replacement || my_count <= my_maximum_index;

    final String hash_input = my_seed + "," + my_count;

    final byte[] hash_output =
        my_sha256_digest.digest(hash_input.getBytes(StandardCharsets.UTF_8));
    System.out.println(bytesToHex(hash_output));
    final BigInteger int_output = new BigInteger(hash_output);

    final BigInteger in_range =
        int_output.mod(BigInteger.valueOf(my_maximum - my_minimum + 1));
    final int pick = my_minimum + in_range.intValueExact();

    if (my_with_replacement || !my_random_numbers.contains(pick)) {
      my_random_numbers.add(pick);
    }

  }

  public String bytesToHex(byte[] in) {
    final StringBuilder builder = new StringBuilder();
    for (byte b : in) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }
}
