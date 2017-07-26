/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.util.List;
import java.util.Set;

import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Contest;

/**
 * A common interface to parsers for various kinds of CSV file needed by the
 * system.
 * 
 * @author Joey Dodds <jdodds@freeandfair.us>
 * @version 0.0.1
 */
public interface CVRExportParser {
  /**
   * Parse the CVRs. The export data and other supplementary information is provided
   * to the CVR parser in some way outside this interface (such as a constructor 
   * call).
   * 
   * @return true if the parse was successful, false otherwise.
   */
  boolean parse();
  
  /**
   * @return the CVRs parsed from the export data. 
   */
  List<CastVoteRecord> cvrs();
  
  /**
   * @return the contest information inferred from the export data. 
   */
  Set<Contest> contests();
  
  /**
   * @return the ballot style information inferred from the export data.
   */
  // Set<BallotStyle> ballotStyles();
  
}
