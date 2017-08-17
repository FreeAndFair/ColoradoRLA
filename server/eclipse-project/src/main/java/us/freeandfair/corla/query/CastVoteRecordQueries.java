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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
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
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class CastVoteRecordQueries {
  /**
   * The "county ID" field.
   */
  private static final String COUNTY_ID = "my_county_id";
  
  /**
   * The "imprinted ID" field.
   */
  private static final String IMPRINTED_ID = "my_imprinted_id";
  
  /**
   * The "record type" field.
   */
  private static final String RECORD_TYPE = "my_record_type";
  
  /**
   * The "timestamp" field.
   */
  private static final String TIMESTAMP = "my_timestamp";
  
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
   * Obtain a stream of CastVoteRecord objects with the specified type, 
   * ordered by their imprinted ID. This method <em>must</em>
   * be called from within a transaction, and the result stream must be
   * used within the same transaction.
   *
   * @param the_type The type.
   * @return the stream of CastVoteRecord objects, or null if one could
   * not be acquired.
   * @exception IllegalStateException if this method is called outside a 
   * transaction.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static Stream<CastVoteRecord> getMatching(final RecordType the_type) {
    if (!Persistence.isTransactionRunning()) {
      throw new IllegalStateException("no running transaction");
    }
    
    Stream<CastVoteRecord> result = null;
   
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(root).where(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.orderBy(cb.asc(root.get(IMPRINTED_ID)));
      final Query<CastVoteRecord> query = s.createQuery(cq);
      result = ((Query<CastVoteRecord>) query).stream();
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
   * and type, ordered by their imprinted ID. This method <em>must</em>
   * be called from within a transaction, and the result stream must be
   * used within the same transaction.
   *
   * @param the_county The county.
   * @param the_type The type.
   * @return the stream of CastVoteRecord objects, or null if one could
   * not be acquired.
   * @exception IllegalStateException if this method is called outside a 
   * transaction.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static Stream<CastVoteRecord> getMatching(final Integer the_county,
                                                   final RecordType the_type) {
    if (!Persistence.isTransactionRunning()) {
      throw new IllegalStateException("no running transaction");
    }
    
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
      cq.orderBy(cb.asc(root.get(IMPRINTED_ID)));
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
   * Obtain a stream of CastVoteRecord objects with the specified timestamp,
   * county, and type, ordered by their imprinted ID. This method <em>must</em>
   * be called from within a transaction, and the result stream must be
   * used within the same transaction.
   *
   * @param the_timestamp The timestamp.
   * @param the_county_id The county.
   * @param the_type The type.
   * @return the stream of CastVoteRecord objects, or null if one could not
   * be acquired.
   * @exception IllegalStateException if this method is called outside a 
   * transaction.
   */
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public static Stream<CastVoteRecord> getMatching(final Instant the_timestamp,
                                                   final Integer the_county_id,
                                                   final RecordType the_type) {
    if (!Persistence.isTransactionRunning()) {
      throw new IllegalStateException("no running transaction");
    }
    
    Stream<CastVoteRecord> result = null;
   
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(TIMESTAMP), the_timestamp));
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      cq.orderBy(cb.asc(root.get(IMPRINTED_ID)));
      final Query<CastVoteRecord> query = s.createQuery(cq);
      result = query.stream();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for timestamp " + the_timestamp + 
                        ", county " + the_county_id + ", type " + 
                        the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR stream");
    }
    return result;
  }
  
  /**
   * Obtain the CastVoteRecord object with the specified timestamp,
   * county, type, and imprinted ID. 
   *
   * @param the_timestamp The timestamp.
   * @param the_county_id The county.
   * @param the_type The type.
   * @param the_imprinted_id The imprinted ID.
   * @return the matching CastVoteRecord object, or null if no objects match
   * or the query fails.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static CastVoteRecord get(final Instant the_timestamp,
                                   final Integer the_county_id,
                                   final RecordType the_type,
                                   final String the_imprinted_id) {
    CastVoteRecord result = null;
   
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(TIMESTAMP), the_timestamp));
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      conjuncts.add(cb.equal(root.get(IMPRINTED_ID), the_imprinted_id));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      final List<CastVoteRecord> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVR for timestamp " + the_timestamp + 
                        ", county " + the_county_id + ", type " + 
                        the_type);
    } else {
      Main.LOGGER.debug("found CVR " + result);
    }
    
    return result;
  }
  
  /**
   * Counts the CastVoteRecord objects with the specified timestamp,
   * county, and type. 
   *
   * @param the_timestamp The timestamp.
   * @param the_county_id The county.
   * @param the_type The type.
   * @return the count, or -1 if the query could not be completed 
   * successfully.
   */
  public static Long countMatching(final Instant the_timestamp,
                                   final Integer the_county_id,
                                   final RecordType the_type) {
    Long result = Long.valueOf(-1);
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(TIMESTAMP), the_timestamp));
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.select(cb.count(root));
      cq.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final Query<Long> query = s.createQuery(cq);
      result = query.getSingleResult();
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for timestamp " + the_timestamp + 
                        ", county " + the_county_id + ", type " + 
                        the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR count");
    }
    return result;
  }
  
  /**
   * Obtain the list of CastVoteRecord database IDs with the specified timestamp,
   * county, and type, ordered by their imprinted ID. 
   *
   * @param the_timestamp The timestamp.
   * @param the_county_id The county.
   * @param the_type The type.
   * @return the list of IDs.
   * @exception IllegalStateException if this method is called outside a 
   * transaction.
   */
  public static List<Long> idsForMatching(final Instant the_timestamp,
                                          final Integer the_county_id,
                                          final RecordType the_type) {
    if (!Persistence.isTransactionRunning()) {
      throw new IllegalStateException("no running transaction");
    }
    
    List<Long> result = null;
   
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get(TIMESTAMP), the_timestamp));
      conjuncts.add(cb.equal(root.get(COUNTY_ID), the_county_id));
      conjuncts.add(cb.equal(root.get(RECORD_TYPE), the_type));
      cq.select(root.get("my_id"));
      cq.where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      cq.orderBy(cb.asc(root.get(IMPRINTED_ID)));
      final Query<Long> query = s.createQuery(cq);
      result = query.getResultList();
    } catch (final PersistenceException e) {
      Main.LOGGER.error(COULD_NOT_QUERY_DATABASE);
    }
    if (result == null) {
      Main.LOGGER.debug("found no CVRs for timestamp " + the_timestamp + 
                        ", county " + the_county_id + ", type " + 
                        the_type);
    } else {
      Main.LOGGER.debug("query succeeded, returning CVR IDs");
    }
    return result;
  }

}
