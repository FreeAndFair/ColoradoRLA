/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@freeandfair.us>
 * @description A system to assist in conducting state-wide risk-limiting audits.
 */

package us.freeandfair.corla.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * A pseudo-random number generator based on Philip Stark's pseudo-random number
 * generator found at
 * <a href="https://www.stat.berkeley.edu/~stark/Java/Html/sha256Rand.htm">
 * https://www.stat.berkeley.edu/~stark/Java/Html/sha256Rand.htm</a>.
 * 
 * @author Joey Dodds <jdodds@freeandfair.us>
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 * @review kiniry Why is this not just a static class?
 */
public class PseudoRandomNumberGenerator {
  /**
   * The minimum seed length specified in CRLS, and hence our formal specification,
   * is 20 characters.
   * @trace corla.randomness.seed
   */
  public static final int MINIMUM_SEED_LENGTH = 20;
  
  /**
   * The message digest we will use for generating hashes.
   */
  private MessageDigest my_sha256_digest;

  /**
   * The random numbers generated so far.
   */
  private final List<Integer> my_random_numbers;

  /**
   * The current number to use for generation.
   */
  //@ private invariant 0 <= my_count;
  private int my_count;

  /**
   * True if we should "replace" drawn numbers once they are drawn. True allows
   * repeats.
   */
  private final boolean my_with_replacement;

  /**
   * The seed given, which must be at least of length MININUM_SEED_LENGTH and
   * whose contents must only be digits.
   */
  //@ private invariant MINIMUM_SEED_LENGTH <= my_seed.length();
  //@ private invariant seedOnlyContainsDigits(my_seed);
  private final String my_seed;

  /**
   * The minimum value to generate.
   */
  private final int my_minimum;

  /**
   * The maximum value to generate.
   */
  private final int my_maximum;
  
  //@ private invariant my_minimum <= my_maximum;

  /**
   * The maximum index that can be generated without replacement.
   */
  private final int my_maximum_index;

  /**
   * Create a pseudo-random number generator with functionality identical to 
   * Rivest's <code>sampler.py</code> example implementation in Python of an 
   * RLA sampler.
   * 
   * @param the_seed The seed to generate random numbers from
   * @param the_with_replacement True if duplicates can be generated
   * @param the_minimum The minimum value to generate
   * @param the_maximum The maximum value to generate
   */
  //@ requires 20 <= the_seed.length();
  //@ requires seedOnlyContainsDigits(the_seed);
  //@ requires the_minimum <= the_maximum;
  public PseudoRandomNumberGenerator(final String the_seed, 
                                     final boolean the_with_replacement,
                                     final int the_minimum, 
                                     final int the_maximum) {
    // @trace randomness.seed side condition
    assert MINIMUM_SEED_LENGTH <= the_seed.length();
    try {
      my_sha256_digest = MessageDigest.getInstance("SHA-256");
    } catch (final NoSuchAlgorithmException e) {
      assert false;
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
   * Generate the specified list of random numbers.
   * 
   * @param the_from the "index" of the first random number to give
   * @param the_to the "index" of the final random number to give
   * 
   * @return A list containing the_to - the_from + 1 random numbers
   */
  //@ requires the_from <= the_to;
  // @todo kiniry Refine this specification to include public model fields.
  // requires my_with_replacement || the_to <= my_maximum_index;
  public List<Integer> getRandomNumbers(final int the_from, final int the_to) {
    assert the_from <= the_to;
    assert my_with_replacement || the_to <= my_maximum_index;
    if (the_to + 1 > my_random_numbers.size()) {
      extendList(the_to + 1);
    }
    // subList has an exclusive upper bound, but we have an inclusive one
    return my_random_numbers.subList(the_from, the_to + 1);
  }

  /**
   * A helper function to extend the list of generated random numbers.
   * @param the_length the number of random numbers to generate.
   */
  //@ private behavior
  //@   requires 0 <= the_length;
  //@   ensures my_random_numbers.size() == the_length;
  private void extendList(final int the_length) {
    while (my_random_numbers.size() < the_length) {
      generateNext();
    }
  }

  /**
   * Attempt to generate the next random number. This will either extend the
   * list of random numbers in length or leave it the same. It will always 
   * advance the count.
   */
  public void generateNext() {
    my_count++;
    assert my_with_replacement || my_count <= my_maximum_index;

    final String hash_input = my_seed + "," + my_count;

    final byte[] hash_output =
        my_sha256_digest.digest(hash_input.getBytes(StandardCharsets.UTF_8));
    final BigInteger int_output = new BigInteger(1, hash_output);

    final BigInteger in_range =
        int_output.mod(BigInteger.valueOf(my_maximum - my_minimum + 1));
    final int pick = my_minimum + in_range.intValueExact();

    if (my_with_replacement || !my_random_numbers.contains(pick)) {
      my_random_numbers.add(pick);
    }
  }
  
  /**
   * Checks to see if the passed potential seed only contains digits.
   * @param the_seed is the seed to check.
   */
  /*@ behavior
    @   ensures (\forall int i; 0 <= i && i < the_seed.length(); 
    @            Character.isDigit(the_seed.charAt(i)));
    @*/ 
  public /*@ pure @*/ static boolean seedOnlyContainsDigits(final String the_seed) {
    for (int i = 0; i < the_seed.length(); i++) {
      if (!Character.isDigit(the_seed.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
