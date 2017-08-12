/*
 * / * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 27, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * 
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
import java.sql.Blob;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.Session;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.csv.CVRExportParser;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.model.UploadedFile.FileType;
import us.freeandfair.corla.model.UploadedFile.HashStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyDashboardQueries;
import us.freeandfair.corla.util.FileHelper;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The "CVR upload" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class CVRExportUpload extends AbstractEndpoint {
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
    return "/upload-cvr-export";
  }

  /**
   * Attempts to save the specified file in the database.
   * 
   * @param the_response The response object (for error reporting).
   * @param the_file The file.
   * @param the_county_id The county that uploaded the file.
   * @param the_hash The claimed hash of the file.
   * @param the_timestamp The timestamp to apply to the file.
   * @return the resulting entity if successful, null otherwise w
   */
  private UploadedFile attemptFilePersistence(final Response the_response, 
                                              final File the_file,
                                              final Integer the_county_id,
                                              final String the_hash,
                                              final Instant the_timestamp) {
    UploadedFile result = null;

    try (FileInputStream is = new FileInputStream(the_file)) {
      final Session session = Persistence.currentSession();
      final Blob blob = session.getLobHelper().createBlob(is, the_file.length());
      result = new UploadedFile(the_timestamp, the_county_id,
                                FileType.CAST_VOTE_RECORD_EXPORT, the_hash,
                                HashStatus.NOT_CHECKED, blob);
      Persistence.saveOrUpdate(result);
    } catch (final PersistenceException | IOException e) {
      badDataType(the_response, "could not persist file of size " + the_file.length());
    }

    return result;
  }

  /**
   * Updates the appropriate county dashboard to reflect a new CVR export upload.
   * @param the_response The response object (for error reporting).
   * @param the_county_id The county ID.
   * @param the_timestamp The timestamp.
   */
  private void updateCountyDashboard(final Response the_response, 
                                     final Integer the_county_id, 
                                     final Instant the_timestamp) {
    final CountyDashboard cdb = CountyDashboardQueries.get(the_county_id);
    if (cdb == null) {
      serverError(the_response, "could not locate county dashboard");
    } else {
      cdb.setCVRUploadTimestamp(the_timestamp);
      try {
        Persistence.saveOrUpdate(cdb);
      } catch (final PersistenceException e) {
        serverError(the_response, "could not update county dashboard");
      }
    }
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

      Main.LOGGER.info("handling CVR upload request from " + raw.getRemoteHost());
      if (the_info.my_ok) {
        final ServletFileUpload upload = new ServletFileUpload();
        final FileItemIterator fii = upload.getItemIterator(raw);
        while (fii.hasNext()) {
          final FileItemStream item = fii.next();
          final String name = item.getFieldName();
          final InputStream stream = item.openStream();

          if (item.isFormField()) {
            the_info.my_form_fields.put(item.getFieldName(), Streams.asString(stream));
          } else if ("cvr_file".equals(name)) {
            // save the file
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
    } catch (final IOException | FileUploadException e) {
      badDataContents(the_response, "Upload Failed");
      the_info.my_ok = false;
    }
  }

  /**
   * Parses an uploaded CVR export and attempts to persist it to the database.
   * 
   * @param the_response The response (for error reporting).
   * @param the_county_id The county identifier for this file.
   * @param the_info The upload information to use and update.
   */
  // the CSV parser can throw arbitrary runtime exceptions, which we must catch
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
  private void parseAndPersistFile(final Response the_response,
                                   final Integer the_county_id,
                                   final UploadInformation the_info) {
    final String hash = the_info.my_form_fields.get("hash");

    if (hash == null || the_info.my_file == null) {
      invariantViolation(the_response, "Bad Request");
      the_info.my_ok = false;
    }

    if (the_info.my_ok) {
      try (InputStream cvr_is = new FileInputStream(the_info.my_file)) {
        final InputStreamReader cvr_isr = new InputStreamReader(cvr_is, "UTF-8");
        final CVRExportParser parser =
            new DominionCVRExportParser(cvr_isr, the_county_id, the_info.my_timestamp);
        if (parser.parse()) {
          Main.LOGGER.info(parser.parsedIDs().size() + " CVRs parsed from " + 
                           the_county_id + " county upload file");
          final OptionalLong count = count();
          if (count.isPresent()) {
            Main.LOGGER.info(count.getAsLong() + " uploaded CVRs in storage");
          }
          updateCountyDashboard(the_response, the_county_id, the_info.my_timestamp);
          attemptFilePersistence(the_response, the_info.my_file, the_county_id, hash,
                                 the_info.my_timestamp);
        } else {
          Main.LOGGER.info("could not parse malformed CVR export file");
          badDataContents(the_response, "Malformed CVR Export File");
          the_info.my_ok = false;
        }
      } catch (final RuntimeException | IOException e) {
        Main.LOGGER.info("could not parse malformed CVR export file: " + e);
        badDataContents(the_response, "Malformed CVR Export File");
        the_info.my_ok = false;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    final UploadInformation info = new UploadInformation();
    info.my_timestamp = Instant.now();
    info.my_ok = true;

    // we know we have county authorization, so let's find out which county
    final Integer county_id = Authentication.authenticatedCountyID(the_request);

    if (county_id == null) {
      unauthorized(the_response, "unauthorized administrator for ballot manifest upload");
    }

    handleUpload(the_request, the_response, info);

    // now process the temp file, putting it in the database if persistence is
    // enabled

    if (info.my_ok) {
      parseAndPersistFile(the_response, county_id, info);
    }

    // delete the temp file, if it exists

    if (info.my_file != null) {
      try {
        if (!info.my_file.delete()) {
          Main.LOGGER.error("Unable to delete temp file " + info.my_file);
        }
      } catch (final SecurityException e) {
        // ignored - should never happen
      }
    }
    // @todo kiniry Review this logic and its interrelationship to
    // endpoint before filtering.
    the_response.status(info.my_response_status);
    return info.my_response_string;
  }

  /**
   * Count the uploaded CVRs in storage.
   * 
   * @return the number of uploaded CVRs.
   */
  private OptionalLong count() {
    OptionalLong result = OptionalLong.empty();

    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(cb.count(root)).where(
          cb.equal(root.get("my_record_type"), RecordType.UPLOADED));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      // ignore
    }

    return result;
  }

  /**
   * This endpoint requires county authorization.
   * 
   * @return COUNTY
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
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
     * An HTTP response status.
     */
    protected int my_response_status = HttpStatus.OK_200;

    /**
     * A response string.
     */
    protected String my_response_string = "OK";
  }
}
