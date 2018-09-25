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

import us.freeandfair.corla.util.NaturalOrderComparator;
import us.freeandfair.corla.util.SuppressFBWarnings;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

/**
 * The standard response provided by the server in response to a request
 * for CVR locations.
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"},
                    justification = "Field is read by Gson.")
public class CVRToAuditResponse implements Comparable<CVRToAuditResponse> {
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
  protected final String my_batch_id;

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
   * The optional audit board index
   */
  protected Integer my_audit_board_index;

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
                            final String the_batch_id,
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
  public String batchID() {
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
   * @return the audit board index or null if not set
   */
  public Integer auditBoardIndex() {
    return my_audit_board_index;
  }

  /**
   * Optionally set the audit board index that will be auditing this ballot.
   */
  public void setAuditBoardIndex(final Integer auditBoardIndex) {
    my_audit_board_index = auditBoardIndex;
  }

  /**
   * @return the audited flag.
   */
  public boolean audited() {
    return my_audited;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object other) {
    boolean result = true;
    if (other instanceof CVRToAuditResponse) {
      final CVRToAuditResponse otherCtar = (CVRToAuditResponse) other;
      result &= nullableEquals(this.dbID(), otherCtar.dbID());
    } else {
      result = false;
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return nullableHashCode(this.dbID());
  }

  /**
   * Compares this object to another.
   *
   * The sorting happens by the tuple
   * (storageLocation(), scannerID(), batchID(), recordID()) and will return a
   * negative, positive, or 0-valued result if this should come before, after,
   * or at the same point as the other object, respectively.
   *
   * @return int
   */
  @Override
  public int compareTo(final CVRToAuditResponse other) {
    final int storageLocation = NaturalOrderComparator.INSTANCE.compare(
        this.storageLocation(), other.storageLocation());

    if (storageLocation != 0) {
      return storageLocation;
    }

    final int scanner = this.scannerID() - other.scannerID();

    if (scanner != 0) {
      return scanner;
    }

    final int batch = NaturalOrderComparator.INSTANCE.compare(
        this.batchID(), other.batchID());

    if (batch != 0) {
      return batch;
    }

    return this.recordID() - other.recordID();
  }
}
