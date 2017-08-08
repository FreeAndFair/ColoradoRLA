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
 * The various stages of an audit.
 * 
 * @author Daniel M. Zimmerman
 * @author Joseph R. Kiniry 
 * @version 0.0.2
 */
public enum AuditStage {
  PRE_AUDIT,
  AUDIT_READY_TO_START, // @todo kiniry Unclear if we need this state.
  AUDIT_ONGOING,
  AUDIT_COMPLETE,
  AUDIT_RESULTS_PUBLISHED;
}
