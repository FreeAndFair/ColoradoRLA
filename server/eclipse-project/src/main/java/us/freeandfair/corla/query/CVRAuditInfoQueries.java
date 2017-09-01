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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Round;
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
   * Obtain the list of persistent CVRAuditInfo objects that match the specified
   * dashboard and CVR.
   *
   * @param the_dashboard The dashboard to match.
   * @param the_cvr The CVR to match.
   * @return the list of matched CVRAuditInfo objects; empty if none exist.
   */
  public static List<CVRAuditInfo> matching(final CountyDashboard the_dashboard,
                                            final CastVoteRecord the_cvr) {
    List<CVRAuditInfo> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CVRAuditInfo> cq = cb.createQuery(CVRAuditInfo.class);
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get("my_dashboard"), the_dashboard));
      conjuncts.add(cb.equal(root.get("my_cvr"), the_cvr));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvr audit info");
    }
    if (result == null) {
      Main.LOGGER.debug("found no cvr audit infos matching county " +
                        the_dashboard.id() + ", CVR " + the_cvr.id());
    } else {
      Main.LOGGER.debug("found cvr audit infos " + result);
    }
    return result;
  }
  
  /**
   * Updates the CVRAuditInfo objects matching the specified CVR so that they all
   * have the same ACVR. This assumes that all such objects that have a non-null
   * ACVR already have the same ACVR, as is the case during an audit when adding
   * new ballots to the sequence.
   * 
   * @param the_dashboard The dashboard to match.
   * @param the_cvr The CVR to match.
   * @param the_acvr The ACVR to set.
   * @exception PersistenceException if the objects cannot be updated.
   */
  public static void updateMatching(final CountyDashboard the_dashboard,
                                    final CastVoteRecord the_cvr,
                                    final CastVoteRecord the_acvr) {
    final Session s = Persistence.currentSession();
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaQuery<CVRAuditInfo> cq = cb.createQuery(CVRAuditInfo.class);
    final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
    final List<Predicate> conjuncts = new ArrayList<>();
    conjuncts.add(cb.equal(root.get("my_dashboard"), the_dashboard));
    conjuncts.add(cb.equal(root.get("my_cvr"), the_cvr));
    cq.select(root);
    cq.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
    final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
    final List<CVRAuditInfo> result = query.getResultList();
    if (!result.isEmpty()) {
      for (final CVRAuditInfo cvrai : result) {
        cvrai.setACVR(the_acvr);
      }
    }
  }

  /**
   * Gets the list of CVR IDs to audit for the specified county dashboard
   * in the current round.
   * 
   * @param the_dashboard The dashboard.
   * @return the list of CVR IDs to audit, or null if it could not be 
   * obtained.
   */
  public static List<Long> cvrsToAudit(final CountyDashboard the_dashboard) {
    List<Long> result = null;
    final Round current = the_dashboard.currentRound();
    if (current != null) {
      try {
        final Session s = Persistence.currentSession();
        // bypassing Hibernate here is a much more effective way of getting
        // this particular set of information
        final Query query = 
            s.createNativeQuery("select cvr_id from cvr_audit_info where " + 
                "dashboard_id=" + the_dashboard.id().toString() + 
                " order by index limit " + current.expectedAuditedPrefixLength() +
                " offset " + current.actualAuditedPrefixLength());
        @SuppressWarnings("unchecked") // we know this gives us a list of numbers
        final List<Number> generic_results = (List<Number>) query.getResultList();
        result = new ArrayList<>();
        for (final Number n : generic_results) {
          result.add(n.longValue());
        }
      } catch (final PersistenceException e) {
        e.printStackTrace(System.out);
        Main.LOGGER.error("could not query database for cvrs to audit list");
      }
      if (result == null) {
        Main.LOGGER.debug("found no cvrs to audit for county " +
            the_dashboard.id());
      } else {
        Main.LOGGER.debug("found list of cvrs to audit " + result);
      }
    } // else there is no active round, so nothing to audit
    return result;
  }
}
