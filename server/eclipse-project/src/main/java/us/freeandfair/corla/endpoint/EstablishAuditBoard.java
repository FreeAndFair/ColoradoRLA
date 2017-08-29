/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide
 * risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.SIGN_IN_AUDIT_BOARD_EVENT;

import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Establish the audit board for a county.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class EstablishAuditBoard extends AbstractAuditBoardDashboardEndpoint {
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
    return "/audit-board";
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
    return SIGN_IN_AUDIT_BOARD_EVENT;
  }
  
  /**
   * Establish the audit board for a county.
   */
  @Override
  // false positive about inner class declaration
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  public String endpoint(final Request the_request,
                         final Response the_response) {
    try {
      final Type list_type = new TypeToken<List<Elector>>() { }.getType();
      final List<Elector> parsed_audit_board = 
          Main.GSON.fromJson(the_request.body(), list_type);
      if (parsed_audit_board.size() >= CountyDashboard.MIN_AUDIT_BOARD_MEMBERS) {
        final County county = Authentication.authenticatedCounty(the_request); 
        if (county == null) {
          Main.LOGGER.error("could not get authenticated county");
          unauthorized(the_response, "not authorized to set an audit board");
        } else {
          final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
          if (cdb == null) {
            Main.LOGGER.error("could not get county dashboard");
            serverError(the_response, "could not set audit board");
          } else {
            cdb.signInAuditBoard(parsed_audit_board);
            Persistence.saveOrUpdate(cdb);
            ok(the_response, "audit board for county " + county +  
                             " set to " + parsed_audit_board);
          }
        }
      } else {
        invariantViolation(the_response, "Invalid audit board membership");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to set audit board: " + e);

    } catch (final JsonParseException e) {
      badDataContents(the_response, "Invalid audit board data");
    }
    return my_endpoint_result.get();
  }
}
