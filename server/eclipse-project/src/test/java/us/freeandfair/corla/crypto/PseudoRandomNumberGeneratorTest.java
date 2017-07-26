/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 26, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.crypto;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class PseudoRandomNumberGeneratorTest {

  @Test()
  public void testRandomGenerator() {
    final String seed = "3546311556112163624615351222";
    final PseudoRandomNumberGenerator gen =
        new PseudoRandomNumberGenerator(seed, false, 1, 876);
    final List<Integer> numbers = gen.getRandomNumbers(0, 47);
    final List<Integer> expected = 
        Arrays.asList(740, 180, 264, 789, 238, 448, 272, 611, 761, 208, 596, 88, 160, 
        113, 766, 427, 184, 816, 653, 411, 779, 331, 339, 487, 594, 235, 65, 527, 821, 
        490, 461, 251, 471, 414, 174, 567, 300, 134, 144, 357, 786, 792, 218, 550, 787, 
        537, 197);
    Assert.assertEquals(numbers, expected);
  }
}
