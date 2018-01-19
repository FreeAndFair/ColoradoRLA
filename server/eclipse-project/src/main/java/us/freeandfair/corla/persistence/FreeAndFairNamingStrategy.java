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

package us.freeandfair.corla.persistence;

import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * A naming strategy for Hibernate that takes our standard instance field names
 * (prepended with "my_", separated by underscores) and translates them to 
 * column names without the "my_".
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// we suppress this PMD warning because this class, despite being
// stateless (and thus ideally being a utility class), is required by the
// Hibernate interface to be instantiable
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class FreeAndFairNamingStrategy extends PhysicalNamingStrategyStandardImpl {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /** 
   * Translates a name from Java conventions by adding underscores and undoing 
   * camel case.
   * 
   * @param the_name The name to translate.
   */
  private static String addUnderscores(final String the_name) {
    final StringBuilder buf = new StringBuilder(the_name.replace('.', '_'));
    int i = 1;
    while (i < buf.length()) {
      if (Character.isLowerCase(buf.charAt(i - 1)) &&
          Character.isUpperCase(buf.charAt(i)) &&
          Character.isLowerCase(buf.charAt(i + 1))) {
        buf.insert(i, '_');
        i = i + 1;
      }
      i = i + 1;
    }
    return buf.toString().toLowerCase(Locale.ROOT);
  }

  /**
   * Translates a Java identifier to a Hibernate table name, by removing all
   * "my_" occurrences and leaving the remainder of the field name intact.
   * 
   * @param the_identifier The identifier to translate.
   * @param the_context The context (ignored).
   */
  @Override
  public Identifier toPhysicalTableName(final Identifier the_identifier, 
                                        final JdbcEnvironment the_context) {
    return new Identifier(addUnderscores(the_identifier.getText()).replaceAll("my_", ""),
                          the_identifier.isQuoted());
  }
  
  /**
   * Translates a Java identifier to a Hibernate column name, by removing all
   * "my_" occurrences and leaving the remainder of the field name intact.
   * 
   * @param the_identifier The identifier to translate.
   * @param the_context The context (ignored).
   */
  @Override
  public Identifier toPhysicalColumnName(final Identifier the_identifier, 
                                         final JdbcEnvironment the_context) {
    return new Identifier(addUnderscores(the_identifier.getText()).replaceAll("my_", ""),
                          the_identifier.isQuoted());
  }
}
