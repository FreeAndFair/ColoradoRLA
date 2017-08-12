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

import java.math.BigDecimal;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.ESTABLISH_RISK_LIMIT_FOR_COMPARISON_AUDITS_EVENT;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.AuditStage;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;
import us.freeandfair.corla.query.PersistentASMStateQueries;

/**
 * The endpoint for establishing the risk limit for comparison audits.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class RiskLimitForComparisonAudits extends AbstractDoSEndpoint {
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
    // the before() action automatically checks that the server is ready,
    // starts a transaction, loads the ASM, checks to see if this endpoint is 
    // permitted given the current state of the ASM, 
    ok(the_response, "Risk limit set");
    BigDecimal risk_limit = null;
    // see if a valid risk limit was passed in
    risk_limit = parseRiskLimit(the_request.queryParams(RISK_LIMIT));
    if (risk_limit == null) {
      Main.LOGGER.info("attempt to set an invalid risk limit");
      invariantViolation(the_response, "Invalid risk limit specified");
    } else {
      final DepartmentOfStateDashboard dosd = DepartmentOfStateDashboardQueries.get();
      if (dosd != null) {
        dosd.setRiskLimitForComparisonAudits(risk_limit);
        try {
          Persistence.saveOrUpdate(dosd);
          Main.LOGGER.info("risk limit for comparison audits set to " + risk_limit);
        } catch (final PersistenceException e) {
          Main.LOGGER.error("unable to set risk limit for comparison audits: " + e);
        }
      } else {
        serverError(the_response, "could not get department of state dashboard"); 
      }
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
