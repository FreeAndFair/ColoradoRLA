/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Sep 6, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * The possible reasons for an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public enum AuditReason {
    STATE_WIDE_CONTEST,
    COUNTY_WIDE_CONTEST,
    CLOSE_CONTEST,
    GEOGRAPHICAL_SCOPE,
    CONCERN_REGARDING_ACCURACY,
    OPPORTUNISTIC_BENEFITS,
    COUNTY_CLERK_ABILITY;
}
