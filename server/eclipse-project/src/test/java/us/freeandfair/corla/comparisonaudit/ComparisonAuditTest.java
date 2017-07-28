/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 27, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.comparisonaudit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;

import org.testng.annotations.Test;


/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class ComparisonAuditTest {
  @Test()
  public void testnminfromrates() {
    assertEquals(ComparisonAudit.nminfromrates(0.05, 1.03905, .2, .001, .0001, .001, .0001, true, false), 34);
  }
  
  @Test()
  public void testAudit() {
    final ComparisonAudit comparison_audit = new ComparisonAudit(100, BigDecimal.valueOf(.05));
    comparison_audit.addContest("Contest", 1, new HashMap<>());
    assertTrue(comparison_audit.addCandidateVotes("Contest", "Candidate1", 60));
    assertTrue(comparison_audit.addCandidateVotes("Contest", "Candidate2", 40));
    assertTrue(comparison_audit.auditComplete(34, 0, 0, 0, 0));
    assertFalse(comparison_audit.auditComplete(34, 1, 0, 0, 0));
  }
}
