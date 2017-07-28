/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The county dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class CountyDashboard {
  /**
   * The county of this dashboard.
   */
  private final County my_county;
  
  /**
   * The county status of this dashboard.
   */
  private CountyStatus my_status;
  
  /**
   * The most recent set of uploaded CVRs.
   */
  private final SortedSet<CastVoteRecord> my_cvrs =
      new TreeSet<CastVoteRecord>(new CastVoteRecord.IDComparator());

  /**
   * The audit board dashboard.
   */
  private final AuditBoardDashboard my_audit_board_dashboard;
  
  /**
   * Constructs a new County dashboard for the specified county.
   * The initial status is NO_DATA.
   * 
   * @param the_county The county.
   */
  public CountyDashboard(final County the_county) {
    my_county = the_county;
    my_status = CountyStatus.NO_DATA;
    my_audit_board_dashboard = new AuditBoardDashboard(my_county);
  }
  
  /**
   * @return the county for this dashboard.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the status for this dashboard.
   */
  public CountyStatus status() {
    return my_status;
  }
  
  /**
   * @return the audit board dashboard for this county.
   */
  public AuditBoardDashboard auditBoardDashboard() {
    return my_audit_board_dashboard;
  }
  
  /**
   * Sets new set of CVRs, replacing any current set of CVRs.
   * 
   * @param the_cvrs The CVRs.
   */
  public void setCVRs(final Collection<CastVoteRecord> the_cvrs) {
    my_cvrs.clear();
    my_cvrs.addAll(the_cvrs);
    my_status = CountyStatus.CVRS_UPLOADED_SUCCESSFULLY;
  }
  
  /**
   * Sets the audit board membership.
   * 
   * @param the_board_members The members of the audit board.
   */
  public void setAuditBoardMembership(final Collection<Elector> the_board_members) {
    my_audit_board_dashboard.setMembers(the_board_members);  
  }
  
  /**
   * Records a failed CVR upload event. This also ensures that the set of CVRs 
   * is empty.
   */
  public void uploadFailed() {
    my_cvrs.clear();
    my_status = CountyStatus.ERROR_IN_UPLOADED_DATA;
  }
}
