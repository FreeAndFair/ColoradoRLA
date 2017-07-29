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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.BallotStyle;

/**
 * JSON adapter for a ballot style.
 */
//the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class BallotStyleJsonAdapter extends TypeAdapter<BallotStyle> {
  /**
   * Writes a ballot style as its ID.
   * 
   * @param the_writer The JSON writer.
   * @param the_style The ballot style to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, final BallotStyle the_style) 
      throws IOException {
    the_writer.value(the_style.id());
  }
  
  /**
   * Reads a list of contests from an array of contest IDs.
   */
  @Override
  public BallotStyle read(final JsonReader the_reader) throws IOException {
    final BallotStyle result = BallotStyle.byID(the_reader.nextLong());
    if (result == null) {
      throw new IOException("invalid ballot style ID");
    }
    return result;
  }
}