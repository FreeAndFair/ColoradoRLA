/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.AuditBoardDashboardTransitionFunction;

/**
 * The ASM for the Audit Board Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@Entity
@DiscriminatorValue(value = "AuditBoardDashboardASM")
public class AuditBoardDashboardASM extends AbstractStateMachine {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 

  /**
   * The final states of this ASM.
   */
  private static final ASMState[] FINAL_STATES = 
      {AuditBoardDashboardState.AUDIT_REPORT_SUBMITTED,
       AuditBoardDashboardState.UNABLE_TO_AUDIT};
  
  /**
   * Create the Audit Board Dashboard ASM for the specified county.
   * 
   * @param the_county_id The county identifier.
   * @trace asm.county_dashboard_asm
   */
  //@ requires the_county_id != null;
  public AuditBoardDashboardASM(final String the_county_id) {
    super(new HashSet<ASMState>(Arrays.asList(AuditBoardDashboardState.values())),
          new HashSet<ASMEvent>(Arrays.asList(AuditBoardDashboardEvent.values())),
          transitionsFor(Arrays.
                         asList(AuditBoardDashboardTransitionFunction.values())),
          AuditBoardDashboardState.AUDIT_INITIAL_STATE,
          new HashSet<ASMState>(Arrays.asList(FINAL_STATES)),
          the_county_id);
  }
}
