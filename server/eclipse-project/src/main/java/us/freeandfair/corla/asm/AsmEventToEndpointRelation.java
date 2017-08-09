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

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.UiToAsmEventRelation.UiEvent;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.util.Pair;

import static us.freeandfair.corla.asm.AsmEventToEndpointRelation.Endpoint.*;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.*;

/**
 * @description The mapping between ASM events and server endpoints.
 * @trace asm.ui_to_asm_event_relation
 * @todo kiniry Introduce AbstractRelation parent class.
 */
public class AsmEventToEndpointRelation {
  /**
   * The relation encoded via a set of pairs.
   */
  private final Set<Pair<AsmEvent, Endpoint>> my_relation = 
      new HashSet<Pair<AsmEvent, Endpoint>>();
   
  /**
   * Create an instance of this relation, which contains the full set of public 
   * ASM events and Endpoints.
   * @design kiniry This should probably be refactored as a singleton.
   */
  public AsmEventToEndpointRelation() {
    addDosDashboardPairs();
    addCountyDashboardPairs();
    addAuditBoardDashboardPairs();
  }
  
  private void addDosDashboardPairs() {
    // All Department of State Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        AUTHENTICATE_STATE_ADMINISTRATOR_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        PUBLIC_SEED_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        PUBLISH_BALLOTS_TO_AUDIT_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        PUBLISH_AUDIT_REPORT_EVENT,
        UNDEFINED));
  }
  
  private void addCountyDashboardPairs() {
    // All County Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        ESTABLISH_AUDIT_BOARD_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        UPLOAD_BALLOT_MANIFEST_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        UPLOAD_CVRS_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        START_AUDIT_EVENT,
        UNDEFINED));
  }
  
  private void addAuditBoardDashboardPairs() {
    // All Audit Board Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        REPORT_MARKINGS_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        REPORT_BALLOT_NOT_FOUND_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        SUBMIT_AUDIT_REPORT_EVENT,
        UNDEFINED));
    my_relation.add(new Pair<AsmEvent, Endpoint>(
        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
        UNDEFINED));
  }
    
  /**
   * Is a_pair a member of this relation?
   * @param a_pair the UI event/AsmEvent pair to check.
   */
  public boolean member(final AsmEvent an_ae, final Endpoint an_e) {
    return my_relation.contains(new Pair<AsmEvent, Endpoint>(an_ae, an_e));
  }
  
  /**
   * Follow the relation from left to right.
   * @param a_ue the UI event to lookup.
   * @return the ASM event corresponding to 'a_ue', or null if no such event exists.
   */
  public AsmEvent rightArrow(final AsmEvent a_ae) {
    // @todo kiniry To be implemented.
    assert false;
    //@ assert false;
    return null;
  }
  
  /**
   * Follow the relation from right to left.
   * @param a_ae the ASM event to lookup.
   * @return the UI event corresponding to 'an_ae', or null if no such event exists.
   */
  public UiEvent leftArrow(final Endpoint an_e) {
    // @todo kiniry To be implemented.
    assert false;
    //@ assert false;
    return null;
  }
  
 /**
   * An enumeration of all public endpoints in the system.
   */
  enum Endpoint {
    UNDEFINED
  }
}
