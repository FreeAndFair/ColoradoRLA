/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 24, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 * A naming strategy for Gson that takes our standard instance field names
 * (prepended with "my_", separated by underscores) and translates them to 
 * JSON field names without the "my_".
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// we suppress this PMD warning because this class, despite being
// stateless (and thus ideally being a utility class), is required by the
// Gson interface to be instantiable
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class FreeAndFairNamingStrategy implements FieldNamingStrategy {
  /**
   * Translates a Java field name to a JSON field name, by removing the
   * "my_" prefix and leaving the remainder of the field name intact.
   * 
   * @param the_field The field to translate the name of.
   */
  @Override
  public String translateName(final Field the_field) {
    return the_field.getName().replaceFirst("^my_", "");
  }
}
