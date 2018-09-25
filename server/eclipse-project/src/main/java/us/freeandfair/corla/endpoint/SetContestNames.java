/*
 * Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc. <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.COMPLETE_AUDIT_INFO_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PARTIAL_AUDIT_INFO_EVENT;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.hibernate.Session;
import org.hibernate.query.Query;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The endpoint for renaming contests.
 *
 * This allows allows the state to rename contests uploaded by counties that
 * may not conform to the state specifications.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
// TODO: This rule and checkstyle conflict. We need to pick one or the other,
// but with both we need a suppression rule for one of them.
@SuppressWarnings({"PMD.AtLeastOneConstructor"})
public class SetContestNames extends AbstractDoSDashboardEndpoint {
  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> asmEvent = new ThreadLocal<ASMEvent>();

  /**
   * Type information for the new contest names.
   */
  private static final Type TYPE_TOKEN =
      new TypeToken<List<Map<String, String>>>() { }.getType();

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
    return "/set-contest-names";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return asmEvent.get();
  }

  /**
   * Updates contest names based on the DoS-preferred contest names.
   *
   * @param request HTTP request
   * @param response HTTP response
   */
  @Override
  public String endpointBody(final Request request, final Response response) {
    try {
      final List<Map<String, String>> mappings =
          Main.GSON.fromJson(request.body(), TYPE_TOKEN);

      if (mappings == null) {
        badDataContents(response, "malformed contest mappings");
      } else {
        final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosdb == null) {
          Main.LOGGER.error("could not get department of state dashboard");
          serverError(response, "could not set contest mappings");
        }

        int updateCount = 0;
        final Session s = Persistence.currentSession();

        final Query q = s.createQuery(
            "update Contest set my_name = :name" +
            " where my_id = :id");

        for (final Map<String, String> mapping : mappings) {
          final Long id = Long.parseLong(mapping.get("contest"));
          final String name = mapping.get("name");

          q.setParameter("id", id);
          q.setParameter("name", name);

          updateCount += q.executeUpdate();
        }

        asmEvent.set(nextEvent(dosdb));
        ok(response, String.format("re-mapped %d contest names", updateCount));
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.error("unable to re-map contest names", e);
      serverError(response, "unable to re-map contest names");
    } catch (final JsonParseException e) {
      badDataContents(response, "malformed contest mapping");
    }
    return my_endpoint_result.get();
  }

  /**
   * Computes the event of this endpoint based on audit info completeness.
   *
   * @param dosDashboard The DoS dashboard.
   */
  private ASMEvent nextEvent(final DoSDashboard dosDashboard) {
    final ASMEvent result;
    final AuditInfo info = dosDashboard.auditInfo();

    if (info.electionDate() == null || info.electionType() == null ||
        info.publicMeetingDate() == null || info.riskLimit() == null ||
        info.seed() == null || dosDashboard.contestsToAudit().isEmpty()) {
      Main.LOGGER.debug("partial audit information submitted");
      result = PARTIAL_AUDIT_INFO_EVENT;
    } else {
      Main.LOGGER.debug("complete audit information submitted");
      result = COMPLETE_AUDIT_INFO_EVENT;
    }

    return result;
  }
}
