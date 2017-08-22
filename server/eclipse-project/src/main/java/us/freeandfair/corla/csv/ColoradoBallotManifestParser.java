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
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.persistence.Persistence;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class ColoradoBallotManifestParser implements BallotManifestParser {
  /**
   * The size of a batch of ballot manifests to be flushed to the database.
   */
  private static final int BATCH_SIZE = 50;

//  TODO: if we want to validate county IDs against the county strings in
//        the file, we'll need this later
//  /**
//   * The column containing the county ID.
//   */
//  private static final int COUNTY_ID_COLUMN = 0;
 
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
   * The timestamp to apply to the parsed manifest lines.
   */
  private final Instant my_timestamp;
  
  /**
   * The county ID to apply to the parsed manifest lines.
   */
  private final Long my_county_id;
  
  /**
   * The number of records parsed from the ballot manifest file.
   */
  private OptionalInt my_record_count = OptionalInt.empty();

  /**
   * The set of parsed ballot manifests that haven't yet been flushed to the 
   * database.
   */
  private final Set<BallotManifestInfo> my_parsed_manifests = new HashSet<>();
  
  /**
   * Construct a new Colorado ballot manifest parser using the specified Reader.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_timestamp The timestamp to apply to the parsed records.
   * @param the_county_id The county ID for the parsed records.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final Reader the_reader, 
                                      final Instant the_timestamp,
                                      final Long the_county_id) 
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
  }
  
  /**
   * Construct a new Colorado ballot manifest parser using the specified String.
   * 
   * @param the_string The CSV string to parse.
   * @param the_timestamp The timestamp to apply to the parsed records.
   * @param the_county_id The county ID for the parsed records.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final String the_string, 
                                      final Instant the_timestamp,
                                      final Long the_county_id)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
  }
  
  /**
   * Checks to see if the set of parsed manifests needs flushing, and does so 
   * if necessary.
   */
  private void checkForFlush() {
    if (my_parsed_manifests.size() % BATCH_SIZE == 0) {
      Persistence.flush();
      for (final BallotManifestInfo bmi : my_parsed_manifests) {
        Persistence.evict(bmi);
      }
      my_parsed_manifests.clear();
    }
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
    BallotManifestInfo result = null;
    
    try {
      // TODO: should we check for mismatched county IDs between the
      // one we were passed at construction and the county name string 
      // in the file?
      result = new BallotManifestInfo(the_timestamp, 
                                      my_county_id,
                                      the_line.get(SCANNER_ID_COLUMN),
                                      the_line.get(BATCH_NUMBER_COLUMN),
                                      Integer.parseInt(the_line.
                                                       get(NUM_BALLOTS_COLUMN)),
                                      the_line.get(BATCH_LOCATION_COLUMN));
      Persistence.saveOrUpdate(result);
      my_parsed_manifests.add(result);
      checkForFlush();
      Main.LOGGER.debug("parsed ballot manifest: " + result);
    } catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
      // return the null result
    }
    
    return result;
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
    
    my_record_count = OptionalInt.of(0);
    
    try {
      // we expect the first line to be the headers, which we currently discard
      records.next();
      
      // subsequent lines contain ballot manifest info
      while (records.hasNext()) {
        final CSVRecord bmi_line = records.next();
        final BallotManifestInfo bmi = extractBMI(bmi_line, my_timestamp);
        if (bmi == null) {
          // we don't record the ballot manifest record since it didn't parse
          Main.LOGGER.error("Could not parse malformed ballot manifest record (" + 
                            bmi_line + ")");
          result = false;
          break;
        } else {
          my_record_count = OptionalInt.of(my_record_count.getAsInt() + 1);
        }
      }
    } catch (final NoSuchElementException e) {
      Main.LOGGER.error("Could not parse ballot manifest file because it had " +
                        "a malformed header");
      result = false;
    }
    
    my_parse_status = true;
    my_parse_success = result;
    
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized OptionalInt recordCount() {
    return my_record_count;
  }
}
