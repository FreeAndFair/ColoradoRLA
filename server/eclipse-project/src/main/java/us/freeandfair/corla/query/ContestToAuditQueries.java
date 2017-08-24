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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.ContestToAudit.AuditType;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Contest entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class ContestToAuditQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private ContestToAuditQueries() {
    // do nothing
  }
  
  /**
   * Obtain the set of contests under audit with the specified audit type.
   *
   * @param the_audit The audit type.
   * @return the matched contest objects.
   */
  public static Set<Contest> contestsMatching(final AuditType the_audit) {
    Set<Contest> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<ContestToAudit> root = cq.from(ContestToAudit.class);
      cq.select(root.get("my_contest"));
      cq.where(cb.equal(root.get("my_audit"), the_audit));
      final TypedQuery<Contest> query = s.createQuery(cq);
      result = new HashSet<>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for contest");
    }
    if (result == null) {
      Main.LOGGER.debug("found no contests to audit");
    } else {
      Main.LOGGER.debug("found " + result.size() + " contests to audit");
    }
    return result;
  }
}
