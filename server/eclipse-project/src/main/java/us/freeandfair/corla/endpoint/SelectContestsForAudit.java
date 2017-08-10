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

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for selecting the contests to audit.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SelectContestsForAudit implements Endpoint {
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
    return "/select-contests";
  }

  /**
   * Attempts to select contests for audit. 
   * 
   * Session query parameters: <tt>random-seed</tt>
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public synchronized String endpoint(final Request the_request, 
                                      final Response the_response) {
    int status = HttpStatus.OK_200;
    String result = "Contests selected";

    try {
      final ContestToAudit[] contests = 
          Main.GSON.fromJson(the_request.body(), ContestToAudit[].class);
      final DepartmentOfStateDashboard dosdb = DepartmentOfStateDashboardQueries.get();
      if (dosdb == null) {
        Main.LOGGER.error("could not get department of state dashboard");
        result = "Could not select contests";
        status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      } else {
        for (final ContestToAudit c : contests) {
          dosdb.updateContestToAudit(c);
        }
      }
      Persistence.saveOrUpdate(dosdb);
    } catch (final JsonSyntaxException e) {
      Main.LOGGER.error("malformed contest selection");
      result = "Invalid contest selection data";
      status = HttpStatus.UNPROCESSABLE_ENTITY_422;
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save contest selection");
      result = "Unable to save contest selection";
      status = HttpStatus.INTERNAL_SERVER_ERROR_500;
    }
    
    the_response.status(status);
    return result;
  }
}
