package us.freeandfair.corla.math;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * A static class that should grow to contain audit related mathematical
 * functions that do not belong in models, controllers, or endpoint
 * bodies.
 */
public final class Audit {

  /**
   * Stark's gamma from the literature. As seen in a controller.
   */
  public static BigDecimal GAMMA = BigDecimal.valueOf(1.03905);

  private Audit() {
  }

  /**
   * μ = V / N
   * @param margin the smallest margin of winning, V votes.
   * @param ballotCount N, the number of ballots cast in a contest.
   * @return BigDecimal the diluted margin
   */
  public static BigDecimal dilutedMargin(final Integer margin,
                                         final Long ballotCount) {
    return dilutedMargin(BigDecimal.valueOf(margin),
                         BigDecimal.valueOf(ballotCount));
  }

  /**
   * μ = V / N
   * @param margin the smallest margin of winning, V votes.
   * @param ballotCount N, the number of ballots cast in a contest.
   * @return BigDecimal the diluted margin
   */
  public static BigDecimal dilutedMargin(final BigDecimal margin,
                                         final BigDecimal ballotCount) {
    if (margin == BigDecimal.ZERO || ballotCount == BigDecimal.ZERO) {
      return BigDecimal.ZERO;
    } else {
      return margin.divide(ballotCount, MathContext.DECIMAL128);
    }
  }

  /**
   * Computes the expected number of ballots to audit overall, assuming
   * zero over- and understatements.
   *
   * @param riskLimit as prescribed
   * @param dilutedMargin of the contest.
   *
   * @return the expected number of ballots remaining to audit.
   * This is the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  public static BigDecimal optimistic(final BigDecimal riskLimit,
                                      final BigDecimal dilutedMargin) {
    return optimistic(riskLimit, dilutedMargin, GAMMA,
                      0, 0, 0, 0);
  }

  /**
   * Computes the expected number of ballots to audit overall given the
   * specified numbers of over- and understatements.
   *
   * @param the_two_under The two-vote understatements.
   * @param the_one_under The one-vote understatements.
   * @param the_one_over The one-vote overstatements.
   * @param the_two_over The two-vote overstatements.
   *
   * @return the expected number of ballots remaining to audit.
   * This is the stopping sample size as defined in the literature:
   * https://www.stat.berkeley.edu/~stark/Preprints/gentle12.pdf
   */
  public static BigDecimal optimistic(final BigDecimal riskLimit,
                                      final BigDecimal dilutedMargin,
                                      final BigDecimal gamma,
                                      final int twoUnder,
                                      final int oneUnder,
                                      final int oneOver,
                                      final int twoOver) {

    if (dilutedMargin.compareTo(BigDecimal.ZERO) == 0) { //hilarious
      // nothing to do here, no samples will need to be audited because the
      // contest is uncontested
      return BigDecimal.ZERO;
    }

    final BigDecimal result;
    final BigDecimal invgamma = BigDecimal.ONE.divide(gamma, MathContext.DECIMAL128);
    final BigDecimal twogamma = BigDecimal.valueOf(2).multiply(gamma);
    final BigDecimal invtwogamma =
      BigDecimal.ONE.divide(twogamma, MathContext.DECIMAL128);
    final BigDecimal two_under_bd = BigDecimal.valueOf(twoUnder);
    final BigDecimal one_under_bd = BigDecimal.valueOf(oneUnder);
    final BigDecimal one_over_bd = BigDecimal.valueOf(oneOver);
    final BigDecimal two_over_bd = BigDecimal.valueOf(twoOver);

    final BigDecimal over_under_sum =
      two_under_bd.add(one_under_bd).add(one_over_bd).add(two_over_bd);
    final BigDecimal two_under =
      two_under_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invgamma),
                                               MathContext.DECIMAL128));
    final BigDecimal one_under =
      one_under_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.add(invtwogamma),
                                               MathContext.DECIMAL128));
    final BigDecimal one_over =
      one_over_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invtwogamma),
                                              MathContext.DECIMAL128));
    final BigDecimal two_over =
      two_over_bd.multiply(BigDecimalMath.log(BigDecimal.ONE.subtract(invgamma),
                                              MathContext.DECIMAL128));
    final BigDecimal numerator =
      twogamma.negate().
      multiply(BigDecimalMath.log(riskLimit, MathContext.DECIMAL128).
               add(two_under.add(one_under).add(one_over).add(two_over)));
      final BigDecimal ceil =
        numerator.divide(dilutedMargin,
                           MathContext.DECIMAL128).setScale(0, RoundingMode.CEILING);
      result = ceil.max(over_under_sum);

    return result;
  }



}
