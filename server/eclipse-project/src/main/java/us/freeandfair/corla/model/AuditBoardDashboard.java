/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The county dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
// TODO: either this needs to be an entity, or it needs to contain an entity
// that encapsulates its state
public class AuditBoardDashboard {
  /**
   * The county of this dashboard.
   */
  private final County my_county;
 
  /**
   * The CVRs to audit in the current round, and the most
   * recent aCVRs submitted for them.
   */
  private final SortedMap<CastVoteRecord, CastVoteRecord> my_cvrs_to_audit = 
      new TreeMap<CastVoteRecord, CastVoteRecord>(new CastVoteRecord.IDComparator());
  
  /**
   * The CVRs remaining to audit in the current round.
   */
  private final List<CastVoteRecord> my_remaining_cvrs = 
      new ArrayList<CastVoteRecord>();
  
  /**
   * The members of the audit board.
   */
  private final Set<Elector> my_members = new HashSet<Elector>();
  
  /**
   * Constructs a new audit board dashboard for the specified county.
   * 
   * @param the_county The county.
   */
  public AuditBoardDashboard(final County the_county) {
    my_county = the_county;
  }
  
  /**
   * @return the county for this dashboard.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the set of audit board members.
   */
  public Set<Elector> members() {
    return Collections.unmodifiableSet(my_members);
  }
  
  /**
   * Sets the membership of the audit board; this must be the full set
   * of electors on the board, and replaces any other set.
   * 
   * @param the_members The members.
   */
  public void setMembers(final Collection<Elector> the_members) {
    my_members.clear();
    my_members.addAll(the_members);
  }
  
  /**
   * Define the CVRs to audit. This clears the set of previously submitted
   * aCVRs (but they are all retained in persistent storage).
   * 
   * @param the_cvrs_to_audit The CVRs to audit.
   */
  public void setCVRsToAudit(final Collection<CastVoteRecord> the_cvrs_to_audit) {
    my_cvrs_to_audit.clear();
    for (final CastVoteRecord cvr : the_cvrs_to_audit) {
      my_cvrs_to_audit.put(cvr, null);
      my_remaining_cvrs.add(cvr);
    }
  }
  
  /**
   * Submit an aCVR for a CVR under audit.
   * 
   * @param the_cvr_under_audit The CVR under audit.
   * @param the_acvr The corresponding aCVR.
   * @exception IllegalArgumentException if the specified CVR under audit
   *  is not, in fact, under audit.
   */
  public void submitAuditCVR(final CastVoteRecord the_cvr_under_audit,
                             final CastVoteRecord the_acvr) {
    if (my_cvrs_to_audit.keySet().contains(the_cvr_under_audit)) {
      my_cvrs_to_audit.put(the_cvr_under_audit, the_acvr);
    } else {
      throw new IllegalArgumentException("attempt to set aCVR for unaudited CVR");
    }
  }
  
  /**
   * @return all the aCVRs that have been submitted to this dashboard in 
   * this round.
   */
  public Set<CastVoteRecord> auditCVRs() {
    final Set<CastVoteRecord> result = 
        new HashSet<CastVoteRecord>(my_cvrs_to_audit.values());
    result.remove(null);
    return result;
  }
}
