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

import java.sql.Blob;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * An uploaded file, kept in persistent storage for archival.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "uploaded_file")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class UploadedFile {
  /**
   * The current ID number to be used.
   */
  private static long current_id;

  /**
   * The table of objects that have been created.
   */
  private static final Map<UploadedFile, UploadedFile> CACHE = 
      new HashMap<UploadedFile, UploadedFile>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, UploadedFile> BY_ID =
      new HashMap<Long, UploadedFile>();
  
  /**
   * The database ID for this ballot manifest info.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private Long my_id = getID();
  
  /**
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Instant my_timestamp;

  /**
   * The county that uploaded the file.
   */
  private String my_county_id;
  
  /**
   * The type of the file.
   */
  private FileType my_type;
  
  /**
   * The hash of the file.
   */
  private String my_hash;
  
  /**
   * The status of hash verification.
   */
  private HashStatus my_hash_status;
  
  /**
   * The uploaded file. 
   */
  @Lob
  private Blob my_file;
  
  /**
   * Constructs an empty uploaded file, solely for persistence.
   */
  protected UploadedFile() {
    // default values for everything
  }
  
  /**
   * Constructs an uploaded file with the specified information.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county that uploaded the file.
   * @param the_type The file type.
   * @param the_hash The hash entered at upload time.
   * @param the_hash_ok A flag indicating whether the file matches
   * the hash.
   * @param the_file The file (as a Blob).
   */
  protected UploadedFile(final Instant the_timestamp,
                         final String the_county_id,
                         final FileType the_type,
                         final String the_hash,
                         final HashStatus the_hash_status,
                         final Blob the_file) {
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
    my_type = the_type;
    my_hash = the_hash;
    my_hash_status = the_hash_status;
    my_file = the_file;
  }
  
  /**
   * Returns an uploaded file with the specified parameters.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county that uploaded the file.
   * @param the_type The file type.
   * @param the_hash The hash entered at upload time.
   * @param the_hash_ok A flag indicating whether the file matches
   * the hash.
   * @param the_file The file (as a Blob).
   * @return the resulting uploaded file.
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public static synchronized UploadedFile instance(final Instant the_timestamp,
                                                   final String the_county_id,
                                                   final FileType the_type,
                                                   final String the_hash,
                                                   final HashStatus the_hash_status,
                                                   final Blob the_file) {
    UploadedFile result = new UploadedFile(the_timestamp, the_county_id,
                                           the_type, the_hash, the_hash_status,
                                           the_file);
    if (CACHE.containsKey(result)) {
      result = CACHE.get(result);
    } else {
      CACHE.put(result, result);
      BY_ID.put(result.id(), result);
    }
    return result;
  }

  /**
   * @return the next ID
   */
  private static synchronized long getID() {
    return current_id++;
  }

  /**
   * @return the ID of this object.
   */
  public Long id() {
    return my_id;
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
  public String countyID() {
    return my_county_id;
  }
  
  /**
   * @return the type of this file.
   */
  public FileType type() {
    return my_type;
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
   * An enumeration of file types that can be uploaded.
   */
  public enum FileType {
    BALLOT_MANIFEST,
    CAST_VOTE_RECORD_EXPORT;
  }
  
  /**
   * An enumeration of hash statuses.
   */
  public enum HashStatus {
    VERIFIED,
    DOES_NOT_VERIFY,
    VERIFICATION_NOT_ATTEMPTED;
  }
}
