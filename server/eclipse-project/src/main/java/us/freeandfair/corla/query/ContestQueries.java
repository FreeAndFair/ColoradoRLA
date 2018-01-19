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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with Contest entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class ContestQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private ContestQueries() {
    // do nothing
  }  
  
  /**
   * Gets contests that are in the specified set of counties.
   * 
   * @param the_counties The counties.
   * @return the matching contests, or null if the query fails.
   */
  public static List<Contest> forCounties(final Set<County> the_counties) {
    List<Contest> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<Contest> root = cq.from(Contest.class);
      final List<Predicate> disjuncts = new ArrayList<Predicate>();
      for (final County county : the_counties) {
        disjuncts.add(cb.equal(root.get("my_county"), county));
      }
      cq.select(root);
      cq.where(cb.or(disjuncts.toArray(new Predicate[disjuncts.size()])));
      cq.orderBy(cb.asc(root.get("my_county").get("my_id")), 
                 cb.asc(root.get("my_sequence_number")));
      final TypedQuery<Contest> query = s.createQuery(cq);
      result = query.getResultList();  
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
  
  /**
   * Gets contests that are in the specified county.
   * 
   * @param the_county The county.
   * @return the matching contests, or null if the query fails.
   */
  public static Set<Contest> forCounty(final County the_county) {
    Set<Contest> result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Contest> cq = cb.createQuery(Contest.class);
      final Root<Contest> root = cq.from(Contest.class);
      cq.select(root);
      cq.where(cb.equal(root.get("my_county"), the_county));
      cq.orderBy(cb.asc(root.get("my_sequence_number")));
      final TypedQuery<Contest> query = s.createQuery(cq);
      result = new HashSet<Contest>(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("Exception when reading contests from database: " + e);
    }

    return result;
  }
  
  /**
   * Deletes all the contests for the county with the specified ID.
   * 
   * @param the_id The county ID.
   */
  public static void deleteForCounty(final Long the_county_id) {
    final Set<Contest> contests = 
        forCounty(Persistence.getByID(the_county_id, County.class));
    if (contests != null) {
      for (final Contest c : contests) {
        Persistence.delete(c);
      }
    }
    Persistence.flush();
  }
}
