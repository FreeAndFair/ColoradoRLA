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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The "audit CVR upload" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ACVRUpload implements Endpoint {
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
    return "/upload-audit-cvr";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  // catching a null pointer exception is the most sensible way to deal with the 
  // request not having the parts we need to process
  @SuppressFBWarnings(value = {"OS_OPEN_STREAM"}, 
                      justification = "FindBugs false positive with resources.")
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
  public String endpoint(final Request the_request, final Response the_response) {
    // this is a multipart request - there's a "county" identifier, and a "cvr_file"
    // containing the actual file
    the_request.attribute("org.eclipse.jetty.multipartConfig", 
                          new MultipartConfigElement("/tmp"));
    boolean ok = true;
    try (InputStream acvr_is = the_request.raw().getPart("audit_cvr").getInputStream()) {
      final InputStreamReader acvr_isr = new InputStreamReader(acvr_is, "UTF-8");
      final BufferedReader acvr_br = new BufferedReader(acvr_isr);
      final String acvr_json = acvr_br.lines().collect(Collectors.joining("\n"));
      
      final CastVoteRecord acvr = Main.GSON.fromJson(acvr_json, CastVoteRecord.class);
      
      // we need to create a new CVR instance the "right" way, so it persists and also
      // has the right type
      final CastVoteRecord real_acvr = 
          CastVoteRecord.instance(RecordType.AUDITOR_ENTERED, Instant.now(), 
                                  acvr.countyID(), acvr.scannerID(), acvr.batchID(), 
                                  acvr.recordID(), acvr.imprintedID(), acvr.ballotStyle(), 
                                  acvr.choices());
      Main.LOGGER.info("Audit CVR parsed and stored as id " + real_acvr.id());
      Main.LOGGER.info(CastVoteRecord.getMatching(null, RecordType.AUDITOR_ENTERED).size() + 
                       " audit CVRs in storage");
    } catch (final JsonSyntaxException | IOException | ServletException | 
                   NullPointerException e) {
      Main.LOGGER.info("Unable to parse ACVR: " + e);
      ok = false;
    }
    
    if (ok) {
      return "OK";
    } else {
      the_response.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
      return "Not OK";
    }
  }
}
