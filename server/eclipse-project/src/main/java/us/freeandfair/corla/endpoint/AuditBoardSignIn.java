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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SIGN_IN_AUDIT_BOARD_EVENT;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.*;

import java.lang.reflect.Type;

import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Signs in the audit board for a county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandFair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor"})
public class AuditBoardSignIn extends AbstractAuditBoardDashboardEndpoint {
  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> asmEvent = new ThreadLocal<ASMEvent>();

  /**
   * Type of a list of electors for easier unmarshaling with GSON
   */
  private static final Type ELECTOR_LIST =
      new TypeToken<List<Elector>>() { }.getType();

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
    return "/audit-board-sign-in";
  }

  /**
   * @return COUNTY authorization is required for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return this.asmEvent.get();
  }

  /**
   * Establish the audit board for a county.
   */
  @Override
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    final JsonParser parser = new JsonParser();
    final JsonObject object;

    try {
      object = parser.parse(the_request.body()).getAsJsonObject();

      final int index = object.get("index").getAsInt();
      final List<Elector> parsed_audit_board =
          Main.GSON.fromJson(
              object.get("audit_board"),
              ELECTOR_LIST);

      if (parsed_audit_board.size() >= CountyDashboard.MIN_AUDIT_BOARD_MEMBERS) {
        final County county = Main.authentication().authenticatedCounty(the_request); 
        if (county == null) { 
          Main.LOGGER.error("could not get authenticated county");
          unauthorized(the_response, "not authorized to sign in audit board");
        } else {
          final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
          if (cdb == null) {
            Main.LOGGER.error("could not get county dashboard");
            serverError(the_response, "could not sign in audit board");
          } else {
            this.asmEvent.set(this.nextEvent(cdb));
            cdb.signInAuditBoard(index, parsed_audit_board);
            Persistence.saveOrUpdate(cdb);
            ok(the_response,
               String.format("audit board #%d for county %d signed in: %s",
                   index, county.id(), parsed_audit_board));
          }
        }
      } else {
        invariantViolation(the_response, "invalid audit board membership");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to sign in audit board: " + e);

    } catch (final JsonParseException e) {
      badDataContents(the_response, "invalid audit board data");
    }

    return my_endpoint_result.get();
  }

  /**
   * Computes an ASM event to emit when the audit board is signed in.
   *
   * Currently only returns SIGN_IN_AUDIT_BOARD_EVENT when all audit boards are
   * marked signed out.
   *
   * @param cdb the county dashboard
   */
  private ASMEvent nextEvent(final CountyDashboard cdb) {
    final AuditBoardDashboardASM asm = ASMUtilities.asmFor(
        AuditBoardDashboardASM.class,
        String.valueOf(cdb.id()));

    ASMState currentState = null;
    if (null != asm) {
      currentState = asm.currentState();
    }

    if (AUDIT_INITIAL_STATE == currentState
        || WAITING_FOR_ROUND_START_NO_AUDIT_BOARD == currentState
        || ROUND_IN_PROGRESS_NO_AUDIT_BOARD == currentState
        || WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD == currentState) {
      return SIGN_IN_AUDIT_BOARD_EVENT;
    }

    return null;
  }
}
