package us.freeandfair.corla.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.IOUtils;

/**
 * A simple CSV parser built atop commons.csv
 *
 */

public class ContestNameParser {
  /**
   * A CSVParser
   */
  private final CSVParser parser;

  /**
   * The format of our CSV
   */
  private final CSVFormat csvFormat =
    CSVFormat
    .DEFAULT
    .withHeader("CountyName", "ContestName")
    .withSkipHeaderRecord(true);

  /**
   * A mapping of county to contests
   */
  private final Map<String, Set<String>> contests = new TreeMap<String, Set<String>>();

  /**
   * A mapping of county to those duplicate contests
   */
  private final Map<String, Set<String>> duplicates = new TreeMap<String, Set<String>>();

  /**
   * A set to hold our ParseErrors in line order
   */
  private final SortedSet<ParseError> errors = new TreeSet<ParseError>();

  /**
   * A ContestNameParser can be built for a Reader
   */
  public ContestNameParser(final Reader r)
    throws IOException {
    final Reader reader = new InputStreamReader(new BOMInputStream(new ReaderInputStream(r)), "UTF-8");
    parser = new CSVParser(reader, csvFormat);
  }

  /**
   * A ContestNameParser can be built for a String
   */
  public ContestNameParser(final String string)
    throws IOException {
    final Reader reader = new InputStreamReader(new BOMInputStream(IOUtils.toInputStream(string)), "UTF-8");
    parser = new CSVParser(reader, csvFormat);
  }

  /**
   * a good practice
   */
  @Override
  public String toString() {
    return String.format("[contests=%s; duplicates=%s; errors=%s]",
                         contests(),
                         duplicates(),
                         errors());
  }
  /**
   * @return OptionalInt Maybe the number of contests we found across
   * all counties, maybe not.
   */
  public OptionalInt contestCount() {
    return contests
      .values()
      .stream()
      .mapToInt((x) -> {
          return x.size();
        })
      .reduce((a, b) -> {
          return a + b;
        });
  }
  /**
   * @return Map<String, Set<String>> A map of county to
   * contests-within-county, sans duplicates.
   */
  public Map<String, Set<String>> contests() {
    return contests;
  }

  /**
   * @return Map<String, Set<String>> A map of county to
   * duplicate-contests-within-county.
   */
  public Map<String, Set<String>> duplicates() {
    return duplicates;
  }

  /**
   * @return SortedSet<ParseError> Any ParserErrors that may have
   * occured, in line order.
   */

  public SortedSet<ParseError> errors() {
    return errors;
  }

  /**
   * @return boolean Did the parser complete successfully?
   */
  public boolean isSuccess() {
    return errors.isEmpty() && duplicates.isEmpty();
  }

  /**
   * Execute the parser, returning whether or not it was successful.
   * TODO pure function?
   * FIXME cyclomatic complexity
   */
  public synchronized boolean parse() {
    final Iterable<CSVRecord> records = parser;

    try {
      for (final CSVRecord record : records) {
        final String countyName = record.get("CountyName");
        final String contestName = record.get("ContestName");

        if (countyName.isEmpty() || contestName.isEmpty()) {
          errors.add(new ParseError("malformed record: (" + record + ")",
                                    parser.getCurrentLineNumber()));
          break;
        } else {
          // TODO extract-fn

          // In Java 10+, we'd use TreeSet.of(contestName) and
          // be happy. Until we leave Java 8, we're stuck with
          // this idiom, right?
          final Set<String> contest = new TreeSet<String>();
          contest.add(contestName);

          contests.merge(countyName, contest, (s1, s2) -> {
              // Accumulate the contests
              final Set<String> union = new TreeSet<String>(s1);
              union.addAll(s2);

              // Keep track up duplicates
              final Set<String> intersection = new TreeSet<String>(s1);
              intersection.retainAll(s2);

              if (!intersection.isEmpty()) {
                duplicates.merge(countyName, intersection, (d1, d2) -> {
                    final Set<String> u2 = new TreeSet<String>(d1);
                    u2.addAll(d2);
                    return u2;
                  });
              }

              return union;
            });
        }
      }
    } catch (final NoSuchElementException e) {
      errors.add(new ParseError("Could not parse contests file",
                                parser.getCurrentLineNumber(), e));
    }

    return this.isSuccess();
  }

  /**
   * A ParseError is an object that we can put in a sorted set
   */
  public static class ParseError implements Comparable<ParseError> {
    /**
     * a message about the ParseError
     */
    private final String msg;

    /**
     * The line on which the error occured. In some cases - where a
     * CSV field contains linebreaks - the line number may be
     * nonsensical. For single line records, you're OK.
     */
    private final long line;

    /**
     * The exception related to a ParseError, if any.
     */
    private final Optional<Exception> e;

    /**
     * A ParseError can be just a message and a line number.
     */
    ParseError(final String msg, final long n) {
      this.msg = msg;
      this.line = n;
      this.e = Optional.empty();
    }

    /**
     * A ParseError can be a message, line number, and an Exception.
     */
    ParseError(final String msg, final long n, final Exception e) {
      this.msg = msg;
      this.line = n;
      this.e = Optional.of(e);
    }

    /**
     * a good practice
     */
    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }

      if (!(o instanceof ParseError)) {
        return false;
      }

      final ParseError pe = (ParseError) o;
      return pe.line == this.line && pe.msg == this.msg;
    }

    /**
     * a good practice
     */
    @Override
    public int hashCode() {
      return Long.hashCode(line) + 31 + msg.hashCode();
    }

    /**
     * ParseErrors are comparable by the tuple [line number, message].
     */
    public int compareTo(final ParseError pe) {
      if (this == pe) {
        return 0;
      }

      int result = Long.compare(this.line, pe.line);
      if (result == 0) {
        result = String.CASE_INSENSITIVE_ORDER.compare(this.msg, pe.msg);
      }

      return result;
    }

    /**
     * @return Optional<Exception> e Maybe the exception thrown,
     * maybe not.
     */
    public Optional<Exception> getException() {
      return e;
    }

    /**
     * @return long The line number of the ParseError
     */
    public long getLine() {
      return line;
    }

    /**
     * @return String A printable representation
     */
    @Override
    public String toString() {
      if (e.isPresent()) {
        return msg + " on line " + line + ". Exception: " + e;
      } else {
        return msg + " on line " + line;
      }
    }
  }
}
