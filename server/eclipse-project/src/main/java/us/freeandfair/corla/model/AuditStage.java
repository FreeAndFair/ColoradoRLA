/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * The various stages of an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public enum AuditStage {
  INITIAL,
  AUTHENTICATED,
  RISK_LIMITS_SET,
  CONTESTS_TO_AUDIT_IDENTIFIED,
  RANDOM_SEED_PUBLISHED,
  BALLOT_ORDER_DEFINED,
  AUDIT_ONGOING,
  AUDIT_COMPLETE,
  AUDIT_RESULTS_PUBLISHED;
}
