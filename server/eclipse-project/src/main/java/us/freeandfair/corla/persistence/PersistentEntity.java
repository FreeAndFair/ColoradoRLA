/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 7, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

/**
 * A persistable entity with an ID number.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public interface PersistentEntity {
  /**
   * @return the ID number of this entity.
   */
  Long id();
  
  /**
   * Set the ID number of this entity. This method should typically not be used 
   * except by the persistence system.
   * 
   * @param the_id The new ID number.
   */
  void setID(Long the_id);
  
  /**
   * @return the version number of this entity. This is primarily for debugging.
   */
  Long version();
}
