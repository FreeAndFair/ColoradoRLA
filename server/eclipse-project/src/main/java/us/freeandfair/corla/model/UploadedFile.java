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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import us.freeandfair.corla.hibernate.Persistence;

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
   * The database ID for this uploaded file.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(updatable = false, nullable = false)
  private Long my_id;
  
  /**
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  @Column(updatable = false, nullable = false)
  private Instant my_timestamp;

  /**
   * The county that uploaded the file.
   */
  @Column(updatable = false, nullable = false)
  private String my_county_id;
  
  /**
   * The type of the file.
   */
  @Column(updatable = false, nullable = false)
  private FileType my_type;
  
  /**
   * The hash of the file.
   */
  @Column(updatable = false, nullable = false)
  private String my_hash;
  
  /**
   * The status of hash verification.
   */
  @Column(nullable = false)
  private HashStatus my_hash_status;
  
  /**
   * The uploaded file. 
   */
  @Lob
  @Column(updatable = false, nullable = false)
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
   * @param the_hash_status A flag indicating whether the file matches
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
   * Returns an uploaded file with the specified parameters after persisting it to
   * the database.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county that uploaded the file.
   * @param the_type The file type.
   * @param the_hash The hash entered at upload time.
   * @param the_hash_status A flag indicating whether the file matches
   * the hash.
   * @param the_file The file (as a java.io.File).
   * @return the resulting uploaded file, or null if persistence is not available.
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public static synchronized UploadedFile instance(final Instant the_timestamp,
                                                   final String the_county_id,
                                                   final FileType the_type,
                                                   final String the_hash,
                                                   final HashStatus the_hash_status,
                                                   final File the_file) {
    UploadedFile result = null;
    Transaction transaction = null;
    InputStream is = null;
    
    if (Persistence.hasDB()) {
      try {
        final Session session = Persistence.currentSession();
        transaction = session.beginTransaction();
        is = new FileInputStream(the_file);
        final Blob blob = 
            Persistence.currentSession().getLobHelper().
            createBlob(is, the_file.length());
        final UploadedFile uploaded_file = new UploadedFile(the_timestamp, the_county_id,
                                                            the_type, the_hash, 
                                                            the_hash_status, blob);
        session.save(uploaded_file);
        transaction.commit();
        result = uploaded_file;
      } catch (final HibernateException | IOException e) {
        if (transaction != null) {
          transaction.rollback();
        }
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (final IOException e) {
            // ignored
          }
        }
      }
    }
    
    return result;
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
    MISMATCH,
    NOT_CHECKED;
  }
}
