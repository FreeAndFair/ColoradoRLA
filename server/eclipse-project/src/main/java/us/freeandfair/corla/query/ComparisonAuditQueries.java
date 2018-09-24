/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.hibernate.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with ComparisonAudit entities.
 */
public final class ComparisonAuditQueries {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ComparisonAuditQueries.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditQueries() {
    // do nothing
  }

  /**
   * Obtain all ComparisonAudit objects for the specified ContestResult.
   *
   * @param contestName The contest name
   * @return the matched objects.
   */
  public static List<ComparisonAudit> matching(final String contestName) {
    List<ComparisonAudit> result = null;
    final ContestResult contestResult = ContestResultQueries.findOrCreate(contestName);

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<ComparisonAudit> cq = cb.createQuery(ComparisonAudit.class);
      final Root<ComparisonAudit> root = cq.from(ComparisonAudit.class);

      cq.select(root).where(cb.equal(root.get("my_contest_result"), contestResult));

      final TypedQuery<ComparisonAudit> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      LOGGER.error("could not query database for comparison audits");
    }
    if (result == null) {
      LOGGER.debug("found no comparison audits matching + " + contestResult);
    } else {
      LOGGER.debug("found comparison audits " + result);
    }
    return result;
  }


  /**
   * Return the ContestResult with the contestName given or create a new
   * ContestResult with the contestName.
   **/
  public static Integer count() {
    final Session s = Persistence.currentSession();
    final Query q = s.createQuery("select count(ca) from ComparisonAudit ca");
    return ((Long)q.uniqueResult()).intValue();
  }

}
