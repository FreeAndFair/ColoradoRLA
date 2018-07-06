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

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for selecting the contests to audit.
 * 
 * @author Daniel M Zimmerman
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SelectContestsForAudit extends AbstractDoSDashboardEndpoint {
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
    return "/select-contests";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return my_event.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void reset() {
    my_event.set(null);
  }
  
  /**
   * Attempts to select contests for audit. 
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public synchronized String endpointBody(final Request the_request, 
                                      final Response the_response) {
    try {
      final ContestToAudit[] contests = 
          Main.GSON.fromJson(the_request.body(), ContestToAudit[].class);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      if (dosdb == null) {
        Main.LOGGER.error("could not get department of state dashboard");
        serverError(the_response, "Could not select contests");
      } else {
        // unchecked contests are not posted so that which is not added, is removed.
        dosdb.removeContestsToAudit();
        for (final ContestToAudit c : contests) {
          Main.LOGGER.info("updating contest audit status: " + c);
          dosdb.updateContestToAudit(c);
          Persistence.saveOrUpdate(dosdb);
        }
        my_event.set(nextEvent(dosdb));
        ok(the_response, "Contests selected");
      }
    } catch (final JsonParseException e) {
      Main.LOGGER.error("malformed contest selection");
      badDataContents(the_response, "Invalid contest selection data");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save contest selection");
      serverError(the_response, "Unable to save contest selection");
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
