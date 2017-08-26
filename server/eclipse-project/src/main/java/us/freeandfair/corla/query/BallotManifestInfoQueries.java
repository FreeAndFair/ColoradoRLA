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
import java.util.OptionalLong;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with BallotManfestInfo entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class BallotManifestInfoQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private BallotManifestInfoQueries() {
    // do nothing
  }
  
  /**
   * Returns the set of ballot manifests matching the specified county IDs.
   * 
   * @param the_county_ids The set of county IDs.
   * @return the ballot manifests matching the specified set of county IDs,
   * or null if the query fails.
   */
  public static Set<BallotManifestInfo> getMatching(final Set<Integer> the_county_ids) {
    Set<BallotManifestInfo> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<BallotManifestInfo> cq = 
          cb.createQuery(BallotManifestInfo.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final Integer county_id : the_county_ids) {
        disjuncts.add(cb.equal(root.get("my_county_id"), county_id));
      }
      cq.select(root).where(cb.or(disjuncts.toArray(new Predicate[disjuncts.size()])));
      final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
      result = new HashSet<BallotManifestInfo>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ballot manifests from database: " + e);
    }

    return result;
  }
  
  /**
   * Deletes the set of ballot manifests for the specified county ID.
   * 
   * @param the_county_id The county ID.
   * @exception PersistenceException if the ballot manifests cannot be deleted.
   */
  public static void deleteMatching(final Long the_county_id) {
    final Session s = Persistence.currentSession();
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaDelete<BallotManifestInfo> cd = 
        cb.createCriteriaDelete(BallotManifestInfo.class);
    final Root<BallotManifestInfo> root = cd.from(BallotManifestInfo.class);
    cd.where(cb.equal(root.get("my_county_id"), the_county_id));
    s.createQuery(cd).executeUpdate();
  }
  
  /**
   * Count the uploaded ballot manifest info records in storage.
   * 
   * @return the number of uploaded records.
   */
  public static OptionalLong count() {
    OptionalLong result = OptionalLong.empty();
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      cq.select(cb.count(root));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      // ignore
    }
    
    return result;
  }
}
