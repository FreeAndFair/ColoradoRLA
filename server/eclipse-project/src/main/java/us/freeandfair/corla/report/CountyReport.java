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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.controller.ComparisonAuditController;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyContestResultQueries;

/**
 * All the data required for a county audit report.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public class CountyReport {
  /**
   * The font size for Excel.
   */
  public static final short FONT_SIZE = 12;
  
  /**
   * The DoS dashboard used to generate this report.
   */
  private final DoSDashboard my_dosdb;
  
  /**
   * The county dashboard used to generate this report.
   */
  private final CountyDashboard my_cdb;
  
  /**
   * The county for which this report was generated.
   */
  private final County my_county;
  
  /**
   * The date and time this report was generated.
   */
  private final Instant my_timestamp;
  
  /**
   * The lists of CVR imprinted IDs to audit for each round.
   */
  private final Map<Integer, List<String>> my_cvrs_to_audit_by_round;
  
  /**
   * The contests driving the audit, and their results.
   */
  private final List<CountyContestResult> my_driving_contest_results;
  
  /**
   * The data for each audit round.
   */
  private final List<Round> my_rounds;
  
  /**
   * Initialize a county report for the specified county, timestamped
   * with the current time.
   * 
   * @param the_county The county.
   */
  public CountyReport(final County the_county) {
    this(the_county, Instant.now());
  }
  /**
   * Initialize a county report object for the specified county, with the
   * specified timestamp.
   * 
   * @param the_county The county.
   * @param the_timestamp The timestamp.
   */
  public CountyReport(final County the_county, final Instant the_timestamp) {
    my_county = the_county;
    my_timestamp = the_timestamp;
    my_driving_contest_results = new ArrayList<CountyContestResult>();
    my_cdb = 
        Persistence.getByID(my_county.id(), CountyDashboard.class);
    for (final CountyContestResult ccr : 
         CountyContestResultQueries.forCounty(my_county)) {
      if (my_cdb.drivingContests().contains(ccr.contest())) {
        my_driving_contest_results.add(ccr);
      }
    }
    my_rounds = my_cdb.rounds();
    my_cvrs_to_audit_by_round = new HashMap<>();
    for (final Round r : my_rounds) {
      final List<CastVoteRecord> cvrs_to_audit = 
          ComparisonAuditController.computeBallotOrder(my_cdb, r.number());
      cvrs_to_audit.sort(new CastVoteRecord.BallotOrderComparator());
      final List<String> cvr_ids_to_audit = new ArrayList<>();
      for (final CastVoteRecord cvr : cvrs_to_audit) {
        cvr_ids_to_audit.add(cvr.imprintedID());
      }
      my_cvrs_to_audit_by_round.put(r.number(), cvr_ids_to_audit);
    }
    my_dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
  }
  
  /**
   * @return the county for this report.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the timestamp for this report.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the CVRs imprinted IDs to audit by round map for this report.
   */
  public Map<Integer, List<String>> cvrsToAuditByRound() {
    return Collections.unmodifiableMap(my_cvrs_to_audit_by_round);
  }
  
  /**
   * @return the driving contest results for this report.
   */
  public List<CountyContestResult> drivingContestResults() {
    return Collections.unmodifiableList(my_driving_contest_results);
  }
  
  /**
   * @return the list of rounds for this report.
   */
  public List<Round> rounds() {
    return Collections.unmodifiableList(my_rounds);
  }
  
  /**
   * @return the Excel representation of this report, as a byte array.
   * @exception IOException if the report cannot be generated.
   */
  public byte[] generateExcel() 
      throws IOException {
    final Workbook workbook = new XSSFWorkbook();

    // data format
    final DataFormat format = workbook.createDataFormat();
    
    // bold font for titles and such
    final Font bold_font = workbook.createFont();
    bold_font.setFontHeightInPoints(FONT_SIZE);
    bold_font.setBold(true);
    final CellStyle bold_style = workbook.createCellStyle();
    bold_style.setFont(bold_font);
    
    // regular font for other fields
    final Font standard_font = workbook.createFont();
    standard_font.setFontHeightInPoints(FONT_SIZE);
    final CellStyle standard_style = workbook.createCellStyle();
    standard_style.setFont(standard_font);
    final CellStyle integer_style = workbook.createCellStyle();
    integer_style.setFont(standard_font);
    integer_style.setDataFormat(format.getFormat("0"));
    final CellStyle decimal_style = workbook.createCellStyle();
    decimal_style.setFont(standard_font);
    decimal_style.setDataFormat(format.getFormat("0.0000"));
    
    // the summary sheet
    final Sheet summary_sheet = workbook.createSheet("Summary");
    int row_number = 0;
    Row row = summary_sheet.createRow(row_number++);
    int cell_number = 0;
    int max_cell_number = 0;
    
    Cell cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    if (my_dosdb.electionType() == null) {
      cell.setCellValue("ELECTION TYPE NOT SET");
    } else {
      cell.setCellValue(my_dosdb.electionType());
    }
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    if (my_dosdb.electionDate() == null) {
      cell.setCellValue("ELECTION DATE NOT SET");
    } else {
      cell.setCellValue(LocalDate.from(my_dosdb.electionDate()).toString());
    }
    
    max_cell_number = Math.max(max_cell_number, cell_number);
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    cell = row.createCell(cell_number++);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Audited Contests");
    
    for (final CountyContestResult ccr : my_driving_contest_results) {
      row_number++;
      row = summary_sheet.createRow(row_number++);
      cell_number = 1;
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue(ccr.contest().name());
      
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Vote For " + ccr.contest().votesAllowed());
      row = summary_sheet.createRow(row_number++);
      
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Choice");
      
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("W/L");
      
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Votes");
      
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Margin");
      
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Diluted Margin %");
      
      max_cell_number = Math.max(max_cell_number, cell_number);
      for (final String choice : ccr.rankedChoices()) {
        row = summary_sheet.createRow(row_number++);
        cell = row.createCell(cell_number++);
        cell.setCellStyle(standard_style);
        cell.setCellValue(choice);
        
        cell = row.createCell(cell_number++);
        cell.setCellStyle(standard_style);
        if (ccr.winners().contains(choice)) {
          cell.setCellValue("W");
        } else {
          cell.setCellValue("L");
        }
        
        cell = row.createCell(cell_number++);
        cell.setCellStyle(integer_style);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(ccr.voteTotals().get(choice));
        
        cell = row.createCell(cell_number++);
        cell.setCellStyle(integer_style);
        cell.setCellType(CellType.NUMERIC);
        final OptionalInt margin = ccr.marginToNext(choice);
        if (margin.isPresent()) {
          cell.setCellValue(margin.getAsInt());
        }
        
        cell = row.createCell(cell_number++);
        cell.setCellStyle(decimal_style);
        cell.setCellType(CellType.NUMERIC);
        final BigDecimal diluted_margin = ccr.countyDilutedMarginToNext(choice);
        if (diluted_margin != null) {
          cell.setCellValue(diluted_margin.doubleValue() * 100);
        }
      }
    }
    
    for (int i = 0; i < max_cell_number; i++) {
      summary_sheet.autoSizeColumn(i);
    }
    
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    workbook.write(baos);
    baos.flush();
    baos.close();
    Main.LOGGER.info("output stream size: " + baos.size());
    workbook.close();
    return baos.toByteArray();
  }
  
  /**
   * @return the PDF representation of this report, as a byte array.
   */
  public byte[] generatePDF() {
    byte[] result = null;
    
    return result;
  }
}
