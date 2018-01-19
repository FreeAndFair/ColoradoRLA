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
public enum AuditSelection {
  AUDITED_CONTEST("Audited Contest"),
  UNAUDITED_CONTEST("Unaudited Contest");

  /**
   * The pretty printing string for this enum value.
   */
  private final String my_pretty_string;

  /**
   * Constructs a new AuditReason.
   * 
   * @param the_pretty_string The pretty printing string.
   */
  AuditSelection(final String the_pretty_string) {
    my_pretty_string = the_pretty_string;
  }
  
  /**
   * @return the pretty printing string for this enum value.
   */
  public String prettyString() {
    return my_pretty_string;
  }
}
