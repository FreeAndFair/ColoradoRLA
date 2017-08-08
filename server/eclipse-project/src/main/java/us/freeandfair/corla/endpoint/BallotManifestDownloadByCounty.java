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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.Session;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The ballot manifest by county download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotManifestDownloadByCounty implements Endpoint {
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
    return "/ballot-manifest/county";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    final Set<String> county_set = the_request.queryParams();
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      
      Main.GSON.toJson(getMatching(county_set), bw);
      bw.flush();
      return "";
    } catch (final IOException e) {
      the_response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      return "Unable to stream response.";
    }
  }
  
  /**
   * Returns the set of ballot manifests matching the specified county IDs.
   * 
   * @param the_county_ids The set of county IDs.
   * @return the ballot manifests matching the specified set of county IDs.
   */
  private Set<BallotManifestInfo> getMatching(final Set<String> the_county_ids) {
    final Set<BallotManifestInfo> result = new HashSet<>();
    
    try {
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<BallotManifestInfo> cq = 
          cb.createQuery(BallotManifestInfo.class);
      final Root<BallotManifestInfo> root = cq.from(BallotManifestInfo.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final String county_id : the_county_ids) {
        disjuncts.add(cb.equal(root.get("my_county_id"), county_id));
      }
      cq.select(root).where(cb.or(disjuncts.toArray(new Predicate[disjuncts.size()])));
      final TypedQuery<BallotManifestInfo> query = s.createQuery(cq);
      result.addAll(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ballot manifests from database: " + e);
    }

    return result;
  }
}
