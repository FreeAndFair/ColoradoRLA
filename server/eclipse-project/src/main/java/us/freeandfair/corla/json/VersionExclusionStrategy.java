/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import javax.persistence.Version;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * An exclusion strategy that excludes our Hibernate version fields from Gson
 * serialization.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class VersionExclusionStrategy implements ExclusionStrategy {
  /**
   * Don't exclude any classes.
   * 
   * @param the_class The class, ignored.
   */
  public boolean shouldSkipClass(final Class<?> the_class) {
    return false;
  }
  
  /**
   * Exclude fields with the @Version annotation.
   * 
   * @param the_field The field attributes.
   */
  public boolean shouldSkipField(final FieldAttributes the_field) {
    return the_field.getAnnotation(Version.class) != null;
  }
}
