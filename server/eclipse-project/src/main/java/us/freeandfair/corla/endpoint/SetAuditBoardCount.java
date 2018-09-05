/*
 * Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc. <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.lang.reflect.Type;
import java.util.Map;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for setting the number of audit boards for a given county.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
// TODO: This rule and checkstyle conflict. We need to pick one or the other,
// but with both we need a suppression rule for one of them.
@SuppressWarnings({"PMD.AtLeastOneConstructor"})
public class SetAuditBoardCount extends AbstractCountyDashboardEndpoint {
  /**
   * Type information for easy unmarshalling of the request body.
   */
  private static final Type TYPE_TOKEN =
      new TypeToken<Map<String, Integer>>() { }.getType();

  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    // TODO: Easy to make this PUT?
    return EndpointType.POST;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/set-audit-board-count";
  }

  /**
   * Set the number of audit boards for a given county.
   *
   * @param request HTTP request
   * @param response HTTP response
   */
  @Override
  public String endpointBody(final Request request, final Response response) {
    try {
      final Map<String, Integer> input =
          Main.GSON.fromJson(request.body(), TYPE_TOKEN);

      final County county = Main.authentication().authenticatedCounty(request);

      if (!validateRequestBody(input)) {
        badDataContents(response, "malformed audit board count request");
      }

      final CountyDashboard countyDashboard =
          Persistence.getByID(county.id(), CountyDashboard.class);

      if (countyDashboard == null) {
        Main.LOGGER.error(String.format("could not get county dashboard [id=%d]",
            county.id()));
        serverError(response, "could not set audit board count");
      }

      countyDashboard.setAuditBoardCount(input.get("count"));
      Persistence.saveOrUpdate(countyDashboard);

      ok(response, String.format("set the number of audit boards to %d",
          countyDashboard.auditBoardCount()));
    } catch (final PersistenceException e) {
      Main.LOGGER.error("unable to set audit board count", e);
      serverError(response, "unable to set audit board count");
    } catch (final JsonParseException e) {
      badDataContents(response, "malformed audit board count request");
    }

    return my_endpoint_result.get();
  }

  /**
   * Check that the unmarshalled input looks like we expect it to.
   *
   * @param body the unmarshalled input
   */
  private static boolean validateRequestBody(final Map<String, Integer> body) {
    return body.containsKey("count");
  }
}
