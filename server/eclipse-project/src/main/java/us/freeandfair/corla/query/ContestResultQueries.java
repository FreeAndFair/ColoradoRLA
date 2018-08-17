/** Copyright (C) 2018 the Colorado Department of State  **/

package us.freeandfair.corla.query;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import us.freeandfair.corla.model.ContestResult;
import us.freeandfair.corla.persistence.Persistence;


public final class ContestResultQueries {
  /**
   * prevent construction
   */
  private ContestResultQueries() {
  }

  /**
   * Return the ContestResult with the contestName given or create a new
   * ContestResult with the contestName.
   **/
  public static ContestResult findOrCreate(final String contestName) {
    final Session s = Persistence.currentSession();
    final Query q = s.createQuery("select cr from ContestResult cr " +
                                  "where cr.contestName = :contestName");
    q.setParameter("contestName", contestName);
    final List<ContestResult> results = q.getResultList();
    if (results.isEmpty()) {
      final ContestResult cr = new ContestResult(contestName);
      Persistence.save(cr);
      return cr;
    } else {
      return results.get(0);
    }
  }
}
