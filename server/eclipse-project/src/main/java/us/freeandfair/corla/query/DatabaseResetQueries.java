/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import javax.persistence.Cache;

import org.hibernate.Session;

import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries for resetting the database.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class DatabaseResetQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private DatabaseResetQueries() {
    // do nothing
  }
  
  /**
   * Deletes everything from the database except authentication 
   * information. This query is very dangerous. 
   * 
   * @exception PersistenceException if the delete is unsuccessful.
   */
  public static void resetDatabase() {
    final Session s = Persistence.currentSession();
    
    // NOTE: this is done with native queries, because otherwise it would be
    // interminably slow (deleting one entity at a time)
    
    // the records in the following list of tables will be deleted, in order:
    
    final String[] tables = {
        "log",
        "audit_board",
        "audit_intermediate_report",
        "audit_investigation_report",
        "ballot_manifest_info",
        "counties_to_contest_results",
        "contest_choice",
        "contest_to_audit",
        "county_contest_vote_total",
        "contest_vote_total",
        "county_contest_comparison_audit_discrepancy", 
        "county_contest_comparison_audit_disagreement",
        "county_contest_comparison_audit", 
        "county_contest_result",
        "contest_result",
        "cvr_contest_info",
        "driving_contest",
        "contest",
        "cvr_audit_info",
        "cast_vote_record",
        "dos_dashboard",
        "round",
        "audit_board",
        "county_dashboard",
        "uploaded_file"
    };
    
    for (final String t : tables) {
      s.createNativeQuery("delete from " + t).executeUpdate();
    }
    
    // delete all the no-longer-referenced LOBs
    
    s.createNativeQuery("select lo_unlink(l.oid) " +
                        "from pg_largeobject_metadata l").getResultList();
    
    // empty all the Hibernate caches
    final Cache cache = s.getSessionFactory().getCache();
    if (cache != null) {
      cache.evictAll();
    }
  }
}
