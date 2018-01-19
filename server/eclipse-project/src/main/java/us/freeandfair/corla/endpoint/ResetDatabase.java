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
import spark.Response;

import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DatabaseResetQueries;
import us.freeandfair.corla.query.PersistentASMStateQueries;

/**
 * Reset the database, except for authentication information and uploaded
 * artifact data (the latter is cleaned up at the database level, not by this 
 * code).
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the endpoint method here is long and has lots of loops, but is not
// at all difficult to understand
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity",
    "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.NPathComplexity"})
public class ResetDatabase extends AbstractEndpoint {
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
    return "/reset-database";
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String asmIdentity(final Request the_request) {
    return null;
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public Class<AbstractStateMachine> asmClass() {
    return null;
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
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    // delete everything
    
    DatabaseResetQueries.resetDatabase();
    
    // create new dashboards
    final DoSDashboard dosdb = new DoSDashboard();
    Persistence.saveOrUpdate(dosdb);
    
    for (final County c : Persistence.getAll(County.class)) {
      final CountyDashboard cdb = new CountyDashboard(c);
      Persistence.saveOrUpdate(cdb);
    }
    
    // reset the DoS dashboard ASM state
    final PersistentASMState dos_asm = 
        PersistentASMStateQueries.get(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);
    dos_asm.updateFrom(new DoSDashboardASM());
    Persistence.saveOrUpdate(dos_asm);
    
    // for each County, reset the states of its ASMs
    for (final County c : Persistence.getAll(County.class)) {
      final String id = String.valueOf(c.id());
      final PersistentASMState county_asm = 
          PersistentASMStateQueries.get(CountyDashboardASM.class, id);
      if (county_asm != null) {
        county_asm.updateFrom(new CountyDashboardASM(id));
        Persistence.saveOrUpdate(county_asm);
      }
      final PersistentASMState audit_asm =
          PersistentASMStateQueries.get(AuditBoardDashboardASM.class, id);
      if (audit_asm != null) {
        audit_asm.updateFrom(new AuditBoardDashboardASM(id));
        Persistence.saveOrUpdate(audit_asm);
      }
    }

    ok(the_response, "database reset");
    return my_endpoint_result.get();
  }
}
