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

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.PersistentASMStateQueries;

/**
 * Reset the database, except for authentication information and uploaded
 * artifact data (the latter is cleaned up at the database level, not by this 
 * code).
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
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
  protected ASMEvent endpointEvent() {
    return DoSDashboardEvent.DOS_SKIP_EVENT;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request,
                         final Response the_response) {
    // delete all the dashboards
    for (final DoSDashboard db : Persistence.getAll(DoSDashboard.class)) {
      Persistence.delete(db);
    }
    for (final CountyDashboard db : Persistence.getAll(CountyDashboard.class)) {
      Persistence.delete(db);
    }
    
    // reset the DoS dashboard ASM state
    final PersistentASMState dos_asm = 
        PersistentASMStateQueries.get(DoSDashboardASM.class, null);
    dos_asm.updateFrom(new DoSDashboardASM());

    // delete all the CVRs 
    for (final CastVoteRecord cvr : Persistence.getAll(CastVoteRecord.class)) {
      Persistence.delete(cvr);
    }
    
    // delete all the ballot manifests
    for (final BallotManifestInfo bmi : Persistence.getAll(BallotManifestInfo.class)) {
      Persistence.delete(bmi);
    }
    
    // delete all the Electors
    for (final Elector e : Persistence.getAll(Elector.class)) {
      Persistence.delete(e);
    }
    
    // for each County, reset its state and the states of its ASMs
    for (final County c : Persistence.getAll(County.class)) {
      c.contests().clear();
      final String id = String.valueOf(c.identifier());
      final PersistentASMState county_asm = 
          PersistentASMStateQueries.get(CountyDashboardASM.class, id);
      if (county_asm != null) {
        county_asm.updateFrom(new CountyDashboardASM(id));
      }
      final PersistentASMState audit_asm =
          PersistentASMStateQueries.get(AuditBoardDashboardASM.class, id);
      if (audit_asm != null) {
        audit_asm.updateFrom(new AuditBoardDashboardASM(id));
      }
    }
    
    // delete all the Contests
    for (final Contest c : Persistence.getAll(Contest.class)) {
      Persistence.delete(c);
    }
    
    // delete all the uploaded files
    for (final UploadedFile uf : Persistence.getAll(UploadedFile.class)) {
      Persistence.delete(uf);
    }
    
    ok(the_response, "database reset; run vacuumlo on the database to " + 
                     "recover space from large object storage");
    return my_endpoint_result;
  }
}
