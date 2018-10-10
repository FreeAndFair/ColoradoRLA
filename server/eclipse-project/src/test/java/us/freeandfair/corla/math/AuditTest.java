package us.freeandfair.corla.math;


import java.math.BigDecimal;

import org.testng.annotations.Test;
import org.testng.Assert;


public final class AuditTest {

  @Test()
  public void testOptimistic() {

    final BigDecimal riskLimit = BigDecimal.valueOf(0.5);
    final BigDecimal dilutedMargin = BigDecimal.valueOf(0.05);
    final BigDecimal gamma = BigDecimal.valueOf(1.2);
    final int twoUnder = 0;
    final int oneUnder = 0;
    final int oneOver = 0;
    final int twoOver = 0;

    BigDecimal result = Audit.optimistic(riskLimit,
                                         dilutedMargin,
                                         gamma,
                                         twoUnder,
                                         oneUnder,
                                         oneOver,
                                         twoOver);
    Assert.assertEquals(34, result.intValue());
  }

}
