/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 28, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.persistence.Persistence;

/**
 * JSON adapter for contest to audit information.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class ContestToAuditJsonAdapter 
    extends TypeAdapter<ContestToAudit> {
  /**
   * The "contest" string (for JSON serialization).
   */
  private static final String CONTEST = "contest";
  
  /**
   * The "reason" string (for JSON serialization).
   */
  private static final String REASON = "reason";
  
  /**
   * The "audit type" string (for JSON serialization).
   */
  private static final String AUDIT = "audit";
  
  /**
   * Writes a contest to audit object.
   * 
   * @param the_writer The JSON writer.
   * @param the_info The object to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final ContestToAudit the_contest) 
      throws IOException {
    the_writer.beginObject();
    the_writer.name(CONTEST).value(the_contest.contest().id());
    the_writer.name(AUDIT).value(the_contest.audit().toString());
    if (the_contest.reason() != null) {
      the_writer.name(REASON).value(the_contest.reason().toString());
    }
    the_writer.endObject();
  }
  
  /**
   * Checks the sanity of a contest to audit.
   * 
   * @param the_id The contest ID.
   * @param the_reason The reason for audit.
   * @param the_audit_type The audit type.
   * @return the corresponding contest, if the data is sane, or null if
   * the data is not.
   */
  private Contest sanityCheck(final Long the_id, 
                              final AuditReason the_reason,
                              final AuditType the_type) {
    Contest result = null;
    final Contest contest = Persistence.getByID(the_id, Contest.class);
    
    if (contest != null && the_type != null &&
        (the_reason != null || the_type != AuditType.COMPARISON)) {
      result = contest;
    }
    
    return result;
  }
  
  /**
   * Reads a contest to audit object.
   * 
   * @param the_reader The JSON reader.
   * @return the object.
   */
  @Override
  public ContestToAudit read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    Long contest_id = null;
    AuditReason reason = null;
    AuditType type = null;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case CONTEST:
          contest_id = the_reader.nextLong();
          break;
        
        case REASON:
          try {
            reason = AuditReason.valueOf(the_reader.nextString());
          } catch (final IllegalArgumentException e) {
            throw new JsonSyntaxException(e);
          }
          break;
          
        case AUDIT:
          try {
            type = AuditType.valueOf(the_reader.nextString());
          } catch (final IllegalArgumentException e) {
            throw new JsonSyntaxException(e);
          }
          break;
          
        default:
          error = true;
          break;
      }
    }
    the_reader.endObject();
    
    // check that the contest exists
    
    final Contest contest = sanityCheck(contest_id, reason, type);
        
    if (error || contest == null) {
      throw new JsonSyntaxException("invalid data detected in contest to audit");
    }
    
    return new ContestToAudit(contest, reason, type);
  }
}
