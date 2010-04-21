/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.efs.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Testing multiple concurrent access to Binary Storage service.
 *
 * @author mcimpean
 */
public class TestConcurrentBSSAccessEFS extends DeclarativeServiceTestCase {

  /** Number of r/w accesses per thread. */
  public static final int ACCESS_NUMBER_PER_THREAD = 20;

  /** Threads number constant. */
  private static final int THREADS_NUMBER = 100;

  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(TestConcurrentBSSAccessEFS.class);

  /** Test key shared between test methods. */
  private final String _attacmentIdKey = "2ea9a6a9d6894a29135f90d1724d69d3b6eea0c68d34161ea3c8cd18324874";

  /** BinaryStorageService. */
  private BinaryStorageService _binaryStorageService;

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _binaryStorageService = super.getService(BinaryStorageService.class);

    _log.debug("Executing concurrent access tests for Binary storage Persistence, THREADS_NUMBER :"
      + THREADS_NUMBER + ", ACCESS_NUMBER_PER_THREAD :" + ACCESS_NUMBER_PER_THREAD);
  }

  /**
   * Concurrent Binary Storage Service access.
   *
   * @throws Exception
   *           the exception
   */
  public void testConcurrentAccess() throws Exception {
    final BSSReadWriter[] threadArray = new BSSReadWriter[THREADS_NUMBER];
    // Create and run threads
    for (int counter = 0; counter < threadArray.length; counter++) {
      threadArray[counter] = new BSSReadWriter(counter, _binaryStorageService, _attacmentIdKey);
      threadArray[counter].start();
      Thread.sleep(1); // give it a chance to get running. all at once seems to be too much sometimes.
    }
    // Join all threads
    for (int i = 0; i < threadArray.length; i++) {
      threadArray[i].join();
    }

    // check for any exceptions and throw them to produce junit failure
    for (int i = 0; i < threadArray.length; i++) {
      final List<Exception> errors = threadArray[i].getErrors();
      assertNotNull(errors);
      if (!errors.isEmpty()) {
        throw errors.get(0);
      }
    }

  }
}

/**
 * Utility class for testing multiple concurrent access to Binary Storage Service.
 *
 * @author mcimpean
 */
class BSSReadWriter extends Thread {

  /** Constant part of record content. */
  private static final String TEXT = "Dummy message text for Binary Storage - ";

  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(BSSReadWriter.class);

  /** {@link BinaryStorageService} service reference. */
  private final BinaryStorageService _binaryStorageService;

  /** Based record id, used to generated others. */
  private final String _baseId;

  /** Random generator. */
  private final Random _generator = new Random();

  /** buffer to store any exceptions during run **/
  private final Vector<Exception> _errors = new Vector<Exception>();

  /**
   * Basic constructor.
   *
   * @param binaryStorageService
   *          the binary storage service
   * @param baseId
   *          the base id
   * @param counter
   *          the counter
   */
  BSSReadWriter(final int counter, final BinaryStorageService binaryStorageService, final String baseId) {
    super(baseId + "-" + counter);
    _binaryStorageService = binaryStorageService;
    _baseId = baseId;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    try {
      executeNonRandomCalls();
      executeRandomCalls();
      executeCallsOnSingleRecord();
    } catch (final Exception exception) {
      _log.error("Error in thread :" + getName(), exception);
      _errors.add(exception);
    }
  }

  /**
   * Returns the error list.
   *
   * @return the error list
   */
  public List<Exception> getErrors() {
    return _errors;
  }

  /**
   * Utility test method to execute Binary Storage Service non-random calls.
   *
   * @throws BinaryStorageException
   *           the binary storage exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void executeNonRandomCalls() throws BinaryStorageException, IOException {
    for (int i = 0; i < TestConcurrentBSSAccessEFS.ACCESS_NUMBER_PER_THREAD; i++) {
      final String currentId = _baseId + i;
      final String initFileContent = TEXT + "___" + currentId;
      _log.debug("exec non-random call [thread:" + getName() + ", current id:" + currentId + "]");
      callBSS(currentId, initFileContent);
    }
  }

  /**
   * Utility test method to execute Binary Storage Service random calls.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws BinaryStorageException
   *           the binary storage exception
   */
  private void executeRandomCalls() throws BinaryStorageException, IOException {
    for (int i = 0; i < TestConcurrentBSSAccessEFS.ACCESS_NUMBER_PER_THREAD; i++) {
      final String currentId = _baseId + _generator.nextInt();
      final String initFileContent = TEXT + "..." + currentId;
      _log.debug("exec random call [thread:" + getName() + ", current id:" + currentId + "]");
      callBSS(currentId, initFileContent);
    }
  }

  /**
   * Utility test method to execute Binary Storage Service operations on single record.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws BinaryStorageException
   *           the binary storage exception
   */
  private void executeCallsOnSingleRecord() throws BinaryStorageException, IOException {
    for (int i = 0; i < TestConcurrentBSSAccessEFS.ACCESS_NUMBER_PER_THREAD; i++) {
      final String currentId = _baseId;
      final String initFileContent =
        TEXT + "+++ " + "owned thread :" + getName() + ", step :" + i + " +++" + currentId;
      _log.debug("exec single record call [thread:" + getName() + ", current id:" + currentId + "]");
      callBSS(currentId, initFileContent);
    }
  }

  /**
   * Binary Storage Service calls.
   *
   * @param currentId
   *          - Binary Storage Service id
   * @param initFileContent
   *          - record content
   *
   * @throws BinaryStorageException
   *           the binary storage exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void callBSS(final String currentId, final String initFileContent) throws BinaryStorageException,
    IOException {
    _binaryStorageService.store(currentId, initFileContent.getBytes());
    _binaryStorageService.store(currentId, initFileContent.getBytes());
    _binaryStorageService.fetchAsByte(currentId);
    _binaryStorageService.fetchAsStream(currentId);

    final InputStream inputStream = _binaryStorageService.fetchAsStream(currentId);
    final String returnedContent = convertStreamToString(inputStream);
    _log.debug(getName() + " - " + returnedContent);
    inputStream.close();
    // if (!returnedContent.equals(initFileContent)) {
    // throw new BinaryStorageException("Invalid returned value for thread " + getName() + ". Expected value :"
    // + initFileContent + ", returned value :" + returnedContent);
    // }
  }

  /**
   * Utility method to convert stream to string.
   *
   * @param inputStream
   *          the input stream
   *
   * @return String
   */
  public String convertStreamToString(final InputStream inputStream) {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    final StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (final IOException e) {
      _log.error("Error reading from stream", e);
    } finally {
      try {
        inputStream.close();
      } catch (final IOException e) {
        _log.error("Error closing stream", e);
      }
    }
    return sb.toString();
  }
}
