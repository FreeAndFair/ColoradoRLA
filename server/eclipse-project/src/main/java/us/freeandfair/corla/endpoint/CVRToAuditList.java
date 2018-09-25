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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.BallotSelection;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.json.CVRToAuditResponse;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The CVR to audit list endpoint.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class CVRToAuditList extends AbstractEndpoint {
  /**
   * The "start" parameter.
   */
  public static final String START = "start";

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
    return "/cvr-to-audit-list";
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
    final String round = the_request.queryParams(ROUND);
    final String county = the_request.queryParams(COUNTY);

    boolean result = start != null || round != null;

    if (result) {
      try {
        if (start != null) {
          final int s = Integer.parseInt(start);
          result &= s >= 0;
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
  @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
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
      final String audited_param = the_request.queryParams(INCLUDE_AUDITED);
      final String round_param = the_request.queryParams(ROUND);

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
        response_list.addAll(BallotSelection.toResponseList(cvr_to_audit_list));
        response_list.sort(null);

        final Round roundObject = cdb.rounds().get(round.getAsInt() - 1);

        final List<Map<String, Integer>> bsa =
          roundObject.ballotSequenceAssignment();

        if (bsa != null) {
          // Walk the sequence assignments getting the audit boards' index and
          // count values. Use that information to set the audit board index for
          // each response row.
          for (int i = 0; i < bsa.size(); i++) {
            final Map<String, Integer> m = bsa.get(i);

            final Integer boardIndex = m.get("index");
            final Integer boardCount = m.get("count");

            for (int j = boardIndex; j < boardIndex + boardCount; j++) {
              // TODO: Will this always agree with the round information?
              final CVRToAuditResponse row = response_list.get(j);
              row.setAuditBoardIndex(i);
            }
          }
        }
      }

      okJSON(the_response, Main.GSON.toJson(response_list));
    } catch (final PersistenceException e) {
      serverError(the_response, "could not generate cvr list");
    }
    return my_endpoint_result.get();
  }
}
