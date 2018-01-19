/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 2, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.json.IntermediateAuditReportJsonAdapter;

/**
 * An audit investigation report.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
@JsonAdapter(IntermediateAuditReportJsonAdapter.class)
public class IntermediateAuditReportInfo implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The timestamp of this report.
   */
  @Column(updatable = false)
  private Instant my_timestamp;
 
  /**
   * The report for this report.
   */
  @Column(updatable = false)
  private String my_report;
  
  /**
   * Constructs an empty AuditInvestigationReport, solely for persistence.
   */
  public IntermediateAuditReportInfo() {
    super();
  }
  
  /**
   * Constructs an audit investigation report with the specified 
   * parameters.
   * 
   * @param the_timestamp The timestamp.
   * @param the_name The name.
   * @param the_report The report.
   */
  public IntermediateAuditReportInfo(final Instant the_timestamp,
                                final String the_report) {
    super();
    my_timestamp = the_timestamp;
    my_report = the_report;
  }
  
  /**
   * @return the timestamp.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the report.
   */
  public String report() {
    return my_report;
  }
  
  /**
   * @return a String representation of this cast vote record.
   */
  @Override
  public String toString() {
    return "AuditInterimReport [timestamp=" + my_timestamp + 
           ", report=" + my_report + "]";
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
    if (the_other instanceof IntermediateAuditReportInfo) {
      final IntermediateAuditReportInfo other_report = 
          (IntermediateAuditReportInfo) the_other;
      result &= nullableEquals(other_report.timestamp(), timestamp());
      result &= nullableEquals(other_report.report(), report());
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
    return nullableHashCode(timestamp());
  }
}
