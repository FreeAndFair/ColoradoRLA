/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import us.freeandfair.corla.crypto.PseudoRandomNumberGenerator;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;

/**
 * Controller methods relevant to comparison audits.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class ComparisonAuditController {
  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditController() {
    // empty
  }
  
  /**
   * Compute the ballot (cards) for audit, for a particular county dashboard, 
   * random seed, and index range.
   * 
   * @param the_cdb The county dashboard.
   * @param the_seed The random seed.
   * @param the_min_index The minimum index to return.
   * @param the_max_index The maximum index to return.
   * @return the list of ballot cards, of size the_max_index - the_min_index + 1; 
   * the first element of this list will be the "min_index"th ballot card to audit, 
   * and the last will be the "max_index"th. 
   */
  public static List<CastVoteRecord> computeBallotOrder(final CountyDashboard the_cdb, 
                                                        final String the_seed, 
                                                        final int the_min_index,
                                                        final int the_max_index) {
    final OptionalLong count = 
        CastVoteRecordQueries.countMatching(the_cdb.cvrUploadTimestamp(), 
                                            the_cdb.id(), 
                                            RecordType.UPLOADED);
    if (!count.isPresent()) {
      throw new IllegalStateException("unable to count CVRs for county " + the_cdb.id());
    }

    final boolean with_replacement = true;
    // assuming that CVRs are indexed from 0
    final int minimum = 0;
    // the number of CVRs for the_contest_to_audit - note that the sequence
    // generator generates a sequence of the numbers minimum ... maximum 
    // inclusive, so we subtract 1 from the number of CVRs to give it the
    // correct range for our actual list of CVRs (indexed from 0).
    final int maximum = (int) count.getAsLong() - 1;

    final PseudoRandomNumberGenerator prng = 
        new PseudoRandomNumberGenerator(the_seed, with_replacement,
                                        minimum, maximum);
    final List<Integer> list_of_cvrs_to_audit = 
        prng.getRandomNumbers(the_min_index, the_max_index);
    final List<Long> list_of_cvr_ids = 
        CastVoteRecordQueries.idsForMatching(the_cdb.cvrUploadTimestamp(), 
                                             the_cdb.id(),
                                             RecordType.UPLOADED);
    final List<CastVoteRecord> result = new ArrayList<>();
    
    for (final int index : list_of_cvrs_to_audit) {
      result.add(Persistence.getByID(list_of_cvr_ids.get(index), CastVoteRecord.class));
    }
    
    return result;
  }
}
