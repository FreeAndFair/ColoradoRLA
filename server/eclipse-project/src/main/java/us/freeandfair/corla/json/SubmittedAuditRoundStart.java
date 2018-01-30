/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * Data submitted to start an audit round.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class SubmittedAuditRoundStart {
  /**
   * The multiplier for the number of ballots per county.
   */
  private final BigDecimal my_multiplier;
  
  /**
   * A flag indicating whether to use the minimum estimates as the base
   * for each county.
   */
  private final Boolean my_use_estimates;
  
  /**
   * A mapping from county IDs to numbers of ballots per county. 
   */
  private final Map<Long, Integer> my_county_ballots;
  
  /**
   * Constructs a new SubmittedAuditRoundStart.
   * 
   * @param the_multiplier The multiplier. Ignored if using absolute numbers
   * of ballots.
   * @param the_use_estimates True to use algorithm estimates, false to use
   * absolute numbers of ballots.
   * @param the_ballots_per_county A map from county IDs to absolute numbers
   * of ballots per county.
   */
  public SubmittedAuditRoundStart(final BigDecimal the_multiplier,
                                  final Boolean the_use_estimates,
                                  final Map<Long, Integer> the_county_ballots) {
    my_multiplier = the_multiplier;
    my_use_estimates = the_use_estimates;
    my_county_ballots = the_county_ballots;
  }
  
  /**
   * @return the multiplier.
   */
  public BigDecimal multiplier() {
    return my_multiplier;
  }
  
  /**
   * @return true if we are using minimum estimates, false otherwise.
   */
  public boolean useEstimates() {
    if (my_use_estimates == null) {
      return false; 
    } else {
      return my_use_estimates;
    }
  }
  
  /**
   * @return the map from county IDs to absolute numbers of ballots per county.
   */
  public Map<Long, Integer> countyBallots() {
    if (my_county_ballots == null) {
      return my_county_ballots;
    } else {
      return Collections.unmodifiableMap(my_county_ballots);
    }
  }
}
