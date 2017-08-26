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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with CountyContestComparisonAudit entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class CountyContestComparisonAuditQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private CountyContestComparisonAuditQueries() {
    // do nothing
  }
  
  /**
   * Obtain all CountyContestComparisonAudit objects for the specified Contest.
   *
   * @param the_contest The contest.
   * @return the matched objects.
   */
  public static List<CountyContestComparisonAudit> matching(final Contest the_contest) {
    List<CountyContestComparisonAudit> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestComparisonAudit> cq = 
          cb.createQuery(CountyContestComparisonAudit.class);
      final Root<CountyContestComparisonAudit> root = 
          cq.from(CountyContestComparisonAudit.class);
      cq.select(root).where(cb.equal(root.get("my_contest"), the_contest));
      final TypedQuery<CountyContestComparisonAudit> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for county comparison audit");
    }
    if (result == null) {
      Main.LOGGER.debug("found no county comparison audit matching + " + the_contest);
    } else {
      Main.LOGGER.debug("found county comparison audits " + result);
    }
    return result;
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
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CountyContestResult> cq = 
          cb.createQuery(CountyContestResult.class);
      final Root<CountyContestResult> root = cq.from(CountyContestResult.class);
      cq.select(root);
      cq.where(cb.equal(root.get("my_county"), the_county));
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
