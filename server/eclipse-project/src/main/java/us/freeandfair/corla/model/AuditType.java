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
 * The possible types for an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public enum AuditType {
  COMPARISON, HAND_COUNT, NOT_AUDITABLE, NONE;
}
