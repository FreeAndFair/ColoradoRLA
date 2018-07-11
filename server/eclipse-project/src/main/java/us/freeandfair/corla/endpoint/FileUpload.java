/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.crypto.HashChecker;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.FileHelper;
import us.freeandfair.corla.util.SparkHelper;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The file upload endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class FileUpload extends AbstractEndpoint {
  /**
   * The "hash" form data field name.
   */
  public static final String HASH = "hash";
  
  /**
   * The "file" form data field name.
   */
  public static final String FILE = "file";
  
  /**
   * The upload buffer size, in bytes.
   */
  private static final int BUFFER_SIZE = 1048576; // 1 MB

  /**
   * The maximum upload size, in bytes.
   */
  private static final int MAX_UPLOAD_SIZE = 1073741824; // 1 GB

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/upload-file";
  }

  /**
   * This endpoint requires county authorization.
   * 
   * @return COUNTY
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }
  
  /**
   * Attempts to save the specified file in the database.
   * 
   * @param the_response The response object (for error reporting).
   * @param the_info The upload info about the file and hash.
   * @param the_county The county that uploaded the file.
   * @return the resulting entity if successful, null otherwise
   */
  // we are deliberately ignoring the return value of lnr.skip()
  @SuppressFBWarnings("SR_NOT_CHECKED")
  private UploadedFile attemptFilePersistence(final Response the_response, 
                                              final UploadInformation the_info,
                                              final County the_county) {
    UploadedFile result = null;
    
    try (FileInputStream is = new FileInputStream(the_info.my_file);
         LineNumberReader lnr = 
             new LineNumberReader(new InputStreamReader(new FileInputStream(the_info.my_file), 
                                                        "UTF-8"))) {
      final Blob blob = Persistence.blobFor(is, the_info.my_file.length());
      final HashStatus hash_status;
      
      // first, compute the approximate number of records in the file
      lnr.skip(Integer.MAX_VALUE);
      final int approx_records = lnr.getLineNumber();
      
      if (the_info.my_computed_hash == null) {
        hash_status = HashStatus.NOT_CHECKED;
      } else if (the_info.my_computed_hash.equals(the_info.my_uploaded_hash)) {
        hash_status = HashStatus.VERIFIED;
      } else {
        hash_status = HashStatus.MISMATCH;
      }
      result = new UploadedFile(the_info.my_timestamp, 
                                the_county,
                                the_info.my_filename,
                                FileStatus.NOT_IMPORTED, 
                                the_info.my_uploaded_hash,
                                hash_status, blob, 
                                the_info.my_file.length(),
                                approx_records);
      Persistence.save(result);
      Persistence.flush();
    } catch (final PersistenceException | IOException e) {
      badDataType(the_response, "could not persist file of size " + 
                                the_info.my_file.length());
      the_info.my_ok = false;
    }
    return result;
  }
  
  /**
   * Handles the upload of the file, updating the provided UploadInformation.
   * 
   * @param the_request The request to use.
   * @param the_info The upload information to update.
   */
  // I don't see any other way to implement the buffered reading
  // than a deeply nested if statement
  @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
  private void handleUpload(final Request the_request,
                            final Response the_response,
                            final UploadInformation the_info) {
    try {
           
      final HttpServletRequest raw = SparkHelper.getRaw(the_request);
      the_info.my_ok = ServletFileUpload.isMultipartContent(raw);

      Main.LOGGER.info("handling file upload request from " + raw.getRemoteHost());
      if (the_info.my_ok) {
        final ServletFileUpload upload = new ServletFileUpload();
        final FileItemIterator fii = upload.getItemIterator(raw);
        while (fii.hasNext()) {
          final FileItemStream item = fii.next();
          final String name = item.getFieldName();
          final InputStream stream = item.openStream();
          final InputStream stream2 = item.openStream();
          
          if (item.isFormField()) {
            the_info.my_form_fields.put(item.getFieldName(), Streams.asString(stream));
          } else if (FILE.equals(name)) {
            // save the file
            the_info.my_filename = item.getName();
            the_info.my_file = File.createTempFile("upload", ".csv");
            final OutputStream os = new FileOutputStream(the_info.my_file);
            final int total =
                FileHelper.bufferedCopy(stream, os, BUFFER_SIZE, MAX_UPLOAD_SIZE);

            if (total >= MAX_UPLOAD_SIZE) {
              Main.LOGGER.info("attempt to upload file greater than max size from " +
                               raw.getRemoteHost());
              badDataContents(the_response, "Upload Failed");
              the_info.my_ok = false;
            } else {
              Main.LOGGER.info("successfully saved file of size " + total + " from " +
                               raw.getRemoteHost());
            }
            os.close();
          }
        }
      }
      
      if (the_info.my_file == null) {
        // no file was actually uploaded
        the_info.my_ok = false;
        badDataContents(the_response, "No file was uploaded");
      } else if (!the_info.my_form_fields.containsKey(HASH)) {
        // no hash was provided
        the_info.my_ok = false;
        badDataContents(the_response, "No hash was provided with the uploaded file");
      }
    } catch (final IOException | FileUploadException e) {
      the_info.my_ok = false;
      badDataContents(the_response, "Upload Failed");
    }
  }

  
  /**
   * Copies uploaded file which is in a temporary location and it's hash into an archival location
   * 
   * Steps:
   *  1. Based on operating system, fetches archival file path from property file
   *  2. Creates the file path if not existing
   *  3. Appends timestamp to file and archives it by making a copy of the temporary file.
   *  4. Appends the same timestamp to create a new file that would contain the hash value and archives it.  
   *  
   * @param uploadInformation contains all the specifices of the uploaded file that is in temporary location along with its hash value.
   */
  private void archive(UploadInformation uploadInformation) {
    
      // name of file that was uploaded  
      String uploadedFileName = uploadInformation.my_filename; 
      
      // Prepend timestamp to file name.
      // Not append timestamp as we can't assure if file names follow filename.ext format) 
      // Example: 2018-05-17-T10-44-04.244-Arapahoe 2016 Primary Ballot Manifest.csv
      // Cannot use ':' as in HH:mm:ss because, when running in Windows, it throws java.nio.file.InvalidPathException
      String arvchiveFileName = new SimpleDateFormat("yyyy-MM-dd-'T'HH-mm-ss.SSS-").format(new Date()) + uploadedFileName;
      
      // create corresponding hash file name to archive. Prepend archiveFileName so it is paired correctly 
      String archiveHashFileName = null;      
      if (StringUtils.contains(uploadedFileName, ".")) { // file name contains a "."
         // get the file name till the "." and append "-Hash.text" to it
         // Example: 
         //        Archive File Name:      2018-05-17-T10-44-04.390-Arapahoe 2016 Primary Ballot Manifest.csv
         //        Archive Hash File Name: 2018-05-17-T10-44-04.390-Arapahoe 2016 Primary Ballot Manifest-Hash.txt        
         archiveHashFileName = StringUtils.substringBeforeLast(arvchiveFileName, ".") + "-Hash.txt";
      } else {
        // file name has no "." just append "-Hash.text" to it
        // Example: 
        //        Archive File Name:      2018-05-17-T10-44-04.390-Arapahoe 2016 Primary Ballot Manifest
        //        Archive Hash File Name: 2018-05-17-T10-44-04.390-Arapahoe 2016 Primary Ballot Manifest-Hash.txt        
        archiveHashFileName = arvchiveFileName + "-Hash.txt";
      }
  
      // fetch location where file needs to be uploaded to for archival
      String archiveFilePath = fetchArchiveFilePath();    
      // create directory if not existing
      File archiveFileDir = new File(archiveFilePath);      
      archiveFileDir.mkdirs();    
      
      // archive file by copying it to destination
      archiveFile (archiveFilePath+arvchiveFileName, uploadInformation.my_file.toPath());
      // create corresponding hash text file with hash value in it
      archiveHashFile(archiveFilePath+archiveHashFileName, uploadInformation.my_uploaded_hash);      

  }

  /**
   * Copies passed in temporary file into archive destination, also renames it to its original name
   * 
   * @param filePathAndName path and file name of the  file to be archived
   * @param sourcePath path and file name of the temporary file created by the server
   */
  private void archiveFile(String filePathAndName, Path sourcePath)  {
    try {        
        // copy the temp file into archive destination
        Path path = Files.copy( sourcePath, Paths.get(filePathAndName));        
        
        if (path != null) {
           Main.LOGGER.info("Successfully archived file (" + filePathAndName + ")." );
         } else {
           Main.LOGGER.info("Error archiving file (" + filePathAndName + ")." );           
         }
                   
      } catch (IOException e) {
          Main.LOGGER.info("Encountered exception while archiving file (" + filePathAndName  + ") - " + e.getMessage());
      }
  }
  

  /**
   * Creates a new hash file and copies passed in hash value and archives it.
   * 
   * @param archiveHashFileName path and file name of the hash file to be archived
   * @param hashValue hash content that will be written into the file
   */
  private void archiveHashFile(String archiveHashFileName, String hashValue) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archiveHashFileName))) {

      bw.write(hashValue);
      Main.LOGGER.info("Successfully archived hash file (" + archiveHashFileName + ")." );

    } catch (IOException e) {
      Main.LOGGER.info("Encountered exception while archiving hash file (" + archiveHashFileName  + ") - " + e.getMessage());
    }   
    
  }


  /**
   * Based on operating system, retrieves archive file location from property file.
   * 
   * @return archive file location 
   */
  private String fetchArchiveFilePath() {
      Properties properties = Main.properties();
      String osName = System.getProperty("os.name").toLowerCase();
      boolean isWindows = osName.startsWith("windows");
      String archiveFileLocation;
      if (isWindows){
        archiveFileLocation = properties.getProperty("windows_upload_file_location");
      } else { // it's UNIX
        archiveFileLocation = properties.getProperty("unix_upload_file_location");        
      }
      return archiveFileLocation;
  }
  
  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    final UploadInformation info = new UploadInformation();
    info.my_timestamp = Instant.now();
    info.my_ok = true;

    // we know we have county authorization, so let's find out which county
    final County county = Main.authentication().authenticatedCounty(the_request);

    if (county == null) {
      unauthorized(the_response, "unauthorized administrator for CVR export upload");
      return my_endpoint_result.get();
    } 

    // we can exit in several different ways, so let's make sure we delete
    // the temp file even if we exit exceptionally
    try {
      handleUpload(the_request, the_response, info);

      // now process the temp file, putting it in the database if persistence is
      // enabled

      UploadedFile uploaded_file = null;
    
      if (info.my_ok) {
        info.my_computed_hash = HashChecker.hashFile(info.my_file);
        info.my_uploaded_hash = 
            info.my_form_fields.get(HASH).toUpperCase(Locale.US).trim();
        uploaded_file = attemptFilePersistence(the_response, info, county);
      }

      if (uploaded_file != null) {
        okJSON(the_response, Main.GSON.toJson(uploaded_file));
      } // else another result code has already been set
    } finally {
      // delete the temp file, if it exists
      if (info.my_file != null) {
        try {
          // archive file before deleting
          archive (info);
          if (!info.my_file.delete()) {
            Main.LOGGER.error("Unable to delete temp file " + info.my_file);
          }
        } catch (final SecurityException e) {
          // ignored - should never happen
        }
      }
    } 
    return my_endpoint_result.get();
  }
  
  /**
   * A small class to encapsulate data dealt with during an upload.
   */
  private static class UploadInformation {
    /**
     * The uploaded file.
     */
    protected File my_file;

    /**
     * The original name of the uploaded file.
     */
    protected String my_filename;
    
    /**
     * The timestamp of the upload.
     */
    protected Instant my_timestamp;

    /**
     * A flag indicating whether the upload is "ok".
     */
    protected boolean my_ok = true;

    /**
     * A map of form field names and values.
     */
    protected Map<String, String> my_form_fields = new HashMap<String, String>();

    /**
     * The uploaded hash.
     */
    protected String my_uploaded_hash;
    
    /**
     * The computed hash.
     */
    protected String my_computed_hash;
  }
}
