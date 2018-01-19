/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.io.Serializable;
import java.util.Comparator;

import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The standard response provided by the server in response to a request
 * for CVR locations.
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"}, 
                    justification = "Field is read by Gson.")
public class CVRToAuditResponse {
  /**
   * The (first) audit sequence number.
   */
  protected final int my_audit_sequence_number;
  
  /**
   * The CVR scanner ID.
   */
  protected final int my_scanner_id;
  
  /**
   * The CVR batch ID.
   */
  protected final int my_batch_id;
 
  /**
   * The CVR record ID.
   */
  protected final int my_record_id;

  /**
   * The CVR imprinted ID.
   */
  protected final String my_imprinted_id;
  
  /**
   * The CVR number (from the CSV file).
   */
  protected final int my_cvr_number;
  
  /**
   * The CVR database ID.
   */
  protected final long my_db_id;
  
  /**
   * The CVR ballot type.
   */
  protected final String my_ballot_type;

  /**
   * The CVR storage location.
   */
  protected final String my_storage_location;

  /**
   * A flag that indicates whether or not the CVR has been audited.
   */
  protected final boolean my_audited;
  
  /**
   * Create a new response object.
   * 
   * @param the_audit_sequence_number The audit sequence number.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_record_id The record ID.
   * @param the_imprinted_id The imprinted ID.
   * @param the_cvr_number The CVR number (from the CSV file).
   * @param the_db_id The database ID.
   * @param the_ballot_type The ballot type.
   * @param the_storage_location The storage location.
   * @param the_audited true if the ballot has been audited, false otherwise.
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public CVRToAuditResponse(final int the_audit_sequence_number,
                            final int the_scanner_id,
                            final int the_batch_id,
                            final int the_record_id,
                            final String the_imprinted_id,
                            final int the_cvr_number,
                            final long the_db_id,
                            final String the_ballot_type,
                            final String the_storage_location,
                            final boolean the_audited) {
    my_audit_sequence_number = the_audit_sequence_number;
    my_scanner_id = the_scanner_id;
    my_batch_id = the_batch_id;
    my_record_id = the_record_id;
    my_imprinted_id = the_imprinted_id;
    my_cvr_number = the_cvr_number;
    my_db_id = the_db_id;
    my_ballot_type = the_ballot_type;
    my_storage_location = the_storage_location;
    my_audited = the_audited;
  }
  
  /**
   * @return the audit sequence number.
   */
  public int auditSequenceNumber() {
    return my_audit_sequence_number;
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
   * @return the imprinted ID.
   */
  public String imprintedID() {
    return my_imprinted_id;
  }
  
  /**
   * @return the CVR number.
   */
  public int cvrNumber() {
    return my_cvr_number;
  }
  
  /**
   * @return the database ID.
   */
  public long dbID() {
    return my_db_id;
  }
  
  /**
   * @return the ballot type.
   */
  public String ballotType() {
    return my_ballot_type;
  }
  
  /**
   * @return the storage location.
   */
  public String storageLocation() {
    return my_storage_location;
  }
  
  /**
   * @return the audited flag.
   */
  public boolean audited() {
    return my_audited;
  }
  
  /**
   * A comparator to sort CVRLocationResponse objects by scanner ID, then batch ID,
   * then record ID.
   */
  @SuppressWarnings("PMD.AtLeastOneConstructor")
  public static class BallotOrderComparator 
      implements Serializable, Comparator<CVRToAuditResponse> {
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1;
    
    /**
     * Orders two CVRToAuditResponses lexicographically by the triple
     * (scanner_id, batch_id, record_id).
     * 
     * @param the_first The first response.
     * @param the_second The second response.
     * @return a positive, negative, or 0 value as the first response is
     * greater than, equal to, or less than the second, respectively.
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    public int compare(final CVRToAuditResponse the_first, 
                       final CVRToAuditResponse the_second) {
      final int scanner = the_first.my_scanner_id - the_second.my_scanner_id;
      final int batch = the_first.my_batch_id - the_second.my_batch_id;
      final int record = the_first.my_record_id - the_second.my_record_id;
      
      final int result;
      
      if (scanner != 0) {
        result = scanner;
      } else if (batch != 0) {
        result = batch;
      } else {
        result = record;
      }
      
      return result;
    }
  }
}
