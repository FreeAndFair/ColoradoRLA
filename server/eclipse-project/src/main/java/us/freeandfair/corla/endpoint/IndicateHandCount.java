/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.INDICATE_FULL_HAND_COUNT_CONTEST_EVENT;

import javax.persistence.PersistenceException;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for indicating that a contest must be hand-counted.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class IndicateHandCount extends AbstractDoSDashboardEndpoint {
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
    return "/hand-count";
  }

  /**
   * @return STATE authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return INDICATE_FULL_HAND_COUNT_CONTEST_EVENT;
  }
  
  /**
   * Indicate that a contest must be hand-counted.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public synchronized String endpoint(final Request the_request, 
                                      final Response the_response) {
    ok(the_response, "Contests selected");
    try {
      final ContestToAudit[] contests = 
          Main.GSON.fromJson(the_request.body(), ContestToAudit[].class);
      final DepartmentOfStateDashboard dosdb = 
          DepartmentOfStateDashboardQueries.get();
      if (dosdb == null) {
        Main.LOGGER.error("could not get department of state dashboard");
        serverError(the_response, "Could not select contests");
      } else {
        for (final ContestToAudit c : contests) {
          Main.LOGGER.info("updating contest audit status: " + c);
          dosdb.updateContestToAudit(c);
        }
      }
      Persistence.saveOrUpdate(dosdb);
    } catch (final JsonSyntaxException e) {
      Main.LOGGER.error("malformed contest selection");
      badDataContents(the_response, "Invalid contest selection data");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save contest selection");
      serverError(the_response, "Unable to save contest selection");
    }
    return my_endpoint_result;
  }
}
