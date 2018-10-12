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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with BallotManfestInfo entities.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class BallotManifestInfoQueries {

  /**
   * The "county ID" field.
   */
  private static final String COUNTY_ID = "my_county_id";


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
  public static Set<BallotManifestInfo> getMatching(final Set<Long> the_county_ids) {
    final Set<BallotManifestInfo> result =
      new TreeSet<BallotManifestInfo>(new BallotManifestInfo.Sort());

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<BallotManifestInfo> cq =
          cb.createQuery(BallotManifestInfo.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final Long county_id : the_county_ids) {
        disjuncts.add(cb.equal(root.get(COUNTY_ID), county_id));
      }
      cq.select(root).where(cb.or(disjuncts.toArray(new Predicate[disjuncts.size()])));
      final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
      result.addAll(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ballot manifests from database: " + e);
    }

    return result;
  }



  /** select ballot_manifest_info where uri in :uris **/
  public static List<BallotManifestInfo> locationFor(final Set<String> uris) {
    if (uris.isEmpty()) {
      return new ArrayList();
    }
    final Session s = Persistence.currentSession();
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaQuery<BallotManifestInfo> cq = cb.createQuery(BallotManifestInfo.class);
    final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
    cq.where(root.get("uri").in(uris));
    final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
    return query.getResultList();
  }

  /**
   * Returns the location for the specified CVR, assuming one can be found.
   *
   * @param the_cvr The CVR.
   * @return the location for the CVR, or null if no location can be found.
   */
  public static Optional<BallotManifestInfo> locationFor(final CastVoteRecord the_cvr) {
    Optional<BallotManifestInfo> result = Optional.empty();

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<BallotManifestInfo> cq = cb.createQuery(BallotManifestInfo.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      cq.where(cb.and(cb.equal(root.get("my_county_id"), the_cvr.countyID()),
                      cb.equal(root.get("my_scanner_id"), the_cvr.scannerID()),
                      cb.equal(root.get("my_batch_id"), the_cvr.batchID())));
      final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
      final List<BallotManifestInfo> query_result = query.getResultList();
      // there should never be more than one result, but if there is, we'll
      // return the first one
      if (!query_result.isEmpty()) {
        result = Optional.of(query_result.get(0));
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when finding ballot location: " + e);
    }

    return result;
  }

  /**
   * Deletes the set of ballot manifests for the specified county ID.
   *
   * @param the_county_id The county ID.
   * @exception PersistenceException if the ballot manifests cannot be deleted.
   */
  public static int deleteMatching(final Long the_county_id) {
    final AtomicInteger count = new AtomicInteger();
    final Session s = Persistence.currentSession();
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaQuery<BallotManifestInfo> cq = cb.createQuery(BallotManifestInfo.class);
    final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
    cq.where(cb.equal(root.get(COUNTY_ID), the_county_id));
    final Query<BallotManifestInfo> query = s.createQuery(cq);
    final Stream<BallotManifestInfo> to_delete = query.stream();
    to_delete.forEach((the_bmi) -> {
      Persistence.delete(the_bmi);
      count.incrementAndGet();
    });
    return count.get();
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

  /**
     Find the batch(bmi) that would hold the sequence number given.
   */
  public static Optional<BallotManifestInfo>
      holdingSequencePosition(final Long rand, final Long countyId) {
    Set<BallotManifestInfo> result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<BallotManifestInfo> cq =
          cb.createQuery(BallotManifestInfo.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      final Predicate start = cb.lessThanOrEqualTo(root.get("my_sequence_start"), rand);
      final Predicate end = cb.greaterThanOrEqualTo(root.get("my_sequence_end"), rand);
      final Predicate county = cb.equal(root.get(COUNTY_ID), countyId);
      disjuncts.add(start);
      disjuncts.add(end);
      disjuncts.add(county);
      cq.select(root).where(cb.and(disjuncts.toArray(new Predicate[disjuncts.size()])));
      final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
      result = new HashSet<BallotManifestInfo>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ballot manifests from database: ", e);
    }
    return result.stream().findFirst();
  }

  /**
   * Get the max sequence number which is the total number of CVRs there should be
   */
  public static OptionalLong maxSequence(final Long countyId) {
    OptionalLong result = OptionalLong.empty();

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      cq.select(cb.max(root.get("my_sequence_end")));
      cq.where(cb.equal(root.get(COUNTY_ID), countyId));

      final TypedQuery<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ballot manifests from database: ", e);
    }
    return result;
  }

  /**
   * Get the number of ballots for a given set of counties.
   */
  public static Long totalBallots(final Set<Long> countyIds) {
    if (countyIds.isEmpty()) {
      return 0L;
    }
    final Session s = Persistence.currentSession();
    final Query q =
      s.createNativeQuery("with county_ballots as " +
                    "(select max(sequence_end) as ballots " +
                    "from ballot_manifest_info " +
                    "where county_id in (:countyIds) " +
                    "group by county_id) " +
                    "select sum(ballots) from county_ballots");
    q.setParameter("countyIds", countyIds);

    final Optional<BigDecimal> res = q.uniqueResultOptional();

    if (res.isPresent()) {
      return res.get().longValue();
    } else {
      return 0L;
    }

  }
}
