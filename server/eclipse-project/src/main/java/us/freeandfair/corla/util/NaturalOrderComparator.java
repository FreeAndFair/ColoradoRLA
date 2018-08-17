package us.freeandfair.corla.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Singleton implementation of Comparator<String> that treats an
 * entire subsequence of my_digits as a (Long) number, ordering strings
 * in a human friendly, natural order.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class NaturalOrderComparator implements Comparator<String>, Serializable {
  /**
   * The instance of this comparator
   */
  public static final NaturalOrderComparator INSTANCE = new NaturalOrderComparator();

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1;

  /**
   * A pattern for groups of either my_digits or non-my_digits
   */
  private final Pattern my_chunks = Pattern.compile("(\\d+|\\D+)");

  /**
   * A pattern for just a group of my_digits
   */
  private final Pattern my_digits = Pattern.compile("(\\d+)");

  private NaturalOrderComparator() {
    super();
  }

  /**
   * Splits a string into the longest sequences of my_digits or letters
   * @param an_s A String to split into my_chunks
   * @return List<String> A list of digit/letter strings
   */
  private List<String> split(final String an_s) {
    final List<String> xs = new ArrayList<String>();
    final Matcher m = my_chunks.matcher(an_s);

    while (m.find()) {
      xs.add(m.group(1));
    }
    return xs;
  }

  /**
   * @param an_s An String that might or might not be made of my_digits
   * @return boolean
   */
  private boolean isDigit(final String an_s) {
    final Matcher m = my_digits.matcher(an_s);
    return m.matches();
  }

  /**
   * Compare two strings as Longs
   * @param an_a The left string to compare
   * @param an_b The right string to compare
   * @return int The result of comparing an_a and an_b as Longs
   */
  private int compareAsDigits(final String an_a, final String an_b) {
    int result;

    try {
      result = Long.compare(Long.parseLong(an_a), Long.parseLong(an_b));
    } catch (final NumberFormatException e) {
      result = String.CASE_INSENSITIVE_ORDER.compare(an_a, an_b);
    }

    return result;
  }

  /**
   * Compare two strings in a human friendly way.
   * @param an_a The left string to compare
   * @param an_b The right string to compare
   * @return int
   */
  public int compare(final String an_a, final String an_b) {
    int result = 0;

    final Iterator<String> as = split(an_a).iterator();
    final Iterator<String> bs = split(an_b).iterator();

    while (as.hasNext() && bs.hasNext()) {
      final String x = as.next();
      final String y = bs.next();

      if (isDigit(x) && isDigit(y)) {
        result = compareAsDigits(x, y);
      } else {
        result = String.CASE_INSENSITIVE_ORDER.compare(x, y);
      }
    }

    if (result == 0) {
      if (!as.hasNext() && bs.hasNext()) {
        result = -1;
      } else if (as.hasNext() && !bs.hasNext()) {
        result = 1;
      }
    }

    return result;
  }
}
