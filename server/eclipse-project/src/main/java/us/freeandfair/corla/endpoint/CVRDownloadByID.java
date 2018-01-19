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

import org.apache.log4j.Level;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The CVR by ID download endpoint.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CVRDownloadByID extends AbstractEndpoint {
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
    return "/cvr/id/:id";
  }
  
  /**
   * This endpoint requires any kind of authentication.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.EITHER;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Level logLevel() {
    return Level.DEBUG;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointBody(final Request the_request, final Response the_response) {
    try {
      final CastVoteRecord c = 
          Persistence.getByID(Long.parseLong(the_request.params(":id")),
                              CastVoteRecord.class);
      if (c == null) {
        dataNotFound(the_response, "CVR not found");
      } else {
        okJSON(the_response, Main.GSON.toJson(Persistence.unproxy(c)));
      }
    } catch (final NumberFormatException e) {
      invariantViolation(the_response, "Bad CVR ID");
    }
    
    return my_endpoint_result.get();
  }
}
