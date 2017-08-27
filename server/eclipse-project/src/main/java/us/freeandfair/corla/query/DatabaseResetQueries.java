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

import javax.persistence.Cache;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestComparisonAudit;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries for resetting the database.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
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
    
    // CriteriaDelete is excellent
    
    final CriteriaBuilder cb = s.getCriteriaBuilder();
    final CriteriaDelete<BallotManifestInfo> bmi = 
        cb.createCriteriaDelete(BallotManifestInfo.class);
    final CriteriaDelete<CastVoteRecord> cvr =
        cb.createCriteriaDelete(CastVoteRecord.class);
    final CriteriaDelete<CountyContestResult> ccr = 
        cb.createCriteriaDelete(CountyContestResult.class);
    final CriteriaDelete<CountyContestComparisonAudit> ccca =
        cb.createCriteriaDelete(CountyContestComparisonAudit.class);
    final CriteriaDelete<Contest> contest =
        cb.createCriteriaDelete(Contest.class);
    final CriteriaDelete<UploadedFile> files =
        cb.createCriteriaDelete(UploadedFile.class);
    
    s.createQuery(bmi).executeUpdate();
    s.createQuery(cvr).executeUpdate();
    s.createQuery(ccr).executeUpdate();
    s.createQuery(ccca).executeUpdate();
    s.createQuery(contest).executeUpdate();
    s.createQuery(files).executeUpdate();
    
    // delete all the no-longer-referenced LOBs
    
    s.createNativeQuery("select lo_unlink(l.oid) " +
                        "from pg_largeobject_metadata l").executeUpdate();
    
    // empty all the Hibernate caches
    final Cache cache = s.getSessionFactory().getCache();
    if (cache != null) {
      cache.evictAll();
    }
  }
}
