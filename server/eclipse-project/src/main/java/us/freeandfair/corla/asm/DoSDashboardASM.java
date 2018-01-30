/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
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
import us.freeandfair.corla.util.SetCreator;

/**
 * The ASM for the Department of State Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@DiscriminatorValue(value = "DoSDashboardASM")
public class DoSDashboardASM extends AbstractStateMachine {
  /**
   * The identity of the singleton DoS dashboard.
   */
  public static final String IDENTITY = "DoS";
  
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
          SetCreator.setOf(FINAL_STATES),
          IDENTITY); // there is only one DoS dashboard
  }
}
