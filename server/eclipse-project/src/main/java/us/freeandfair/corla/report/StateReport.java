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
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.SortedMap;
import java.util.TreeMap;

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
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CVRAuditInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;

/**
 * All the data required for a state audit report.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity", "PMD.ExcessiveImports"})
public class StateReport {
  /**
   * The font size for Excel.
   */
  @SuppressWarnings("PMD.AvoidUsingShortType")
  public static final short FONT_SIZE = 12;
  
  /**
   * The date and time this report was generated.
   */
  private final Instant my_timestamp;
  
  /**
   * The county audit reports.
   */
  private final SortedMap<County, CountyReport> my_county_reports;
  
  /**
   * The DoS dashboard.
   */
  private final DoSDashboard my_dosdb;
  
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
    my_county_reports = new TreeMap<>(new CountyComparator());
    my_timestamp = the_timestamp;
    for (final County c : Persistence.getAll(County.class)) {
      my_county_reports.put(c, new CountyReport(c, my_timestamp));
    }
    my_dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
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
    cell.setCellValue("State Audit Report");
    
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
    if (my_dosdb.electionType() == null && my_dosdb.electionDate() == null) {
      cell.setCellValue("ELECTION TYPE/DATE NOT SET");
    } else {
      cell.setCellValue(my_dosdb.electionType() + " - " +
                        LocalDateTime.ofInstant(my_dosdb.electionDate(), 
                                                ZoneOffset.UTC).toLocalDate().toString());
    }
    
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total Ballot Cards Cast");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    final OptionalLong ballots = 
        CastVoteRecordQueries.countMatching(RecordType.UPLOADED);
    if (ballots.isPresent()) {
      cell.setCellValue(ballots.getAsLong());
    }
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    int ballots_audited = 0;
    int audit_rounds = 0;
    for (final CountyReport cr : my_county_reports.values()) {
      ballots_audited = ballots_audited + cr.dashboard().ballotsAudited();
      audit_rounds = Math.max(audit_rounds, cr.dashboard().rounds().size());
    }
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total Ballot Cards Audited");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(ballots_audited);
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Number of Audit Rounds");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(audit_rounds);
    
    max_cell_number = Math.max(max_cell_number, cell_number);

    for (final Entry<County, CountyReport> e : my_county_reports.entrySet()) {
      row_number++;
      row = summary_sheet.createRow(row_number++);
      cell_number = 0;
      cell = row.createCell(cell_number++);
      cell.setCellStyle(bold_style);
      cell.setCellType(CellType.STRING);
      cell.setCellValue(e.getKey().name() + " County");
      
      if (e.getValue().drivingContestResults().isEmpty()) {
        cell = row.createCell(cell_number++);
        cell.setCellStyle(bold_style);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("No Contests Audited");
      } else {  
        cell.setCellValue(cell.getStringCellValue() + " - Ballot Cards Audited by Round");
        for (final Round round : e.getValue().rounds()) {
          cell = row.createCell(cell_number++);
          cell.setCellStyle(standard_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(round.actualCount());
        }

        row = summary_sheet.createRow(row_number++);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Audited Contests");
        
        for (final CountyContestResult ccr : e.getValue().drivingContestResults()) {
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
      }
    }
    
    for (int i = 0; i < max_cell_number; i++) {
      summary_sheet.autoSizeColumn(i);
    }
    
    // county sheets
    
    for (final Entry<County, CountyReport> e : my_county_reports.entrySet()) {
      final Sheet county_sheet = workbook.createSheet(e.getKey().name() + " County");
      row_number = 0;
      row = county_sheet.createRow(row_number++);
      cell_number = 0;
      max_cell_number = 0;

      cell = row.createCell(cell_number++);
      cell.setCellType(CellType.STRING);
      cell.setCellStyle(bold_style);
      cell.setCellValue(e.getKey().name() + " County Summary Report");
      for (final Round round : e.getValue().rounds()) {
        row_number++;
        row = county_sheet.createRow(row_number++);
        cell_number = 0;
        max_cell_number = 0;

        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Round " + round.number());

        row_number++;
        row = county_sheet.createRow(row_number++);
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

        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Discrepancies Recorded");

        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(integer_style);
        cell.setCellValue(round.discrepancies());

        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Disagreements Recorded");

        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(integer_style);
        cell.setCellValue(round.disagreements());

        row_number++;
        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Ballot Cards To Audit");

        row = county_sheet.createRow(row_number++);
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
        for (final CastVoteRecord cvr : 
             e.getValue().cvrsToAuditByRound().get(round.number())) {
          final List<CVRAuditInfo> audit_info_list = 
              CVRAuditInfoQueries.matching(e.getValue().dashboard(), cvr);
          if (audit_info_list == null || audit_info_list.isEmpty()) {
            continue;
          }
          final CVRAuditInfo audit_info = audit_info_list.get(0);
          row = county_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(standard_style);
          cell.setCellValue(cvr.imprintedID());
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.BOOLEAN);
          cell.setCellStyle(standard_style);
          cell.setCellValue(audit_info.acvr().recordType() == RecordType.AUDITOR_ENTERED);
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.BOOLEAN);
          cell.setCellStyle(standard_style);
          cell.setCellValue(audit_info.discrepancy());
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.BOOLEAN);
          cell.setCellStyle(standard_style);
          cell.setCellValue(audit_info.disagreement());
        }
      }
      for (int i = 0; i < max_cell_number; i++) {
        county_sheet.autoSizeColumn(i);
      }
    }
    
    return workbook;
  }
  
  /**
   * @return the PDF representation of this report, as a byte array.
   */
  public byte[] generatePDF() {
    return new byte[0];
  }
  
  /**
   * A comparator to sort County objects alphabetically by county name.
   */
  @SuppressWarnings("PMD.AtLeastOneConstructor")
  public static class CountyComparator 
      implements Serializable, Comparator<County> {
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = 1;
    
    /**
     * Orders two County objects lexicographically by county name.
     * 
     * @param the_first The first response.
     * @param the_second The second response.
     * @return a positive, negative, or 0 value as the first response is
     * greater than, equal to, or less than the second, respectively.
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    public int compare(final County the_first, 
                       final County the_second) {
      final int result;
      if (the_first == null && the_second == null) {
        result = 0;
      } else if (the_first == null || the_first.name() == null) {
        result = -1;
      } else if (the_second == null || the_second.name() == null) {
        result = 1;
      } else {
        result = the_first.name().compareTo(the_second.name());
      }
      return result;
    }
  }
}
