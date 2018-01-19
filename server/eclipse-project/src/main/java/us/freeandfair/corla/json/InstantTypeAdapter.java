/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 23, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A Gson type converter for java.time.Instant.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class InstantTypeAdapter 
    implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
  /**
   * Renders an Instant in ISO-8601 format.
   * 
   * @param the_instant The Instant.
   * @param the_type The type (ignored).
   * @param the_context The JSON serialization context (ignored).
   */
  @Override
  public JsonElement serialize(final Instant the_instant, 
                               final Type the_type, 
                               final JsonSerializationContext the_context) {
    return new JsonPrimitive(the_instant.toString());
  }

  /**
   * Reconstitutes an Instant from ISO-8601 format.
   * 
   * @param the_json_element The JSON element to reconstitute.
   * @param the_type The type (ignored).
   * @param the_context The JSON deserialization context (ignored).
   * @return the reconstituted Instant.
   */
  @Override
  public Instant deserialize(final JsonElement the_json_element, 
                             final Type the_type, 
                             final JsonDeserializationContext the_context)
      throws JsonParseException {
    final Instant result;
    try {
      result = Instant.parse(the_json_element.getAsString());
    } catch (final DateTimeParseException e) {
      throw new JsonParseException(e);
    }
    return result;
  }
}
