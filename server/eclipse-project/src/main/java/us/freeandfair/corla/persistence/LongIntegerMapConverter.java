package us.freeandfair.corla.persistence;

import java.lang.reflect.Type;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A converter between maps from longs to integers and JSON
 * representations of those maps, for database efficiency.
 *
 */
@Converter
public class LongIntegerMapConverter
  implements AttributeConverter<Map<Long, Integer>, String> {

  /**
   * The type information for a map.
   */
  private static final Type LONG_INTEGER_MAP =
    new TypeToken<Map<Long, Integer>>() { }.getType();

  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON =
    new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  /**
   * Converts the specified list of Strings to a database column entry.
   *
   * @param the_set The list of Strings.
   */
  @Override
  public String convertToDatabaseColumn(final Map<Long, Integer> the_map) {
    return GSON.toJson(the_map);
  }

  /**
   * Converts the specified database column entry to a list of strings.
   *
   * @param the_column The column entry.
   */
  @Override
  public Map<Long, Integer> convertToEntityAttribute(final String the_column) {
    return GSON.fromJson(the_column, LONG_INTEGER_MAP);
  }
}
