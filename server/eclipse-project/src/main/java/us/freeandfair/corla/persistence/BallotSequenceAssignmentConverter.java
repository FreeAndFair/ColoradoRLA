/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Aug 26, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;

/**
 * A converter between ballot sequence assignment data and the database.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class BallotSequenceAssignmentConverter
    implements AttributeConverter<List<Map<String, Integer>>, String> {
  /**
   * The required type information.
   */
  private static final Type BALLOT_SEQUENCE_ASSIGNMENT_TYPE =
      new TypeToken<List<Map<String, Integer>>>() { }.getType();

  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  /**
   * Convert the type into JSON for database storage.
   *
   * @param l the list to persist.
   */
  @Override
  public String convertToDatabaseColumn(final List<Map<String, Integer>> l) {
    return GSON.toJson(l);
  }

  /**
   * Converts a type stored as JSON in the database to a Java object.
   *
   * @param s the JSON-encoded string
   */
  @Override
  public List<Map<String, Integer>> convertToEntityAttribute(final String s) {
    return GSON.fromJson(s, BALLOT_SEQUENCE_ASSIGNMENT_TYPE);
  }
}
