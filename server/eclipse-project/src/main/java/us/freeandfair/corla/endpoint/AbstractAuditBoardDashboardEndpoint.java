/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import spark.Request;

import us.freeandfair.corla.asm.AuditBoardDashboardASM;

/**
 * Functionality that spans endpoints on the Audit Board Dashboard.
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class AbstractAuditBoardDashboardEndpoint extends AbstractEndpoint {
  /**
   * @return County authorization is required for these endpoints.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }
  
  /**
   * @return subclasses of this endpoint use the Department of State ASM.
   */
  @Override
  protected Class<AuditBoardDashboardASM> asmClass() {
    return AuditBoardDashboardASM.class;
  }
  
  /**
   * Gets the ASM identity for the specified request.
   * 
   * @param the_request The request.
   * @return the county ID of the authenticated county.
   */
  @Override
  protected String asmIdentity(final Request the_request) {
    return String.valueOf(Authentication.authenticatedCounty(the_request).id());
  }
}
