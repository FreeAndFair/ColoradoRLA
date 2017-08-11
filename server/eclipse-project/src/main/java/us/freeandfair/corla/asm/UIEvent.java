/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 11, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

/**
 * An enumeration of all user-triggered external inbound events in the client UI.
 * 
 * @author Joseph R. Kiniry
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public enum UIEvent implements Event {
  LOGIN,
  FETCH_INITIAL_STATE_SEND,
  FETCH_INITIAL_STATE_RECEIVE,
  SELECT_NEXT_BALLOT,
  UPDATE_BOARD_MEMBER,
  UPDATE_BALLOT_MARKS,
  UNDEFINED
}