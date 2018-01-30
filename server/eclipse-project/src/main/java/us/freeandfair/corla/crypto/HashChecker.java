/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import us.freeandfair.corla.Main;

/**
 * Generate a SHA-256 hash of a given file.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class HashChecker {
  /**
   * The size of the buffer that we repeatedly read into to compute the SHA-256
   * of a file.
   */
  public static final int BUFFER_SIZE = 8192;
  
  /**
   * Private constructor to prevent instantiation.
   */
  private HashChecker() {
    // empty
  }
  
  /**
   * @trace cryptography.sha256
   * @param a_filename The name of the file to read.
   * @return the SHA-256 hash of `a_filename`, encoded as a hexadecimal string,
   * or null if the file cannot be hashed.
   */
  public static String hashFile(final String a_filename) {
    return hashFile(new File(a_filename));
  }
  
  /**
   * @trace cryptography.sha256
   * @param a_file the file to read.
   * @return the SHA-256 hash of `a_file`, encoded as a hexadecimal string,
   * or null if the file cannot be hashed.
   */
  public static String hashFile(final File a_file) {
    String result = null;
    try {
      final byte[] buffer = new byte[BUFFER_SIZE];
      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      final InputStream is = new FileInputStream(a_file);
      final DigestInputStream dis = new DigestInputStream(is, md);
      try {
        int bytes;
        do {
          bytes = dis.read(buffer);
        } while (bytes != -1);
        final BigInteger bi = new BigInteger(1, md.digest());
        result = String.format("%0" + (md.digest().length << 1) + "X", bi);
      } finally {
        dis.close();
      }
    } catch (final NoSuchAlgorithmException e) {
      Main.LOGGER.error("No Java security framework installed.");
      Main.LOGGER.info("Unable to compute SHA-256 hashes.");
    } catch (final FileNotFoundException e) {
      Main.LOGGER.warn("File to hash '" + a_file + 
                       "' disappeared before it could be hashed.");
    } catch (final IOException e) {
      Main.LOGGER.warn("Unable to close file '" + a_file +
                       "' after hashing it.");
    }
    
    return result;
  }
}
