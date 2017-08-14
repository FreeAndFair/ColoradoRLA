/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with County entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class DoSDashboardQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private DoSDashboardQueries() {
    // do nothing
  }
  
  /**
   * Obtain the (single) Department of State dashboard object. To safely _use_
   * the returned dashboard, this method must be called within a transaction.
   *
   * @param the Department of State dashboard, if one exists, 
   * and null otherwise.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static DoSDashboard get() {
    DoSDashboard result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final List<DoSDashboard> db_list = 
          Persistence.getAll(DoSDashboard.class);
      DoSDashboard db = null;
      if (db_list.isEmpty()) {
        // create a Department of State Dashboard
        db = new DoSDashboard();
        Persistence.saveOrUpdate(db);
        Main.LOGGER.info("attempting to create new department of state dashboard");
      } else if (db_list.size() > 1) {
        Main.LOGGER.error("multiple department of state dashboards found");
      } else {
        db = db_list.get(0);
      }
      if (transaction) {
        try {
          Persistence.commitTransaction();
          result = db;
        } catch (final RollbackException e) {
          Main.LOGGER.error("could not create department of state dashboard");
          Persistence.rollbackTransaction();
        }
      } else {
        result = db;
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for department of state dashboard");
    }
    return result;
  }
}
