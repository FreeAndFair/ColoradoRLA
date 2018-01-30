/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.SubmittedBallotNotFound;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The endpoint for reporting ballots that could not be found by auditors.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class BallotNotFound extends AbstractAuditBoardDashboardEndpoint {
  /**
   * The event we will return for the ASM.
   */
  private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();
  
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
    return my_event.get();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void reset() {
    my_event.set(null);
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
  // FindBugs thinks we can deference a null CVR, but we can't because
  // badDataContents() (which ends the method's execution) would get called first
  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
  @SuppressWarnings("PMD.NPathComplexity")
  public String endpointBody(final Request the_request, final Response the_response) {
    // we must be authenticated as a county
    final County county = Main.authentication().authenticatedCounty(the_request);
    if (county == null) {
      unauthorized(the_response, "not authorized for audit board operations");
      return my_endpoint_result.get();
    }
    
    final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
    if (cdb == null) {
      serverError(the_response, "could not load audit board information");
      return my_endpoint_result.get();
    }
    
    // attempt to find the CVR under audit specified in the request
    try {
      final SubmittedBallotNotFound sbnf = 
          Main.GSON.fromJson(the_request.body(), SubmittedBallotNotFound.class);
      if (sbnf.id() == null) {
        throw new JsonSyntaxException("invalid ballot ID");
      }
      final CastVoteRecord cvr = Persistence.getByID(sbnf.id(), CastVoteRecord.class);
      if (cvr == null) {
        badDataContents(the_response, "nonexistent CVR ID");
      }
      final CVRAuditInfo matching = Persistence.getByID(cvr.id(), CVRAuditInfo.class);
      if (matching == null) {
        badDataContents(the_response, "specified CVR not under audit");
      }
      // construct a phantom ballot ACVR
      final List<CVRContestInfo> contest_info = new ArrayList<>();
      for (final CVRContestInfo ci : cvr.contestInfo()) {
        contest_info.add(new CVRContestInfo(ci.contest(), "ballot not found", 
                                            null, new ArrayList<String>()));
      }
      final CastVoteRecord acvr =
          new CastVoteRecord(RecordType.PHANTOM_BALLOT,
                             Instant.now(), cvr.countyID(), cvr.cvrNumber(), null,
                             cvr.scannerID(), cvr.batchID(), cvr.recordID(),
                             cvr.imprintedID(), cvr.ballotType(),
                             contest_info);
      
      Persistence.saveOrUpdate(acvr);
      if (ComparisonAuditController.submitAuditCVR(cdb, cvr, acvr)) {
        ok(the_response, "audit CVR submitted");
      }
      if (cdb.ballotsRemainingInCurrentRound() == 0) {
        // the round is over
        my_event.set(ROUND_COMPLETE_EVENT);
      } else {
        my_event.set(REPORT_BALLOT_NOT_FOUND_EVENT);
      }
    } catch (final JsonParseException e) {
      badDataType(the_response, "invalid request format");
    } catch (final NumberFormatException e) {
      badDataContents(the_response, "malformed CVR id");
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to save audit CVR");
    }
    return my_endpoint_result.get();
  }

}
