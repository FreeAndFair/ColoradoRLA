package us.freeandfair.corla.util;

import static org.testng.Assert.*;
import org.testng.annotations.*;
import java.util.Comparator;

public class NaturalOrderComparatorTest {
  private Comparator c;

  @BeforeClass()
  public void NaturalOrderComparatorTest() {
      this.c = NaturalOrderComparator.INSTANCE;
  }

  @Test()
  public void compareTest() {
      assertEquals(c.compare("Batch 1", "Batch 11"), -1);
      assertEquals(c.compare("Batch 1", "batch 1"), 0);
      assertEquals(c.compare("Batch 11", "Batch 2"), 1);
      assertEquals(c.compare("Batch 20", "batch 19"), 1);
      assertEquals(c.compare("Batch 123", "Batch 123.a"), -1);
  }
}
