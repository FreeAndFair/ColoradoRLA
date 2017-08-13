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

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PUBLIC_SEED_EVENT;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for uploading the random seed.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class UploadRandomSeed extends AbstractDoSDashboardEndpoint {
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
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return PUBLIC_SEED_EVENT;
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
    ok(the_response, "Random seed set");
    String random_seed = null;
    // see if a valid random seed was passed in
    random_seed = the_request.queryParams(RANDOM_SEED);
    if (DepartmentOfStateDashboard.isValidSeed(random_seed)) {
      final DepartmentOfStateDashboard dosd = 
          DepartmentOfStateDashboardQueries.get();
      if (dosd == null) {
        serverError(the_response, 
            "Could not get department of state dashboard to set random seed");
      } else {
        dosd.setRandomSeed(random_seed);
        try {
          Persistence.saveOrUpdate(dosd);
          Main.LOGGER.info("random seed set to " + random_seed);
        } catch (final PersistenceException e) {
          Main.LOGGER.error("unable to set random seed: " + e);
        }
      }
    } else {
      invariantViolation(the_response, "Invalid random seed specified");
    }
    return my_endpoint_result;
  }
}
