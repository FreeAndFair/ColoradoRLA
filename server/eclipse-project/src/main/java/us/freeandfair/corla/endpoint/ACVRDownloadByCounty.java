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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The ballot manifest download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ACVRDownloadByCounty extends AbstractEndpoint implements Endpoint {
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
    return "/acvr/county";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    final Set<Integer> county_set = new HashSet<Integer>();
    for (final String s : the_request.queryParams()) {
      county_set.add(Integer.valueOf(s));
    }
    final Set<CastVoteRecord> matches = getMatching(county_set);
    if (matches == null) {
      serverError(the_response, "Error retrieving records from database");
    } else {
      try {
        final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
        final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

        Main.GSON.toJson(matches, bw);
        bw.flush();
      } catch (final IOException e) {
        serverError(the_response, "Unable to stream response");
      }
    }
    return my_endpoint_result;
  }
  
  /**
   * For this endpoint, the parameter names must all be integers.
   * 
   * @param the_request The request.
   * @return true if the parameters are valid, false otherwise.
   */
  protected boolean validateParameters(final Request the_request) {
    boolean result = true;
    
    for (final String s : the_request.queryParams()) {
      try {
        Integer.parseInt(s);
      } catch (final NumberFormatException e) {
        result = false;
        break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns the set of ACVRs matching the specified county IDs.
   * 
   * @param the_county_ids The set of county IDs.
   * @return the ACVRs matching the specified set of county IDs, or
   * null if the query fails.
   */
  private Set<CastVoteRecord> getMatching(final Set<Integer> the_county_ids) {
    Set<CastVoteRecord> result = null;
    
    try {
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<CastVoteRecord> cq = 
          cb.createQuery(CastVoteRecord.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final Integer county_id : the_county_ids) {
        disjuncts.add(cb.equal(root.get("my_county_id"), county_id));
      }
      cq.select(root).where(cb.and(cb.equal(root.get("my_record_type"), 
                                            RecordType.AUDITOR_ENTERED),
                                   cb.or(disjuncts.
                                         toArray(new Predicate[disjuncts.size()]))));
      final TypedQuery<CastVoteRecord> query = s.createQuery(cq);
      result = new HashSet<CastVoteRecord>(query.getResultList());
      try {
        Persistence.commitTransaction();
      } catch (final RollbackException e) {
        Persistence.rollbackTransaction();
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ACVRs from database: " + e);
    }

    return result;
  }
}
