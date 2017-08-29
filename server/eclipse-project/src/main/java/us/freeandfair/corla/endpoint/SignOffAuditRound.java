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

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;

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
 * Signs off on the current audit round for a county.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ExcessiveImports"})
public class SignOffAuditRound extends AbstractAuditBoardDashboardEndpoint {
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
    return "/sign-off-audit-round";
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
    return ROUND_SIGN_OFF_EVENT;
  }
  
  /**
   * Signs off on the current audit round, regardless of its state of
   * completion.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  // false positive about inner class declaration
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  public String endpoint(final Request the_request,
                         final Response the_response) {
    try {
      final Type list_type = new TypeToken<List<Elector>>() { }.getType();
      final List<Elector> parsed_signatories = 
          Main.GSON.fromJson(the_request.body(), list_type);
      if (parsed_signatories.size() >= CountyDashboard.MIN_ROUND_SIGN_OFF_MEMBERS) {
        final County county = Main.authentication().authenticatedCounty(the_request); 
        if (county == null) {
          Main.LOGGER.error("could not get authenticated county");
          unauthorized(the_response, "not authorized to set an audit board");
        } else {
          final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);
          if (cdb == null) {
            Main.LOGGER.error("could not get county dashboard");
            serverError(the_response, "could not sign off round");
          } else if (cdb.currentRound() == null) {
            invariantViolation(the_response, "no round to sign off");
          } else {
            cdb.endRound(parsed_signatories);
          }
        }
      } else {
        invariantViolation(the_response, "invalid round sign off signatories");
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to sign off round: " + e);
    } catch (final JsonParseException e) {
      badDataContents(the_response, "invalid round signatories");
    }
    ok(the_response, "audit round signed off");
    return my_endpoint_result.get();
  }
}
