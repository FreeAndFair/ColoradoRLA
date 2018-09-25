/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Signs out the audit board for a county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandFair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor"})
public class AuditBoardSignOut extends AbstractAuditBoardDashboardEndpoint {
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
    return "/audit-board-sign-out";
  }

  /**
   * @return COUNTY authorization is required for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }

  /**
   * Signs the audit board out for the logged in county at the specified index.
   */
  @Override
  public String endpointBody(final Request the_request,
                             final Response the_response) {
    final JsonParser parser = new JsonParser();

    try {
      final int index = parser.parse(the_request.body()).getAsInt();
      final County county = Main.authentication().authenticatedCounty(the_request); 
      if (county == null) {
        Main.LOGGER.error("could not get authenticated county");
        unauthorized(the_response, "not authorized to perform audit board login");
      } else {
        final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
        if (cdb == null) {
          Main.LOGGER.error("could not get county dashboard");
          serverError(the_response, "could not log in audit board");
        } else {
          cdb.signOutAuditBoard(index);
          Persistence.saveOrUpdate(cdb);
          ok(the_response,
             String.format("audit board #%d for county %d signed out",
                           index, county.id()));
        }
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to sign out audit board: " + e);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "unable to sign out audit board");
    }

    return my_endpoint_result.get();
  }
}
