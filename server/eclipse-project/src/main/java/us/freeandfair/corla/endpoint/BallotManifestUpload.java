/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.csv.BallotManifestParser;
import us.freeandfair.corla.csv.ColoradoBallotManifestParser;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileType;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.util.FileHelper;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The "ballot manifest upload" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class BallotManifestUpload implements Endpoint {
  /**
   * The upload buffer size, in bytes.
   */
  private static final int BUFFER_SIZE = 1048576; // 1 MB
  
  /**
   * The maximum upload size, in bytes.
   */
  private static final int MAX_UPLOAD_SIZE = 314572800; // 300 MB
  
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
    return "/upload-ballot-manifest";
  }

  /**
   * Attempts to save the specified file in the database.
   * 
   * @param the_file The file.
   * @param the_county The county that uploaded the file.
   * @param the_hash The claimed hash of the file.
   * @return the resulting entity if successful, null otherwise
 w */
  private UploadedFile attemptFilePersistence(final File the_file, 
                                              final String the_county,
                                              final String the_hash) {
    UploadedFile result = null;
    
    if (Persistence.hasDB()) {
      try {
        result = UploadedFile.instance(Instant.now(), the_county, 
                                       FileType.BALLOT_MANIFEST, 
                                       the_hash, HashStatus.NOT_CHECKED, the_file);
      } catch (final HibernateException e) {
        Main.LOGGER.info("could not persist uploaded file of size " + 
                         the_file.length() + ": " + e);
      }
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
                            final UploadInformation the_info) {
    try {
      final HttpServletRequest raw = SparkHelper.getRaw(the_request);
      the_info.my_ok = ServletFileUpload.isMultipartContent(raw);
      
      Main.LOGGER.info("handling ballot manifest upload request from " + 
                       raw.getRemoteHost());
      if (the_info.my_ok) {
        final ServletFileUpload upload = new ServletFileUpload();
        final FileItemIterator fii = upload.getItemIterator(raw);
        while (fii.hasNext()) {
          final FileItemStream item = fii.next();
          final String name = item.getFieldName();
          final InputStream stream = item.openStream();
          
          if (item.isFormField()) {
            the_info.my_form_fields.put(item.getFieldName(), 
                                        Streams.asString(stream));
          } else if ("bmi_file".equals(name)) {
            // save the file
            the_info.my_file = File.createTempFile("upload", ".csv");
            final OutputStream os = new FileOutputStream(the_info.my_file);
            final int total =
                FileHelper.bufferedCopy(stream, os, BUFFER_SIZE, MAX_UPLOAD_SIZE);

            if (total >= MAX_UPLOAD_SIZE) {
              Main.LOGGER.info("attempt to upload file greater than max size from " + 
                               raw.getRemoteHost());
              the_info.my_response_string = "Upload Failed";
              the_info.my_response_status = HttpStatus.UNPROCESSABLE_ENTITY_422;
              the_info.my_ok = false;
            } else {
              Main.LOGGER.info("successfully saved file of size " + total + " from " + 
                               raw.getRemoteHost());
            }
            os.close();
          }
        }
      }
    } catch (final IOException | FileUploadException e) {
      the_info.my_response_string = "Upload Failed";
      the_info.my_response_status = HttpStatus.UNPROCESSABLE_ENTITY_422;
      the_info.my_ok = false;
    }
  }

  /**
   * Parses an uploaded ballot manifest and attempts to persist it to the database.
   * 
   * @param the_info The upload information to use and update.
   */
  // the CSV parser can throw arbitrary runtime exceptions, which we must catch
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
  private void parseAndPersistFile(final UploadInformation the_info) {
    String county = the_info.my_form_fields.get("county");
    final String hash = the_info.my_form_fields.get("hash");
    
    if (hash == null || the_info.my_file == null) {
      the_info.my_response_string = "Bad Request";
      the_info.my_response_status = HttpStatus.BAD_REQUEST_400;
      the_info.my_ok = false;
    }

    // it's not required to specify a county on a ballot manifest, since they 
    // have the county as a column
    
    if (county == null) {
      county = "unknown";
    }
    
    if (the_info.my_ok) {
      try (InputStream bmi_is = new FileInputStream(the_info.my_file)) {
        final InputStreamReader bmi_isr = new InputStreamReader(bmi_is, "UTF-8");
        final BallotManifestParser parser = new ColoradoBallotManifestParser(bmi_isr);
        if (!parser.parse()) {
          the_info.my_response_status = HttpStatus.UNPROCESSABLE_ENTITY_422;
          the_info.my_ok = false;
          the_info.my_response_string = "Malformed Ballot Manifest File";
        }
        Main.LOGGER.info(parser.parsedIDs().size() + 
                         " ballot manifest records parsed from upload file");
        final long count = count();
        if (count >= 0) {
          Main.LOGGER.info(count + " uploaded ballot manifest records in storage");
        }
        attemptFilePersistence(the_info.my_file, county, hash);
      } catch (final RuntimeException | IOException e) {
        Main.LOGGER.info("could not parse malformed ballot manifest file: " + e);
        the_info.my_ok = false;
        the_info.my_response_status = HttpStatus.UNPROCESSABLE_ENTITY_422;
        the_info.my_response_string = "Malformed Ballot Manifest File";
      } 
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  // the CSV parser can throw arbitrary runtime exceptions, which we must catch
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public String endpoint(final Request the_request, final Response the_response) {
    final UploadInformation info = new UploadInformation();
    info.my_ok = true;
    
    handleUpload(the_request, info);
    
    // process the temp file, putting it in the database if persistence is enabled 
    
    if (info.my_ok) {
      parseAndPersistFile(info);
    }
    
    if (info.my_file != null) {
      try {
        if (!info.my_file.delete()) {
          Main.LOGGER.error("Unable to delete temp file " + info.my_file);
        }
      } catch (final SecurityException e) {
        // ignored - should never happen
      }
    }

    the_response.status(info.my_response_status);
    return info.my_response_string;
  }
  
  /**
   * Count the ballot manifests in storage.
   * 
   * @return the number of ACVRs, or -1 if the count could not be determined.
   */
  private long count() {
    long result = -1;
    
    try {
      Persistence.beginTransaction();
      final Session s = Persistence.currentSession();
      final Query<Long> query = 
          s.createQuery("select count(1) from BallotManifestInfo", Long.class);
      result = query.getSingleResult();
      Persistence.commitTransaction();
    } catch (final PersistenceException e) {
      // ignore
    }
    
    return result;
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
     * A flag indicating whether the upload is "ok".
     */
    protected boolean my_ok = true;
    
    /**
     * A map of form field names and values.
     */
    protected Map<String, String> my_form_fields = new HashMap<String, String>();
    
    /**
     * An HTTP response status.
     */
    protected int my_response_status = HttpStatus.OK_200;
    
    /**
     * A response string.
     */
    protected String my_response_string = "OK";
  }
}
