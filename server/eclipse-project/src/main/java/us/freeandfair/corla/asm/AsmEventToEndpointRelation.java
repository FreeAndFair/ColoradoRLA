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

import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.REPORT_BALLOT_NOT_FOUND_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.REPORT_MARKINGS_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.SUBMIT_AUDIT_REPORT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.AuditBoardDashboardEvent.SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.ESTABLISH_AUDIT_BOARD_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.START_AUDIT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.UPLOAD_BALLOT_MANIFEST_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.CountyDashboardEvent.UPLOAD_CVRS_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.AUTHENTICATE_STATE_ADMINISTRATOR_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.INDICATE_FULL_HAND_COUNT_CONTEST_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.PUBLIC_SEED_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.PUBLISH_AUDIT_REPORT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.PUBLISH_BALLOTS_TO_AUDIT_EVENT;
import static us.freeandfair.corla.asm.AsmEvent.DosDashboardEvent.SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT;

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.asm.UiToAsmEventRelation.UiEvent;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.util.Pair;

/**
 * @description The mapping between ASM events and server endpoints.
 * @trace asm.ui_to_asm_event_relation 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 * @todo kiniry Introduce AbstractRelation parent class.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class AsmEventToEndpointRelation {
  /**
   * A constant encoding that we have not yet implemented a particular endpoint.
   */
  public static final String UNIMPLEMENTED = "UNIMPLEMENTED";
  
  /**
   * The relation encoded via a set of pairs.
   */
  private final Set<Pair<AsmEvent, String>> my_relation = 
      new HashSet<Pair<AsmEvent, String>>();
   
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
    my_relation.add(new Pair<AsmEvent, String>(
        AUTHENTICATE_STATE_ADMINISTRATOR_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        SELECT_CONTESTS_FOR_COMPARISON_AUDIT_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        PUBLIC_SEED_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        PUBLISH_BALLOTS_TO_AUDIT_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        INDICATE_FULL_HAND_COUNT_CONTEST_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        PUBLISH_AUDIT_REPORT_EVENT,
        UNIMPLEMENTED));
  }
  
  private void addCountyDashboardPairs() {
    // All County Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, String>(
        AUTHENTICATE_COUNTY_ADMINISTRATOR_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        ESTABLISH_AUDIT_BOARD_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        UPLOAD_BALLOT_MANIFEST_EVENT,
        "BallotManifestUpload"));
    my_relation.add(new Pair<AsmEvent, String>(
        UPLOAD_CVRS_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        START_AUDIT_EVENT,
        UNIMPLEMENTED));
  }
  
  private void addAuditBoardDashboardPairs() {
    // All Audit Board Dashboard pairs.
    my_relation.add(new Pair<AsmEvent, String>(
        REPORT_MARKINGS_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        REPORT_BALLOT_NOT_FOUND_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        SUBMIT_AUDIT_REPORT_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<AsmEvent, String>(
        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
        UNIMPLEMENTED));
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
