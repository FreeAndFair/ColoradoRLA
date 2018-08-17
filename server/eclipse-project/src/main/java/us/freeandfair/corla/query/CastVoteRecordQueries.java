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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
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
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with CastVoteRecord entities.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class CastVoteRecordQueries {
  /**
   * The "county ID" field.
   */
  private static final String COUNTY_ID = "my_county_id";

  /**
   * The "cvr number" field.
   */
  private static final String SEQUENCE_NUMBER = "my_sequence_number";

  /**
   * The "record type" field.
   */
  private static final String RECORD_TYPE = "my_record_type";

  /**
   * The "could not query database for CVRs error message.
   */
  private static final String COULD_NOT_QUERY_DATABASE =
      "could not query database for CVRs";

  /**
   * Private constructor to prevent instantiation.
   */
  private CastVoteRecordQueries() {
    // do nothing
  }

  /**
   * Obtain a stream of CastVoteRecord objects with the specified type.
   *
   * @param the_type The type.
   * @return the stream of CastVoteRecord objects, or null if one could
   * not be acquired.
   * @exception IllegalStateException if this method is called outside a
   * transaction.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static Stream<CastVoteRecord> getMatching(final RecordType the_type) {
    if (!Persistence.isTransactionActive()) {
      throw new IllegalStateException("no running transaction");
    }

    Stream<CastVoteRecord> result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(root).where(cb.equal(root.get(RECORD_TYPE), the_type));
      final Query<CastVoteRecord> query = s.createQuery(cq);
      result = query.stream();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for type " + the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR stream");
    }
    return result;
  }

  /**
   * Counts the CastVoteRecord objects with the specified type.
   *
   * @param the_type The type.
   * @return the count, empty if the query could not be completed
   * successfully.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static OptionalLong countMatching(final RecordType the_type) {
    if (!Persistence.isTransactionActive()) {
      throw new IllegalStateException("no running transaction");
    }

    OptionalLong result = OptionalLong.empty();

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(cb.count(root)).where(cb.equal(root.get(RECORD_TYPE), the_type));
      final Query<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for type " + the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR stream");
    }
    return result;
  }

  /**
   * Obtain a stream of CastVoteRecord objects with the specified county
   * and type, ordered by their sequence number.
   *
   * @param the_county The county.
   * @param the_type The type.
   * @return the stream of CastVoteRecord objects, or null if one could
   * not be acquired.
   * @exception IllegalStateException if this method is called outside a
   * transaction.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static Stream<CastVoteRecord> getMatching(final Long the_county,
                                                   final RecordType the_type) {
    Stream<CastVoteRecord> result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      cq.orderBy(cb.asc(root.get(SEQUENCE_NUMBER)));
      final Query<CastVoteRecord> query = s.createQuery(cq);
      result = query.stream();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for county " + the_county +
                        ", type " + the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR stream");
    }
    return result;
  }

  /**
   * Counts the CastVoteRecord objects with the specified county and type.
   *
   * @param the_county_id The county.
   * @param the_type The type.
   * @return the count, empty if the query could not be completed
   * successfully.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static OptionalLong countMatching(final Long the_county,
                                           final RecordType the_type) {
    OptionalLong result = OptionalLong.empty();

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.select(cb.count(root));
      cq.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final Query<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for county " + the_county +
                        ", type " + the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR stream");
    }
    return result;
  }

  /**
   * Deletes the set of cast vote records for the specified county ID and
   * record type.
   *
   * @param the_county_id The county ID.
   * @param the_type The record type.
   * @return the number of records deleted.
   * @exception PersistenceException if the cast vote records cannot be deleted.
   */
  public static int deleteMatching(final Long the_county_id,
                                    final RecordType the_type) {
    final AtomicInteger count = new AtomicInteger();
    final Session s = Persistence.currentSession();
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
    final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
    cq.where(cb.and(cb.equal(root.get(COUNTY_ID), the_county_id),
                    cb.equal(root.get(RECORD_TYPE), the_type)));
    final Query<CastVoteRecord> query = s.createQuery(cq);
    final Stream<CastVoteRecord> to_delete = query.stream();
    to_delete.forEach((the_cvr) -> {
      Persistence.delete(the_cvr);
      count.incrementAndGet();
    });
    return count.get();
  }

  /**
   * Obtain the CastVoteRecord object with the specified county, type,
   * and sequence number.
   *
   * @param the_county_id The county.
   * @param the_type The type.
   * @param the_sequence_number.
   * @return the matching CastVoteRecord object, or null if no objects match
   * or the query fails.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static CastVoteRecord get(final Long the_county_id,
                                   final RecordType the_type,
                                   final Integer the_sequence_number) {
    CastVoteRecord result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      conjuncts.add(cb.equal(root.get(SEQUENCE_NUMBER), the_sequence_number));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      final List<CastVoteRecord> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVR for county " + the_county_id +
                        ", type " + the_type + ", sequence " + the_sequence_number);
    } else {
      Main.LOGGER.debug("found CVR " + result);
    }

    return result;
  }

  /**
   * Obtain the CastVoteRecord objects with the specified county, type, and
   * sequence numbers.
   *
   * @param the_county_id The county.
   * @param the_type The type.
   * @param the_sequence_numbers The sequence numbers.
   * @return the matching CastVoteRecord objects, mapped by sequence number,
   * an empty map if no records match, or null if the query fails.
   */
  public static Map<Integer, CastVoteRecord> get(final Long the_county_id,
                                                 final RecordType the_type,
                                                 final List<Integer> the_sequence_numbers) {
    Map<Integer, CastVoteRecord> result = null;
    final Set<Integer> unique_numbers = new HashSet<>(the_sequence_numbers);

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      conjuncts.add(root.get("my_sequence_number").in(unique_numbers));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      final List<CastVoteRecord> query_results = query.getResultList();
      result = new HashMap<>();
      for (final CastVoteRecord cvr : query_results) {
        result.put(cvr.sequenceNumber(), cvr);
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for county " + the_county_id +
                        ", type " + the_type + ", sequence " + unique_numbers);
    } else {
      Main.LOGGER.debug("found " + result.keySet().size() + "CVRs ");
    }

    return result;
  }

  /**
   * Obtain the CastVoteRecord objects with the specified IDs.
   *
   * @param the_ids The IDs.
   * @return the matching CastVoteRecord objects, an empty list if none are found,
   * or null if the query fails.
   */
  public static List<CastVoteRecord> get(final List<Long> the_ids) {
    List<CastVoteRecord> result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<>();
      conjuncts.add(root.get("my_id").in(the_ids));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs with ids " + the_ids);
    } else {
      Main.LOGGER.debug("found " + result.size() + "CVRs ");
    }

    return result;
  }

  /**
   * join query
   **/
  public static CastVoteRecord atPosition(final Long county_id,
                                          final Integer scanner_id,
                                          final String batch_id,
                                          final Long position) {
    List<CastVoteRecord> result = null;

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(root).where(cb.and(cb.equal(root.get("my_county_id"), county_id),
                                   cb.equal(root.get("my_scanner_id"), scanner_id),
                                   cb.equal(root.get("my_batch_id"), batch_id),
                                   cb.equal(root.get("my_record_id"), position)));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }

    switch (result.size()) {
      case 1:
        return result.get(0);
      case 0:
        return null;
      default:
        Main.LOGGER.error("found more than one cvr atPosition: \n" + result);
        return result.get(0);
    }
  }
}
