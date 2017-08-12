/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.asm;

import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.ASMState.CountyDashboardState;
import us.freeandfair.corla.asm.ASMTransitionFunction.CountyDashboardTransitionFunction;

/**
 * The ASM for the County Dashboard.
 * @trace asm.dos_dashboard_next_state
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
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
      {CountyDashboardState.UPLOAD_BALLOT_MANIFEST_TOO_LATE,
       CountyDashboardState.UPLOAD_CVRS_TOO_LATE,
       CountyDashboardState.COUNTY_AUDIT_COMPLETE};
  
  /**
   * Create the County Dashboard ASM.
   * 
   * @param the_county_id The county identifier.
   * @trace asm.county_dashboard_asm
   */
  //@ requires the_county_id != null
  public CountyDashboardASM(final Integer the_county_id) {
    super(new HashSet<ASMState>(Arrays.asList(CountyDashboardState.values())),
          new HashSet<ASMEvent>(Arrays.asList(CountyDashboardEvent.values())),
          transitionsFor(Arrays.
                         asList(CountyDashboardTransitionFunction.values())),
          CountyDashboardState.COUNTY_INITIAL_STATE,
          new HashSet<ASMState>(Arrays.asList(FINAL_STATES)),
          String.valueOf(the_county_id));
  } 
}
