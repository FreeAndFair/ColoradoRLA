/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
 * @design In the formal model this concept is currently called "option".
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * A contest choice; has a name and a description.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
public class Choice implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The choice name.
   */
  private String my_name;

  /**
   * The choice description.
   */
  private String my_description;
  
  /**
   * A flag that indicates whether or not a choice is a qualified write-in.
   */
  private boolean my_qualified_write_in;
  
  /**
   * A flag that indicates whether or not a choice is "fictitious" (i.e., whether
   * its votes should be counted and it should be displayed). This is to
   * handle cases where specific "fake" choice names are used to delineate
   * sections of a ballot, as with Dominion and qualified write-ins.
   */
  private boolean my_fictitious;
  
  /**
   * Constructs a choice with default values, solely for persistence.
   */
  public Choice() {
    // defaults
  }
  
  /**
   * Constructs a choice with the specified parameters.
   * 
   * @param the_name The choice name.
   * @param the_description The choice description.
   * @param the_qualified_write_in True if this choice is a qualified
   * write-in candidate, false otherwise.
   * @param the_fictitious True of this choice is fictitious (should not be
   * counted), false otherwise.
   */
  public Choice(final String the_name, final String the_description,
                final boolean the_qualified_write_in,
                final boolean the_fictitious) {
    my_name = the_name;
    my_description = the_description;
    my_qualified_write_in = the_qualified_write_in;
    my_fictitious = the_fictitious;
  }
  
  /**
   * @return the name.
   */
  public String name() {
    return my_name;
  }
  
  /**
   * @return the description.
   */
  public String description() {
    return my_description;
  }
  
  /**
   * @return true if this choice is a qualified write-in, false otherwise.
   */
  public boolean qualifiedWriteIn() {
    return my_qualified_write_in;
  }
  
  /**
   * @return true if this choice is fictitious, false otherwise.
   */
  public boolean fictitious() {
    return my_fictitious;
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "Choice [name=" + my_name + ", description=" +
           my_description + "]";
  }

  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof Choice) {
      final Choice other_choice = (Choice) the_other;
      result &= nullableEquals(other_choice.name(), name());
      result &= nullableEquals(other_choice.description(), description());
      result &= nullableEquals(other_choice.qualifiedWriteIn(), qualifiedWriteIn());
      result &= nullableEquals(other_choice.fictitious(), fictitious());
    } else {
      result = false;
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return nullableHashCode(name());
  }
}
