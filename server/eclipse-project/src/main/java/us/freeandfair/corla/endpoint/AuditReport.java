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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_REPORT_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COMPLETE_AUDIT_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.CountyDashboardState.*;

import java.util.HashSet;
import java.util.Set;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Publish the intermediate audit report by the audit board.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuditReport extends AbstractAuditBoardDashboardEndpoint {
  /**
   * The set of states that indicate a county is "done" with the audit.
   */
  private static final Set<ASMState> AUDIT_DONE_STATES = new HashSet<>();
  
  static {
    // a county is done if they're not auditing
    AUDIT_DONE_STATES.add(COUNTY_INITIAL_STATE); 
    // a county is done if they're done
    AUDIT_DONE_STATES.add(COUNTY_AUDIT_COMPLETE);
    // a county is done if they uploaded something too late
    AUDIT_DONE_STATES.add(UPLOAD_BALLOT_MANIFEST_TOO_LATE);
    AUDIT_DONE_STATES.add(UPLOAD_CVRS_TOO_LATE);
  }
  
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
    return "/audit-report";
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
    return SUBMIT_AUDIT_REPORT_EVENT;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request,
                         final Response the_response) {
    try {
      if (!DISABLE_ASM) {
        ASMUtilities.step(COMPLETE_AUDIT_EVENT, CountyDashboardASM.class, my_asm.identity());
        // check to see if all counties are complete
        boolean all_complete = true;
        for (final County c : Persistence.getAll(County.class)) {
          final CountyDashboardASM asm = 
              ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(c.identifier()));
          all_complete &= AUDIT_DONE_STATES.contains(asm.currentState());           
        }
        if (all_complete) {
          ASMUtilities.step(AUDIT_COMPLETE_EVENT, DoSDashboardASM.class, null);
        } else {
          ASMUtilities.step(COUNTY_AUDIT_COMPLETE_EVENT, DoSDashboardASM.class, null);
        }
      }
      ok(the_response, "Final audit report saved (actual action to be specified by CDOS)");
    } catch (final IllegalStateException e) {
      illegalTransition(the_response, e.getMessage());
    }
    return my_endpoint_result;
  }
}
