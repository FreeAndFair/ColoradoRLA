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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The contest by county download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ContestDownloadByCounty implements Endpoint {
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
    return "/contest/county/:counties";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    final String[] counties = the_request.params(":counties").split(",");
    final Set<String> county_set = new HashSet<String>(Arrays.asList(counties));
    final Set<Contest> contest_set = getMatchingContests(county_set);
    String result = "";
    int status = HttpStatus.OK_200;
    if (contest_set == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      result = "Error retrieving records from database";
    } else {
      try {
        final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
        final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        
        Main.GSON.toJson(contest_set, bw);
        bw.flush();
      } catch (final IOException e) {
        status = HttpStatus.INTERNAL_SERVER_ERROR_500;
        result = "Unable to stream response.";
      }
    }
    
    the_response.status(status);
    return result;
  }
  
  /**
   * Gets contests that are in the specified set of counties.
   * 
   * @param the_counties The counties.
   * @return the matching contests.
   */
  private Set<Contest> getMatchingContests(final Set<String> the_counties) {
    Set<Contest> result = null;
    
    try {
      final Set<Contest> query_results = new HashSet<>();
      Persistence.beginTransaction();
      //this is very naive - it should be a single query that joins CVRs and contests
      for (final String c : the_counties) {
        final CastVoteRecord template =
            new CastVoteRecord(RecordType.UPLOADED, null, c, null, null, 
                               null, null, null, null);
        for (final CastVoteRecord cvr : 
             Persistence.getMatching(template, CastVoteRecord.class)) {
          for (final CVRContestInfo i : cvr.contestInfo()) {
            query_results.add(i.contest());
          }
        }
      }
      Persistence.commitTransaction();
      result = query_results;
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Error retrieving CVRs from database: " + e);
    }
    
    return result;
  }
}