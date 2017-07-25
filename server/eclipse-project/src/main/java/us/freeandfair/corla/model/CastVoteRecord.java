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

package us.freeandfair.corla.model;

import java.util.Map;
import java.util.Set;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class CastVoteRecord {
  /**
   * The county ID of this cast vote record.
   */
  private final int my_county_id;
  
  /**
   * The scanner ID of this cast vote record.
   */
  private final int my_scanner_id;
  
  /**
   * The batch ID of this cast vote record.
   */
  private final int my_batch_id;
  
  /**
   * The record ID of this cast vote record.
   */
  private final int my_record_id;
  
  /**
   * A map from contests to choices made in this cast vote record.
   */
  private final Map<Contest, Set<String>> my_choices;
  
  /**
   * Constructs a new cast vote record.
   * 
   * @param the_county_id The county ID.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_record_id The record ID.
   * @param the_choices The contest choices.
   */
  public CastVoteRecord(final int the_county_id, final int the_scanner_id,
                        final int the_batch_id, final int the_record_id,
                        final Map<Contest, Set<String>> the_choices) {
    super();
    my_county_id = the_county_id;
    my_scanner_id = the_scanner_id;
    my_batch_id = the_batch_id;
    my_record_id = the_record_id;
    // TODO: make a clean copy of the_choices so it can't be tampered with
    my_choices = the_choices;
  }

  /**
   * @return the county ID.
   */
  public int countyID() {
    return my_county_id;
  }
  
  /**
   * @return the scanner ID.
   */
  public int scannerID() {
    return my_scanner_id;
  }
  
  /**
   * @return the batch ID.
   */
  public int batchID() {
    return my_batch_id;
  }
  
  /**
   * @return the record ID.
   */
  public int recordID() {
    return my_record_id;
  }
  
  /**
   * @return the choices made in this cast vote record.
   */
  public Map<Contest, Set<String>> choices() {
    return my_choices;
  }
  
  /**
   * @return the String identifier for this cast vote record,
   * comprised of the scanner, batch, and record IDs.
   */
  public String identifier() {
    return my_scanner_id + "-" + my_batch_id + "-" + my_record_id;
  }

  /**
   * @return a String representation of this cast vote record.
   */
  @Override
  public String toString() {
    return "CastVoteRecord [my_county_id=" + my_county_id + ", my_scanner_id=" +
           my_scanner_id + ", my_batch_id=" + my_batch_id + ", my_record_id=" + 
           my_record_id + ", my_choices=" + my_choices + "]";
  }
}
