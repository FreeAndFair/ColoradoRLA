/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created September 12, 2018
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc <dev@democracy.works>
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
 * A converter between lists of Integers and JSON representations of such lists,
 * for database efficiency.
 *
 * @author Democracy Works, Inc <dev@democracy.works>
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
  /**
   * The type information for a list of Integer
   */
  private static final Type INTEGER_LIST = new TypeToken<List<Integer>>() { }.getType();

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
  public String convertToDatabaseColumn(final List<Integer> the_list) {
    return GSON.toJson(the_list);
  }

  /**
   * Converts the specified database column entry to a list of strings.
   *
   * @param the_column The column entry.
   */
  @Override
  public List<Integer> convertToEntityAttribute(final String the_column) {
    return GSON.fromJson(the_column, INTEGER_LIST);
  }
}
