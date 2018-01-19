/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
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
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Administrator entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class AdministratorQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private AdministratorQueries() {
    // do nothing
  }
  
  /**
   * Obtains the Administrator object with the specified username, if one exists. 
   * 
   * @param the_username The string.
   * @return the matched Administrator. If the results are ambiguous or empty 
   * (more than one match, or no match), returns null.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static Administrator byUsername(final String the_username) {
    Administrator result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Administrator> cq = cb.createQuery(Administrator.class);
      final Root<Administrator> root = cq.from(Administrator.class);
      cq.select(root).where(cb.equal(root.get("my_username"), the_username));
      final TypedQuery<Administrator> query = s.createQuery(cq);
      final List<Administrator> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for administrator");
    }
    if (result == null) {
      Main.LOGGER.debug("found no administrator for username " + the_username);
    } else {
      Main.LOGGER.debug("found administrator " + result + " for string " + the_username);
    }
    return result;
  }
}
