/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator, Ivan Churkin(brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.management.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.smila.utils.conversion.DefaultConversionUtils;

/**
 * The Class ErrorsBuffer.
 */
public class ErrorsBuffer {

  /**
   * The Constant SEPARATOR.
   */
  private static final String SEPARATOR = "--- %s ---\n";

  /**
   * The Constant SEPARATOR.
   */
  private static final String CRITICAL_EXCEPTION_SEPARATOR = "--- %s Critical exception! ---\n";

  /**
   * Default maximum number of errors that buffer can keep.
   */
  private static final int DEFAULT_CAPACITY = 10;

  /**
   * Errors list.
   */
  private final List<String> _errors = new CopyOnWriteArrayList<String>();

  /**
   * Maximum number of errors that buffer can keep.
   */
  private final int _capacity;

  /**
   * Instantiates a new errors buffer with default capacity.
   */
  public ErrorsBuffer() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Instantiates a new errors buffer.
   * 
   * @param capacity
   *          the capacity
   */
  public ErrorsBuffer(final int capacity) {
    _capacity = capacity;
  }

  /**
   * Adds the error.
   * 
   * @param exception
   *          the exception
   */
  public void addError(final Throwable exception) {
    addError(exception, false);
  }

  /**
   * Adds the critical error.
   * 
   * @param exception
   *          the exception
   */
  public void addCriticalError(final Throwable exception) {
    addError(exception, true);
  }

  /**
   * Adds error to the buffer.
   * 
   * @param exception
   *          the exception
   * @param isCritical
   *          the is critical
   */
  public void addError(final Throwable exception, final boolean isCritical) {
    _errors.add(getStackTrace(exception, isCritical));
    if (_errors.size() > _capacity) {
      _errors.remove(0);
    }
  }

  /**
   * Returns the list of errors that are in the buffer. This method is exposed for management.
   * 
   * @return the errors
   */
  public List<String> getErrors() {
    return _errors;
  }

  /**
   * Returns String representation of stacktrace for given Throwable.
   * 
   * @param exception
   *          the exception
   * @param isCritical
   *          the is critical
   * 
   * @return the stack trace
   */
  private String getStackTrace(final Throwable exception, final boolean isCritical) {
    final Writer stackTrace = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stackTrace);
    final String dateString = DefaultConversionUtils.convert(new Date());
    if (isCritical) {
      printWriter.append(String.format(CRITICAL_EXCEPTION_SEPARATOR, dateString));
    } else {
      printWriter.append(String.format(SEPARATOR, dateString));
    }
    exception.printStackTrace(printWriter);
    printWriter.close();
    return stackTrace.toString();
  }
}
