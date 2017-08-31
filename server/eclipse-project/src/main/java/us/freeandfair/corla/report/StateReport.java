/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.report;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

/**
 * All the data required for a state audit report.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class StateReport {
  /**
   * The date and time this report was generated.
   */
  private final Instant my_timestamp;
  
  /**
   * The county audit reports.
   */
  private final Map<County, CountyReport> my_county_reports;
  
  /**
   * Initialize a state report object, timestamped at the current time.
   */
  public StateReport() {
    this(Instant.now());
  }
  
  /**
   * Initialize a state report object with the specified timestamp. All
   * of the individual county reports will have the same timestamp.
   * 
   * @param the_timestamp The timestamp.
   */
  public StateReport(final Instant the_timestamp) {
    my_county_reports = new HashMap<>();
    my_timestamp = the_timestamp;
    for (final County c : Persistence.getAll(County.class)) {
      my_county_reports.put(c, new CountyReport(c, my_timestamp));
    }
  }
  
  /**
   * @return the timestamp of this report.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the county reports comprising this report.
   */
  public Map<County, CountyReport> countyReports() {
    return Collections.unmodifiableMap(my_county_reports);
  }
  
  /**
   * @return the CSV representation of this report, as a byte array.
   */
  public byte[] generateCSV() {
    byte[] result = null;
    
    return result;
  }
  
  /**
   * @return the PDF representation of this report, as a byte array.
   */
  public byte[] generatePDF() {
    byte[] result = null;
    
    return result;
  }
}
