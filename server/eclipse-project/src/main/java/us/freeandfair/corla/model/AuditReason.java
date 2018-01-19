/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Sep 6, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * The possible reasons for an audit.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public enum AuditReason {
  STATE_WIDE_CONTEST("Statewide Contest"),
  COUNTY_WIDE_CONTEST("Countywide Contest"),
  CLOSE_CONTEST("Close Contest"),
  TIED_CONTEST("Tied Contest"),
  GEOGRAPHICAL_SCOPE("Geographical Scope"),
  CONCERN_REGARDING_ACCURACY("Concern Regarding Accuracy"),
  OPPORTUNISTIC_BENEFITS("Opportunistic Benefits"),
  COUNTY_CLERK_ABILITY("County Clerk Ability");

  /**
   * The pretty printing string for this enum value.
   */
  private final String my_pretty_string;

  /**
   * Constructs a new AuditReason.
   * 
   * @param the_pretty_string The pretty printing string.
   */
  AuditReason(final String the_pretty_string) {
    my_pretty_string = the_pretty_string;
  }
  
  /**
   * @return the pretty printing string for this enum value.
   */
  public String prettyString() {
    return my_pretty_string;
  }
  
  /**
   * @return the audit selection corresponding to this audit reason.
   */
  public AuditSelection selection() {
    if (this.equals(TIED_CONTEST) || this.equals(OPPORTUNISTIC_BENEFITS)) {
      return AuditSelection.UNAUDITED_CONTEST;
    } else {
      return AuditSelection.AUDITED_CONTEST;
    }
  }
}
