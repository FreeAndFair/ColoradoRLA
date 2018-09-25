/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 26, 2018
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A converter between maps from String to Sets of Strings
 * @author Democracy Works <dev@democracy.works>
 * @version 1.3.2
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CountyCanonicalContestsMapConverter
    implements AttributeConverter<Map<String, Set<String>>, String> {

  /**
   * The Type of the thing
   */
  private static final Type COUNTY_CONTEST_MAP =
      new TypeToken<Map<String, Set<String>>>() { }.getType();

  /**
   * A serializer for the thing
   */
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  /**
   * We can use the default JSON serialization for this type of thing
   */
  @Override
  public String convertToDatabaseColumn(final Map<String, Set<String>> m) {
    return GSON.toJson(m);
  }

  /**
   * When deserializing, we'll use the Java collection type
   */
  @Override
  public Map<String, Set<String>> convertToEntityAttribute(final String column) {
    return GSON.fromJson(column, COUNTY_CONTEST_MAP);
  }
}
