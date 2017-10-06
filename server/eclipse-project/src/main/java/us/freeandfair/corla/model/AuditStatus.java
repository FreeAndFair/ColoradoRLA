/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Sep 6, 2017
 * @copyright 2017 Colorado Department of State
 * @license GNU Affero General Public License v3 with Classpath Exception
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

/**
 * The possible statuses for an audit.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public enum AuditStatus {
    NOT_STARTED,
    NOT_AUDITABLE,
    IN_PROGRESS,
    RISK_LIMIT_ACHIEVED,
    ENDED;
}
