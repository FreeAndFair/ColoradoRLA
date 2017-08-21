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

package us.freeandfair.corla.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Contest entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
   * @param the_county_id The county ID.
   * @param the_contest The contest.
   * @return the matched CountyContestResult object, if one exists.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static CountyContestResult matching(final Long the_county_id,
                                             final Contest the_contest) {
    CountyContestResult result = null;
    
    try {
      @SuppressWarnings("PMD.PrematureDeclaration")
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestResult> cq = 
          cb.createQuery(CountyContestResult.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get("my_county_id"), the_county_id));
      conjuncts.add(cb.equal(root.get("my_contest_id"), the_contest.id()));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CountyContestResult> query = s.createQuery(cq);
      final List<CountyContestResult> query_results = query.getResultList();
      // there should only be one, if one exists
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } else if (query_results.isEmpty()) {
        result = new CountyContestResult(the_county_id, the_contest);
        Persistence.saveOrUpdate(result);
      } else {
        throw new IllegalStateException("unique constraint violated on CountyContestResult");
      }
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for contest");
    }
    if (result == null) {
      Main.LOGGER.debug("found no contest matching + " + the_contest);
    } else {
      Main.LOGGER.debug("found contest " + result);
    }
    return result;
  }
  
  /**
   * Gets CountyContestResults that are in the specified county.
   * 
   * @param the_county_id The county ID.
   * @return the matching CountyContestResults, or null if the query fails.
   */
  public static Set<CountyContestResult> forCounty(final Long the_county_id) {
    Set<CountyContestResult> result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestResult> cq = 
          cb.createQuery(CountyContestResult.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      cq.select(root);
      cq.where(cb.equal(root.get("my_county_id"), the_county_id));
      final TypedQuery<CountyContestResult> query = s.createQuery(cq);
      result = new HashSet<CountyContestResult>(query.getResultList());
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
}
