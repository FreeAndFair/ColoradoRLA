/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.PersistenceException;

import com.google.gson.stream.JsonWriter;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The ballot manifest download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CVRDownloadByCounty extends AbstractEndpoint {
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
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  // necessary to break out of the lambda expression in case of IOException
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  public String endpointBody(final Request the_request, final Response the_response) {
    final Set<Long> county_set = new HashSet<Long>();
    for (final String s : the_request.queryParams()) {
      county_set.add(Long.valueOf(s));
    }
    try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
         JsonWriter jw = new JsonWriter(bw)) {
      jw.beginArray();
      for (final Long county : county_set) {
        final Stream<CastVoteRecord> matches = 
            CastVoteRecordQueries.getMatching(county, RecordType.UPLOADED);
        matches.forEach((the_cvr) -> {
          try {
            jw.jsonValue(Main.GSON.toJson(Persistence.unproxy(the_cvr)));
            Persistence.evict(the_cvr);
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          } 
        });
      }
      jw.endArray();
      jw.flush();
      jw.close();
      ok(the_response);
    } catch (final UncheckedIOException | IOException | PersistenceException e) {
      serverError(the_response, "Unable to stream response");
    }
    return my_endpoint_result.get();
  }
  
  /**
   * Validates the parameters of a request. For this endpoint, 
   * the paramter names must all be integers.
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
}
