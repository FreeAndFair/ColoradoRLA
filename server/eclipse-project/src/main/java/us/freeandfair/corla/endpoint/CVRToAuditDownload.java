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

import static us.freeandfair.corla.util.PrettyPrinter.booleanYesNo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;

import javax.persistence.PersistenceException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.cxf.attachment.Rfc5987Util;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.json.CVRToAuditResponse.BallotOrderComparator;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.util.SparkHelper;

/**
 * The CVR to audit download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class CVRToAuditDownload extends AbstractEndpoint {
  /**
   * The "start" parameter.
   */
  public static final String START = "start";
  
  /**
   * The "ballot_count" parameter.
   */
  public static final String BALLOT_COUNT = "ballot_count";
  
  /**
   * The "include duplicates" parameter.
   */
  public static final String INCLUDE_DUPLICATES = "include_duplicates";
  
  /**
   * The "include audited" parameter.
   */
  public static final String INCLUDE_AUDITED = "include_audited";
  
  /**
   * The "round" parameter.
   */
  public static final String ROUND = "round";
  
  /**
   * The "county" parameter.
   */
  public static final String COUNTY = "county";
  
  /**
   * The CSV headers for formatting the response.
   */
  private static final String[] CSV_HEADERS = {
      "scanner_id", "batch_id", "record_id", "imprinted_id", "ballot_type",
      "storage_location", "cvr_number", "audited"
  };
  
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
    return "/cvr-to-audit-download";
  }
  
  /**
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }

  /**
   * Validate the request parameters. In this case, the two parameters
   * must exist and both be non-negative integers.
   * 
   * @param the_request The request.
   */
  @Override
  protected boolean validateParameters(final Request the_request) {
    final String start = the_request.queryParams(START);
    final String ballot_count = the_request.queryParams(BALLOT_COUNT);
    final String round = the_request.queryParams(ROUND);
    final String county = the_request.queryParams(COUNTY);
    
    boolean result = start != null && ballot_count != null ||
                     round != null;
    
    if (result) {
      try {
        if (start != null) {
          final int s = Integer.parseInt(start);
          result &= s >= 0;
          final int b = Integer.parseInt(ballot_count);
          result &= b >= 0;
        }
        
        if (round != null) {
          final int r = Integer.parseInt(round);
          result &= r > 0;
        }
        
        if (county == null && Main.authentication().authenticatedCounty(the_request) == null) {
          // it's a DoS user, but they didn't specify a county
          result = false;
        } else if (county != null) {
          Long.parseLong(county);
        }
      } catch (final NumberFormatException e) {
        result = false;
      }
    }
    
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength", 
                     "checkstyle:methodlength", "checkstyle:executablestatementcount"})
  public String endpointBody(final Request the_request, final Response the_response) {
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
    
    try {
      // get the request parameters
      final String start_param = the_request.queryParams(START);
      final String ballot_count_param = the_request.queryParams(BALLOT_COUNT);
      final String duplicates_param = the_request.queryParams(INCLUDE_DUPLICATES);
      final String audited_param = the_request.queryParams(INCLUDE_AUDITED);
      final String round_param = the_request.queryParams(ROUND);

      int ballot_count = 0;
      if (ballot_count_param != null) {
        ballot_count = Integer.parseInt(ballot_count_param);
      }
      int index = 0;
      if (start_param != null) {
        index = Integer.parseInt(start_param);
      }
      final boolean duplicates;
      if (duplicates_param == null) {
        duplicates = false;
      } else {
        duplicates = true;
      }
      final boolean audited;
      if (audited_param == null) {
        audited = false;
      } else {
        audited = true;
      }
      // get other things we need
      final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
      final List<CastVoteRecord> cvr_to_audit_list;      
      final List<CVRToAuditResponse> response_list = new ArrayList<>();
      
      // compute the round, if any
      OptionalInt round = OptionalInt.empty(); 
      if (round_param != null) {
        final int round_number = Integer.parseInt(round_param);
        if (0 < round_number && round_number <= cdb.rounds().size()) {
          round = OptionalInt.of(round_number);
        } else {
          badDataContents(the_response, "cvr list requested for invalid round " + 
                                        round_param + " for county " + cdb.id());
        }
      }
      
      if (round.isPresent()) {
        cvr_to_audit_list = 
            ComparisonAuditController.ballotsToAudit(cdb, round.getAsInt(), audited);
      } else {
        cvr_to_audit_list = 
            ComparisonAuditController.computeBallotOrder(cdb, index, ballot_count, 
                                                         duplicates, audited);
      }
     
      for (int i = 0; i < cvr_to_audit_list.size(); i++) {
        final CastVoteRecord cvr = cvr_to_audit_list.get(i);
        final String location = BallotManifestInfoQueries.locationFor(cvr);
        response_list.add(new CVRToAuditResponse(i, cvr.scannerID(), 
                                                 cvr.batchID(), cvr.recordID(), 
                                                 cvr.imprintedID(), 
                                                 cvr.cvrNumber(), cvr.id(),
                                                 cvr.ballotType(), location,
                                                 cvr.auditFlag()));
      }
      response_list.sort(new BallotOrderComparator());
      
      // generate a CSV file from the response list
      the_response.type("text/csv");
      
      // the file name should be constructed from the county name and round
      // or start/count
      final StringBuilder sb = new StringBuilder(32);
      sb.append("ballot-list-");
      sb.append(county.name().toLowerCase(Locale.getDefault()).replace(" ", "_"));
      sb.append('-');
      if (round.isPresent()) {
        sb.append("round-");
        sb.append(round.getAsInt());
      } else {
        sb.append("start-");
        sb.append(index);
        sb.append("-count-");
        sb.append(ballot_count);
      }
      sb.append(".csv");
      
      try {
        the_response.raw().setHeader("Content-Disposition", "attachment; filename=\"" + 
                                     Rfc5987Util.encode(sb.toString(), "UTF-8") + "\"");
      } catch (final UnsupportedEncodingException e) {
        serverError(the_response, "UTF-8 is unsupported (this should never happen)");
      }
      
      try (OutputStream os = SparkHelper.getRaw(the_response).getOutputStream();
           BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
        writeCSV(response_list, bw);
        ok(the_response);
      } catch (final IOException e) {
        serverError(the_response, "Unable to stream response");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "could not generate cvr list");
    }
    return my_endpoint_result.get();
  }
  
  /**
   * Writes the specified list of CVRToAuditResponse objects as CSV.
   * 
   * @param the_cvrs The list of objects.
   * @param the_writer The writer to write to.
   * @exception IOException if there is a problem writing the CSV file.
   */
  private void writeCSV(final List<CVRToAuditResponse> the_cvrs, final Writer the_writer) 
      throws IOException {
    try (CSVPrinter csvp = new CSVPrinter(the_writer, 
                                          CSVFormat.DEFAULT.withHeader(CSV_HEADERS).
                                          withQuoteMode(QuoteMode.NON_NUMERIC))) {
      for (final CVRToAuditResponse cvr : the_cvrs) {
        csvp.printRecord(cvr.scannerID(), cvr.batchID(), cvr.recordID(), cvr.imprintedID(),
                         cvr.ballotType(), cvr.storageLocation(), cvr.cvrNumber(), 
                         booleanYesNo(cvr.audited()));
      }
    } 
  }
}
