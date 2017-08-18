/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT;

import java.math.BigDecimal;

import javax.persistence.PersistenceException;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.json.SubmittedRiskLimit;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for establishing the risk limit for comparison audits.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class RiskLimitForComparisonAudits extends AbstractDoSDashboardEndpoint {
  /**
   * The "risk limit" parameter.
   */
  public static final String RISK_LIMIT = "risk_limit";
  
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
    return "/risk-limit-comp-audits";
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT;
  }
  
  /**
   * Attempts to set the risk limit for comparison audits. The risk limit
   * should be provided as a decimal number (i.e., 0.10 for 10%). 
   * 
   * Session query parameters: <tt>risk-limit</tt>
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public String endpoint(final Request the_request, final Response the_response) {
    try {
      final SubmittedRiskLimit risk_limit = 
          Main.GSON.fromJson(the_request.body(), SubmittedRiskLimit.class);
      final BigDecimal parsed_limit = parseRiskLimit(risk_limit.riskLimit());
      if (parsed_limit == null) {
        invariantViolation(the_response, "invalid risk limit specified");
      } else {
        final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(the_response, "could not set risk limit");
        } else {
          dosd.setRiskLimitForComparisonAudits(parsed_limit);
          Persistence.saveOrUpdate(dosd);
          ok(the_response, "risk limit set to " + parsed_limit);
        }
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to set risk limit: " + e);

    } catch (final JsonSyntaxException e) {
      invariantViolation(the_response, "Invalid risk limit specified");
    }
    return my_endpoint_result;
  }
  
  /**
   * Parses a string to obtain the risk limit, or determine its invalidity.
   * A valid risk limit string must be parsable by java.math.BigDecimal, must
   * be greater than or equal to 0, and must be less than or equal to 1.
   * 
   * @param the_string The string to parse.
   * @return the risk limit, or null if the string was not a valid risk limit.
   */
  private BigDecimal parseRiskLimit(final String the_string) {
    BigDecimal result = null;
    
    try {
      final BigDecimal parsed = new BigDecimal(the_string);
      if (BigDecimal.ZERO.compareTo(parsed) <= 0 && 
          parsed.compareTo(BigDecimal.ONE) <= 0) {
        result = parsed;
      } // else the parsed risk limit is out of range
    } catch (final NumberFormatException e) {
      // the string was invalid
    }
    
    return result;
  }
}
