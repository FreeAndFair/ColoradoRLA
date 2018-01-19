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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.stream.JsonWriter;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The contest download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ContestDownload extends AbstractEndpoint {
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
    return "/contest";
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
  public String endpointBody(final Request the_request, final Response the_response) {
    // only return contests for counties that have finished their uploads
    final Set<County> county_set = new HashSet<>();
    for (final CountyDashboard cdb : Persistence.getAll(CountyDashboard.class)) {
      if (cdb.manifestFile() != null && cdb.cvrFile() != null) {
        county_set.add(cdb.county());
      }
    }
    final List<Contest> contest_list = ContestQueries.forCounties(county_set);
    try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
         JsonWriter jw = new JsonWriter(bw)) {
      jw.beginArray();
      for (final Contest contest : contest_list) {
        jw.jsonValue(Main.GSON.toJson(Persistence.unproxy(contest)));
        Persistence.evict(contest);
      } 
      jw.endArray();
      jw.flush();
      jw.close();
      ok(the_response);
    } catch (final IOException e) {
      serverError(the_response, "Unable to stream response");
    }
    return my_endpoint_result.get();
  }
}
