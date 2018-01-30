/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 9, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PARTIAL_AUDIT_INFO_EVENT;

import java.math.BigDecimal;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for establishing the risk limit for comparison audits.
 * 
 * @author Daniel M Zimmerman
 * @version 1.0.0
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
    return PARTIAL_AUDIT_INFO_EVENT;
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
  public String endpointBody(final Request the_request, final Response the_response) {
    try {
      final AuditInfo risk_limit = 
          Main.GSON.fromJson(the_request.body(), AuditInfo.class);
      if (risk_limit == null || risk_limit.riskLimit() == null ||
          0 < BigDecimal.ZERO.compareTo(risk_limit.riskLimit()) || 
          0 < risk_limit.riskLimit().compareTo(BigDecimal.ONE)) {
        invariantViolation(the_response, "invalid risk limit specified");
      } else {
        final DoSDashboard dosd = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosd == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(the_response, "could not set risk limit");
        } else {
          dosd.updateAuditInfo(risk_limit);
          Persistence.saveOrUpdate(dosd);
          ok(the_response, "risk limit set to " + risk_limit.riskLimit());
        }
      }
    } catch (final PersistenceException e) {
      serverError(the_response, "unable to set risk limit: " + e);

    } catch (final JsonParseException e) {
      invariantViolation(the_response, "Invalid risk limit specified");
    }
    return my_endpoint_result.get();
  }
}
