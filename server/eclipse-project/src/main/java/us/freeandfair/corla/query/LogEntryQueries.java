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

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.LogEntry;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with LogEntry entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class LogEntryQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private LogEntryQueries() {
    // do nothing
  }

  /**
   * Obtains the last LogEntry object in the database. By definition, this is
   * the one with the largest ID.
   * 
   * @return the corresponding LogEntry object, or null if no such object exists.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static LogEntry last() {
    LogEntry result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<LogEntry> cq = cb.createQuery(LogEntry.class);
      final Subquery<Long> sq = cq.subquery(Long.class);
      final Root<LogEntry> c_root = cq.from(LogEntry.class);
      final Root<LogEntry> s_root = sq.from(LogEntry.class);
      
      sq.select(cb.max(s_root.get("my_id")));
      cq.where(cb.equal(c_root.get("my_id"), sq));
      final TypedQuery<LogEntry> query = s.createQuery(cq);
      // there should never be more than one result for max, but there could be 
      // zero, so let's be safe
      final List<LogEntry> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } else if (query_results.size() > 1) {
        // there should never be more than one result
        throw new PersistenceException("more than one max unique log entry id");
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for log entry: " + e.getMessage());
    }
    if (result == null) {
      Main.LOGGER.debug("found no log entries");
    } else {
      Main.LOGGER.debug("found last log entry " + result);
    }
    
    return result;
  }
}
