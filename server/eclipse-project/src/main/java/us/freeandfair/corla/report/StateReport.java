/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.report;

import static us.freeandfair.corla.util.PrettyPrinter.booleanYesNo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import us.freeandfair.corla.model.AuditSelection;
import us.freeandfair.corla.model.CVRAuditInfo;
import us.freeandfair.corla.model.CastVoteRecord.RecordType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.County.NameComparator;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.Round;
import us.freeandfair.corla.persistence.Persistence;

/**
 * All the data required for a state audit report.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.ExcessiveImports", "PMD.GodClass"})
public class StateReport {
  /**
   * The font size for Excel.
   */
  @SuppressWarnings("PMD.AvoidUsingShortType")
  public static final short FONT_SIZE = 12;

  /**
   * The date formatter.
   */
  private static final DateTimeFormatter DATE_FORMATTER = 
      DateTimeFormatter.ofPattern("MM/dd/yyyy");
  
  /**
   * The date/time formatter.
   */
  private static final DateTimeFormatter DATE_TIME_FORMATTER = 
      DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
  
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
    my_county_reports = new TreeMap<>(new NameComparator());
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
    workbook.close();
    return baos.toByteArray();
  }
  
  /**
   * @return the Excel workbook for this report.
   */
  @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:executablestatementcount",
      "checkstyle:methodlength", "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount",
      "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition"})
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
    final CellStyle bold_right_style = workbook.createCellStyle();
    bold_right_style.setFont(bold_font);
    bold_right_style.setAlignment(HorizontalAlignment.RIGHT);
    
    // regular font for other fields
    final Font standard_font = workbook.createFont();
    standard_font.setFontHeightInPoints(FONT_SIZE);
    final CellStyle standard_style = workbook.createCellStyle();
    standard_style.setFont(standard_font);
    standard_style.setDataFormat(format.getFormat("@"));
    final CellStyle standard_right_style = workbook.createCellStyle();
    standard_right_style.setFont(standard_font);
    standard_right_style.setAlignment(HorizontalAlignment.RIGHT);
    standard_right_style.setDataFormat(format.getFormat("@"));
    final CellStyle integer_style = workbook.createCellStyle();
    integer_style.setFont(standard_font);
    integer_style.setDataFormat(format.getFormat("0"));
    final CellStyle decimal_style = workbook.createCellStyle();
    decimal_style.setFont(standard_font);
    decimal_style.setDataFormat(format.getFormat("0.000#####"));
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
                      DATE_TIME_FORMATTER.
                      format(LocalDateTime.ofInstant(my_timestamp,
                                                     TimeZone.getDefault().toZoneId())));
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    if (my_dosdb.auditInfo().electionType() == null && 
        my_dosdb.auditInfo().electionDate() == null) {
      cell.setCellValue("ELECTION TYPE/DATE NOT SET");
    } else {
      cell.setCellValue(my_dosdb.auditInfo().capitalizedElectionType() + " Election - " +
                        DATE_FORMATTER.
                        format(LocalDateTime.ofInstant(my_dosdb.auditInfo().electionDate(), 
                                                       ZoneOffset.UTC)));
    }
    
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Audit Random Seed");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(standard_right_style);
    cell.setCellValue(my_dosdb.auditInfo().seed());
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Audit Risk Limit");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(decimal_style);
    cell.setCellValue(my_dosdb.auditInfo().riskLimit().doubleValue());
    
    row_number++;
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    int ballots_in_manifests = 0;
    int cvrs_in_export_files = 0;
    for (final CountyReport cr : my_county_reports.values()) {
      ballots_in_manifests += cr.dashboard().ballotsInManifest();
      cvrs_in_export_files += cr.dashboard().cvrsImported();
    }
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total Ballot Cards In Manifests");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);

    cell.setCellValue(ballots_in_manifests);
    
    row = summary_sheet.createRow(row_number++);
    cell_number = 0;
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.STRING);
    cell.setCellStyle(bold_style);
    cell.setCellValue("Total CVRs in CVR Export Files");
    
    cell = row.createCell(cell_number++);
    cell.setCellType(CellType.NUMERIC);
    cell.setCellStyle(integer_style);
    cell.setCellValue(cvrs_in_export_files);
    
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
        cell.setCellValue(cell.getStringCellValue() + " - No Contests Audited");
      } else {  
        if (!e.getValue().rounds().isEmpty()) {
          cell.setCellValue(cell.getStringCellValue() + " - Round Summary");
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(bold_style);
            cell.setCellValue(round.number());
          }
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue("Total");
          
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Ballot Cards Audited");
          int accumulator = 0;
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(round.actualCount());
            accumulator = accumulator + round.actualCount();
          }
          cell = row.createCell(cell_number++);
          cell.setCellStyle(integer_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(accumulator);
          
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Discrepancies (Audited Contests)");
          accumulator = 0;
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            final int discrepancies;
            if (round.discrepancies().containsKey(AuditSelection.AUDITED_CONTEST)) {
              discrepancies = round.discrepancies().get(AuditSelection.AUDITED_CONTEST);
            } else {
              discrepancies = 0;
            }
            cell.setCellValue(discrepancies);
            accumulator = accumulator + discrepancies;
          }
          cell = row.createCell(cell_number++);
          cell.setCellStyle(integer_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(accumulator);
          
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Discrepancies (Non-Audited Contests)");
          accumulator = 0;
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            final int discrepancies;
            if (round.discrepancies().containsKey(AuditSelection.UNAUDITED_CONTEST)) {
              discrepancies = round.discrepancies().get(AuditSelection.UNAUDITED_CONTEST);
            } else {
              discrepancies = 0;
            }
            cell.setCellValue(discrepancies);
            accumulator = accumulator + discrepancies;
          }
          cell = row.createCell(cell_number++);
          cell.setCellStyle(integer_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(accumulator);
          
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Disagreements (Audited Contests)");
          accumulator = 0;
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            final int disagreements;
            if (round.disagreements().containsKey(AuditSelection.AUDITED_CONTEST)) {
              disagreements = round.disagreements().get(AuditSelection.AUDITED_CONTEST);
            } else {
              disagreements = 0;
            }
            cell.setCellValue(disagreements);
            accumulator = accumulator + disagreements;
          }
          cell = row.createCell(cell_number++);
          cell.setCellStyle(integer_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(accumulator);
          
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Disagreements (Non-Audited Contests)");
          accumulator = 0;
          for (final Round round : e.getValue().rounds()) {
            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            final int disagreements;
            if (round.disagreements().containsKey(AuditSelection.UNAUDITED_CONTEST)) {
              disagreements = round.disagreements().get(AuditSelection.UNAUDITED_CONTEST);
            } else {
              disagreements = 0;
            }
            cell.setCellValue(disagreements);
            accumulator = accumulator + disagreements;
          }
          cell = row.createCell(cell_number++);
          cell.setCellStyle(integer_style);
          cell.setCellType(CellType.NUMERIC);
          cell.setCellValue(accumulator);
        }
        
        row_number++;
        row = summary_sheet.createRow(row_number++);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellStyle(bold_style);
        cell.setCellValue(e.getKey().name() + " County - Audited Contests");
        
        row_number = row_number - 1; // don't skip a line before first contest
        
        for (final CountyContestResult ccr : e.getValue().drivingContestResults()) {
          row_number++;
          row = summary_sheet.createRow(row_number++);
          cell_number = 0;
          
          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_style);
          cell.setCellValue(ccr.contest().name() + " - Vote For " + 
                            ccr.contest().votesAllowed());

          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_style);
          cell.setCellValue("Choice");

          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue("W/L");

          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue("Votes");

          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue("Margin");

          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue("Diluted Margin %");

          for (final String choice : ccr.rankedChoices()) {
            row = summary_sheet.createRow(row_number++);
            max_cell_number = Math.max(max_cell_number, cell_number);
            cell_number = 1;
            cell = row.createCell(cell_number++);
            cell.setCellStyle(standard_style);
            cell.setCellValue(choice);

            cell = row.createCell(cell_number++);
            cell.setCellStyle(standard_right_style);
            if (ccr.winners().contains(choice)) {
              cell.setCellValue("W");
            } else {
              cell.setCellValue("L");
            }

            cell = row.createCell(cell_number++);
            cell.setCellStyle(integer_style);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(ccr.voteTotals().get(choice));

            if (ccr.winners().contains(choice)) {
              cell = row.createCell(cell_number++);
              cell.setCellStyle(integer_style);
              cell.setCellType(CellType.NUMERIC);
              final OptionalInt margin = ccr.marginToNearestLoser(choice);
              if (margin.isPresent()) {
                cell.setCellValue(margin.getAsInt());
              }

              cell = row.createCell(cell_number++);
              cell.setCellStyle(decimal_style);
              cell.setCellType(CellType.NUMERIC);
              final BigDecimal diluted_margin = ccr.countyDilutedMarginToNearestLoser(choice);
              if (diluted_margin != null) {
                cell.setCellValue(diluted_margin.doubleValue() * 100);
              }
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
      if (e.getValue().drivingContestResults().isEmpty()) {
        // don't generate empty sheets
        continue;
      }
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
        cell.setCellValue("Number of Ballot Cards Audited");

        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(integer_style);
        cell.setCellValue(round.actualCount());

        row = county_sheet.createRow(row_number++);
        cell_number = 1; // these are headers for audit selections
        final List<AuditSelection> listed_selections = new ArrayList<>();
        final Map<AuditSelection, Integer> discrepancies = round.discrepancies();
        final Map<AuditSelection, Integer> disagreements = round.disagreements();
        
        for (final AuditSelection r : AuditSelection.values()) {
          if (discrepancies.containsKey(r) && discrepancies.get(r) >= 0 || 
              disagreements.containsKey(r) && disagreements.get(r) >= 0) {
            listed_selections.add(r);
          }
        }
        
        Collections.sort(listed_selections);
        
        for (final AuditSelection s : listed_selections) {
          cell = row.createCell(cell_number++);
          cell.setCellStyle(bold_right_style);
          cell.setCellValue(s.prettyString());
        }
        
        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        if (discrepancies.isEmpty()) {
          cell.setCellValue("No Discrepancies Recorded");
        } else {
          cell.setCellValue("Discrepancies Recorded");
          for (final AuditSelection s : listed_selections) {
            cell = row.createCell(cell_number++);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(integer_style);
            final int cell_value;
            if (discrepancies.containsKey(s)) {
              cell_value = discrepancies.get(s);
            } else {
              cell_value = 0;
            }
            cell.setCellValue(cell_value);
          }
        }
        
        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        if (disagreements.isEmpty()) {
          cell.setCellValue("No Disagreements Recorded");
        } else {
          cell.setCellValue("Disagreements Recorded");
          for (final AuditSelection s : listed_selections) {
            cell = row.createCell(cell_number++);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(integer_style);
            final int cell_value;
            if (disagreements.containsKey(s)) {
              cell_value = disagreements.get(s);
            } else {
              cell_value = 0;
            }
            cell.setCellValue(cell_value);
          }
        }
        row_number++;
        row = county_sheet.createRow(row_number++);
        max_cell_number = Math.max(max_cell_number, cell_number);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Ballot Cards Selected");

        row = county_sheet.createRow(row_number++);
        cell_number = 0;
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_style);
        cell.setCellValue("Imprinted ID");
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_right_style);
        cell.setCellValue("Audited");
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_right_style);
        cell.setCellValue("Discrepancy");
        cell = row.createCell(cell_number++);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(bold_right_style);
        cell.setCellValue("Disagreement");

        max_cell_number = Math.max(max_cell_number, cell_number);
        for (final CVRAuditInfo audit_info : 
             e.getValue().cvrsToAuditByRound().get(round.number())) {
          row = county_sheet.createRow(row_number++);
          cell_number = 0;
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(standard_style);
          cell.setCellValue(audit_info.cvr().imprintedID());
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(standard_right_style);
          if (audit_info.acvr() == null) {
            cell.setCellValue(booleanYesNo(false));
          } else {
            cell.setCellValue(booleanYesNo(audit_info.acvr().recordType() == 
                                           RecordType.AUDITOR_ENTERED));
          }
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(standard_right_style);
          cell.setCellValue(booleanYesNo(!audit_info.discrepancy().isEmpty()));
          cell = row.createCell(cell_number++);
          cell.setCellType(CellType.STRING);
          cell.setCellStyle(standard_right_style);
          cell.setCellValue(booleanYesNo(!audit_info.disagreement().isEmpty()));
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
   * @return the filename for the Excel version of this report.
   */
  public String filenameExcel() {
    // the file name should be constructed from the county name, election
    // type and date, and report generation time
    final LocalDateTime election_datetime = 
        LocalDateTime.ofInstant(my_dosdb.auditInfo().electionDate(), ZoneOffset.UTC);
    final LocalDateTime report_datetime = 
        LocalDateTime.ofInstant(my_timestamp, TimeZone.getDefault().toZoneId()).
        truncatedTo(ChronoUnit.SECONDS);
    final StringBuilder sb = new StringBuilder(32);

    sb.append("state-");
    sb.append(my_dosdb.auditInfo().electionType().
              toLowerCase(Locale.getDefault()).replace(" ", "_"));
    sb.append('-');
    sb.append(DATE_FORMATTER.format(election_datetime).replace("/", "-"));
    sb.append("-report-");
    sb.append(DATE_TIME_FORMATTER.format(report_datetime).replace("/", "-").replace(":", "_"));
    sb.append(".xlsx");
    
    return sb.toString();
  }
  
  /**
   * @return the filename for the PDF version of this report.
   */
  public String filenamePDF() {
    return filenameExcel().replaceAll(".xlsx$", ".pdf");
  }
}
