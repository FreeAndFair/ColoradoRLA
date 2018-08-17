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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with CountyContestResult entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class CountyContestResultQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private CountyContestResultQueries() {
    // do nothing
  }
  
  /**
   * Obtain a persistent CountyContestResult object for the specified
   * county ID and contest. If there is not already a matching persistent 
   * object, one is created and returned.
   *
   * @param the_county The county.
   * @param the_contest The contest.
   * @return the matched CountyContestResult object, if one exists.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static CountyContestResult matching(final County the_county,
                                             final Contest the_contest) {
    CountyContestResult result = null;
    
    try {
      @SuppressWarnings("PMD.PrematureDeclaration")
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestResult> cq = 
          cb.createQuery(CountyContestResult.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get("my_county"), the_county));
      conjuncts.add(cb.equal(root.get("my_contest"), the_contest));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CountyContestResult> query = s.createQuery(cq);
      final List<CountyContestResult> query_results = query.getResultList();
      // there should only be one, if one exists
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } else if (query_results.isEmpty()) {
        result = new CountyContestResult(the_county, the_contest);
        Persistence.saveOrUpdate(result);
      } else {
        throw new IllegalStateException("unique constraint violated on CountyContestResult");
      }
    } catch (final PersistenceException e) {
      e.printStackTrace(System.out);
      Main.LOGGER.error("could not query database for contest results");
    }
    if (result == null) {
      Main.LOGGER.debug("found no contest results matching + " + the_contest);
    } else {
      Main.LOGGER.debug("found contest results " + result);
    }
    return result;
  }

  /**
   * Gets CountyContestResults that have the contestName.
   *
   * @param contestName The name to match Contest#name.
   * @return the matching CountyContestResults, or an empty set.
   */
  public static List<CountyContestResult> withContestName(final String contestName) {
    final Session s = Persistence.currentSession();
    final Query q = s.createQuery("select ccr from CountyContestResult ccr " +
                                  "inner join Contest c " +
                                  "on ccr.my_contest = c " +
                                  "where c.my_name = :contestName");
    q.setParameter("contestName", contestName);
    return q.list();
  }

  /**
   * Gets CountyContestResults that are in the specified county.
   * 
   * @param the_county The county.
   * @return the matching CountyContestResults, or null if the query fails.
   */
  public static Set<CountyContestResult> forCounty(final County the_county) {
    Set<CountyContestResult> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestResult> cq = 
          cb.createQuery(CountyContestResult.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      cq.select(root);
      cq.where(cb.equal(root.get("my_county"), the_county));
      final TypedQuery<CountyContestResult> query = s.createQuery(cq);
      result = new HashSet<CountyContestResult>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
  
  /**
   * Deletes all the contest results for the county with the specified ID.
   * 
   * @param the_id The county ID.
   */
  public static void deleteForCounty(final Long the_county_id) {
    final Set<CountyContestResult> results = 
        forCounty(Persistence.getByID(the_county_id, County.class));
    if (results != null) {
      for (final CountyContestResult c : results) {
        Persistence.delete(c);
        Persistence.delete(c.contest());
      }
    }
    Persistence.flush();
  }
}
