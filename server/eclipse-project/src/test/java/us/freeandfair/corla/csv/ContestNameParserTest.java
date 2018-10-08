package us.freeandfair.corla.csv;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.testng.annotations.*;

public class ContestNameParserTest {
  private String byteOrderMark;
  private String csvWithBOM;
  private String simpleCSV;
  private String dupesInCSV;
  private String malformedCSV;

  @BeforeClass()
  public void ContestNameParserTest() {
    simpleCSV = "CountyName,ContestName\n" +
      "boulder,IPA\nboulder,kombucha\nboulder,coffee\n" +
      "denver,IPA\ndenver,stout\ndenver,coffee";

    byteOrderMark = "﻿";

    csvWithBOM = byteOrderMark + "CountyName,ContestName\n" +
      "boulder,IPA\nboulder,kombucha\nboulder,coffee\n" +
      "denver,IPA\ndenver,stout\ndenver,coffee";

    dupesInCSV = "CountyName,ContestName\n" +
      "boulder,IPA\nboulder,kombucha\nboulder,coffee\n" +
      "denver,IPA\ndenver,IPA\ndenver,coffee";

    malformedCSV = "County Name\",Contest Name\t\nboulder,\n";
  }

  @Test()
  public void parseTest() {
    try {
      ContestNameParser p = new ContestNameParser(simpleCSV);
      boolean successfulP = p.parse();

      assertTrue(successfulP, "A parser returns its successfullness");

      assertEquals(p.contestCount(),
                   OptionalInt.of(6),
                   "Boulder and Denver have six potentially-shared contests");
    } catch (IOException ioe) { fail("Edge case", ioe); }
  }

  @Test()
  public void parseBOMTest() {
    try {
      ContestNameParser p = new ContestNameParser(csvWithBOM);
      boolean successfulP = p.parse();

      assertEquals(p.contestCount(),
                   OptionalInt.of(6),
                   "A Byte Order Mark isn't a dealbreaker");
    } catch (IOException ioe) { fail("Edge case", ioe); }
  }

  @Test()
  public void duplicatesTest() {
    try {
      ContestNameParser p = new ContestNameParser(dupesInCSV);
      boolean successfulP = p.parse();

      Map<String,
        Set<String>>
        expectedDupes = new TreeMap<String, Set<String>>();

      Set<String>
        duplicates = new TreeSet<String>();

      duplicates.add("IPA");
      expectedDupes.put("denver", duplicates);

      assertEquals(p.contestCount(),
                   OptionalInt.of(5),
                   "missing stout by way of a double IPA");

      assertEquals(p.duplicates(),
                   expectedDupes,
                   "duplicates are collected by county");
    } catch (IOException ioe) { fail("Edge case", ioe); }
  }

  @Test
  public void errorsTest() {
    try {
      ContestNameParser p = new ContestNameParser(malformedCSV);
      boolean successfulP = p.parse();

      assertFalse(successfulP, "A malformed CSV is considered a failure");
      assertFalse(p.errors().isEmpty(), "Parse failure results in errors");

      assertEquals(p.errors().first().toString(),
                   "malformed record: (CSVRecord [comment=null, mapping={CountyName=0, ContestName=1}, recordNumber=1, values=[boulder, ]]) on line 2",
                   "Line two is missing a contest name value");
    } catch (Exception e) { fail("Edge case", e);}
  }
}
