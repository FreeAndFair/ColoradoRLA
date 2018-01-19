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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.cxf.attachment.Rfc5987Util;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMState.DoSDashboardState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.report.CountyReport;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The county report download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CountyReportDownload extends AbstractEndpoint {
  /**
   * The "county" parameter.
   */
  public static final String COUNTY = "county";
  
  /**
   * The states in which this endpoint can provide a result.
   */
  private static final List<ASMState> LEGAL_STATES = 
      Arrays.asList(DoSDashboardState.COMPLETE_AUDIT_INFO_SET,
                    DoSDashboardState.DOS_AUDIT_ONGOING, 
                    DoSDashboardState.DOS_ROUND_COMPLETE,
                    DoSDashboardState.DOS_AUDIT_COMPLETE,
                    DoSDashboardState.AUDIT_RESULTS_PUBLISHED);
  
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
    return AuthorizationType.EITHER;
  }
  
  /**
   * Validate the request parameters. In this case, if the county
   * parameter exists, it must be parseable as a long.
   * 
   * @param the_request The request.
   */
  @Override
  protected boolean validateParameters(final Request the_request) {
    final String county = the_request.queryParams(COUNTY);
    boolean result = true;
    
    try { 
      if (county == null && Main.authentication().authenticatedCounty(the_request) == null) {
        // it's a DoS user, but they didn't specify a county
        result = false;
      } else if (county != null) {
        Long.parseLong(county);
      }
    } catch (final NumberFormatException e) {
      result = false;
    }

    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  // necessary to break out of the lambda expression in case of IOException
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  public String endpointBody(final Request the_request, final Response the_response) {
    // if we haven't defined the election, this is "data not found"
    final DoSDashboardASM dos_asm = ASMUtilities.asmFor(DoSDashboardASM.class, 
                                                        DoSDashboardASM.IDENTITY);
    if (!LEGAL_STATES.contains(dos_asm.currentState())) {
      dataNotFound(the_response, "No state report available in this state.");
    }
    
    // we know we have either state or county authentication; this will be null
    // for state authentication
    County county = Main.authentication().authenticatedCounty(the_request);

    if (county == null) {
      county = 
          Persistence.getByID(Long.parseLong(the_request.queryParams(COUNTY)), County.class);
      if (county == null) {
        badDataContents(the_response, "county " + the_request.queryParams(COUNTY) +
                                      " does not exist");
      }
      assert county != null; // makes FindBugs happy
    }

    final boolean pdf = "pdf".equalsIgnoreCase(the_request.queryParams("file_type"));
    final CountyReport cr = new CountyReport(county);
    byte[] file = new byte[0];
    String filename = "";
    
    if (pdf) {
      the_response.type("application/pdf");
      filename = cr.filenamePDF();
      file = cr.generatePDF();
    } else {
      the_response.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      filename = cr.filenameExcel();
      try {
        file = cr.generateExcel();
      } catch (final IOException e) {
        serverError(the_response, "Unable to generate Excel file");
      }
    }
    
    try {
      the_response.raw().setHeader("Content-Disposition", "attachment; filename=\"" + 
          Rfc5987Util.encode(filename, "UTF-8") + "\"");
    } catch (final UnsupportedEncodingException e) {
      serverError(the_response, "UTF-8 is unsupported (this should never happen)");
    }
    
    try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
         BufferedOutputStream bos = new BufferedOutputStream(os)) {
      bos.write(file);
      bos.flush();
      ok(the_response);
    } catch (final IOException | PersistenceException e) {
      serverError(the_response, "Unable to stream response");
    }
    
    return my_endpoint_result.get();
  }
}
