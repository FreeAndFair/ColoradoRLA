/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

/**
 * A submitted random seed.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class SubmittedRandomSeed {
  /**
   * The seed.
   */
  private final String my_seed;
  
  /**
   * Constructs a new SubmittedRandomSeed.
   * 
   * @param the_seed The seed.
   */
  public SubmittedRandomSeed(final String the_seed) {
    my_seed = the_seed;
  }
  
  /**
   * @return the seed.
   */
  public String seed() {
    return my_seed;
  }
}
