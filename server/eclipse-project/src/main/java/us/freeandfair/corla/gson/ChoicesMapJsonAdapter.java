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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;

/**
 * JSON adapter for a map from contests to choices.
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
/**
 * JSON adapter for the internal map from contests to choices.
 */
public final class ChoicesMapJsonAdapter 
    extends TypeAdapter<Map<Contest, Set<Choice>>> {
  /**
   * The "contest" string (for JSON serialization).
   */
  private static final String CONTEST = "contest";
  
  /**
   * The "choices" string (for JSON serialization).
   */
  private static final String CHOICES = "choices";
  
  /**
   * Writes a map from contests to choices as a mapping from IDs
   * to arrays of IDs.
   * 
   * @param the_writer The JSON writer.
   * @param the_choice_map The map to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final Map<Contest, Set<Choice>> the_choice_map) 
      throws IOException {
    the_writer.beginObject();
    for (final Entry<Contest, Set<Choice>> e : the_choice_map.entrySet()) {
      the_writer.name(CONTEST);
      the_writer.value(e.getKey().id());
      the_writer.name(CHOICES);
      the_writer.beginArray();
      for (final Choice c : e.getValue()) {
        the_writer.value(c.id());
      }
      the_writer.endArray();
    }
    the_writer.endObject();
  }
  
  /**
   * Reads a map from contests to choices from a mapping from IDs to
   * arrays of IDs.
   */
  @Override
  public Map<Contest, Set<Choice>> read(final JsonReader the_reader) 
      throws IOException {
    final Map<Contest, Set<Choice>> result = 
        new HashMap<Contest, Set<Choice>>();
    boolean error = false;
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      if (CONTEST.equals(the_reader.nextName())) {
        final Contest contest = Contest.byID(the_reader.nextLong());
        final Set<Choice> choices = new HashSet<Choice>();
        if (CHOICES.equals(the_reader.nextName())) {
          the_reader.beginArray();
          while (the_reader.hasNext()) {
            choices.add(Choice.byID(the_reader.nextLong()));
          }
          the_reader.endArray();
        }
        result.put(contest, choices);
      } else {
        error = true;
      }
    }
    the_reader.endObject();
    if (error) {
      throw new IOException("invalid contest to choice mapping");
    }
    return result;
  }
}