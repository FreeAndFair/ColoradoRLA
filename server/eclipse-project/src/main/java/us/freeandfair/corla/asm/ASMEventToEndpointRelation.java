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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;

import java.util.HashSet;
import java.util.Set;

import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.util.Pair;

/**
 * @description The mapping between ASM events and server endpoints.
 * @trace asm.ui_to_asm_event_relation 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 * @todo kiniry Introduce AbstractRelation parent class.
 * @todo dmz use an entity instead of Pair<> to enable persistence
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ASMEventToEndpointRelation {
  /**
   * A constant encoding that we have not yet implemented a particular
   * endpoint.
   */
  public static final String UNIMPLEMENTED = "UNIMPLEMENTED";
  
  /**
   * The relation encoded via a set of pairs.
   */
  private final Set<Pair<ASMEvent, String>> my_relation = 
      new HashSet<Pair<ASMEvent, String>>();
   
  /**
   * Create an instance of this relation, which contains the full set
   * of public ASM events and Endpoints.
   * @design kiniry This should probably be refactored as a singleton.
   */
  public ASMEventToEndpointRelation() {
    addDoSDashboardPairs();
    addCountyDashboardPairs();
    addAuditBoardDashboardPairs();
  }
  
  private void addDoSDashboardPairs() {
    // All Department of State Dashboard pairs.
    my_relation.add(new Pair<ASMEvent, String>(
        PARTIAL_AUDIT_INFO_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        PUBLIC_SEED_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        DOS_START_ROUND_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        PUBLISH_AUDIT_REPORT_EVENT,
        UNIMPLEMENTED));
  }
  
  private void addCountyDashboardPairs() {
    // All County Dashboard pairs.
    my_relation.add(new Pair<ASMEvent, String>(
        IMPORT_BALLOT_MANIFEST_EVENT,
        "BallotManifestUpload"));
    my_relation.add(new Pair<ASMEvent, String>(
        IMPORT_CVRS_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        COUNTY_START_AUDIT_EVENT,
        UNIMPLEMENTED));
  }
  
  private void addAuditBoardDashboardPairs() {
    // All Audit Board Dashboard pairs.
    my_relation.add(new Pair<ASMEvent, String>(
        REPORT_MARKINGS_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        REPORT_BALLOT_NOT_FOUND_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        SUBMIT_AUDIT_INVESTIGATION_REPORT_EVENT,
        UNIMPLEMENTED));
    my_relation.add(new Pair<ASMEvent, String>(
        SUBMIT_INTERMEDIATE_AUDIT_REPORT_EVENT,
        UNIMPLEMENTED));
  }
    
  /**
   * Is a_pair a member of this relation?
   * @param a_pair the UIEvent/ASMEvent pair to check.
   */
  public boolean member(final ASMEvent an_ae, final Endpoint an_e) {
    return my_relation.contains(new Pair<ASMEvent, Endpoint>(an_ae, an_e));
  }

  // @todo kiniry Do we need these arrows anymore, especially given
  // that relations are not 1-1?

  /**
   * Follow the relation from left to right.
   * @param a_ae the ASM event to lookup.
   * @return the endpoints corresponding to 'a_ae', or null if no such
   * endpoints exists.
   */
  public Set<String> rightArrow(final ASMEvent a_ae) {
    // iterate over all elements in the map and, for each one whose
    // left element matches a_ae, include the right element in the
    // resulting set.
    final Set<String> result = new HashSet<String>();
    for (final Pair<ASMEvent, String> p : my_relation) {
      if (p.first().equals(a_ae)) {
        result.add(p.second());
      }
    }
    return result;
  }
  
  /**
   * Follow the relation from right to left.
   * @param an_endpoint the endpoint to lookup.
   * @return the ASM events corresponding to 'an_endpoint', or null if
   * no such events exists.
   */
  public Set<ASMEvent> leftArrow(final String an_endpoint) {
    // iterate over all elements in the map and, for each one whose
    // right element matches an_ae, include the left element in the
    // resulting set.
    final Set<ASMEvent> result = new HashSet<ASMEvent>();
    for (final Pair<ASMEvent, String> p : my_relation) {
      if (p.second().equals(an_endpoint)) {
        result.add(p.first());
      }
    }
    return result;
  }
}
