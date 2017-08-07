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

/**
 * A common interface to parsers for ballot manifest info in various formats.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
public interface BallotManifestParser {
  /**
   * Parse the CVRs. The export data and other supplementary information is provided
   * to the CVR parser in some way outside this interface (such as a constructor 
   * call).
   * 
   * @return true if the parse was successful, false otherwise.
   */
  boolean parse();
  
  /**
   * @return the IDs of the ballot manifest information parsed from the export data. 
   */
  List<Long> parsedIDs();
}
