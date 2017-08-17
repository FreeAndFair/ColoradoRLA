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

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with UploadedFile entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class UploadedFileQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private UploadedFileQueries() {
    // do nothing
  }
  
  /**
   * Obtain the UploadedFile object with a specific county identifier, 
   * timestamp, and type, if one exists.
   *
   * @param the_county_id The county ID.
   * @param the_timestamp The timestamp.
   * @param the_type The type.
   * @return the matched UploadedFile, if one exists, or null otherwise.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static UploadedFile matching(final Integer the_county_id,
                                      final Instant the_timestamp,
                                      final UploadedFile.FileType the_type) {
    UploadedFile result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<UploadedFile> cq = cb.createQuery(UploadedFile.class);
      final Root<UploadedFile> root = cq.from(UploadedFile.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get("my_county_id"), the_county_id));
      conjuncts.add(cb.equal(root.get("my_timestamp"), the_timestamp));
      conjuncts.add(cb.equal(root.get("my_type"), the_type));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<UploadedFile> query = s.createQuery(cq);
      final List<UploadedFile> query_results = query.getResultList();
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
      Main.LOGGER.error("could not query database for uploaded file");
    }
    if (result == null) {
      Main.LOGGER.debug("found no uploaded file for county " + the_county_id + 
                        ", timestamp " + the_timestamp + ", type " + 
                        the_type);
    } else {
      Main.LOGGER.debug("found uploaded file " + result);
    }
    return result;
  }
}
