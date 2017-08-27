/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 1, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.sql.Blob;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.json.UploadedFileJsonAdapter;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * An uploaded file, kept in persistent storage for archival.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
// note that unlike our other entities, uploaded files are not Serializable
@Entity
@Cacheable(false) // uploaded files are explicitly not cacheable
@Table(name = "uploaded_file",
       indexes = { @Index(name = "idx_uploaded_file_county", columnList = "county_id") })
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
@JsonAdapter(UploadedFileJsonAdapter.class)
public class UploadedFile implements PersistentEntity {
  /**
   * The database ID.
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
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  @Column(updatable = false, nullable = false)
  private Instant my_timestamp;

  /**
   * The county that uploaded the file.
   */
  @Column(name = "county_id", updatable = false, nullable = false)
  private Long my_county_id;
  
  /**
   * The status of the file.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FileStatus my_status;
  
  /**
   * The orignal filename.
   */
  @Column(updatable = false)
  private String my_filename;
  
  /**
   * The hash of the file.
   */
  @Column(updatable = false, nullable = false)
  private String my_hash;
  
  /**
   * The status of hash verification.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private HashStatus my_hash_status;
  
  /**
   * The uploaded file. 
   */
  @Lob
  @Column(updatable = false, nullable = false)
  private Blob my_file;
  
  /**
   * The file size.
   */
  @Column(updatable = false, nullable = false)
  private Long my_size;
  
  /**
   * Constructs an empty uploaded file, solely for persistence.
   */
  public UploadedFile() {
    super();
  }
  
  /**
   * Constructs an uploaded file with the specified information.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county that uploaded the file.
   * @param the_filename The original filename.
   * @param the_status The file status.
   * @param the_hash The hash entered at upload time.
   * @param the_hash_status A flag indicating whether the file matches
   * the hash.
   * @param the_file The file (as a Blob).
   * @param the_size The file size (in bytes).
   */
  public UploadedFile(final Instant the_timestamp,
                      final Long the_county_id,
                      final String the_filename,
                      final FileStatus the_status,
                      final String the_hash,
                      final HashStatus the_hash_status,
                      final Blob the_file,
                      final Long the_size) {
    super();
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
    my_filename = the_filename;
    my_status = the_status;
    my_hash = the_hash;
    my_hash_status = the_hash_status;
    my_file = the_file;
    my_size = the_size;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }
  
  /**
   * {@inheritDoc}.
   */
  @Override
  public final void setID(final Long the_id) {
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
   * @return the timestamp of this file.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the county ID that uploaded this file.
   */
  public Long countyID() {
    return my_county_id;
  }
  
  /**
   * @return the original filename of this file.
   */
  public String filename() {
    return my_filename;
  }
  
  /**
   * @return the status of this file.
   */
  public FileStatus status() {
    return my_status;
  }
  
  /**
   * Sets the file status.
   * 
   * @param the_status The new status.
   */
  public void setStatus(final FileStatus the_status) {
    my_status = the_status;
  }
  
  /**
   * @return the hash uploaded with this file.
   */
  public String hash() {
    return my_hash;
  }
  
  /**
   * @return the status of hash verification.
   */
  public HashStatus hashStatus() {
    return my_hash_status;
  }
  
  /**
   * @return the file, as a binary blob.
   */
  public Blob file() {
    return my_file;
  }
  
  /**
   * @return the file size (in bytes).
   */
  public Long size() {
    return my_size;
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "UploadedFile [id=" + my_id + ", timestamp=" +
           my_timestamp + ", county_id=" + my_county_id + 
           ", status=" + my_status + ", filename=" +
           my_filename + ", hash=" + my_hash + ", hash_status=" + 
           my_hash_status + ", size=" + my_size + "]";
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
    if (the_other instanceof UploadedFile) {
      final UploadedFile other_file = (UploadedFile) the_other;
      result &= nullableEquals(other_file.timestamp(), timestamp());
      result &= nullableEquals(other_file.countyID(), countyID());
      result &= nullableEquals(other_file.filename(), filename());
      result &= nullableEquals(other_file.status(), status());
      result &= nullableEquals(other_file.hash(), hash());
      result &= nullableEquals(other_file.hashStatus(), hash());
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
  
  /**
   * An enumeration of file types that can be uploaded.
   */
  public enum FileStatus {
    NOT_IMPORTED,
    IMPORTED_AS_BALLOT_MANIFEST,
    IMPORTED_AS_CVR_EXPORT
  }
  
  /**
   * An enumeration of hash statuses.
   */
  public enum HashStatus {
    VERIFIED,
    MISMATCH,
    NOT_CHECKED;
  }
}
