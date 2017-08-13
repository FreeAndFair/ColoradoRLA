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

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.ESTABLISH_AUDIT_BOARD_EVENT;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;

/**
 * Establish the audit board for a county.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class EstablishAuditBoard extends AbstractCountyDashboardEndpoint {
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
    return "/audit-board";
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
    return ESTABLISH_AUDIT_BOARD_EVENT;
  }
  
  /**
   * Establish the audit board for a county.
   */
  @Override
  public String endpoint(final Request the_request,
                         final Response the_response) {
    return "The audit board specified has been saved.";
  }
}
