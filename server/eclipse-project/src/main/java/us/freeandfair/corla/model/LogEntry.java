/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 16, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * A log entry that is stored in the database.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Immutable // this is a Hibernate-specific annotation, but there is no JPA alternative
@Cacheable(true)
@Table(name = "log")
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class LogEntry implements PersistentEntity, Serializable {
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
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;
  
  /**
   * The result code of this log entry, if any. In most cases, this will be an HTTP
   * result code, as enumerated in HttpStatus.
   */
  @Column(updatable = false)
  private Integer my_result_code;
  
  /**
   * The informational string of this log entry.
   */
  @Column(updatable = false, nullable = false)
  private String my_information;
  
  /**
   * Information about the authentication status at the time of this log entry,
   * if any.
   */
  @Column(updatable = false)
  private String my_authentication_data;
  
  /**
   * Information about the client host that generated this log entry, if any.
   */
  @Column(updatable = false)
  private String my_client_host;
  
  /**
   * The timestamp of this log entry.
   */
  @Column(updatable = false, nullable = false)
  private Instant my_timestamp;
  
  /**
   * The previous log entry for this log entry.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "previous_entry")
  private LogEntry my_previous_entry;
  
  /**
   * The hash chain entry of this log entry.
   */
  @Column(updatable = false, nullable = false)
  private String my_hash;
  
  /**
   * Constructs a new empty log entry, solely for persistence.
   */
  public LogEntry() {
    super();
  }
  
  /**
   * Constructs a new log entry with the specified information. If the previous 
   * entry is null, it is assumed that this is the beginning of a new log hash 
   * chain.
   * 
   * @param the_result_code The result code, if any.
   * @param the_information The information.
   * @param the_authentication_data The authentication data, if any.
   * @param the_client_host The client host, if any.
   * @param the_timestamp The timestamp.
   * @param the_previous_entry The previous log entry.
   */
  public LogEntry(final Integer the_result_code, final String the_information,
                  final String the_authentication_data, final String the_client_host,
                  final Instant the_timestamp, final LogEntry the_previous_entry) {
    super();
    my_result_code = the_result_code;
    my_information = the_information;
    my_authentication_data = the_authentication_data;
    my_client_host = the_client_host;
    my_timestamp = the_timestamp;
    my_previous_entry = the_previous_entry;
    my_hash = calculateHash(the_previous_entry);
  }
  
  /**
   * Constructs a new, unhashed log entry with the specified information; 
   * such a log entry cannot be persisted, and is useful only for subsequently
   * building persistable log entries (as, for example, at the end of request
   * processing).
   * 
   * @param the_result_code The result code.
   * @param the_information The information.
   * @param the_timestamp The timestamp.
   */
  public LogEntry(final Integer the_result_code, final String the_information,
                  final Instant the_timestamp) {
    super();
    my_result_code = the_result_code;
    my_information = the_information;
    my_timestamp = the_timestamp;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
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
    final StringBuilder hash_input = new StringBuilder(hashString());
    if (the_previous_entry == null) {
      hash_input.append(ROOT_HASH);
    } else {
      hash_input.append(the_previous_entry.hash());
    }
    try {      
      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      final BigInteger bi = 
          new BigInteger(1, md.digest(hash_input.toString().
                                      getBytes(Charset.forName("UTF-8"))));
      result = String.format("%0" + (md.digest().length << 1) + "X", bi);
    } catch (final NoSuchAlgorithmException e) {
      Main.LOGGER.error("could not use SHA-256");
    }
    return result;
  }
  
  /**
   * @return the result code in this log entry.
   */
  public Integer resultCode() {
    return my_result_code;
  }
  
  /**
   * @return the information in this log entry.
   */
  public String information() {
    return my_information;
  }
  
  /**
   * @return the authentication data of this log entry.
   */
  public String authenticationData() {
    return my_authentication_data;
  }
  
  /**
   * @return the client host of this log entry.
   */
  public String clientHost() {
    return my_client_host;
  }
  
  /**
   * @return the timestamp of this log entry.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the previous log entry.
   */
  public LogEntry previousEntry() {
    return my_previous_entry;
  }
  
  /**
   * @return the hash of this log entry.
   */
  public String hash() {
    return my_hash;
  }
  
  /**
   * Returns a String based on the data in this log entry and used as part
   * of the hash computation.
   * 
   * @return the String.
   */
  public final String hashString() {
    final StringBuilder hash_input = new StringBuilder();
    hash_input.append(my_result_code.toString());
    hash_input.append(my_information);
    hash_input.append(my_timestamp.toString());
    return hash_input.toString();
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
      result &= nullableEquals(other_entry.resultCode(), resultCode());
      result &= nullableEquals(other_entry.information(), information());
      result &= nullableEquals(other_entry.authenticationData(), 
                               authenticationData());
      result &= nullableEquals(other_entry.clientHost(), clientHost());
      result &= nullableEquals(other_entry.timestamp(), timestamp());
      // we don't include the previous entry because it would be very recursive
      result &= nullableEquals(other_entry.hash(), hash());
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
    return nullableHashCode(hash());
  }
}
