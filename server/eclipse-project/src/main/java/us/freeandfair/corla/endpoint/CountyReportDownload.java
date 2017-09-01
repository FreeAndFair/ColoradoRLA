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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.report.CountyReport;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The county report download endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CountyReportDownload extends AbstractEndpoint {
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
    return "/county-report";
  }

  /**
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  // necessary to break out of the lambda expression in case of IOException
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  public String endpoint(final Request the_request, final Response the_response) {
    final boolean pdf = "pdf".equalsIgnoreCase(the_request.queryParams("file_type"));
    try {
      final OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
      final BufferedOutputStream bos = new BufferedOutputStream(os);
      final CountyReport cr = 
          new CountyReport(Main.authentication().authenticatedCounty(the_request));
      if (pdf) {
        the_response.type("application/pdf");
        bos.write(cr.generatePDF());
      } else {
        the_response.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        bos.write(cr.generateExcel());
      }
      bos.flush();
      ok(the_response);
    } catch (final IOException | PersistenceException e) {
      serverError(the_response, "Unable to stream response");
    }
    return my_endpoint_result.get();
  }
}
