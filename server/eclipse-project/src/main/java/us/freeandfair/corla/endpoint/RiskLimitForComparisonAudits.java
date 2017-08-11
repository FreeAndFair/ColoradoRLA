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

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.AuditStage;
import us.freeandfair.corla.model.DepartmentOfStateDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DepartmentOfStateDashboardQueries;

/**
 * The endpoint for establishing the risk limit for comparison audits.
 * 
 * @author Daniel M Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class RiskLimitForComparisonAudits implements Endpoint {
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
    int status = HttpStatus.OK_200;
    String result = "Risk limit set";
    BigDecimal risk_limit = null;
    
    if (Authentication.isAuthenticatedAs(the_request, AdministratorType.STATE)) {
      // see if a valid risk limit was passed in
      risk_limit = parseRiskLimit(the_request.queryParams(RISK_LIMIT));

      if (risk_limit == null) {
        Main.LOGGER.info("attempt to set an invalid risk limit");
        status = HttpStatus.BAD_REQUEST_400;
        result = "Invalid risk limit specified";
      } else {
        final DepartmentOfStateDashboard dosd = DepartmentOfStateDashboardQueries.get();
        if (dosd != null && dosd.auditStage() == AuditStage.PRE_AUDIT) {
          dosd.setRiskLimitForComparisonAudits(risk_limit);
          try {
            Persistence.saveOrUpdate(dosd);
            Main.LOGGER.info("risk limit for comparison audits set to " + risk_limit);
          } catch (final PersistenceException e) {
            Main.LOGGER.error("unable to set risk limit for comparison audits: " + e);
          }
        } else if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          status = HttpStatus.INTERNAL_SERVER_ERROR_500;
          result = "Could not set risk limit";
        } else {
          Main.LOGGER.info("attempt to set the risk limit for comparision audits " +
                           "in incorrect state " + dosd.auditStage());
          status = HttpStatus.FORBIDDEN_403;
          result = "Attempt to set the risk limit in incorrect state";
        }
      } 
    } else {
      Main.LOGGER.info("unauthorized attempt to set the risk limit for comparison audits");
      status = HttpStatus.UNAUTHORIZED_401;
      result = "Unauthorized attempt to set the risk limit"; 
    }
    
    the_response.status(status);
    return result;
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
