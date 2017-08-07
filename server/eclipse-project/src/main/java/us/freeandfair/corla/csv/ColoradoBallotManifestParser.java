/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.BallotManifestInfo;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class ColoradoBallotManifestParser implements BallotManifestParser {
  /**
   * The column containing the county ID.
   */
  private static final int COUNTY_ID_COLUMN = 0;
  
  /**
   * The column containing the scanner ID.
   */
  private static final int SCANNER_ID_COLUMN = 1;
  
  /**
   * The column containing the batch number.
   */
  private static final int BATCH_NUMBER_COLUMN = 2;
  
  /**
   * The column containing the number of ballots in the batch.
   */
  private static final int NUM_BALLOTS_COLUMN = 3;
  
  /**
   * The column containing the storage location.
   */
  private static final int BATCH_LOCATION_COLUMN = 4;

  /**
   * A flag indicating whether parse() has been run or not.
   */
  private boolean my_parse_status;
  
  /**
   * A flag indicating whether or not a parse was successful.
   */
  private boolean my_parse_success;
  
  /**
   * The parser to be used.
   */
  private final CSVParser my_parser;
  
  /**
   * The list of ballot manifest information IDs parsed from the supplied data.
   */
  private final List<Long> my_manifest_info = new ArrayList<Long>();
  
  
  /**
   * Construct a new Colorado ballot manifest parser using the specified Reader.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final Reader the_reader) 
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
  }
  
  /**
   * Construct a new Colorado ballot manifest parser using the specified String.
   * 
   * @param the_string The CSV string to parse.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final String the_string)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
  }
  
  /**
   * Extracts ballot manifest information from a single CSV line.
   * 
   * @param the_line The CSV line.
   * @param the_timestamp The timestamp to apply to the result.
   * @return the extracted information.
   */
  private BallotManifestInfo extractBMI(final CSVRecord the_line,
                                        final Instant the_timestamp) {
    try {
      return Persistence.get(new BallotManifestInfo(the_timestamp, 
                                                    the_line.get(COUNTY_ID_COLUMN),
                                                    the_line.get(SCANNER_ID_COLUMN),
                                                    the_line.get(BATCH_NUMBER_COLUMN),
                                                    Integer.parseInt(the_line.
                                                                     get(NUM_BALLOTS_COLUMN)),
                                                    the_line.get(BATCH_LOCATION_COLUMN)),
                             BallotManifestInfo.class);
    } catch (final NumberFormatException e) {
      return null;
    } catch (final ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }
  
  /**
   * Aborts the changes from parsing.
   */
  private void abort() {
    for (final Long id : my_manifest_info) {
      Persistence.delete(BallotManifestInfo.class, id);
    }
  }
  
  /**
   * Parse the supplied data export. If it has already been parsed, this
   * method returns immediately.
   * 
   * @return true if the parse was successful, false otherwise
   */
  @Override
  public synchronized boolean parse() {
    if (my_parse_status) {
      // no need to parse if we've already parsed
      return my_parse_success;
    }
    
    boolean result = true; // presume the parse will succeed
    final Iterator<CSVRecord> records = my_parser.iterator();
    final Instant timestamp = Instant.now();
    
    try {
      // we expect the first line to be the headers, which we currently discard
      records.next();
      
      // subsequent lines contain ballot manifest info
      while (records.hasNext()) {
        final CSVRecord bmi_line = records.next();
        final BallotManifestInfo bmi = extractBMI(bmi_line, timestamp);
        if (bmi == null) {
          // we don't record the ballot manifest record since it didn't parse
          Main.LOGGER.error("Could not parse malformed ballot manifest record (" + 
                            bmi_line + ")");
          result = false;          
        } else {
          my_manifest_info.add(bmi.id());
        }
      }
    } catch (final NoSuchElementException e) {
      Main.LOGGER.error("Could not parse ballot manifest file because it had " +
                        "a malformed header");
      result = false;
    }
    
    my_parse_status = true;
    my_parse_success = result;
    
    // if we had any kind of parse error, we scrap the whole import
    
    if (!result) {
      abort();
    }

    return result;
  }

  /**
   * @return the CVRs parsed from the supplied data export.
   */
  @Override
  public synchronized List<Long> parsedIDs() {
    return Collections.unmodifiableList(my_manifest_info);
  }
}
