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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ContestQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class DominionCVRExportParser implements CVRExportParser {
  /**
   * The size of a batch of CVRs to be flushed to the database.
   */
  private static final int BATCH_SIZE = 50;

  /**
   * The column containing the CVR number in a Dominion export file.
   */
  private static final int CVR_NUMBER_COLUMN = 0;
  
  /**
   * The column containing the tabulator number in a Dominion export file.
   */
  private static final int TABULATOR_NUMBER_COLUMN = 1;
  
  /**
   * The column containing the batch ID in a Dominion export file.
   */
  private static final int BATCH_ID_COLUMN = 2;
  
  /**
   * The column containing the record ID in a Dominion export file.
   */
  private static final int RECORD_ID_COLUMN = 3;
  
  /**
   * The column containing the imprinted ID in a Dominion export file.
   */
  private static final int IMPRINTED_ID_COLUMN = 4;
  
  /**
   * The column containing the ballot type in a Dominion export file.
   */
  private static final int BALLOT_TYPE_COLUMN = 7;

  /**
   * The first column of contest names/choices in a Dominion export file.
   */
  private static final int FIRST_CHOICE_COLUMN = 8;
  
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
   * The list of contests parsed from the supplied data export.
   */
  private final List<Contest> my_contests = new ArrayList<Contest>();

  /**
   * The list of county contest results we build from the supplied
   * data export.
   */
  private final List<CountyContestResult> my_results = 
      new ArrayList<CountyContestResult>();
  
  /**
   * The county whose CVRs we are parsing.
   */
  private final County my_county;
  
  /**
   * The timestamp to apply to the parsed CVRs.
   */
  private final Instant my_timestamp;
  
  /**
   * The number of parsed CVRs.
   */
  private OptionalInt my_record_count = OptionalInt.empty();
  
  /**
   * The set of parsed CVRs that haven't yet been flushed to the database.
   */
  private final Set<CastVoteRecord> my_parsed_cvrs = new HashSet<>();
  
  /**
   * Construct a new Dominion CVR export parser using the specified Reader,
   * for CVRs provided by the specified county.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final Reader the_reader, final Instant the_timestamp,
                                 final County the_county) 
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_county = the_county;
    my_timestamp = the_timestamp;
  }
  
  /**
   * Construct a new Dominion CVR export parser to parse the specified
   * CSV string, for CVRs provided by the specified county.
   * 
   * @param the_string The CSV string to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final String the_string, final Instant the_timestamp,
                                 final County the_county)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county = the_county;
    my_timestamp = the_timestamp;
  }
  
  /**
   * Strip the '="..."' from a column.
   * 
   * @param the_value The value to strip.
   * @return the stripped value, as a String, or the original String if it 
   * does not have the '="..."' form.
   */
  private String stripEqualQuotes(final String the_value) {
    String result = the_value;
    if (the_value.startsWith("=\"") && the_value.endsWith("\"")) {
      result = the_value.substring(0, the_value.length() - 1).replaceFirst("=\"", "");
    }
    return result;
  }
  
  /** 
   * Updates the contest names, max selections, and choice counts structures.
   * 
   * @param the_line The CSV line containing the contest information.
   * @param the_names The contest names.
   * @param the_votes_allowed The votes allowed table.
   * @param the_choice_counts The choice counts table.
   */
  private void updateContestStructures(final CSVRecord the_line, 
                                       final List<String> the_names,
                                       final Map<String, Integer> the_votes_allowed,
                                       final Map<String, Integer> the_choice_counts) {
    int index = FIRST_CHOICE_COLUMN;
    do {
      final String c = the_line.get(index);
      int count = 0;
      while (index < the_line.size() && 
             c.equals(the_line.get(index))) {
        index = index + 1;
        count = count + 1;
      }
      // get the "(Vote For=" number from the contest name and clean up the name
      final String cn = c.substring(0, c.indexOf("(Vote For=")).trim();
      final String vf = c.replace(cn, "").replace("(Vote For=", "").replace(")", "");
      int ms = 1; // this is our default maximum selections
      try {
        ms = Integer.parseInt(vf);
      } catch (final NumberFormatException e) {
        // ignored
      }
      the_names.add(cn);
      the_choice_counts.put(cn, count);
      the_votes_allowed.put(cn, ms);
    } while (index < the_line.size());
  }
  
  /**
   * Create contest and result objects for use later in parsing.
   * 
   * @param the_choice_line The CSV line containing the choice information.
   * @param the_expl_line The CSV line containing the choice explanations.
   * @param the_contest_names The list of contest names.
   * @param the_votes_allowed The table of votes allowed values.
   * @param the_choice_counts The table of contest choice counts.
   */
  private void addContests(final CSVRecord the_choice_line, 
                           final CSVRecord the_expl_line, 
                           final List<String> the_contest_names,
                           final Map<String, Integer> the_votes_allowed,
                           final Map<String, Integer> the_choice_counts) {
    int index = FIRST_CHOICE_COLUMN;
    for (final String cn : the_contest_names) {
      final List<Choice> choices = new ArrayList<Choice>();
      final int end = index + the_choice_counts.get(cn); 
      while (index < end) {
        final String ch = the_choice_line.get(index).trim();
        final String ex = the_expl_line.get(index).trim();
        choices.add(new Choice(ch, ex));
        index = index + 1;
      }
      // now that we have all the choices, we can create a Contest object for 
      // this contest (note the empty contest description at the moment, below, 
      // as that's not in the CVR files and may not actually be used)
      final Contest c = ContestQueries.matching(new Contest(cn, "", choices, 
                                                            the_votes_allowed.get(cn)));
      CountyContestResult r = 
          CountyContestResultQueries.matching(my_county, c);
      // in case the contests were defined wrong, we just drop the results object
      Persistence.delete(r);
      r = CountyContestResultQueries.matching(my_county, c);
      my_contests.add(c);
      my_results.add(r);
    }
  }
  
  /**
   * Checks to see if the set of parsed CVRs needs flushing, and does so 
   * if necessary.
   */
  private void checkForFlush() {
    if (my_parsed_cvrs.size() % BATCH_SIZE == 0) {
      Persistence.flush();
      for (final CastVoteRecord cvr : my_parsed_cvrs) {
        Persistence.evict(cvr);
      }
      my_parsed_cvrs.clear();
    }
  }
  
  /**
   * Extract a CVR from a line of the file.
   * 
   * @param the_line The line representing the CVR.
   * @param the_timestamp The import timestamp.
   * @return the resulting CVR.
   */
  private CastVoteRecord extractCVR(final CSVRecord the_line, 
                                    final Instant the_timestamp) {
    try {
      final int cvr_id =
          Integer.parseInt(stripEqualQuotes(the_line.get(CVR_NUMBER_COLUMN)));
      final int tabulator_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(TABULATOR_NUMBER_COLUMN)));
      final int batch_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(BATCH_ID_COLUMN)));
      final int record_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(RECORD_ID_COLUMN)));
      final String imprinted_id = 
          stripEqualQuotes(the_line.get(IMPRINTED_ID_COLUMN));
      final String ballot_type = 
          stripEqualQuotes(the_line.get(BALLOT_TYPE_COLUMN));
      final List<CVRContestInfo> contest_info = new ArrayList<CVRContestInfo>();
      
      // for each contest, see if choices exist on the CVR; "0" or "1" are
      // votes or absences of votes; "" means that the contest is not in this style
      int index = FIRST_CHOICE_COLUMN;
      for (final Contest co : my_contests) {
        boolean present = false;
        final List<String> votes = new ArrayList<String>();
        for (final Choice ch : co.choices()) {
          final String mark_string = the_line.get(index);
          final boolean p = !mark_string.isEmpty();
          final boolean mark = "1".equals(mark_string);
          present |= p;
          if (p && mark) {
            votes.add(ch.name());
          }
          index = index + 1;
        }
        // if this contest was on the ballot, add it to the votes
        if (present) {
          contest_info.add(new CVRContestInfo(co, null, null, votes));
        }
      }
      
      // we don't need to look for an existing CVR with this data because,
      // by definition, there cannot be one unless the same line appears
      // twice in the CVR export file... and if it does, we need it to
      // appear twice here too. 
      final CastVoteRecord new_cvr = 
          new CastVoteRecord(RecordType.UPLOADED,
                             the_timestamp, my_county.id(),
                             cvr_id, tabulator_id, batch_id, record_id,
                             imprinted_id, ballot_type,
                             contest_info);
      Persistence.saveOrUpdate(new_cvr);
      my_parsed_cvrs.add(new_cvr);
      checkForFlush();
      
      // add the CVR to all of our results
      for (final CountyContestResult r : my_results) {
        r.addCVR(new_cvr);
      }
      Main.LOGGER.debug("parsed CVR: " + new_cvr);
      return new_cvr;
    } catch (final NumberFormatException e) {
      return null;
    } catch (final ArrayIndexOutOfBoundsException e) {
      return null;
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
    
    my_record_count = OptionalInt.of(0);
    
    try {
      // we expect the first line to be the election name, which we currently discard
      records.next();
      
      // find all the contest names, how many choices each has, 
      // and how many choices can be made in each
      final List<String> contest_names = new ArrayList<String>();
      final Map<String, Integer> contest_votes_allowed = new HashMap<String, Integer>();
      final Map<String, Integer> contest_choice_counts = new HashMap<String, Integer>();
      
      // we expect the second line to be a list of contest names, each appearing once 
      // for each choice in the contest

      updateContestStructures(records.next(), contest_names, contest_votes_allowed, 
                              contest_choice_counts);

      // we expect the third and fourth lines to be a list of contest choices
      // and a list of explanations of those choices (such as party affiliations)
      
      addContests(records.next(), records.next(), contest_names,
                  contest_votes_allowed, contest_choice_counts);
      
      // subsequent lines contain cast vote records
      while (records.hasNext()) {
        final CSVRecord cvr_line = records.next();
        final CastVoteRecord cvr = extractCVR(cvr_line, my_timestamp);
        if (cvr == null) {
          // we don't record the CVR since it didn't parse
          Main.LOGGER.error("Could not parse malformed CVR record (" + cvr_line + ")");
          result = false;   
          break;
        } else {
          my_record_count = OptionalInt.of(my_record_count.getAsInt() + 1);
        }
      }
    } catch (final NoSuchElementException | StringIndexOutOfBoundsException |
                   ArrayIndexOutOfBoundsException e) {
      Main.LOGGER.error("Could not parse CVR file because it was malformed");
      result = false;
    }
    
    // if we had any kind of parse error, we scrap the whole import
    
    my_parse_status = true;
    my_parse_success = result;
    
    for (final CountyContestResult r : my_results) {
      r.updateResults();
      Persistence.saveOrUpdate(r);
    }
    
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
