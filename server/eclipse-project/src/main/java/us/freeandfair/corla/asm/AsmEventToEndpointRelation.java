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
import us.freeandfair.corla.endpoint.BallotManifestUpload;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.util.Pair;

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
  private final Set<Pair<AsmEvent, Class>> my_relation = 
      new HashSet<Pair<AsmEvent, Class>>();
   
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
    my_relation.add(new Pair<AsmEvent, Class>(
        AUTHENTICATE_STATE_ADMINISTRATOR_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        PUBLIC_SEED_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        PUBLISH_BALLOTS_TO_AUDIT_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        PUBLISH_AUDIT_REPORT_EVENT,
        Endpoint.class));
  }
  
  private void addCountyDashboardPairs() {
    // All County Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, Class>(
        AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        ESTABLISH_AUDIT_BOARD_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        UPLOAD_BALLOT_MANIFEST_EVENT,
        BallotManifestUpload.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        UPLOAD_CVRS_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        START_AUDIT_EVENT,
        Endpoint.class));
  }
  
  private void addAuditBoardDashboardPairs() {
    // All Audit Board Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, Class>(
        REPORT_MARKINGS_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        REPORT_BALLOT_NOT_FOUND_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        SUBMIT_AUDIT_REPORT_EVENT,
        Endpoint.class));
    my_relation.add(new Pair<AsmEvent, Class>(
        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
        Endpoint.class));
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
}
