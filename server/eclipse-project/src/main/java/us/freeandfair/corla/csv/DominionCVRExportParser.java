/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.Set;

import javax.persistence.PersistenceException;

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
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.util.ExponentialBackoffHelper;

/**
 * Parser for Dominion CVR export files.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CyclomaticComplexity", "PMD.ExcessiveImports",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class DominionCVRExportParser implements CVRExportParser {
  /**
   * The name of the transaction size property.
   */
  public static final String TRANSACTION_SIZE_PROPERTY = "cvr_import_transaction_size";
  
  /**
   * The name of the batch size property.
   */
  public static final String BATCH_SIZE_PROPERTY = "cvr_import_batch_size";
  
  /**
   * The number of times to retry a county dashboard update operation.
   */
  private static final int UPDATE_RETRIES = 15;
  
  /**
   * The number of milliseconds to sleep between transaction retries.
   */
  private static final long TRANSACTION_SLEEP_MSEC = 10;
  
  /**
   * The interval at which to log progress.
   */
  private static final int PROGRESS_INTERVAL = 500;
  
  /**
   * The default size of a batch of CVRs to be flushed to the database.
   */
  private static final int DEFAULT_BATCH_SIZE = 80;

  /**
   * The default size of a batch of CVRs to be committed as a transaction.
   */
  private static final int DEFAULT_TRANSACTION_SIZE = 400;
  
  /**
   * The column containing the CVR number in a Dominion export file.
   */
  private static final String CVR_NUMBER_HEADER = "CvrNumber";
  
  /**
   * The column containing the tabulator number in a Dominion export file.
   */
  private static final String TABULATOR_NUMBER_HEADER = "TabulatorNum";
  
  /**
   * The column containing the batch ID in a Dominion export file.
   */
  private static final String BATCH_ID_HEADER = "BatchId";
  
  /**
   * The column containing the record ID in a Dominion export file.
   */
  private static final String RECORD_ID_HEADER = "RecordId";
  
  /**
   * The column containing the imprinted ID in a Dominion export file.
   */
  private static final String IMPRINTED_ID_HEADER = "ImprintedId";
  
  /**
   * The column containing the counting group in a Dominion export file.
   */
  private static final String COUNTING_GROUP_HEADER = "CountingGroup";
  
  /**
   * The column containing the precinct portion in a Dominion export file.
   */
  @SuppressWarnings({"PMD.UnusedPrivateField", "unused"})
  private static final String PRECINCT_PORTION_HEADER = "PrecinctPortion";
  
  /**
   * The column containing the ballot type in a Dominion export file.
   */
  private static final String BALLOT_TYPE_HEADER = "BallotType";

  /**
   * The prohibited headers.
   */
  private static final String[] PROHIBITED_HEADERS = {COUNTING_GROUP_HEADER};
  
  /**
   * The required headers.
   */
  private static final String[] REQUIRED_HEADERS = {
      CVR_NUMBER_HEADER, TABULATOR_NUMBER_HEADER, BATCH_ID_HEADER,
      RECORD_ID_HEADER, IMPRINTED_ID_HEADER, BALLOT_TYPE_HEADER 
      };
  
  /**
   * A flag indicating whether parse() has been run or not.
   */
  private boolean my_parse_status;
  
  /**
   * A flag indicating whether or not a parse was successful.
   */
  private boolean my_parse_success;
  
  /**
   * The error message.
   */
  private String my_error_message;
  
  /**
   * The parser to be used.
   */
  private final CSVParser my_parser;
  
  /**
   * The map from column names to column numbers.
   */
  private final Map<String, Integer> my_columns = new HashMap<String, Integer>();
  
  /**
   * The index of the first choice/contest column.
   */
  private int my_first_contest_column;
  
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
   * The number of parsed CVRs.
   */
  private int my_record_count = -1;
  
  /**
   * The set of parsed CVRs that haven't yet been flushed to the database.
   */
  private final Set<CastVoteRecord> my_parsed_cvrs = new HashSet<>();
  
  /**
   * The size of a batch of CVRs to be flushed to the database.
   */
  private final int my_batch_size;
  
  /**
   * The size of a batch of CVRs to be committed as a transaction.
   */
  private final int my_transaction_size;
  
  /**
   * A flag that indicates whether the parse is processed as multiple 
   * transactions.
   */
  private final boolean my_multi_transaction;
  
  /**
   * Construct a new Dominion CVR export parser using the specified Reader,
   * for CVRs provided by the specified county.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @param the_properties The properties from which to read any overrides to the 
   * default transaction and batch sizes.
   * @param the_multi_transaction true to commit the CVRs in multiple transactions,
   * false otherwise. If this is true, the parser assumes that a transaction is
   * in progress when invoked, and periodically commits that transaction and 
   * starts a new one to continue parsing, leaving a transaction open at completion.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final Reader the_reader, final County the_county,
                                 final Properties the_properties,
                                 final boolean the_multi_transaction) 
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_county = the_county;
    my_multi_transaction = the_multi_transaction;
    my_batch_size = parseProperty(the_properties, BATCH_SIZE_PROPERTY, 
                                  DEFAULT_BATCH_SIZE);
    my_transaction_size = parseProperty(the_properties, TRANSACTION_SIZE_PROPERTY, 
                                        DEFAULT_TRANSACTION_SIZE);
  }
  
  /**
   * Construct a new Dominion CVR export parser to parse the specified
   * CSV string, for CVRs provided by the specified county.
   * 
   * @param the_string The CSV string to parse.
   * @param the_county The county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final String the_string, final County the_county)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county = the_county;
    my_multi_transaction = false;
    my_batch_size = DEFAULT_BATCH_SIZE;
    my_transaction_size = DEFAULT_TRANSACTION_SIZE;
  }
  
  /**
   * Parse an integer value from the specified property, returning the specified
   * default if the property doesn't exist or is not an integer.
   * 
   * @param the_properties The properties to use.
   * @param the_property_name The name of the property to parse.
   * @param the_default_value The default value.
   */
  private int parseProperty(final Properties the_properties, 
                            final String the_property_name, 
                            final int the_default_value) {
    int result;
    
    try {
      result = Integer.parseInt(the_properties.getProperty(the_property_name, 
                                                           String.valueOf(the_default_value)));
    } catch (final NumberFormatException e) {
      result = the_default_value;
    }
    
    return result;
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
    int index = my_first_contest_column;
    do {
      final String c = the_line.get(index);
      int count = 0;
      while (index < the_line.size() && 
             c.equals(the_line.get(index))) {
        index = index + 1;
        count = count + 1;
      }
      // get the contest name and "(Vote For=" number
      String cn = c.substring(0, c.indexOf("(Vote For="));
      final String vf = c.replace(cn, "").replace("(Vote For=", "").replace(")", "");
      // clean up the contest name (we used it to get the number, before cleaning)
      cn = cn.trim();
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
    int index = my_first_contest_column;
    int contest_count = 0;
    for (final String cn : the_contest_names) {
      final List<Choice> choices = new ArrayList<Choice>();
      final int end = index + the_choice_counts.get(cn); 
      boolean write_in = false;
      while (index < end) {
        final String ch = the_choice_line.get(index).trim();
        final String ex = the_expl_line.get(index).trim();
        // "Write-in" is a fictitious candidate that denotes the beginning of
        // the list of qualified write-in candidates
        final boolean fictitious = "Write-in".equals(ch);
        choices.add(new Choice(ch, ex, write_in, fictitious));
        if (fictitious) {
          // consider all subsequent choices in this contest to be qualified 
          // write-in candidates
          write_in = true;
        }
        index = index + 1;
      }
      // now that we have all the choices, we can create a Contest object for 
      // this contest (note the empty contest description at the moment, below, 
      // as that's not in the CVR files and may not actually be used)
      // note that we're using the "Vote For" number as the number of winners
      // allowed as well, because the Dominion format doesn't give us that
      // separately
      final Contest c = new Contest(cn, my_county, "", choices, 
                                    the_votes_allowed.get(cn), the_votes_allowed.get(cn),
                                    contest_count);
      contest_count = contest_count + 1;
      Persistence.saveOrUpdate(c);
      final CountyContestResult r = 
          CountyContestResultQueries.matching(my_county, c);
      my_contests.add(c);
      my_results.add(r);
    }
  }
  
  /**
   * Checks to see if the set of parsed CVRs needs flushing, and does so 
   * if necessary.
   */
  private void checkForFlush() {
    if (my_multi_transaction && my_record_count % my_transaction_size == 0) {
      commitCVRsAndUpdateCountyDashboard();
    }
    
    if (my_record_count % my_batch_size == 0) {
      Persistence.flush();
      for (final CastVoteRecord cvr : my_parsed_cvrs) {
        Persistence.evict(cvr);
      }
      my_parsed_cvrs.clear();
    }
  }
  
  /**
   * Commits the currently outstanding CVRs and updates the county dashboard
   * accordingly.
   */
  private void commitCVRsAndUpdateCountyDashboard() {
    // commit all the CVR records and contest tracking data
    Persistence.commitTransaction();
    
    boolean success = false;
    int retries = 0;
    while (!success && retries < UPDATE_RETRIES) {
      try {
        retries = retries + 1;
        Main.LOGGER.debug("updating county " + my_county.id() + " dashboard, attempt " +
                          retries);
        Persistence.beginTransaction();
        final CountyDashboard cdb = 
            Persistence.getByID(my_county.id(), CountyDashboard.class);
        // if we can't get a reference to the county dashboard, we've got problems -
        // but we'll deal with them elsewhere
        if (cdb == null) {
          Persistence.rollbackTransaction();
        } else {
          cdb.setCVRsImported(my_record_count);
          Persistence.saveOrUpdate(cdb);
          Persistence.commitTransaction();
          success = true;
        }
      } catch (final PersistenceException e) {
        // something went wrong, let's try again
        if (Persistence.canTransactionRollback()) {
          try {
            Persistence.rollbackTransaction();
          } catch (final PersistenceException ex) {
            // not much we can do about it
          }
        }
        // let's give other transactions time to breathe
        try {
          final long delay = 
              ExponentialBackoffHelper.exponentialBackoff(retries, TRANSACTION_SLEEP_MSEC);
          Main.LOGGER.info("retrying county " + my_county.id() + 
                           " dashboard update in " + delay + "ms");
          Thread.sleep(delay);         
        } catch (final InterruptedException ex) {
          // it's OK to be interrupted
        }
      }
    }
    // we always need a running transaction
    Persistence.beginTransaction();
    if (success && retries > 1) {
      Main.LOGGER.info("updated state machine for county " + my_county.id() + 
                       " in " + retries + " tries");
    } else if (!success) {
      throw new PersistenceException("could not update state machine for county " + 
                                     my_county.id() + " after " + retries + " tries");
    } 
  } 
  
  /**
   * Extract a CVR from a line of the file.
   * 
   * @param the_line The line representing the CVR.
   * @param the_timestamp The import timestamp.
   * @return the resulting CVR.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private CastVoteRecord extractCVR(final CSVRecord the_line) {
    try {
      final int cvr_id =
          Integer.parseInt(stripEqualQuotes(the_line.get(my_columns.get(CVR_NUMBER_HEADER))));
      final int tabulator_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(my_columns.
                                                         get(TABULATOR_NUMBER_HEADER))));
      final int batch_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(my_columns.get(BATCH_ID_HEADER))));
      final int record_id = 
          Integer.parseInt(stripEqualQuotes(the_line.get(my_columns.get(RECORD_ID_HEADER))));
      final String imprinted_id = 
          stripEqualQuotes(the_line.get(my_columns.get(IMPRINTED_ID_HEADER)));
      final String ballot_type = 
          stripEqualQuotes(the_line.get(my_columns.get(BALLOT_TYPE_HEADER)));
      final List<CVRContestInfo> contest_info = new ArrayList<CVRContestInfo>();
      
      // for each contest, see if choices exist on the CVR; "0" or "1" are
      // votes or absences of votes; "" means that the contest is not in this style
      int index = my_first_contest_column;
      for (final Contest co : my_contests) {
        boolean present = false;
        final List<String> votes = new ArrayList<String>();
        for (final Choice ch : co.choices()) {
          final String mark_string = the_line.get(index);
          final boolean p = !mark_string.isEmpty();
          final boolean mark = "1".equals(mark_string);
          present |= p;
          if (!ch.fictitious() && p && mark) {
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
          new CastVoteRecord(RecordType.UPLOADED, null, my_county.id(),
                             cvr_id, my_record_count, tabulator_id, 
                             batch_id, record_id, imprinted_id, 
                             ballot_type, contest_info);
      Persistence.saveOrUpdate(new_cvr);
      my_parsed_cvrs.add(new_cvr);
      
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
   * Processes the headers from the specified CSV record. This includes checking
   * for the use of forbidden headers, and that all required headers are 
   * present.
   * 
   * @return true if the headers are OK, false otherwise; this method also
   * sets the error message if necessary.
   */
  @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.AvoidDeeplyNestedIfStmts",
      "PMD.ModifiedCyclomaticComplexity", "PMD.CyclomaticComplexity",
      "PMD.StdCyclomaticComplexity", "PMD.NPathComplexity"})
  private boolean processHeaders(final CSVRecord the_line) {
    boolean result = true;
    
    // the explanations line includes the column names for the non-contest/choice
    // columns, so let's get those
    for (int i = 0; i < my_first_contest_column; i++) {
      my_columns.put(the_line.get(i), i);
    }
    
    // let's make sure none of our prohibited headers are present
    final List<String> prohibited_headers = new ArrayList<>();
    for (final String h : PROHIBITED_HEADERS) {
      if (my_columns.get(h) != null) {
        result = false;
        prohibited_headers.add(h);        
      }
    }
    
    // let's make sure no required headers are missing
    final Set<String> required_headers = 
        new HashSet<>(Arrays.asList(REQUIRED_HEADERS));
    for (final String header : REQUIRED_HEADERS) {
      if (my_columns.get(header) != null) {
        required_headers.remove(header);
      }
    }
    
    result = prohibited_headers.isEmpty() && required_headers.isEmpty();
    
    if (!result) {
      final StringBuilder sb = new StringBuilder();
      sb.append("malformed CVR file: ");
      
      if (!prohibited_headers.isEmpty()) {
        sb.append("prohibited header");
        if (prohibited_headers.size() > 1) {
          sb.append('s');
        }
        sb.append(' ');
        sb.append(stringList(prohibited_headers));
        sb.append(" present");
        if (!required_headers.isEmpty()) {
          sb.append(", ");
        }
      }
      
      if (!required_headers.isEmpty()) {
        sb.append("required header");
        if (required_headers.size() > 1) {
          sb.append('s');
        }
        sb.append(' ');
        sb.append(stringList(required_headers));
        sb.append(" missing");
      }
      
      my_error_message = sb.toString();
    }
    
    return result;
  }
  
  /**
   * Makes a comma-separated string of the specified collection of 
   * strings.
   * 
   * @param the_list The list.
   * @return the comma-separated string.
   */
  private String stringList(final Collection<String> the_strings) {
    final List<String> strings = new ArrayList<>(the_strings);
    final StringBuilder sb = new StringBuilder();
    
    Collections.sort(strings);
    sb.append(strings.get(0));
    for (int i = 1; i < strings.size(); i++) {
      sb.append(", ");
      sb.append(strings.get(i));
    }
    
    return sb.toString();
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
    
    Main.LOGGER.info("parsing CVR export for county " + my_county.id() + 
                     ", batch_size=" + my_batch_size + 
                     ", transaction_size=" + my_transaction_size);
    
    boolean result = true; // presume the parse will succeed
    final Iterator<CSVRecord> records = my_parser.iterator();
    
    my_record_count = 0;
    
    try {
      // we expect the first line to be the election name, which we currently discard
      records.next();
      
      // for the second line, we count the number of empty strings to find the first
      // contest/choice column
      
      final CSVRecord contest_line = records.next();
      my_first_contest_column = 0;
      while ("".equals(contest_line.get(my_first_contest_column))) {
        my_first_contest_column = my_first_contest_column + 1;
      }
      
      // find all the contest names, how many choices each has, 
      // and how many choices can be made in each
      final List<String> contest_names = new ArrayList<String>();
      final Map<String, Integer> contest_votes_allowed = new HashMap<String, Integer>();
      final Map<String, Integer> contest_choice_counts = new HashMap<String, Integer>();
      
      // we expect the second line to be a list of contest names, each appearing once 
      // for each choice in the contest

      updateContestStructures(contest_line, contest_names, contest_votes_allowed, 
                              contest_choice_counts);

      // we expect the third and fourth lines to be a list of contest choices
      // and a list of explanations of those choices (such as party affiliations)
      
      final CSVRecord choice_line = records.next();
      final CSVRecord expl_line = records.next();
      
      if (processHeaders(expl_line)) {
        addContests(choice_line, expl_line, contest_names,
                    contest_votes_allowed, contest_choice_counts);

        // subsequent lines contain cast vote records
        while (records.hasNext()) {
          final CSVRecord cvr_line = records.next();
          final CastVoteRecord cvr = extractCVR(cvr_line);
          if (cvr == null) {
            // we don't record the CVR since it didn't parse
            Main.LOGGER.error("Could not parse malformed CVR record (" + cvr_line + ")");
            my_error_message = "malformed CVR record (" + cvr_line + ")";
            result = false;   
            break;
          } else {
            my_record_count = my_record_count + 1;
            if (my_record_count % PROGRESS_INTERVAL == 0) {
              Main.LOGGER.info("parsed " + my_record_count + 
                               " CVRs for county " + my_county.id());
            }
          }
          checkForFlush();
        }
        
        for (final CountyContestResult r : my_results) {
          r.updateResults();
          Persistence.saveOrUpdate(r);
        }
        
        // commit any uncommitted records
        
        commitCVRsAndUpdateCountyDashboard();
      } else {
        // error message was set when validating columns
        result = false;
      }
    } catch (final NoSuchElementException | StringIndexOutOfBoundsException |
                   ArrayIndexOutOfBoundsException e) {
      Main.LOGGER.error("Could not parse CVR file because it was malformed");
      my_error_message = "malformed CVR file";
      result = false;
    }
    
    // if we had any kind of parse error, we scrap the whole import
    
    my_parse_status = true;
    my_parse_success = result;
    
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized OptionalInt recordCount() {
    if (my_record_count < 0) {
      return OptionalInt.empty();
    } else {
      return OptionalInt.of(my_record_count);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized String errorMessage() {
    return my_error_message;
  }
}
