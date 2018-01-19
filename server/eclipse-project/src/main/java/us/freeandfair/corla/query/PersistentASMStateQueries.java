/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 11, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with persistent ASM state.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class PersistentASMStateQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private PersistentASMStateQueries() {
    // do nothing
  }
  
  /**
   * Retrieves the persistent ASM state from the database matching the specified
   * ASM class and identity, if one exists.
   * 
   * @param the_class The class of ASM to retrieve. 
   * @param the_identity The identity of the ASM to retrieve, or null if the ASM
   * is a singleton.
   * @return the persistent ASM state, or null if it does not exist.
   * @exception PersistenceException if there is more than one matching ASM state
   * in the database.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static PersistentASMState get(final Class<? extends AbstractStateMachine> the_class,
                                       final String the_identity) 
      throws PersistenceException {
    PersistentASMState result = null;
    try {
      final String class_name = the_class.getName();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<PersistentASMState> cq = cb.createQuery(PersistentASMState.class);
      final Root<PersistentASMState> root = cq.from(PersistentASMState.class);
      Predicate predicate = cb.equal(root.get("my_asm_class"), class_name);
      if (the_identity != null) {
        predicate = cb.and(predicate, cb.equal(root.get("my_asm_identity"), the_identity));
      }
      cq.select(root).where(predicate);
      final TypedQuery<PersistentASMState> query = s.createQuery(cq);
      final List<PersistentASMState> query_results = query.getResultList();
      PersistentASMState asm = null;
      if (query_results.size() > 1) {
        Main.LOGGER.error("multiple ASM states found");
        throw new PersistenceException("multiple ASM states found for " + 
                                       the_class.getName() + ", identity " + the_identity);
      } else if (!query_results.isEmpty()) {
        asm = query_results.get(0);
        Main.LOGGER.debug("found ASM state " + asm + " for class " + the_class.getName() + 
                          ", identity " + the_identity);
      }
      result = asm;
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for persistent ASM state");
    }
    return result;
  }

}
