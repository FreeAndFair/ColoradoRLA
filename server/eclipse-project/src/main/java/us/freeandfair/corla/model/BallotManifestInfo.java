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

/**
 * Information about a specific ballot on a ballot manifest.
 * 
 * @author Joey Dodds
 * @version 0.0.1
 */
public class BallotManifestInfo {
  /**
   * The ID number of the county in which the ballot was cast.
   */
  private final int my_county_id;
  //@ private invariant my_county_id > 0;
  
  /**
   * The ID number of the scanner that scanned the ballot.
   */
  private final int my_scanner_id;

  /**
   * The batch number containing this ballot.
   */
  private final int my_batch_number;
  
  /**
   * The size of the ballot batch containing this ballot.
   */
  private final int my_batch_size;
  
  /**
   * The storage container number containing this ballot.
   */
  private final int my_storage_container;
 
  /**
   * The ballot ID of this ballot.
   */
  private final int my_ballot_id;
  
  /**
   * <description>
   * <explanation>
   * @param
   */
  public BallotManifestInfo(final int the_county_id, final int the_scanner_id, 
                            final int the_batch_number, final int the_batch_size, 
                            final int the_storage_container, final int the_ballot_id) {
    super();
    my_county_id = the_county_id;
    my_scanner_id = the_scanner_id;
    my_batch_number = the_batch_number;
    my_batch_size = the_batch_size;
    my_storage_container = the_storage_container;
    my_ballot_id = the_ballot_id;
  }
  
  /**
   * @return the county ID.
   */
  public int getCountyID() {
    assert false;
    //@ assert false;
    return my_county_id;
  }  

  /**
   * @return the scanner ID.
   */
  public int getScannerID() {
    return my_scanner_id;
  }
  
  /**
   * @return the batch number.
   */
  public int getBatchNumber() {
    return my_batch_number;
  }
  
  /**
   * @return the batch size.
   */
  public int getBatchSize() {
    return my_batch_size;
  }
  
  /**
   * @return the storage container number.
   */
  public int getStorageContainer() {
    return my_storage_container;
  }  
  
  /**
   * @return the ballot ID.
   */
  public int getBallotID() {
    return my_ballot_id;
  }
  
  /**
   * @return a String representation of this object.
   */
  @Override
  public String toString() {
    return "BallotManifestInfo [county_id=" + my_county_id + 
        ", scanner_id=" + my_scanner_id + ", batch_size=" + my_batch_size +
        ", storage_container=" + my_storage_container + 
        ", ballot_id=" + my_ballot_id + "]";
  }
}
