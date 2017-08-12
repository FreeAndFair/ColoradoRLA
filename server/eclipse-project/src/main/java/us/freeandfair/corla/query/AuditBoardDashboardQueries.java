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

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.AuditBoardDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with AuditBoardDashboard entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class AuditBoardDashboardQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private AuditBoardDashboardQueries() {
    // do nothing
  }
  
  /**
   * Obtain the county dashboard object for the specified county identifier. 
   * To safely _use_ the returned dashboard, this method must be called within 
   * a transaction.
   *
   * @param the requested county dashboard, if one exists, 
   * and null otherwise.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static AuditBoardDashboard get(final Integer the_county_id) {
    AuditBoardDashboard result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<AuditBoardDashboard> cq = cb.createQuery(AuditBoardDashboard.class);
      final Root<AuditBoardDashboard> root = cq.from(AuditBoardDashboard.class);
      cq.select(root).where(cb.equal(root.get("my_county_id"), the_county_id));
      final TypedQuery<AuditBoardDashboard> query = s.createQuery(cq);
      final List<AuditBoardDashboard> query_results = query.getResultList();

      AuditBoardDashboard db = null;
      if (query_results.isEmpty()) {
        // create a new audit board dashboard for the specified county
        db = new AuditBoardDashboard(the_county_id);
        Persistence.saveOrUpdate(db);
        Main.LOGGER.info("attempting to create new department of state dashboard");
      } else if (query_results.size() > 1) {
        Main.LOGGER.error("multiple department of state dashboards found");
      } else {
        db = query_results.get(0);
      }
      if (transaction) {
        try {
          Persistence.commitTransaction();
          result = db;
        } catch (final RollbackException e) {
          Main.LOGGER.error("could not create department of state dashboard");
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for department of state dashboard");
    }
    return result;
  }
}
