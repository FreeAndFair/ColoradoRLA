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
 * A submitted risk limit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class SubmittedRiskLimit {
  /**
   * The risk limit.
   */
  private final String my_risk_limit;
  
  /**
   * Constructs a new SubmittedRandomSeed.
   * 
   * @param the_risk_limit The risk limit.
   */
  public SubmittedRiskLimit(final String the_risk_limit) {
    my_risk_limit = the_risk_limit;
  }
  
  /**
   * @return the risk limit.
   */
  public String riskLimit() {
    return my_risk_limit;
  }
}
