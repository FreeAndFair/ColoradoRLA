/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;

/**
 * Queries having to do with County entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class CountyQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private CountyQueries() {
    // do nothing
  }
  
  /**
   * Obtain the County object with a specific name and ID number
   * (not database ID), if one exists.
   * 
   * @param the_name The name.
   * @param the_id The ID number.
   * @return the matched County, if one exists, or null otherwise.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static County getCountyMatching(final String the_name, final Integer the_id) {
    County result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<County> cq = cb.createQuery(County.class);
      final Root<County> root = cq.from(County.class);
      cq.select(root).where(cb.or(cb.equal(root.get("my_name"), the_name),
                                  cb.equal(root.get("my_identifier"), the_id)));
      final TypedQuery<County> query = s.createQuery(cq);
      final List<County> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for county");
    }
    if (result == null) {
      Main.LOGGER.info("found no county for " + the_name + " id " + the_id);
    } else {
      Main.LOGGER.info("found county " + result);
    }
    return result;
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
  public static County getCountyFromString(final String the_string) {
    County result = null;
    Integer parsed_id;
    
    try {
      parsed_id = Integer.parseInt(the_string);
    } catch (final NumberFormatException e) {
      parsed_id = -1;
    }
    
    try {
      final boolean transaction = Persistence.beginTransaction();
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
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for county");
    }
    if (result == null) {
      Main.LOGGER.info("found no county for string " + the_string);
    } else {
      Main.LOGGER.info("found county " + result + " for string " + the_string);
    }
    return result;
  }
}
