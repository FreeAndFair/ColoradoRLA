/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 16, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.persistence.AbstractEntity;
import us.freeandfair.corla.util.EqualsHashcodeHelper;

/**
 * A log entry that is stored in the database.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "log")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class LogEntry extends AbstractEntity implements Serializable {
  /**
   * The root hash for the hash chain (a 256-bit block of zeros).
   */
  public static final String ROOT_HASH = 
      "0000000000000000000000000000000000000000000000000000000000000000";
      
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The informational string of this log entry.
   */
  private String my_information;
  
  /**
   * The timestamp of this log entry.
   */
  private Instant my_timestamp;
  
  /**
   * The hash chain entry of this log entry.
   */
  private String my_hash;
  
  /**
   * Constructs a new empty log entry, solely for persistence.
   */
  public LogEntry() {
    super();
  }
  
  /**
   * Constructs a new log entry with the specified information, timestamp,
   * and previous log entry. If the previous entry is null, it is assumed
   * that this is the beginning of a new log hash chain.
   * 
   * @param the_information The information.
   * @param the_timestamp The timestamp.
   * @param the_previous_entry The previous log entry.
   */
  public LogEntry(final String the_information, final Instant the_timestamp,
                  final LogEntry the_previous_entry) {
    super();
    my_information = the_information;
    my_timestamp = the_timestamp;
    my_hash = calculateHash(the_previous_entry);
  }
  
  /**
   * Generates a hash from the previous log entry and the contents of this
   * log entry. If the previous entry is null, the hash is based on the
   * root hash.
   * 
   * @param the_previous_entry The previous log entry.
   * @return the hash. If the hash cannot be calculated, this method
   * returns the root hash.
   */
  private String calculateHash(final LogEntry the_previous_entry) {
    String result = ROOT_HASH;
    final StringBuilder hash_input = new StringBuilder();
    hash_input.append(my_timestamp.toString());
    hash_input.append(my_information);
    if (the_previous_entry == null) {
      hash_input.append(ROOT_HASH);
    } else {
      hash_input.append(the_previous_entry.hash());
    }
    try {      
      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      final BigInteger bi = 
          new BigInteger(1, md.digest(hash_input.toString().getBytes()));
      result = String.format("%0" + (md.digest().length << 1) + "X", bi);
    } catch (final NoSuchAlgorithmException e) {
      Main.LOGGER.error("could not use SHA-256");
    }
    return result;
  }
  
  /**
   * @return the information in this log entry.
   */
  public String information() {
    return my_information;
  }
  
  /**
   * @return the timestamp of this log entry.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the hash of this log entry.
   */
  public String hash() {
    return my_hash;
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "LogEntry [information=" + my_information + ", timestamp=" +
           my_timestamp + ", hash=" + my_hash + "]";
  }

  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof LogEntry) {
      final LogEntry other_entry = (LogEntry) the_other;
      result &= EqualsHashcodeHelper.nullableEquals(other_entry.information(), information());
      result &= EqualsHashcodeHelper.nullableEquals(other_entry.timestamp(), timestamp());
      result &= EqualsHashcodeHelper.nullableEquals(other_entry.hash(), hash());
    } else {
      result = false;
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
