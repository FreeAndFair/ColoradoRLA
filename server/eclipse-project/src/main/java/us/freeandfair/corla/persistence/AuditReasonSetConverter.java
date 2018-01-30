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
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import us.freeandfair.corla.model.AuditReason;

/**
 * A converter between maps from audit reasons to booleans and JSON 
 * representations of those maps, for database efficiency.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class AuditReasonSetConverter 
    implements AttributeConverter<Set<AuditReason>, String> {
  /**
   * The type information for a map from AuditReason to Boolean.
   */
  private static final Type AUDIT_REASON_SET = new TypeToken<Set<AuditReason>>() { }.getType();
  
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
  public String convertToDatabaseColumn(final Set<AuditReason> the_map) {
    return GSON.toJson(the_map); 
  }

  /**
   * Converts the specified database column entry to a list of strings.
   * 
   * @param the_column The column entry.
   */
  @Override
  public Set<AuditReason> convertToEntityAttribute(final String the_column) {
    return GSON.fromJson(the_column, AUDIT_REASON_SET);
  }
}
