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
import javax.persistence.criteria.Subquery;

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
   * The "my_dashboard" string.  
   */
  private static final String MY_DASHBOARD = "my_dashboard";
  
  /**
   * The "my_index" string.
   */
  private static final String MY_INDEX = "my_index";
  
  /**
   * The "my_cvr" string.
   */
  private static final String MY_CVR = "my_cvr";
  
  /**
   * The "range" string.
   */
  private static final String RANGE = ", range[";
  
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
      conjuncts.add(cb.equal(root.get(MY_DASHBOARD), the_dashboard));
      conjuncts.add(cb.equal(root.get(MY_CVR), the_cvr));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvr audit info");
    }
    if (result == null || result.isEmpty()) {
      Main.LOGGER.debug("found no cvr audit infos matching county " +
                        the_dashboard.id() + ", CVR " + the_cvr.id());
    } else {
      Main.LOGGER.debug("found cvr audit infos " + result);
    }
    return result;
  }
  
  /**
   * Obtain the sublist of CVRAuditInfo objects for the specified dashboard,
   * starting at the specified start index (inclusive) and running 
   * through the specified end index (exclusive).
   * 
   * @param the_dashboard The dashboard.
   * @param the_start_index The start index (inclusive).
   * @param the_end_index The end index (exclusive).
   * @return the list of CVRAuditInfo objects, which may be empty (if the
   * query succeeds and no objects are found in the range) or null (if the
   * query fails).
   */
  public static List<CVRAuditInfo> range(final CountyDashboard the_dashboard,
                                         final Integer the_start_index,
                                         final Integer the_end_index) {
    List<CVRAuditInfo> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CVRAuditInfo> cq = cb.createQuery(CVRAuditInfo.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      conjuncts.add(cb.equal(root.get(MY_DASHBOARD), the_dashboard));
      conjuncts.add(cb.greaterThanOrEqualTo(root.get(MY_INDEX), the_start_index));
      conjuncts.add(cb.lessThan(root.get(MY_INDEX), the_end_index));
      cq.select(root);
      cq.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      cq.orderBy(cb.asc(root.get(MY_INDEX)));
      final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvr audit info range");
    }
    if (result == null || result.isEmpty()) {
      Main.LOGGER.debug("found no cvr audit infos matching county " +
                        the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
    } else {
      Main.LOGGER.debug("found " + result.size() + " cvr audit infos matching county " +
                        the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
    }
    return result;
  }
  
  /**
   * Obtain the sublist of CVRAuditInfo objects for the specified 
   * dashboard, starting at the specified start index (inclusive) and running 
   * through the specified end index (exclusive). The returned list contains
   * no duplicate CVRs; only the first occurrence of each CVR is listed.
   * 
   * @param the_dashboard The dashboard.
   * @param the_start_index The start index (inclusive).
   * @param the_end_index The end index (exclusive).
   * @return the list of CVRAuditInfo objects, which may be empty (if the 
   * query succeeds and no objects are found in the range) or null (if the
   * query fails).
   */
  public static List<CVRAuditInfo> rangeUnique(final CountyDashboard the_dashboard,
                                               final Integer the_start_index,
                                               final Integer the_end_index) {
    List<CVRAuditInfo> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CVRAuditInfo> cq = cb.createQuery(CVRAuditInfo.class);
      final Subquery<Integer> sq1 = cq.subquery(Integer.class);
      final Subquery<CVRAuditInfo> sq2 = cq.subquery(CVRAuditInfo.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      final Root<CVRAuditInfo> subroot = sq1.from(CVRAuditInfo.class);
      final Root<CVRAuditInfo> subroot2 = sq2.from(CVRAuditInfo.class);
      conjuncts.add(cb.equal(subroot.get(MY_DASHBOARD), the_dashboard));
      conjuncts.add(cb.greaterThanOrEqualTo(subroot.get(MY_INDEX), the_start_index));
      conjuncts.add(cb.lessThan(subroot.get(MY_INDEX), the_end_index));
      conjuncts.add(cb.equal(root.get(MY_INDEX), subroot.get(MY_INDEX)));
      sq1.select(cb.min(subroot.get(MY_INDEX)));
      sq1.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      sq2.select(subroot2);
      sq2.where(cb.and(cb.equal(root.get(MY_CVR),
                                subroot2.get(MY_CVR)),
                       cb.greaterThan(root.get(MY_INDEX),
                                      subroot2.get(MY_INDEX))));
      cq.select(root);
      cq.where(cb.and(cb.equal(root.get(MY_INDEX), sq1),
                      cb.not(cb.exists(sq2))));
      cq.orderBy(cb.asc(root.get(MY_INDEX)));
      final TypedQuery<CVRAuditInfo> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for cvr audit info range");
    }
    if (result == null || result.isEmpty()) {
      Main.LOGGER.debug("found no cvr audit infos matching county " +
                        the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
    } else {
      Main.LOGGER.debug("found " + result.size() + " cvr audit infos matching county " +
                        the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
    }
    return result;
  }
  
  /**
   * Obtain the sublist of unaudited CVRAuditInfo objects for the specified 
   * dashboard, starting at the specified start index (inclusive) and running 
   * through the specified end index (exclusive), with no duplicate CVRs.
   * Only the first occurrence of each CVR is listed.
   * 
   * @param the_dashboard The dashboard.
   * @param the_start_index The start index (inclusive).
   * @param the_end_index The end index (exclusive).
   * @return the list of CVRAuditInfo objects, which may be empty (if the 
   * query succeeds and no objects are found in the range) or null (if the
   * query fails).
   */
  public static List<Long> unauditedCVRIDsInRange(final CountyDashboard the_dashboard,
                                                  final Integer the_start_index,
                                                  final Integer the_end_index) {
    List<Long> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Subquery<Integer> sq1 = cq.subquery(Integer.class);
      final Subquery<CVRAuditInfo> sq2 = cq.subquery(CVRAuditInfo.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      final Root<CVRAuditInfo> root = cq.from(CVRAuditInfo.class);
      final Root<CVRAuditInfo> subroot = sq1.from(CVRAuditInfo.class);
      final Root<CVRAuditInfo> subroot2 = sq2.from(CVRAuditInfo.class);
      conjuncts.add(cb.equal(subroot.get(MY_DASHBOARD), the_dashboard));
      conjuncts.add(cb.greaterThanOrEqualTo(subroot.get(MY_INDEX), the_start_index));
      conjuncts.add(cb.lessThan(subroot.get(MY_INDEX), the_end_index));
      conjuncts.add(cb.equal(root.get(MY_INDEX), subroot.get(MY_INDEX)));
      conjuncts.add(cb.isNull(subroot.get("my_acvr")));
      sq1.select(cb.min(subroot.get(MY_INDEX)));
      sq1.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      sq2.select(subroot2);
      sq2.where(cb.and(cb.equal(root.get(MY_CVR),
                                subroot2.get(MY_CVR)),
                       cb.greaterThan(root.get(MY_INDEX),
                                      subroot2.get(MY_INDEX))));
      cq.select(root.get(MY_CVR).get("my_id"));
      cq.where(cb.and(cb.equal(root.get(MY_INDEX), sq1),
                      cb.not(cb.exists(sq2))));
      cq.orderBy(cb.asc(root.get(MY_INDEX)));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for unaudited cvr range");
    }
    if (result == null || result.isEmpty()) {
      Main.LOGGER.debug("found no unaudited cvrs matching county " +
                        the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
    } else {
      Main.LOGGER.debug("found " + result.size() + " unaudited cvrs matching " +
                        "county " + the_dashboard.id() + RANGE + the_start_index +
                        ", " + the_end_index + ")");
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
    conjuncts.add(cb.equal(root.get(MY_DASHBOARD), the_dashboard));
    conjuncts.add(cb.equal(root.get(MY_CVR), the_cvr));
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
