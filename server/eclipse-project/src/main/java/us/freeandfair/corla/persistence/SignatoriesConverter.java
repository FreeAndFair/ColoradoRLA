/*
 * Free & Fair Colorado RLA System
 */

package us.freeandfair.corla.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import us.freeandfair.corla.model.Elector;

/**
 * A converter between a Signatories object and JSON stored in a database.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class SignatoriesConverter
    implements AttributeConverter<Map<Integer, List<Elector>>, String> {
  /**
   * The type information for a set of String.
   */
  private static final Type SIGNATORIES_TYPE =
      new TypeToken<Map<Integer, List<Elector>>>() { }.getType();

  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON =
      new GsonBuilder().
      serializeNulls().
      disableHtmlEscaping().
      create();

  /**
   * Converts the Java object to JSON for the database.
   *
   * @param o The specified type we are converting
   */
  @Override
  public String convertToDatabaseColumn(final Map<Integer, List<Elector>> o) {
    return GSON.toJson(o);
  }

  /**
   * Converts the JSON from the database into a Java object.
   *
   * @param s the JSON string
   */
  @Override
  public Map<Integer, List<Elector>> convertToEntityAttribute(final String s) {
    return GSON.fromJson(s, SIGNATORIES_TYPE);
  }
}
