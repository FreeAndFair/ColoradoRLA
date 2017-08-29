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

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.json.SubmittedRandomSeed;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the random seed.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SetRandomSeed extends AbstractDoSDashboardEndpoint {
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
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final SubmittedRandomSeed seed = 
          Main.GSON.fromJson(the_request.body(), SubmittedRandomSeed.class);
      if (DoSDashboard.isValidSeed(seed.seed())) {
        final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(the_response, "could not set random seed");
        } else {
          dosd.setRandomSeed(seed.seed());
          Persistence.saveOrUpdate(dosd);
          ok(the_response, "random seed set to " + seed.seed());
        }
      } else {
        invariantViolation(the_response, "Invalid random seed specified: " + seed.seed());
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to set random seed: " + e);

    } catch (final JsonSyntaxException e) {
      badDataContents(the_response, "Invalid random seed request");
    }
    return my_endpoint_result.get();
  }
}
