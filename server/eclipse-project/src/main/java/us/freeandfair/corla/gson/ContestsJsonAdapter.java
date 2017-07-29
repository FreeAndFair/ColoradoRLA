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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.Contest;

/**
 * JSON adapter for a list of contests.
 */
//the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class ContestsJsonAdapter extends TypeAdapter<List<Contest>> {
  /**
   * Writes a list of contests as an array of contest IDs.
   * 
   * @param the_writer The JSON writer.
   * @param the_list The list of contests to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, final List<Contest> the_list) 
      throws IOException {
    the_writer.beginArray();
    for (final Contest c : the_list) {
      the_writer.value(c.id());
    }
    the_writer.endArray();
  }
  
  /**
   * Reads a list of contests from an array of contest IDs.
   */
  @Override
  public List<Contest> read(final JsonReader the_reader) throws IOException {
    final List<Contest> result = new ArrayList<Contest>();
    the_reader.beginArray();
    while (the_reader.hasNext()) {
      final Contest c = Contest.byID(the_reader.nextLong());
      if (c == null) {
        throw new IOException("invalid contest ID");
      } else {
        result.add(c);
      }
    }
    the_reader.endArray();
    return result;
  }
}