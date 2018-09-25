/** Copyright (C) 2018 the Colorado Department of State  **/

package us.freeandfair.corla.query;

import java.util.Optional;

import org.hibernate.query.Query;
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
    final Optional<ContestResult> contestResultMaybe = q.uniqueResultOptional();
    if (contestResultMaybe.isPresent()) {
      return contestResultMaybe.get();
    } else {
      final ContestResult cr = new ContestResult(contestName);
      Persistence.save(cr);
      return cr;
    }
  }

  /**
   * Return the ContestResult with the contestName given or create a new
   * ContestResult with the contestName.
   **/
  public static Integer count() {
    final Session s = Persistence.currentSession();
    final Query q = s.createQuery("select count(cr) from ContestResult cr ");
    return ((Long)q.uniqueResult()).intValue();
  }

}
