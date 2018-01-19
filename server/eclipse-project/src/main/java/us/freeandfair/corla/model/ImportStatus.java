/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Sep 6, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Status information for a file import.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class ImportStatus implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The import state.
   */
  @Enumerated(EnumType.STRING)
  private ImportState my_import_state;
  
  /**
   * The error message, if any.
   */
  private String my_error_message;
  
  /**
   * The timestamp of the status update.
   */
  private Instant my_timestamp;
  
  /**
   * Constructs an empty ImportStatus, solely for persistence.
   */
  public ImportStatus() {
    super();
  }
  
  /**
   * Constructs a new ImportStatus with the specified contents.
   * 
   * @param the_import_state The import state.
   * @param the_error_message The error message, or null if there has been no error.
   * @param the_timestamp The timestamp.
   */
  public ImportStatus(final ImportState the_import_state,
                      final String the_error_message,
                      final Instant the_timestamp) {
    my_import_state = the_import_state;
    my_error_message = the_error_message;
    my_timestamp = the_timestamp;
  }
  
  /**
   * Constructs a new ImportStatus with the specified contents, using the
   * current time as a timestamp.
   * 
   * @param the_import_state The import state.
   * @param the_error_message The error message, or null if there has been no error.
   */
  public ImportStatus(final ImportState the_import_state,
                      final String the_error_message) {
    this(the_import_state, the_error_message, Instant.now());
  }
  
  /**
   * Constructs a new ImportStatus with the specified import state, no error
   * message, and the current time as a timestamp.
   * 
   * @param the_import_state The import state.
   * @param the_error_message The error message, or null if there has been no error.
   */
  public ImportStatus(final ImportState the_import_state) {
    this(the_import_state, null, Instant.now());
  }
 
  /**
   * @return the import state.
   */
  public ImportState importState() {
    return my_import_state;
  }
  
  /**
   * @return the error message.
   */
  public String errorMessage() {
    return my_error_message;
  }
  
  /**
   * @return the timestamp.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * The state of an import.
   */
  public enum ImportState {
    NOT_ATTEMPTED,
    IN_PROGRESS,
    SUCCESSFUL,
    FAILED;
  }
}
