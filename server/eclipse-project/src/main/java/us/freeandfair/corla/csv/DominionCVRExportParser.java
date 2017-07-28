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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.hibernate.Persistence;
import us.freeandfair.corla.model.BallotStyle;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class DominionCVRExportParser implements CVRExportParser {
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
   * The list of CVRs parsed from the supplied data export.
   */
  private final List<CastVoteRecord> my_cvrs = new ArrayList<CastVoteRecord>();
  
  /**
   * The list of contests parsed from the supplied data export.
   */
  private final List<Contest> my_contests = new ArrayList<Contest>();
  
  /**
   * The ballot styles inferred from the supplied data export.
   */
  private final Map<String, BallotStyle> my_ballot_styles = 
      new HashMap<String, BallotStyle>();
  
  /**
   * The ID of the county whose CVRs we are parsing.
   */
  private final String my_county_id;
  
  /**
   * The session we're using for persistence.
   */
  private Session my_session = Persistence.NO_SESSION;
  
  /**
   * The transaction we're using for persistence.
   */
  private Transaction my_transaction = Persistence.NO_TRANSACTION; 
  
  /**
   * Construct a new Dominion CVR export parser using the specified Reader,
   * for CVRs provided by the specified county.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_county_id The ID of the county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final Reader the_reader, final String the_county_id) 
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_county_id = the_county_id;
  }
  
  /**
   * Construct a new Dominion CVR export parser to parse the specified
   * CSV string, for CVRs provided by the specified county.
   * 
   * @param the_string The CSV string to parse.
   * @param the_county_id The ID of the county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final String the_string, final String the_county_id)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county_id = the_county_id;
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
        ms = Integer.valueOf(vf);
      } catch (final NumberFormatException e) {
        // ignored
      }
      the_names.add(cn);
      the_choice_counts.put(cn, count);
      the_votes_allowed.put(cn, ms);
    } while (index < the_line.size());
  }
  
  /**
   * Add full contest objects to our list of contests.
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
        choices.add(Persistence.getEntity(Choice.instance(ch, ex), Choice.class,
                                          my_session));
        index = index + 1;
      }
      // now that we have all the choices, we can create a Contest object for 
      // this contest (note the empty contest description at the moment, below, 
      // as that's not in the CVR files and may not actually be used)
      my_contests.add(Persistence.getEntity(Contest.instance(cn, "", choices, 
                                                             the_votes_allowed.get(cn)),
                                            Contest.class, my_session));
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
      final String tabulator_id = 
          stripEqualQuotes(the_line.get(TABULATOR_NUMBER_COLUMN));
      final String batch_id = 
          stripEqualQuotes(the_line.get(BATCH_ID_COLUMN));
      final String record_id = 
          stripEqualQuotes(the_line.get(RECORD_ID_COLUMN));
      final String imprinted_id = 
          stripEqualQuotes(the_line.get(IMPRINTED_ID_COLUMN));
      final String ballot_style_name = 
          stripEqualQuotes(the_line.get(BALLOT_TYPE_COLUMN));
      final List<Contest> contests = new ArrayList<Contest>();
      final Map<Contest, Set<Choice>> choices = new HashMap<Contest, Set<Choice>>();
      
      // for each contest, see if choices exist on the CVR; "0" or "1" are
      // votes or absences of votes; "" means that the contest is not in this style
      int index = FIRST_CHOICE_COLUMN;
      for (final Contest co : my_contests) {
        boolean present = false;
        final Set<Choice> votes = new HashSet<Choice>();
        for (final Choice ch : co.choices()) {
          final String mark = the_line.get(index);
          final boolean p = !mark.isEmpty();
          present |= p;
          if (p && Integer.valueOf(mark) != 0) {
            votes.add(ch);
          }
          index = index + 1;
        }
        // if this contest was on the ballot, add it to the votes
        if (present) {
          contests.add(co);
          choices.put(co, votes);
        }
      }
      
      // we should now have the votes for each contest; if the ballot style
      // doesn't exist for this ballot style name, create it now
      
      if (!my_ballot_styles.containsKey(ballot_style_name)) {
        final BallotStyle bs = BallotStyle.instance(ballot_style_name, contests);
        /*
        if (my_session != Persistence.NO_SESSION) {
          my_session.save(bs);
        }
        */
        my_ballot_styles.put(ballot_style_name, bs);
      }
      
      return CastVoteRecord.instance(false, the_timestamp, my_county_id, tabulator_id,
                                     batch_id, record_id, imprinted_id, 
                                     my_ballot_styles.get(ballot_style_name), choices);
    } catch (final NumberFormatException e) {
      return null;
    } catch (final ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }
  
  /**
   * Commits the changes from the parsing to persistent storage.
   */
  private void commit() {
    if (my_transaction != Persistence.NO_TRANSACTION) {
      my_transaction.commit();
      my_session.close();
    }
  }
  
  /**
   * Aborts the changes from parsing.
   */
  private void abort() {
    if (my_transaction != Persistence.NO_TRANSACTION) {
      my_transaction.rollback();
      my_session.close();
    }
    for (final CastVoteRecord cvr : my_cvrs) {
      CastVoteRecord.forget(cvr);
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
    // we are not doing persistence for now
    // my_session = Persistence.openSession();
    if (my_session != Persistence.NO_SESSION) {
      my_transaction = my_session.beginTransaction();
    }
    
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
        final CastVoteRecord cvr = extractCVR(cvr_line, timestamp);
        if (cvr == null) {
          // we don't record the CVR since it didn't parse
          Main.LOGGER.error("Could not parse malformed CVR record (" + cvr_line + ")");
          result = false;          
        } else {
          my_cvrs.add(cvr);
        }
      }
    } catch (final NoSuchElementException | StringIndexOutOfBoundsException e) {
      Main.LOGGER.error("Could not parse CVR file because it had a malformed header");
      result = false;
    }
    
    // if we had any kind of parse error, we scrap the whole import
    
    my_parse_success = result;
    my_parse_status = true;
    if (my_parse_success) {
      commit();
    } else {
      abort();
    }
    return result;
  }

  /**
   * @return the CVRs parsed from the supplied data export.
   */
  @Override
  public synchronized List<CastVoteRecord> cvrs() {
    return Collections.unmodifiableList(my_cvrs);
  }

  /**
   * @return the contests inferred from the supplied data export.
   */
  @Override
  public synchronized List<Contest> contests() {
    return Collections.unmodifiableList(my_contests);
  }

  /**
   * @return the ballot styles inferred from the supplied data export.
   */
  @Override
  public synchronized Set<BallotStyle> ballotStyles() {
    return new HashSet<BallotStyle>(my_ballot_styles.values());
  }
  
  /**
   * 
   * <description>
   * <explanation>
   * @param
   */
  //@ behavior
  //@   requires P;
  //@   ensures Q;
  /*@ pure @
   */
  @SuppressWarnings("PMD.SystemPrintln")
  public static void main(final String... the_args) throws IOException {
    final Reader r = new FileReader(
         "/Unsorted/dominion-2017-CVR_Export_20170310104116-clean.csv");
    final DominionCVRExportParser thing = new DominionCVRExportParser(r, "Foo");
    thing.parse();
    //System.err.println(thing.parse());
    //System.err.println(thing.cvrs());
    //System.err.println(thing.contests());
    //System.err.println(thing.ballotStyles());
    
    final File file = new File("/Unsorted/test.json");
    file.createNewFile();
    final FileWriter fw = new FileWriter(file);
    fw.write(Main.GSON.toJson(thing.cvrs()));
    fw.close();
  }

}
