/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.Session;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The ballot manifest download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CVRDownload implements Endpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/cvr";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    String result = "";
    int status = HttpStatus.OK_200;
    
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      final Set<CastVoteRecord> matches = getMatching();
      if (matches == null) {
        result = "Unable to fetch records from database";
        status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      } else {
        Main.GSON.toJson(getMatching(), bw);
        bw.flush();
        result =  "";
      }
    } catch (final IOException e) {
      status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      result = "Unable to stream response";
    }
    
    the_response.status(status);
    return result;
  }
  
  /**
   * @return the set of cast vote records that were uploaded by counties,
   * or null if the query fails.
   */
  private Set<CastVoteRecord> getMatching() {
    Set<CastVoteRecord> result = null;
    
    try {
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = 
          cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(root).where(cb.equal(root.get("my_record_type"), 
                                     RecordType.UPLOADED));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      result = new HashSet<CastVoteRecord>(query.getResultList());
      try {
        Persistence.commitTransaction();
      } catch (final RollbackException e) {
        Persistence.rollbackTransaction();
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading CVRs from database: " + e);
    }

    return result;
  }
}
