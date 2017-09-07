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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.apache.poi.ss.usermodel.BorderStyle;
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
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
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
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity", "PMD.ExcessiveImports"})
public class CountyReport {
  /**
   * The affirmation statement.
   */
  public static final String AFFIRMATION_STATEMENT = 
      "We hereby affirm that the results presented in this report\n" + 
      "are accurate to the best of our knowledge.";
  
  /**
   * The font size for Excel.
   */
  @SuppressWarnings("PMD.AvoidUsingShortType")
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
   * The CVRs to audit for each round.
   */
  private final Map<Integer, List<CVRAuditInfo>> my_cvrs_to_audit_by_round;
  
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
    Main.LOGGER.info("driving contests: " + my_cdb.drivingContests());
    for (final CountyContestResult ccr : 
         CountyContestResultQueries.forCounty(my_county)) {
      if (my_cdb.drivingContests().contains(ccr.contest())) {
        my_driving_contest_results.add(ccr);
      }
    }
    my_rounds = my_cdb.rounds();
    my_cvrs_to_audit_by_round = new HashMap<>();
    for (final Round r : my_rounds) {
      final List<CVRAuditInfo> cvrs_to_audit = 
          ComparisonAuditController.cvrsToAuditInRound(my_cdb, r.number());
      cvrs_to_audit.sort(new CVRAuditInfo.BallotOrderComparator());
      Main.LOGGER.info("cvrs to audit: " + cvrs_to_audit);
      my_cvrs_to_audit_by_round.put(r.number(), cvrs_to_audit);
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
  public Map<Integer, List<CVRAuditInfo>> cvrsToAuditByRound() {
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
   * @return the county dashboard for this report.
   */
  public CountyDashboard dashboard() {
    return my_cdb;
  }
  
  /**
   * @return the Excel representation of this report, as a byte array.
   * @exception IOException if the report cannot be generated.
   */
  public byte[] generateExcel() throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final Workbook workbook = generateExcelWorkbook();
    workbook.write(baos);
    baos.flush();
    baos.close();
    Main.LOGGER.info("output stream size: " + baos.size());
    workbook.close();
    return baos.toByteArray();
  }
  
  /**
   * @return the Excel workbook for this report.
   */
  @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:executablestatementcount",
      "checkstyle:methodlength", "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount",
      "PMD.NPathComplexity"})
  public Workbook generateExcelWorkbook() {
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
    final CellStyle box_style = workbook.createCellStyle();
    box_style.setBorderBottom(BorderStyle.THICK);
    box_style.setBorderTop(BorderStyle.THICK);
    box_style.setBorderLeft(BorderStyle.THICK);
    box_style.setBorderRight(BorderStyle.THICK);
    
    // the summary sheet
    final Sheet summary_sheet = workbook.createSheet("Summary");
    int row_number = 0;
    Row row = summary_sheet.createRow(row_number++);
    int cell_number = 0;
    int max_cell_number = 0;
    
    Cell cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue(my_county.name() + " County Audit Report");
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Generated " + 
                      LocalDateTime.ofInstant(my_timestamp,
                                              ZoneOffset.systemDefault()).toString());
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    if (my_dosdb.auditInfo().electionType() == null && 
        my_dosdb.auditInfo().electionDate() == null) {
      cell.setCellValue("ELECTION TYPE/DATE NOT SET");
    } else {
      cell.setCellValue(my_dosdb.auditInfo().electionType() + " - " +
                        LocalDateTime.ofInstant(my_dosdb.auditInfo().electionDate(), 
                                                ZoneOffset.systemDefault()).
                        toLocalDate().toString());
    }
    
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total Ballot Cards In Manifest");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(my_cdb.ballotsInManifest());
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total CVRs in CVR Export File");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(my_cdb.cvrsImported());
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;

    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total Ballot Cards Audited");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(my_cdb.ballotsAudited());
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Number of Audit Rounds");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(my_rounds.size());
    
    if (!my_rounds.isEmpty()) {
      row = summary_sheet.createRow(row_number++);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Ballot Cards Audited by Round");
      for (final Round round : my_rounds) {
        cell = row.createCell(cell_number++);
        cell.setCellStyle(standard_style);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(round.actualCount());
      }
    }
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    cell = row.createCell(cell_number++);
    cell.setCellStyle(bold_style);
    if (my_driving_contest_results.isEmpty()) {
      cell.setCellValue("No Audited Contests");
    } else {
      cell.setCellValue("Audited Contests");
    }
    
    max_cell_number = Math.max(max_cell_number, cell_number);

    for (final CountyContestResult ccr : my_driving_contest_results) {
      row_number++;
      row = summary_sheet.createRow(row_number++);
      cell_number = 0;
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
      
      for (final String choice : ccr.rankedChoices()) {
        row = summary_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
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
    
    row_number++;
    
    for (int i = 0; i < max_cell_number; i++) {
      summary_sheet.autoSizeColumn(i);
    }

    // round sheets
    
    for (final Round round : my_rounds) {
      final Sheet round_sheet = workbook.createSheet("Round " + round.number());
      row_number = 0;
      row = round_sheet.createRow(row_number++);
      cell_number = 0;
      max_cell_number = 0;
      
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Round " + round.number());
      
      row_number++;
      row = round_sheet.createRow(row_number++);
      max_cell_number = Math.max(max_cell_number, cell_number);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Ballot Cards Audited");
      
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.NUMERIC);
      cell.setCellStyle(integer_style);
      cell.setCellValue(round.actualCount());
      
      row = round_sheet.createRow(row_number++);
      
      row = round_sheet.createRow(row_number++);
      cell_number = 1; // these are headers for audit reasons
      final List<AuditReason> listed_reasons = new ArrayList<>();
      final Map<AuditReason, Integer> discrepancies = round.discrepancies();
      final Map<AuditReason, Integer> disagreements = round.disagreements();
      
      for (final AuditReason r : AuditReason.values()) {
        if (discrepancies.containsKey(r) && discrepancies.get(r) >= 0 || 
            disagreements.containsKey(r) && disagreements.get(r) >= 0) {
          listed_reasons.add(r);
        }
      }
      for (final AuditReason r : listed_reasons) {
        cell = row.createCell(cell_number++);
        cell.setCellStyle(bold_style);
        cell.setCellValue(r.toString());
      }
      
      row = round_sheet.createRow(row_number++);
      max_cell_number = Math.max(max_cell_number, cell_number);
      cell_number = 0;      
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Discrepancies Recorded by Audit Reason");

      for (final AuditReason r : listed_reasons) {
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(integer_style);
        final int cell_value;
        if (discrepancies.containsKey(r)) {
          cell_value = discrepancies.get(r);
        } else {
          cell_value = 0;
        }
        cell.setCellValue(cell_value);
      }
      
      row = round_sheet.createRow(row_number++);
      max_cell_number = Math.max(max_cell_number, cell_number);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Disagreements Recorded by Audit Reason");
      
      for (final AuditReason r : listed_reasons) {
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(integer_style);
        final int cell_value;
        if (disagreements.containsKey(r)) {
          cell_value = disagreements.get(r);
        } else {
          cell_value = 0;
        }
        cell.setCellValue(cell_value);
      }
      
      row_number++;
      row = round_sheet.createRow(row_number++);
      max_cell_number = Math.max(max_cell_number, cell_number);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Ballot Cards To Audit");
      
      row = round_sheet.createRow(row_number++);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Imprinted ID");
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Audited");
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Discrepancy");
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Disagreement");
      
      max_cell_number = Math.max(max_cell_number, cell_number);
      for (final CVRAuditInfo audit_info : 
           my_cvrs_to_audit_by_round.get(round.number())) {
        row = round_sheet.createRow(row_number++);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(standard_style);
        cell.setCellValue(audit_info.cvr().imprintedID());
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellStyle(standard_style);
        if (audit_info.acvr() == null) {
          cell.setCellValue(false);
        } else {
          cell.setCellValue(audit_info.acvr().recordType() == RecordType.AUDITOR_ENTERED);
        }
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellStyle(standard_style);
        cell.setCellValue(!audit_info.discrepancy().isEmpty());
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellStyle(standard_style);
        cell.setCellValue(!audit_info.disagreement().isEmpty());
      }
      
      for (int i = 0; i < max_cell_number; i++) {
        round_sheet.autoSizeColumn(i);
      }
    }
    
    // affirmation sheet
    final Sheet affirmation_sheet = workbook.createSheet("Affirmation");
    row_number = 0;
    row = affirmation_sheet.createRow(row_number++);
    cell_number = 0;
    max_cell_number = 0;

    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Affirmation");
    
    cell_number = 0;  
    row_number++;
    row = affirmation_sheet.createRow(row_number++);
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(standard_style);
    cell.setCellValue(AFFIRMATION_STATEMENT);
    
    for (int i = 0; i < 2; i++) {
      cell_number = 0;
      row_number++;
      row = affirmation_sheet.createRow(row_number++);
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue("Audit Board Member");

      cell_number = 0;
      row = affirmation_sheet.createRow(row_number++);
      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(box_style);
      row.setHeight((short) 800);
    }
    
    cell_number = 0;
    row_number++;
    row = affirmation_sheet.createRow(row_number++);
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("County Clerk");

    cell_number = 0;
    row = affirmation_sheet.createRow(row_number++);
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(box_style);
    
    row.setHeight((short) 800);
    
    affirmation_sheet.autoSizeColumn(0);
    
    return workbook;
  }
  
  /**
   * @return the PDF representation of this report, as a byte array.
   */
  public byte[] generatePDF() {
    return new byte[0];
  }
}
