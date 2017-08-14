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
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PUBLISH_BALLOTS_TO_AUDIT_EVENT;

import java.util.List;

import javax.persistence.PersistenceException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.RLAAlgorithm;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DoSDashboardQueries;

/**
 * Download all ballots to audit for the entire state.
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class PublishBallotsToAudit extends AbstractDoSDashboardEndpoint {
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
    return "/ballots-to-audit";
  }

  /**
   * @return STATE authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return PUBLISH_BALLOTS_TO_AUDIT_EVENT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String endpoint(final Request the_request,
                         final Response the_response) {
    // update every county dashboard with a list of ballots to audit
    try {
      final DoSDashboard dosdb = DoSDashboardQueries.get();
      final List<CountyDashboard> cdbs = Persistence.getAll(CountyDashboard.class);
      
      for (final CountyDashboard cdb : cdbs) {
        try {
          final RLAAlgorithm rlaa = new RLAAlgorithm(cdb);
          if (cdb.cvrUploadTimestamp() != null) {
            cdb.setCVRsToAudit(rlaa.computeBallotOrder(dosdb.randomSeed()));
          }
        } catch (final IllegalArgumentException e) {
          Main.LOGGER.info("could not set ballot list for county " + cdb.countyID());
        }
      }
      
      ok(the_response, "ballot lists published");
    } catch (final PersistenceException e) {
      serverError(the_response, "could not publish list of ballots to audit");
    }
    
    return my_endpoint_result;
  }
}
