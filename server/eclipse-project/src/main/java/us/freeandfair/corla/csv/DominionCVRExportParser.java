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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import us.freeandfair.corla.model.CastVoteRecord;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class DominionCVRExportParser implements Parser {

   /** 
    * The parser we will use for the file
    */
   private CSVParser my_parser; 
  
  @Override
  public List<CastVoteRecord> parseCSV(String the_file_name, int the_county_id) {
    Reader csv_reader;
    try {
      csv_reader = new FileReader(the_file_name);
      my_parser = new CSVParser(csv_reader, CSVFormat.DEFAULT);
      for(CSVRecord record : my_parser) {
        System.out.println(record.toString());
      }
    } catch (IOException e) {
      assert false;
      e.printStackTrace();
    }
    assert false;
    //@ assert false;
    return null;
  }
  
  public static void main(String args[]) {
    DominionCVRExportParser thing = new DominionCVRExportParser();
    thing.parseCSV("C:\\Users\\dist0\\Downloads\\CVR_Export_20170723212105.csv", 0);
  }

}
