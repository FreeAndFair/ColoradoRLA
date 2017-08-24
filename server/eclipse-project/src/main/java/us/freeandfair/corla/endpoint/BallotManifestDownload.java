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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import com.google.gson.stream.JsonWriter;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The ballot manifest download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotManifestDownload extends AbstractCountyDashboardEndpoint {
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.GET;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/ballot-manifest";
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      final JsonWriter jw = new JsonWriter(bw);
      jw.beginArray();
      final Stream<BallotManifestInfo> bmi_stream = 
          Persistence.getAllAsStream(BallotManifestInfo.class);
      bmi_stream.forEach((the_bmi) -> {
        try {
          jw.jsonValue(Main.GSON.toJson(Persistence.unproxy(the_bmi)));
          Persistence.evict(the_bmi);
        } catch (final IOException e) {
          throw new UncheckedIOException(e);
        } 
      });
      jw.endArray();
      jw.flush();
      jw.close();
      ok(the_response);
    } catch (final IOException e) {
      serverError(the_response, "Unable to stream response");
    }
    return my_endpoint_result;
  }
}
