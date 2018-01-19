/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 30, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.persistence.PersistenceException;

import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;

/**
 * A Runnable class that provides streaming read access to an UploadedFile.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class UploadedFileStreamer implements Runnable {
  /**
   * The uploaded file.
   */
  private UploadedFile my_file;
  
  /**
   * The input stream from the blob.
   */
  private InputStream my_stream;
  
  /**
   * The running flag.
   */
  @SuppressWarnings("PMD.AvoidUsingVolatile")
  private volatile boolean my_running;
  
  /**
   * Constructs a new streamer for the specified file.
   * 
   * @param the_file The file.
   */
  public UploadedFileStreamer(final UploadedFile the_file) {
    my_file = the_file;
  }
  
  /**
   * The run method. This opens up a new persistence session and database
   * transaction, and sets up the stream for reading. This method should 
   * only be called as a result of Thread.start(), in a fresh thread; any
   * other use may have unpredictable consequences due to the persistence 
   * subsystem's handling of threads.
   * 
   * @exception PersistenceException if there is a problem during the
   * execution.
   */
  @Override
  public synchronized void run() throws PersistenceException {
    my_running = true;
    Persistence.beginTransaction();
    // get a session-local reference to the file
    my_file = Persistence.getByID(my_file.id(), UploadedFile.class);
    // get the blob stream
    try {
      my_stream = my_file.file().getBinaryStream();
      notifyAll();
    } catch (final SQLException e) {
      throw new PersistenceException(e);
    }
    while (my_running) {
      try {
        wait();
      } catch (final InterruptedException e) {
        // ignored, since we don't care if we were interrupted
      }
    }
    try {
      my_stream.close();
    } catch (final IOException e) {
      // ignored, since we're already done with it
    }
    Persistence.rollbackTransaction();
  }
  
  /**
   * Stops this thread, closing the persistence session and the transaction. 
   * The transactions is rolled back, so as to not interfere with any other 
   * transactions, since we have already read all the data we needed.
   */
  public synchronized void stop() {
    my_running = false;
    notifyAll();
  }
  
  /**
   * @return the open binary stream. This stream can only be used once; to
   * read the same uploaded file again, a new UploadedFileStreamer is required. 
   */
  public synchronized InputStream inputStream() {
    while (my_stream == null) {
      try {
        wait();
      } catch (final InterruptedException e) {
        // ignored, since we don't care if we're interrupted
      }
    }
    return my_stream;
  }
}
