/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import us.freeandfair.corla.model.CastVoteRecord;

/**
 * A submitted audit CVR.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class SubmittedAuditCVR {
  /**
   * The original CVR ID for this audit CVR.
   */
  private final Long my_cvr_id;
  
  /**
   * The audit CVR.
   */
  private final CastVoteRecord my_audit_cvr;
  
  /**
   * Constructs a new SubmittedAuditCVR.
   * 
   * @param the_cvr_id The original CVR ID.
   * @param the_audit_cvr The audit CVR.
   */
  public SubmittedAuditCVR(final Long the_cvr_id, final CastVoteRecord the_audit_cvr) {
    my_cvr_id = the_cvr_id;
    my_audit_cvr = the_audit_cvr;
  }
  
  /**
   * @return the original CVR ID.
   */
  public Long cvrID() {
    return my_cvr_id;
  }
  
  /**
   * @return the audit CVR.
   */
  public CastVoteRecord auditCVR() {
    return my_audit_cvr;
  }
}
