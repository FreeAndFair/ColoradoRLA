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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Contest entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class ContestQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private ContestQueries() {
    // do nothing
  }
  
  /**
   * Obtain a persistent Contest object equivalent to the specified Contest
   * object. If there is not already such a persistent Contest object,
   * one is created and returned.
   *
   * @param the_contest The contest object to match.
   * @return the matched contest object, if one exists.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static Contest matching(final Contest the_contest) {
    Contest result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<Contest> root = cq.from(Contest.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get("my_name"), the_contest.name()));
      conjuncts.add(cb.equal(root.get("my_description"), the_contest.description()));
      conjuncts.add(cb.equal(root.get("my_votes_allowed"), the_contest.votesAllowed()));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<Contest> query = s.createQuery(cq);
      final List<Contest> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } else if (query_results.isEmpty()) {
        // we need to persist a new object
        Persistence.saveOrUpdate(the_contest);
        result = the_contest;
      } else {
        Main.LOGGER.error("multiple matching contests found");
        throw new PersistenceException("multiple matching contests found for " + 
                                       the_contest);
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
   * Gets contests that are in the specified set of counties.
   * 
   * @param the_counties The counties.
   * @return the matching contests, or null if the query fails.
   */
  public static Set<Contest> forCounties(final Set<County> the_counties) {
    Set<Contest> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final County county : the_counties) {
        disjuncts.add(cb.equal(root.get("my_county"), county));
      }
      cq.select(root.get("my_contest"));
      cq.where(cb.or(disjuncts.toArray(new Predicate[disjuncts.size()])));
      cq.distinct(true);
      final TypedQuery<Contest> query = s.createQuery(cq);
      result = new HashSet<Contest>(query.getResultList());  
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
  
  /**
   * Gets contests that are in the specified county.
   * 
   * @param the_county The county.
   * @return the matching contests, or null if the query fails.
   */
  public static Set<Contest> forCounty(final County the_county) {
    Set<Contest> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      cq.select(root.get("my_contest"));
      cq.where(cb.equal(root.get("my_county"), the_county));
      cq.distinct(true);
      final TypedQuery<Contest> query = s.createQuery(cq);
      result = new HashSet<Contest>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
}
