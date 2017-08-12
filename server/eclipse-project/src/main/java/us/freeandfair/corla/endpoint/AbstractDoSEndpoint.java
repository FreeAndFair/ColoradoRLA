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

import us.freeandfair.corla.asm.DoSDashboardASM;

/**
 * Functionality that spans endpoints on the Department of State Dashboard.
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class AbstractDoSEndpoint extends AbstractEndpoint {
  /**
   * @return State authorization is required for these endpoints.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }
  
  /**
   * @return subclasses of this endpoint use the Department of State ASM.
   */
  @Override
  protected Class<DoSDashboardASM> asmClass() {
    return DoSDashboardASM.class;
  }
}
