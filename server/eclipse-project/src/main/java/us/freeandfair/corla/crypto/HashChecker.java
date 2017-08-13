/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @creator Joe Kiniry <kiniry@freeandfair.us>
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
   * @param a_filename the file to read.
   * @return the SHA-256 hash of `a_filename`, encoded as a hexadecimal string.
   */
  @SuppressWarnings("checkstyle:emptystatement")
  public static String hashFile(final String a_filename) {
    String result = null;
    try {
      final byte[] buffer = new byte[BUFFER_SIZE];
      final MessageDigest md = MessageDigest.getInstance("SHA-256");
      final File file = new File(a_filename);
      final InputStream is = new FileInputStream(file);
      final DigestInputStream dis = new DigestInputStream(is, md);
      try {
        while (dis.read(buffer) != -1) {
          // skip intentionally to push all bytes through the 
          // DigestInputStream to compute the hash
          ;
        }
        final BigInteger bi = new BigInteger(1, md.digest());
        result = String.format("%0" + (md.digest().length << 1) + "X", bi);
      } finally {
        dis.close();
      }
    } catch (final NoSuchAlgorithmException e) {
      Main.LOGGER.error("No Java security framework installed.");
      Main.LOGGER.info("Unable to compute SHA-256 hashes.");
    } catch (final FileNotFoundException e) {
      Main.LOGGER.warn("File to hash '" + a_filename + 
                       "' disappeared before it could be hashed.");
    } catch (final IOException e) {
      Main.LOGGER.warn("Unable to close file '" + a_filename +
                       "' after hashing it.");
    }
    
    return result;
  }
}
