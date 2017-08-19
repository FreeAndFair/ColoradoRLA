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
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with CVRAuditInfo entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class CVRAuditInfoQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private CVRAuditInfoQueries() {
    // do nothing
  }
  
  /**
   * Obtain a persistent CVRAuditInfo object for the specified county 
   * dashboard and CVR to audit. If one does not exist, it is created
   * and persisted.
   *
   * @param the_dashboard The dashboard to match.
   * @param the_cvr_id The CVR ID to match.
   * @return the matched CVRAuditInfo object, if one exists.
   */
  public static List<CVRAuditInfo> matching(final CountyDashboard the_dashboard,
                                            final Long the_cvr_id) {
    List<CVRAuditInfo> result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CVRAuditInfo> cq = cb.createQuery(CVRAuditInfo.class);
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get("my_dashboard"), the_dashboard));
      conjuncts.add(cb.equal(root.get("my_cvr_id"), the_cvr_id));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
      result = query.getResultList();
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvr audit info");
    }
    if (result == null) {
      Main.LOGGER.debug("found no cvr audit infos matching county " +
                        the_dashboard.id() + ", CVR " + the_cvr_id);
    } else {
      Main.LOGGER.debug("found cvr audit infos " + result);
    }
    return result;
  }
  
  /**
   * Gets the list of CVR IDs to audit for the specified county dashboard.
   * 
   * @param the_dashboard The dashboard.
   * @return the list of CVR IDs to audit, or null if it could not be 
   * obtained.
   */
  public static List<Long> cvrsToAudit(final CountyDashboard the_dashboard) {
    List<Long> result = null;
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      cq.select(root.get("my_cvr_id"));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = query.getResultList();
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvrs to audit list");
    }
    if (result == null) {
      Main.LOGGER.debug("found no cvrs to audit for county " +
                        the_dashboard.id());
    } else {
      Main.LOGGER.debug("found list of cvrs to audit " + result);
    }
    return result;

  }
}
