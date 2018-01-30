/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 26, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * A test case for the PseudoRandomNumberGenerator.
 *
 * @author Joey Dodds <jdodds@galois.com>
 * @version 1.0.0
 */
// TestNG classes do not need constructors
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class HashCheckerTest {
  /**
   * A single test case that simply hashes a file containing the characters
   * "ColoradoRLA".
   */
  @Test()
  public void testHashing() {
    try {
    final String test_string = "ColoradoRLA";
    final Instant now = Instant.now();
    final File test_file = File.createTempFile("to_hash-" + now, "txt");
    final FileOutputStream fos = new FileOutputStream(test_file);
    fos.write(test_string.getBytes());
    fos.close();
    assertEquals("F9A25DA7060735572E32FCF72C33EE73476E589F7F02256DAFFB4C618D8F9EA2", 
                 HashChecker.hashFile(test_file));
    assertEquals("F9A25DA7060735572E32FCF72C33EE73476E589F7F02256DAFFB4C618D8F9EA2", 
                 HashChecker.hashFile(test_file.toString()));
    } catch (final IOException e) {
      fail();
    }
  }
}
