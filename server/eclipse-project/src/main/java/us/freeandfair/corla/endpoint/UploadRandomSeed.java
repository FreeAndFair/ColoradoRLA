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

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.AuditStage;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for uploading the random seed.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class UploadRandomSeed implements Endpoint {
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
    return "/random-seed";
  }

  /**
   * Attempts to set the random seed for comparison audits. The random seed
   * should be provided as an integer in base 10, as Colorado rolls a
   * 10-sided die to determine each digit.
   * 
   * Session query parameters: <tt>random-seed</tt>
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    int status = HttpStatus.OK_200;
    String result = "Random seed set";
    String random_seed = null;
    
    if (Authentication.isAuthenticatedAs(the_request, AdministratorType.STATE)) {
      // see if a valid risk limit was passed in
      random_seed = the_request.queryParams(RANDOM_SEED);

      if (DepartmentOfStateDashboard.isValidSeed(random_seed)) {
        final DepartmentOfStateDashboard dosd = DepartmentOfStateDashboardQueries.get();
        if (dosd != null && dosd.auditStage() == AuditStage.PRE_AUDIT) {
          dosd.setRandomSeed(random_seed);
          try {
            Persistence.saveOrUpdate(dosd);
            Main.LOGGER.info("random seed set to " + random_seed);
          } catch (final PersistenceException e) {
            Main.LOGGER.error("unable to set random seed: " + e);
          }
        } else if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          status = HttpStatus.INTERNAL_SERVER_ERROR_500;
          result = "Could not set random seed";
        } else {
          Main.LOGGER.info("attempt to set the random seed " +
                           "in incorrect state " + dosd.auditStage());
          status = HttpStatus.FORBIDDEN_403;
          result = "Attempt to set the random seed in incorrect state";
        }
      } else {
        Main.LOGGER.info("attempt to set an invalid random seed");
        status = HttpStatus.BAD_REQUEST_400;
        result = "Invalid random seed specified";
      }
    } else {
      Main.LOGGER.info("unauthorized attempt to set the random seed");
      status = HttpStatus.UNAUTHORIZED_401;
      result = "Unauthorized attempt to set the random seed"; 
    }
    
    the_response.status(status);
    return result;
  }
}
