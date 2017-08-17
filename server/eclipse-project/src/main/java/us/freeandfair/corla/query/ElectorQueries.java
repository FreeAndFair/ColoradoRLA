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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Elector entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class ElectorQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private ElectorQueries() {
    // do nothing
  }
  
  /**
   * Obtains the Elector object with the specified first name, last name,
   * and political party, if one exists. 
   * 
   * @param the_first_name The first name.
   * @param the_last_name The last name.
   * @param the_political_party The political party.
   * @return the matched Elector. If the results are ambiguous or empty 
   * (more than one match, or no match), returns null.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static Elector matching(final String the_first_name, 
                                 final String the_last_name,
                                 final String the_political_party) {
    Elector result = null;
    
    try {
      final boolean transaction = Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Elector> cq = cb.createQuery(Elector.class);
      final Root<Elector> root = cq.from(Elector.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      disjuncts.add(cb.equal(root.get("my_first_name"), the_first_name));
      disjuncts.add(cb.equal(root.get("my_last_name"), the_last_name));
      disjuncts.add(cb.equal(root.get("my_political_party"), the_political_party));
      cq.select(root).where(cb.and(disjuncts.toArray(new Predicate[disjuncts.size()])));
      final TypedQuery<Elector> query = s.createQuery(cq);
      final List<Elector> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
      if (transaction) {
        try {
          Persistence.commitTransaction();
        } catch (final RollbackException e) {
          Persistence.rollbackTransaction();
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for administrator");
    }
    if (result == null) {
      Main.LOGGER.debug("found no elector for data (" + the_first_name + ", " +
                        the_last_name + ", " + the_political_party + ")");
    } else {
      Main.LOGGER.debug("found elector " + result);
    }
    return result;
  }
}
