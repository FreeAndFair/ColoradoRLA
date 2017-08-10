/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 7, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

/**
 * A persistable entity with an ID number.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
}
