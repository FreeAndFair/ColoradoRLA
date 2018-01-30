/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 4, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class of helper methods for dealing with files.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class FileHelper {
  /**
   * Private constructor to prevent instantiation.
   */
  private FileHelper() {
    // empty
  }
  
  /**
   * Perform a buffered copy from the input stream to the output stream, up 
   * to a maximum number of bytes.
   * 
   * @param the_input_stream The input stream.
   * @param the_output_stream The output stream.
   * @param the_buffer_size The buffer size.
   * @param the_max_bytes The maximum number of bytes to copy.
   */
  public static int bufferedCopy(final InputStream the_input_stream,
                                 final OutputStream the_output_stream,
                                 final int the_buffer_size, final int the_max_bytes) 
      throws IOException {
    final byte[] buffer = new byte[the_buffer_size];
    int length = 1;
    int total = 0;
    while (total < the_max_bytes && length > 0) {
      length = the_input_stream.read(buffer);
      if (length > 0) {
        the_output_stream.write(buffer, 0, length);
        total = total + length;
      }
    }
    return total;
  }
}
