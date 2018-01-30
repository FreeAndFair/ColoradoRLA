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

import spark.Request;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.CountyDashboardASM;

/**
 * Functionality that spans endpoints on the Department of State Dashboard.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class AbstractCountyDashboardEndpoint extends AbstractEndpoint {
  /**
   * @return State authorization is required for these endpoints.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }
  
  /**
   * @return subclasses of this endpoint use the Department of State ASM.
   */
  @Override
  protected Class<CountyDashboardASM> asmClass() {
    return CountyDashboardASM.class;
  }
  
  /**
   * Gets the ASM identity for the specified request.
   * 
   * @param the_request The request.
   * @return the county ID of the authenticated county.
   */
  @Override
  protected String asmIdentity(final Request the_request) {
    return String.valueOf(Main.authentication().authenticatedCounty(the_request).id());
  }
}
