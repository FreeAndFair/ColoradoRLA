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

import us.freeandfair.corla.util.Pair;

import static us.freeandfair.corla.asm.UiToAsmEventRelation.UiEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.*;

/**
 * @description The mapping between UI events and ASM events.
 * @trace asm.ui_to_asm_event_relation
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
public class UiToAsmEventRelation {
  /**
   * The relation encoded via a set of pairs.
   */
  private final Set<Pair<UiEvent, AsmEvent>> my_relation = 
      new HashSet<Pair<UiEvent, AsmEvent>>();
  
  /**
   * Create an instance of this relation, which contains the full set of public 
   * inbound UI and ASM events.
   * @design kiniry This should probably be refactored as a singleton.
   */
  public UiToAsmEventRelation() {
    addDosDashboardPairs();
    addCountyDashboardPairs();
    addAuditBoardDashboardPairs();
  }
  
  private void addDosDashboardPairs() {
    // All Department of State Dashboard pairs.
    my_relation.add(new Pair<UiEvent, AsmEvent>(LOGIN, 
        AUTHENTICATE_STATE_ADMINISTRATOR_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        PUBLIC_SEED_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        PUBLISH_BALLOTS_TO_AUDIT_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        PUBLISH_AUDIT_REPORT_EVENT));
  }
  
  private void addCountyDashboardPairs() {
    // All County Dashboard pairs.
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        ESTABLISH_AUDIT_BOARD_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        UPLOAD_BALLOT_MANIFEST_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        UPLOAD_CVRS_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        START_AUDIT_EVENT));
  }
  
  private void addAuditBoardDashboardPairs() {
    // All Audit Board Dashboard pairs.
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        REPORT_MARKINGS_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        REPORT_BALLOT_NOT_FOUND_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        SUBMIT_AUDIT_REPORT_EVENT));
    my_relation.add(new Pair<UiEvent, AsmEvent>(UNDEFINED, 
        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT));
  }
  
  /**
   * Is a_pair a member of this relation?
   * @param a_pair the UI event/AsmEvent pair to check.
   */
  public boolean member(final UiEvent a_ue, final AsmEvent an_ae) {
    return my_relation.contains(new Pair<UiEvent, AsmEvent>(a_ue, an_ae));
  }
  
  /**
   * Follow the relation from left to right.
   * @param a_ue the UI event to lookup.
   * @return the ASM event corresponding to 'a_ue', or null if no such event exists.
   */
  public AsmEvent rightArrow(final UiEvent a_ue) {
    return null;
  }
  
  /**
   * Follow the relation from right to left.
   * @param a_ae the ASM event to lookup.
   * @return the UI event corresponding to 'an_ae', or null if no such event exists.
   */
  public UiEvent leftArrow(final AsmEvent an_ae) {
    return null;
  }
  
  /**
   * An enumeration of all user-triggered external inbound events in the client UI.
   */
  enum UiEvent {
    LOGIN,
    FETCH_INITIAL_STATE_SEND,
    FETCH_INITIAL_STATE_RECEIVE,
    SELECT_NEXT_BALLOT,
    UPDATE_BOARD_MEMBER,
    UPDATE_BALLOT_MARKS,
    UNDEFINED
  }
}
