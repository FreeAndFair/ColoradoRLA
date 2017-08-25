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
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PUBLISH_AUDIT_REPORT_EVENT;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;

/**
 * Download all of the data relevant to public auditing of a RLA.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class PublishAuditReport extends AbstractDoSDashboardEndpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/publish-audit-report";
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
    return PUBLISH_AUDIT_REPORT_EVENT;
  }
  
  /**
   * Download all of the data relevant to public auditing of a RLA.
   */
  @Override
  public String endpoint(final Request the_request,
                         final Response the_response) {
    ok(the_response, "Publish the audit report for the entire state-wide RLA.");
    return my_endpoint_result.get();
  }
}
