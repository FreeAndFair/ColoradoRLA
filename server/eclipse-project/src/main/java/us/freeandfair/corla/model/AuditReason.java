/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * The possible reasons for selecting a contest to audit.
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
