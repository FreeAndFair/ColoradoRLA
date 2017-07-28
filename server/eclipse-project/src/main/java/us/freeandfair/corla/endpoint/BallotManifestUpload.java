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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.csv.BallotManifestParser;
import us.freeandfair.corla.csv.ColoradoBallotManifestParser;
import us.freeandfair.corla.model.BallotManifestInfo;

/**
 * The "ballot manifest upload" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotManifestUpload implements Endpoint {
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
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    // this is a multipart request - there's a "county" identifier, and a "cvr_file"
    // containing the actual file
    the_request.attribute("org.eclipse.jetty.multipartConfig", 
                          new MultipartConfigElement("/tmp"));
    boolean ok = true;
    try (InputStream is = the_request.raw().getPart("bmi_file").getInputStream()) {
      final InputStreamReader isr = new InputStreamReader(is);
      final BallotManifestParser parser = new ColoradoBallotManifestParser(isr);
      ok = parser.parse();
      Main.LOGGER.info(parser.ballotManifestInfo().size() + 
                       " ballot manifest records parsed from upload file");
      Main.LOGGER.info(BallotManifestInfo.getMatching(null).size() + 
                       " uploaded ballot manifest records in storage");
    } catch (final IOException | ServletException e) {
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
