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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;

/**
 * Publish the intermediate audit report by the audit board.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
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
  public String endpoint(final Request the_request,
                         final Response the_response) {
    return "Save an intermediate audit report so that the audit board members " +
        "can take a break, go to lunch, go home for the night, etc.";
    // deauthenticate user
  }
}
