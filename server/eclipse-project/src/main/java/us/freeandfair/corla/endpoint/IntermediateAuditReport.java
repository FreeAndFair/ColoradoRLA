/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.IntermediateAuditReportInfo;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Publish the intermediate audit report by the audit board.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class IntermediateAuditReport extends AbstractAuditBoardDashboardEndpoint {
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
    return "/intermediate-audit-report";
  }

  /**
   * @return COUNTY authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT;
  }
  
  /**
   * Publish the intermediate audit report by the audit board.
   */
  @Override
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    try {
      final IntermediateAuditReportInfo report =
          Main.GSON.fromJson(the_request.body(), IntermediateAuditReportInfo.class);
      final CountyDashboard cdb = 
          Persistence.getByID(Main.authentication().authenticatedCounty(the_request).id(),
                              CountyDashboard.class);
      if (cdb == null) {
        Main.LOGGER.error("could not get audit board dashboard");
        serverError(the_response, "Could not save intermediate audit report");
      } else {
        cdb.submitIntermediateReport(report);
        // sign the audit board out
        cdb.signOutAuditBoard();

      }
      Persistence.saveOrUpdate(cdb);
    } catch (final JsonParseException e) {
      Main.LOGGER.error("malformed intermediate audit report");
      badDataContents(the_response, "Invalid intermediate audit report");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save intermediate audit report");
      serverError(the_response, "Unable to save intermediate audit report");
    }
    ok(the_response, "Report submitted");
    // de-authenticate user?
    return my_endpoint_result.get();    
  }
}
