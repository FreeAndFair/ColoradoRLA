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
 * The possible statuses for a county in an audit.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public enum CountyStatus {
  NO_DATA,
  CVRS_UPLOADED_SUCCESSFULLY,
  ERROR_IN_UPLOADED_DATA;
}
