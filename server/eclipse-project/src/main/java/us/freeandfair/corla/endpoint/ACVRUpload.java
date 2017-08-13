/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.REPORT_MARKINGS_EVENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import org.hibernate.Session;

import com.google.gson.JsonSyntaxException;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The "audit CVR upload" endpoint.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
// TODO: consider rewriting along the same lines as CVRExportUpload
public class ACVRUpload extends AbstractAuditBoardDashboardEndpoint {
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
    return "/upload-audit-cvr";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return REPORT_MARKINGS_EVENT;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  // catching a null pointer exception is the most sensible way to deal with the 
  // request not having the parts we need to process
  @SuppressFBWarnings(value = {"OS_OPEN_STREAM"}, 
                      justification = "FindBugs false positive with resources.")
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidCatchingNPE"})
  public String endpoint(final Request the_request, final Response the_response) {
    // this is a multipart request - there's a "county" identifier, and a "cvr_file"
    // containing the actual file
    the_request.attribute("org.eclipse.jetty.multipartConfig", 
                          new MultipartConfigElement("/tmp"));
    try (InputStream acvr_is = the_request.raw().getPart("audit_cvr").getInputStream()) {
      final InputStreamReader acvr_isr = new InputStreamReader(acvr_is, "UTF-8");
      final BufferedReader acvr_br = new BufferedReader(acvr_isr);
      final String acvr_json = acvr_br.lines().collect(Collectors.joining("\n"));
      
      final CastVoteRecord acvr = Main.GSON.fromJson(acvr_json, CastVoteRecord.class);
      
      // we need to create a new CVR instance the "right" way, so it persists and also
      // has the right type
      final CastVoteRecord real_acvr = 
          Persistence.get(new CastVoteRecord(RecordType.AUDITOR_ENTERED, Instant.now(), 
                                  acvr.countyID(), acvr.scannerID(), acvr.batchID(), 
                                  acvr.recordID(), acvr.imprintedID(), acvr.ballotType(), 
                                  acvr.contestInfo()),
                          CastVoteRecord.class);
      Main.LOGGER.info("Audit CVR parsed and stored as id " + real_acvr.id());
      final OptionalLong count = count();
      if (count.isPresent()) {
        Main.LOGGER.info(count.getAsLong() + " ACVRs in storage");
      }
    } catch (final JsonSyntaxException | IOException | ServletException | 
                   NullPointerException e) {
      badDataContents(the_response, "Unable to parse ACVR: " + e);
    }
    return my_endpoint_result;
  }
  
  /**
   * Count the ACVRs in storage.
   * 
   * @return the number of ACVRs, or -1 if the count could not be determined.
   */
  private OptionalLong count() {
    OptionalLong result = OptionalLong.empty();
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      final Root<CastVoteRecord> root = cq.from(CastVoteRecord.class);
      cq.select(cb.count(root)).where(cb.equal(root.get("my_record_type"), 
                                               RecordType.AUDITOR_ENTERED));
      final TypedQuery<Long> query = s.createQuery(cq);
      result = OptionalLong.of(query.getSingleResult());
    } catch (final PersistenceException e) {
      // ignore
    }
    
    return result;
  }
}
