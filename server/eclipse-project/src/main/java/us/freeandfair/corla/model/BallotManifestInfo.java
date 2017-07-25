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
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class BallotManifestInfo {
  private final int my_county_id;
  private final int my_scanner_id_number;
  private final int my_batch_size;
  private final int my_storage_container;
 
  /**
   * <description>
   * <explanation>
   * @param
   * @return the my_county_id
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/
  
  public int getMy_county_id() {
    assert false;
    //@ assert false;
    return my_county_id;
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  //@ also behavior
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/

  
  
  @Override
  public String toString() {
    return "BallotManifestInfo [my_county_id=" + my_county_id + ", my_scanner_id_number=" +
           my_scanner_id_number + ", my_batch_size=" + my_batch_size +
           ", my_storage_container=" + my_storage_container + "]";
  }
  /**
   * <description>
   * <explanation>
   * @param
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/
  
  public BallotManifestInfo(int my_county_id, int my_scanner_id_number, int my_batch_size,
                            int my_storage_container) {
    super();
    this.my_county_id = my_county_id;
    this.my_scanner_id_number = my_scanner_id_number;
    this.my_batch_size = my_batch_size;
    this.my_storage_container = my_storage_container;
  }
  /**
   * <description>
   * <explanation>
   * @param
   * @return the my_scanner_id_number
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/
  
  public int getMy_scanner_id_number() {
    assert false;
    //@ assert false;
    return my_scanner_id_number;
  }
  /**
   * <description>
   * <explanation>
   * @param
   * @return the my_batch_size
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/
  
  public int getMy_batch_size() {
    assert false;
    //@ assert false;
    return my_batch_size;
  }
  /**
   * <description>
   * <explanation>
   * @param
   * @return the my_storage_container
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @*/
  
  public int getMy_storage_container() {
    assert false;
    //@ assert false;
    return my_storage_container;
  }  
  
}
