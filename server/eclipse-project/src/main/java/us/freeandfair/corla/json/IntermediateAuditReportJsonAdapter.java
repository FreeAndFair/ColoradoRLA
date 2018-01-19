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
import java.time.Instant;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.IntermediateAuditReportInfo;

/**
 * JSON adapter for audit investigation reports.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class IntermediateAuditReportJsonAdapter 
    extends TypeAdapter<IntermediateAuditReportInfo> {
  /**
   * The "contest" string (for JSON serialization).
   */
  private static final String TIMESTAMP = "timestamp";
  
  /**
   * The "audit type" string (for JSON serialization).
   */
  private static final String REPORT = "report";
  
  /**
   * Writes an audit interim report object.
   * 
   * @param the_writer The JSON writer.
   * @param the_info The object to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final IntermediateAuditReportInfo the_report) 
      throws IOException {
    the_writer.beginObject();
    the_writer.name(TIMESTAMP).value(Main.GSON.toJson(the_report.timestamp()));
    the_writer.name(REPORT).value(the_report.report());
    the_writer.endObject();
  }
  
  /**
   * Reads an audit investigation report object.
   * 
   * @param the_reader The JSON reader.
   * @return the object.
   */
  @Override
  public IntermediateAuditReportInfo read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    String report = null;
    Instant timestamp = null;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case TIMESTAMP:
          timestamp = Main.GSON.fromJson(the_reader.nextString(), Instant.class);
          break;
          
        case REPORT:
          report = the_reader.nextString();
          break;
          
        default:
          error = true;
          break;
      }
    }
    the_reader.endObject();
    
    if (error) {
      throw new JsonSyntaxException("invalid data detected in audit investigation report");
    }
    
    return new IntermediateAuditReportInfo(timestamp, report);
  }
}
