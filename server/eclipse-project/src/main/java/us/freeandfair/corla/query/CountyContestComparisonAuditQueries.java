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
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with CountyContestComparisonAudit entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
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
      cq.orderBy(cb.asc(root.get("my_dashboard").get("my_county").get("my_id")), 
                 cb.asc(root.get("my_contest").get("my_sequence_number")));
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
}
