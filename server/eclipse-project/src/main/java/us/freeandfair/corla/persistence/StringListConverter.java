/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 26, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A converter between lists of Strings and comma-separated strings.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class StringListConverter implements AttributeConverter<List<String>, String> {
  /**
   * The type information for a list of String.
   */
  private static final Type STRING_LIST = new TypeToken<List<String>>() { }.getType();
  
  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON = 
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
  
  /**
   * Converts the specified list of Strings to a database column entry.
   * 
   * @param the_list The list of Strings.
   */
  @Override
  public String convertToDatabaseColumn(final List<String> the_list) {
    return GSON.toJson(the_list); 
  }

  /**
   * Converts the specified database column entry to a list of strings.
   * 
   * @param the_column The column entry.
   */
  @Override
  public List<String> convertToEntityAttribute(final String the_column) {
    return GSON.fromJson(the_column, STRING_LIST);
  }
}