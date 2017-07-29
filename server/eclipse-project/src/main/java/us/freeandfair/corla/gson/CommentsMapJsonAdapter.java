/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 28, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.Contest;

/**
 * JSON adapter for the internal map from contests to consensus flags.
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
/**
 * JSON adapter for the internal map from contests to comments.
 */
public final class CommentsMapJsonAdapter 
    extends TypeAdapter<Map<Contest, String>> {
  /**
   * The "contest" string (for JSON serialization).
   */
  private static final String CONTEST = "contest";
  
  /**
   * The "comment" string (for JSON serialization).
   */
  private static final String COMMENT = "comment";
  
  /**
   * Writes a map from contests to comments as a mapping from IDs
   * to strings.
   * 
   * @param the_writer The JSON writer.
   * @param the_comments_map The map to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final Map<Contest, String> the_comments_map) 
      throws IOException {
    the_writer.beginObject();
    for (final Entry<Contest, String> e : the_comments_map.entrySet()) {
      the_writer.name(CONTEST);
      the_writer.value(e.getKey().id());
      the_writer.name(COMMENT);
      the_writer.value(e.getValue());
    }
    the_writer.endObject();
  }
  
  /**
   * Reads a map from contests to comments from a mapping from IDs to
   * strings.
   */
  @Override
  public Map<Contest, String> read(final JsonReader the_reader) 
      throws IOException {
    final Map<Contest, String> result = 
        new HashMap<Contest, String>();
    boolean error = false;
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      if (CONTEST.equals(the_reader.nextName())) {
        final Contest contest = Contest.byID(the_reader.nextLong());
        if (COMMENT.equals(the_reader.nextName())) {
          result.put(contest, the_reader.nextString());
        } else {
          error = true;
        }
      } else {
        error = true;
      }
    }
    the_reader.endObject();
    if (error) {
      throw new IOException("invalid contest to comment mapping");
    }
    return result;
  }
}