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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInvestigationReportInfo;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Submit an audit investigation report.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
// TODO: consider rewriting along the same lines as CVRExportUpload
public class AuditInvestigationReport extends AbstractAuditBoardDashboardEndpoint {
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
    return "/audit-investigation-report";
  }

  /**
   * @return COUNTY authorization is required for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT;
  }

  /**
   * Submit an audit investigation report.
   */
  @Override
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    try {
      final AuditInvestigationReportInfo report =
          Main.GSON.fromJson(the_request.body(), AuditInvestigationReportInfo.class);
      final CountyDashboard cdb = 
          Persistence.getByID(Main.authentication().authenticatedCounty(the_request).id(), 
                              CountyDashboard.class);
      if (cdb == null) {
        Main.LOGGER.error("could not get audit board dashboard");
        serverError(the_response, "Could not save audit investigation report");
      } else {
        cdb.submitInvestigationReport(report);
      }
      Persistence.saveOrUpdate(cdb);
    } catch (final JsonParseException e) {
      Main.LOGGER.error("malformed audit investigation report");
      badDataContents(the_response, "Invalid audit investigation report");
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not save audit investigation report");
      serverError(the_response, "Unable to save audit investigation report");
    }
    ok(the_response, "Report submitted");
    return my_endpoint_result.get();
  }
}
