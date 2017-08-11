/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent;
import us.freeandfair.corla.asm.ASMState.DoSDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.DoSDashboardTransitionFunction;

/**
 * The ASM for the Department of State Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@Entity
@DiscriminatorValue(value = "DoSDashboardASM")
public class DoSDashboardASM extends AbstractStateMachine {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 

  /**
   * The final states of this ASM.
   */
  private static final ASMState[] FINAL_STATES = 
      {DoSDashboardState.AUDIT_RESULTS_PUBLISHED};

  /**
   * Create the Department of State Dashboard ASM.
   * @trace asm.dos_asm 
   */
  public DoSDashboardASM() {
    super(new HashSet<ASMState>(Arrays.asList(DoSDashboardState.values())),
          new HashSet<ASMEvent>(Arrays.asList(DoSDashboardEvent.values())),
          transitionsFor(Arrays.
                         asList(DoSDashboardTransitionFunction.values())),
          DoSDashboardState.DOS_INITIAL_STATE,
          new HashSet<ASMState>(Arrays.asList(FINAL_STATES)),
          null); // there is only one DoS dashboard, so no identity specified
  }
}
