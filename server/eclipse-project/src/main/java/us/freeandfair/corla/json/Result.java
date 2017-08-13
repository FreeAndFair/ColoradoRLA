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
 * A result returned by the server.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class Result {
  /**
   * The result string.
   */
  private final String my_result;
  
  /**
   * Constructs a new Result.
   * 
   * @param the_result The result.
   */
  public Result(final String the_result) {
    my_result = the_result;
  }
  
  /**
   * @return the result.
   */
  public String result() {
    return my_result;
  }
}
