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
import java.util.ArrayList;
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
   * The first column of contest names/choices in a Dominion export file.
   */
  private static final int FIRST_CHOICE_COLUMN = 8;
  
  /**
   * A flag indicating whether parse() has been run or not.
   */
  private final boolean my_parse_status = false;
  
  /**
   * A flag indicating whether or not a parse was successful.
   */
  private final boolean my_parse_success = false;
  
  /**
   * The parser to be used.
   */
  private final CSVParser my_parser;
  
  /**
   * The set of CVRs parsed from the supplied data export.
   */
  private final Set<CastVoteRecord> my_cvrs = new HashSet<CastVoteRecord>();
  
  /**
   * The set of contests inferred from the supplied data export.
   */
  private final Set<Contest> my_contests = new HashSet<Contest>();
  
  /**
   * The ID of the county whose CVRs we are parsing.
   */
  private final int my_county_id;
  
  /**
   * Construct a new Dominion CVR export parser using the specified Reader,
   * for CVRs provided by the specified county.
   * 
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_county_id The ID of the county whose CVRs are to be parsed.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public DominionCVRExportParser(final Reader the_reader, final int the_county_id) 
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
  public DominionCVRExportParser(final String the_string, final int the_county_id)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county_id = the_county_id;
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
    
    try {
      // we expect the first line to be the election name, which we currently discard
      records.next();
      
      // we expect the second line to be a list of contest names, each appearing once 
      // for each choice in the contest
      final CSVRecord contest_line = records.next();
      
      // find all the contest names, and how many choices each has
      final List<String> contest_names = new ArrayList<String>();
      final Map<String, Integer> contest_choice_counts = new HashMap<String, Integer>();
      
      int index = FIRST_CHOICE_COLUMN;
      do {
        final String c = contest_line.get(index);
        int count = 0;
        while (index < contest_line.size() && 
               c.equals(contest_line.get(index))) {
          index = index + 1;
          count = count + 1;
        }
        contest_names.add(c);
        contest_choice_counts.put(c, count);
      } while (index < contest_line.size());

      // we expect the third line to be a list of contest choices
      final CSVRecord contest_choices_line = records.next();
      
      // we expect the fourth line to be a list of contest choice "explanations" 
      // (such as political party affiliations)
      final CSVRecord contest_choice_explanations_line = records.next();
      
      index = FIRST_CHOICE_COLUMN;
      for (final String cn : contest_names) {
        final Set<Choice> choice_set = new HashSet<Choice>();
        final int count = contest_choice_counts.get(cn); 
        while (index < index + count) {
          final String ch = contest_choices_line.get(index);
          final String ex = contest_choice_explanations_line.get(index);
          choice_set.add(new Choice(ch, ex));
          index = index + 1;
        }
        
      }
      
      
      
    } catch (final NoSuchElementException e) {
      result = false;
    }
    
    return result;
  }

  /**
   * @return the CVRs parsed from the supplied data export.
   */
  @Override
  public synchronized List<CastVoteRecord> cvrs() {
    // TODO Auto-generated method stub
    assert false;
    //@ assert false;
    return null;
  }

  /**
   * @return the contests inferred from the supplied data export.
   */
  @Override
  public synchronized Set<Contest> contests() {
    // TODO Auto-generated method stub
    assert false;
    //@ assert false;
    return null;
  }

  public static void main(final String... the_args) {
    /*
    final DominionCVRExportParser thing = new DominionCVRExportParser();
    thing.parseCVRFile("C:\\Users\\dist0\\Downloads\\CVR_Export_20170723212105.csv", 0);
    */
  }

}
