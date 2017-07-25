/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 25, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.util.List;

import us.freeandfair.corla.model.CastVoteRecord;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public interface Parser {

  /**
   * 
   * <description> <explanation>
   * 
   * @param fileName The filename of the CSV file to be parsed
   * @param countyId The identification number of the county that generated the
   *          CSV
   */
  List<CastVoteRecord> parseCSV(String the_file_name, int the_county_id);

}
