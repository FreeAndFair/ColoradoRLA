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

import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;

/**
 * JSON adapter for uploaded files. This enapsulates in JSON all the information
 * about the file except for its actual contents, and retrieves from the database
 * the uploaded file corresponding to the specified county and database ID 
 * ignoring other fields; note that "null" can be returned if there is no
 * matching uploaded file in the database.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class UploadedFileJsonAdapter 
    extends TypeAdapter<UploadedFile> {
  /**
   * The "file_id" string (for JSON serialization).
   */
  private static final String FILE_ID = "file_id";
  
  /**
   * The "county_id" string (for JSON serialization).
   */
  private static final String COUNTY_ID = "county_id";
  
  /**
   * The "filename" string (for JSON serialization).
   */
  private static final String FILENAME = "filename";
  
  /**
   * The "size" string (for JSON serialization).
   */
  private static final String SIZE = "size";

  /**
   * The "timestamp" string (for JSON serialization).
   */
  private static final String TIMESTAMP = "timestamp";
  
  /**
   * The "hash" string (for JSON serializaton).
   */
  private static final String HASH = "hash";
  
  /**
   * The "hash_status" string (for JSON serialization).
   */
  private static final String HASH_STATUS = "hash_status";
  
  /**
   * The "status" string (for JSON serialization).
   */
  private static final String STATUS = "status";

  /**
   * The "approximate_record_count" string (for JSON serialization).
   */
  private static final String APPROXIMATE_RECORD_COUNT = "approximate_record_count";
  
  /**
   * Writes an uploaded file object.
   * 
   * @param the_writer The JSON writer.
   * @param the_file The object to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final UploadedFile the_file) 
      throws IOException {
    the_writer.beginObject();
    the_writer.name(FILE_ID).value(the_file.id());
    the_writer.name(COUNTY_ID).value(the_file.county().id());
    the_writer.name(FILENAME).value(the_file.filename());
    the_writer.name(SIZE).value(the_file.size());
    the_writer.name(TIMESTAMP).value(the_file.timestamp().toString());
    the_writer.name(HASH).value(the_file.hash());
    the_writer.name(HASH_STATUS).value(the_file.hashStatus().toString());
    the_writer.name(STATUS).value(the_file.status().toString());
    the_writer.name(APPROXIMATE_RECORD_COUNT).value(the_file.approximateRecordCount());
    the_writer.endObject();
  }
  
  /**
   * Reads an uploaded file object.
   * 
   * @param the_reader The JSON reader.
   * @return the object.
   */
  @Override
  public UploadedFile read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    Long file_id = null;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case FILE_ID:
          file_id = the_reader.nextLong();
          break;
          
        case COUNTY_ID:
        case FILENAME:
        case SIZE:
        case TIMESTAMP:
        case HASH:
        case HASH_STATUS:
        case STATUS:
        case APPROXIMATE_RECORD_COUNT:
          the_reader.skipValue();
          break;
          
        default:
          error = true;
          break;
      }
    }
    the_reader.endObject();
    
    // check the sanity of the contest
    
    if (error || file_id == null) {
      throw new JsonSyntaxException("invalid data detected in uploaded file");
    }
    
    return Persistence.getByID(file_id, UploadedFile.class);
  }
}
