/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.SubmittedBallotNotFound;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CVRAuditInfoQueries;

/**
 * The endpoint for reporting ballots that could not be found by auditors.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class BallotNotFound extends AbstractAuditBoardDashboardEndpoint {
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
    return "/ballot-not-found";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ASMEvent endpointEvent() {
    return AuditBoardDashboardEvent.REPORT_BALLOT_NOT_FOUND_EVENT;
  }
  
  /**
   * Marks the specified ballot as "not found" by the audit board.
   * The ballot to so mark is indicated by the ID of its corresponding
   * CVR, which must match a ballot under audit by the authenticated
   * county.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    // we must be authenticated as a county
    final County county = Authentication.authenticatedCounty(the_request);
    if (county == null) {
      unauthorized(the_response, "not authorized for audit board operations");
      return my_endpoint_result.get();
    }
    
    final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
    if (cdb == null) {
      serverError(the_response, "could not load audit board information");
      return my_endpoint_result.get();
    }
    
    // attempt to read the CVR ID from the request
    try {
      final SubmittedBallotNotFound sbnf = 
          Main.GSON.fromJson(the_request.body(), SubmittedBallotNotFound.class);
      if (sbnf.id() == null) {
        throw new JsonSyntaxException("invalid ballot ID");
      }
      final int index = CVRAuditInfoQueries.cvrsToAudit(cdb).indexOf(sbnf.id());
      if (index >= 0) {
        final CastVoteRecord cvr = Persistence.getByID(sbnf.id(), CastVoteRecord.class);
        if (cvr == null) {
          badDataContents(the_response, "could not find CVR");
        } else {
          final List<CVRContestInfo> contest_info = new ArrayList<>();
          for (final CVRContestInfo ci : cvr.contestInfo()) {
            contest_info.add(new CVRContestInfo(ci.contest(), "ballot not found", 
                                                null, new ArrayList<String>()));
          }
          final CastVoteRecord acvr =
              new CastVoteRecord(RecordType.PHANTOM_BALLOT,
                                 Instant.now(), cvr.countyID(), cvr.cvrNumber(),
                                 cvr.scannerID(), cvr.batchID(), cvr.recordID(),
                                 cvr.imprintedID(), cvr.ballotType(),
                                 contest_info);
          Persistence.saveOrUpdate(acvr);
          if (ComparisonAuditController.submitAuditCVR(cdb, cvr, acvr)) {
            ok(the_response, "audit CVR submitted");
          }
        }
      }
    } catch (final JsonSyntaxException e) {
      this.badDataType(the_response, "invalid request format");
    } catch (final NumberFormatException e) {
      this.badDataContents(the_response, "invalid CVR id");
    } catch (final PersistenceException e) {
      this.serverError(the_response, "unable to save audit CVR");
    }
    return my_endpoint_result.get();
  }

}
