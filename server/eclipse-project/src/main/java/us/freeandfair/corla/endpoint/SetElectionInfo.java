/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import javax.persistence.PersistenceException;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.SubmittedElectionInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the election information.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SetElectionInfo extends AbstractDoSDashboardEndpoint {
  /**
   * The "random seed" parameter.
   */
  public static final String RANDOM_SEED = "random_seed";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/set-election-info";
  }
  
  /**
   * Attempts to set the election information.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  @SuppressWarnings("PMD.UselessParentheses")
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final SubmittedElectionInfo info = 
          Main.GSON.fromJson(the_request.body(), SubmittedElectionInfo.class);
      if (info == null || (info.electionDate() == null && info.electionType() == null)) {
        badDataContents(the_response, "no election information specified");
      } else {
        final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(the_response, "could not set election info");
        } 
        if (info.electionDate() != null) {
          dosd.setElectionDate(info.electionDate());
          Main.LOGGER.info("election date set to " + info.electionDate());
        }
        if (info.electionType() != null) {
          dosd.setElectionType(info.electionType());
          Main.LOGGER.info("election type set to " + info.electionType());
        }
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to set election information: " + e);

    } catch (final JsonSyntaxException e) {
      badDataContents(the_response, "malformed election information specified");
    }
    return my_endpoint_result.get();
  }
}
