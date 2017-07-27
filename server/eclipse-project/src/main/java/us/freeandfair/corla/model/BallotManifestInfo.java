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

package us.freeandfair.corla.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Information about the locations of specific batches of ballots.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>6
 * @version 0.0.1
 */
@Entity
@Table(name = "ballot_manifest_info")
public class BallotManifestInfo {
  /**
   * The database ID for this ballot manifest info.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long my_db_id;
  
  /**
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  private final Instant my_timestamp;
  
  /**
   * The ID number of the county in which the batch was scanned.
   */
  private final String my_county_id;
  //@ private invariant my_county_id >= 0;
  
  /**
   * The ID number of the scanner that scanned the batch.
   */
  private final String my_scanner_id;

  /**
   * The batch number.
   */
  private final String my_batch_id;
  
  /**
   * The size of the batch.
   */
  private final int my_batch_size;
  
  /**
   * The storage location for the batch.
   */
  private final String my_storage_location;
 
  /** 
   * Constructs an empty ballot manifest information record, solely for persistence.
   */
  protected BallotManifestInfo() {
    my_timestamp = Instant.now();
    my_county_id = "";
    my_scanner_id = "";
    my_batch_id = "";
    my_batch_size = 0;
    my_storage_location = "";
  }
  
  /**
   * Constructs a ballot manifest information record.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county ID.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_batch_size The batch size.
   * @param the_storage_location The storage location.
   */
  public BallotManifestInfo(final Instant the_timestamp,
                            final String the_county_id, final String the_scanner_id, 
                            final String the_batch_id, final int the_batch_size, 
                            final String the_storage_location) {
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
    my_scanner_id = the_scanner_id;
    my_batch_id = the_batch_id;
    my_batch_size = the_batch_size;
    my_storage_location = the_storage_location;
  }
  
  /**
   * @return the database ID.
   */
  public long dbID() {
    return my_db_id;
  }
  
  /**
   * @return the timestamp.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the county ID.
   */
  public String countyID() {
    return my_county_id;
  }  

  /**
   * @return the scanner ID.
   */
  public String scannerID() {
    return my_scanner_id;
  }
  
  /**
   * @return the batch number.
   */
  public String batchID() {
    return my_batch_id;
  }
  
  /**
   * @return the batch size.
   */
  public int batchSize() {
    return my_batch_size;
  }
  
  /**
   * @return the storage container number.
   */
  public String storageLocation() {
    return my_storage_location;
  }  
  
  /**
   * @return a String representation of this object.
   */
  @Override
  public String toString() {
    return "BallotManifestInfo [timestamp=" + my_timestamp + 
        ", county_id=" + my_county_id + ", scanner_id=" + my_scanner_id + 
        ", batch_size=" + my_batch_size +
        ", storage_container=" + my_storage_location + "]";
  }
  
  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = false;
    if (the_other instanceof BallotManifestInfo) {
      final BallotManifestInfo other_bmi = (BallotManifestInfo) the_other;
      result &= other_bmi.timestamp().equals(timestamp());
      result &= other_bmi.countyID().equals(countyID());
      result &= other_bmi.scannerID().equals(scannerID());
      result &= other_bmi.batchID().equals(batchID());
      result &= other_bmi.batchSize() == batchSize();
      result &= other_bmi.storageLocation().equals(storageLocation());
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
