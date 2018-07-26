/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.COMPLETE_AUDIT_INFO_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PARTIAL_AUDIT_INFO_EVENT;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the random seed.
 *
 * @author Daniel M Zimmerman
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SetRandomSeed extends AbstractDoSDashboardEndpoint {
  /**
   * The "random seed" parameter.
   */
  public static final String RANDOM_SEED = "random_seed";

  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();

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
    return my_event.get();
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
  public String endpointBody(final Request the_request, final Response the_response) {
    try {
      final AuditInfo submitted =
          Main.GSON.fromJson(the_request.body(), AuditInfo.class);

      if (submitted == null) {
        badDataContents(the_response, "malformed random seed");
      } else if (DoSDashboard.isValidSeed(submitted.seed())) {
        // if the rest of the audit info isn't set, we can't set the seed
        final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosdb == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(the_response, "could not set random seed");
        }

        // anything in the submitted audit info that isn't a random seed is ignored
        final AuditInfo seed =
            new AuditInfo(null, null, null, submitted.seed(), null);
        dosdb.updateAuditInfo(seed);
        Persistence.saveOrUpdate(dosdb);
        my_event.set(nextEvent(dosdb));
        ok(the_response, "random seed set to " + seed.seed());
      } else {
        invariantViolation(the_response, "invalid random seed specified: " + submitted.seed());
      }
    } catch (final PersistenceException e) {

      serverError(the_response, "unable to set random seed: " + e);

    } catch (final JsonParseException e) {
      badDataContents(the_response, "malformed random seed");
    }
    return my_endpoint_result.get();
  }


  /**
   * Computes the event of this endpoint based on audit info completeness.
   *
   * @param the_dosdb The DoS dashboard.
   */
  private ASMEvent nextEvent(final DoSDashboard the_dosdb) {
    final ASMEvent result;
    final AuditInfo info = the_dosdb.auditInfo();

    if (info.electionDate() == null || info.electionType() == null ||
        info.publicMeetingDate() == null || info.riskLimit() == null) {
      Main.LOGGER.debug("partial audit information submitted");
      result = PARTIAL_AUDIT_INFO_EVENT;
    } else {
      Main.LOGGER.debug("complete audit information submitted");
      result = COMPLETE_AUDIT_INFO_EVENT;
    }

    return result;
  }
}
