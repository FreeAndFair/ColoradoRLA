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
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with County entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class CountyQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private CountyQueries() {
    // do nothing
  }
  
  /**
   * Obtains a County object from a county name or ID string. If the string is 
   * numeric, we assume it is an ID; otherwise, we assume it is a name and match
   * it as closely as we can to a county in the database. 
   * 
   * @param the_string The string.
   * @return the matched County. If the results are ambiguous or empty (more than 
   * one match, or no match), returns null.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static County fromString(final String the_string) {
    County result = null;
    Integer parsed_id;
    
    try {
      parsed_id = Integer.parseInt(the_string);
    } catch (final NumberFormatException e) {
      parsed_id = -1;
    }
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<County> cq = cb.createQuery(County.class);
      final Root<County> root = cq.from(County.class);
      cq.select(root).where(cb.or(cb.like(root.get("my_name"), "%" + the_string + "%"),
                                  cb.equal(root.get("my_identifier"), parsed_id)));
      final TypedQuery<County> query = s.createQuery(cq);
      final List<County> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for county");
    }
    if (result == null) {
      Main.LOGGER.debug("found no county for string " + the_string);
    } else {
      Main.LOGGER.debug("found county " + result + " for string " + the_string);
    }
    
    return result;
  }
}
