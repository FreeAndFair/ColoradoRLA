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

import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.Session;
import org.hibernate.query.Query;

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
public class CVRDownloadByCounty implements Endpoint {
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
    return "/cvr/county";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    final Set<String> county_set = the_request.queryParams();
    String result = "";
    int status = HttpStatus.OK_200;
    
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      final Set<CastVoteRecord> matches = getMatching(county_set);
      if (matches == null) {
        result = "Unable to fetch records from database";
        status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      } else {
        Main.GSON.toJson(matches, bw);
        bw.flush();
        result =  "";
      }
    } catch (final IOException e) {
      status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      result = "Unable to stream response.";
    }
    
    the_response.status(status);
    return result;
  }
  
  
  /**
   * Returns the set of ACVRs matching the specified county IDs.
   * 
   * @param the_county_ids The set of county IDs.
   * @return the ACVRs matching the specified set of county IDs.
   */
  private Set<CastVoteRecord> getMatching(final Set<String> the_county_ids) {
    Set<CastVoteRecord> result = null;

    try {
      final Set<CastVoteRecord> query_result = new HashSet<>();
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      for (final String county_id : the_county_ids) {
        final Query<CastVoteRecord> query = 
            s.createQuery("from CastVoteRecord where county_id = '" + 
                          county_id + "' and record_type = '" + 
                          RecordType.UPLOADED + "'", 
                          CastVoteRecord.class);
        query_result.addAll(query.getResultList());
      }
      Persistence.commitTransaction();
      result = query_result;
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading uploaded CVRs from database: " + e);
    }

    return result;
  }
}
