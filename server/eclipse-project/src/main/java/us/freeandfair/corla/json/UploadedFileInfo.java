/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.time.Instant;

import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The standard response provided by the server to indicate the state of an 
 * uploaded file.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class UploadedFileInfo {
  /**
   * The file ID.
   */
  private final Long my_file_id;
  
  /**
   * The county ID.
   */
  private final Long my_county_id;

  /**
   * The filename.
   */
  private final String my_filename;
  
  /**
   * The file size (in bytes).
   */
  private final Long my_file_size;
  
  /**
   * The timestamp.
   */
  private final Instant my_timestamp;
  
  /**
   * The hash status.
   */
  private final HashStatus my_hash_status;
  
  /**
   * The file status.
   */
  private final FileStatus my_status;
  
  /**
   * Constructs a new UploadedFileResponse for the specified UploadedFile.
   * 
   * @param the_uploaded_file The uploaded file.
   * @param the_file_size The size of the uploaded file, in bytes.
   */
  public UploadedFileInfo(final UploadedFile the_uploaded_file,
                              final Long the_file_size) {
    my_file_id = the_uploaded_file.id();
    my_county_id = the_uploaded_file.countyID();
    my_filename = the_uploaded_file.filename();
    my_file_size = the_file_size;
    my_timestamp = the_uploaded_file.timestamp();
    my_hash_status = the_uploaded_file.hashStatus();
    my_status = the_uploaded_file.status();
  }
  
  /**
   * @return the file ID.
   */
  public Long fileID() {
    return my_file_id;
  }
  
  /**
   * @return the county ID.
   */
  public Long countyID() {
    return my_county_id;
  }
  
  /**
   * @return the filename.
   */
  public String filename() {
    return my_filename;
  }
  
  /**
   * @return the file size.
   */
  public Long fileSize() {
    return my_file_size;
  }
  
  /**
   * @return the timestamp.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the hash status.
   */
  public HashStatus hashStatus() {
    return my_hash_status;
  }
  
  /**
   * @return the status.
   */
  public FileStatus status() {
    return my_status;
  }
}
