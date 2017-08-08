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
public class ACVRDownload implements Endpoint {
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
    return "/acvr";
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
   * @return the set of cast vote records that were submitted by auditors.
   */
  private Set<CastVoteRecord> getMatching() {
    Set<CastVoteRecord> result = null;

    try {
      final Set<CastVoteRecord> query_result = new HashSet<>();
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final Query<CastVoteRecord> query = 
          s.createQuery("from CastVoteRecord where record_type = '" + 
              RecordType.AUDITOR_ENTERED + "'", CastVoteRecord.class);
      query_result.addAll(query.getResultList());
      Persistence.commitTransaction();
      result = query_result;
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading ACVRs from database: " + e);
    }

    return result;
  }
}
