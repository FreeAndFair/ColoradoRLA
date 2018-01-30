/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.util.OptionalInt;

/**
 * A common interface to parsers for ballot manifest info in various formats.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
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
   * The number of records parsed from the ballot manifest file.
   * 
   * @return the number of records; empty if parsing has not yet occurred.
   */
  OptionalInt recordCount();
  
  /**
   * The number of ballots represented by the parsed ballot manifest records.
   * 
   * @return the number of ballots; empty if parsing has not yet occurred.
   */
  OptionalInt ballotCount();
}
