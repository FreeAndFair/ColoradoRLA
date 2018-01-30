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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import us.freeandfair.corla.json.FreeAndFairNamingStrategy;
import us.freeandfair.corla.model.Elector;

/**
 * A converter between lists of AuditBoard objects and JSON representations
 * of AuditBoard objects, for database efficiency.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ElectorListConverter implements AttributeConverter<List<Elector>, String> {
  /**
   * The type information for a set of String.
   */
  private static final Type ELECTOR_LIST = 
      new TypeToken<List<Elector>>() { }.getType();
  
  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON = 
      new GsonBuilder().
      setFieldNamingStrategy(new FreeAndFairNamingStrategy()).
      serializeNulls().
      disableHtmlEscaping().
      create();
  
  /**
   * Converts the specified list of Strings to a database column entry.
   * 
   * @param the_set The list of Strings.
   */
  @Override
  public String convertToDatabaseColumn(final List<Elector> the_set) {
    return GSON.toJson(the_set); 
  }

  /**
   * Converts the specified database column entry to a list of strings.
   * 
   * @param the_column The column entry.
   */
  @Override
  public List<Elector> convertToEntityAttribute(final String the_column) {
    return GSON.fromJson(the_column, ELECTOR_LIST);
  }
}
