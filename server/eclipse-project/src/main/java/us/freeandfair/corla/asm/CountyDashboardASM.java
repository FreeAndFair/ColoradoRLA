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

import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.CountyDashboardTransitionFunction;
import us.freeandfair.corla.util.SetCreator;

/**
 * The ASM for the County Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@DiscriminatorValue(value = "CountyDashboardASM")
public class CountyDashboardASM extends AbstractStateMachine {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1;

  /**
   * The final states of this ASM.
   */
  private static final ASMState[] FINAL_STATES =
      {CountyDashboardState.DEADLINE_MISSED,
       CountyDashboardState.COUNTY_AUDIT_COMPLETE};

  /**
   * Create the County Dashboard ASM.
   * 
   * @param the_county_id The county identifier.
   * @trace asm.county_dashboard_asm
   */
  //@ requires the_county_id != null
  public CountyDashboardASM(final String the_county_id) {
    super(new HashSet<ASMState>(Arrays.asList(CountyDashboardState.values())),
          new HashSet<ASMEvent>(Arrays.asList(CountyDashboardEvent.values())),
          transitionsFor(Arrays.
                         asList(CountyDashboardTransitionFunction.values())),
          CountyDashboardState.COUNTY_INITIAL_STATE,
          SetCreator.setOf(FINAL_STATES),
          the_county_id);
  }
}
