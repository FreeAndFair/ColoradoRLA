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
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static CVRAuditInfo matching(final CountyDashboard the_dashboard,
                                      final Long the_cvr_id) {
    CVRAuditInfo result = null;
    
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
      final List<CVRAuditInfo> query_results = query.getResultList();
      // there should be only a single result because of the unique constraint 
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } else if (query_results.isEmpty()) {
        // we need to persist a new object
        result = new CVRAuditInfo(the_dashboard, the_cvr_id);
        Persistence.saveOrUpdate(result);
      } else {
        if (transaction) {
          Persistence.rollbackTransaction();
        }
        throw new IllegalStateException("violation of CVRAuditInfo unique constraint");
      }
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
      Main.LOGGER.info("found no cvr audit info matching county " +
                        the_dashboard.id() + ", CVR " + the_cvr_id);
    } else {
      Main.LOGGER.info("found cvr audit info " + result);
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
      // this is going to be far, far faster with a native query
      final TypedQuery<Long> query = 
          s.createNativeQuery("select cvr_id from cvr_audit_info where dashboard_id = " + 
                              the_dashboard.id(), Long.class);
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
