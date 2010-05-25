/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.eclipse.smila.search.index.IndexException;

/**
 * The Class SynchronizedExecutor.
 * 
 * @param <InitObject>
 *          class of initialized object.
 * @param <ReturnedType>
 *          class of returned type.
 */
public abstract class SynchronizedAbstractExecutor<InitObject, ReturnedType> {

  /**
   * The Constant INDEX_ACCESS_ATTEMPTS_NUMBER.
   */
  private static final int INDEX_ACCESS_ATTEMPTS_NUMBER = 10;

  /**
   * The Constant INDEX_ACCESS_BASE_PAUSE.
   */
  private static final int INDEX_ACCESS_BASE_PAUSE = 50;

  /**
   * The Constant OPERATION_EXECUTE_ATTEMPTS_NUMBER.
   */
  private static final int OPERATION_EXECUTE_ATTEMPTS_NUMBER = 10;

  /**
   * The Constant OPERATION_EXECUTE_BASE_PAUSE.
   */
  private static final int OPERATION_EXECUTE_BASE_PAUSE = 50;

  /**
   * The _monitored.
   */
  private final Object _monitored;

  /**
   * Instantiates a new synchronized executor.
   * 
   * @param monitored
   *          the monitored
   */
  public SynchronizedAbstractExecutor(final Object monitored) {
    _monitored = monitored;
  }

  /**
   * Execute.
   * 
   * @param condition
   *          the condition
   * @param operation
   *          the operation
   * 
   * @return true, if successful
   * 
   * @throws IndexException
   *           the index exception
   */
  public ReturnedType execute(final ISynchronizedCondition<InitObject> condition,
    final ISynchronizedOperation<InitObject, ReturnedType> operation) throws IndexException {
    // avoiding file-system block
    int i = 0;
    InitObject initObject = null;
    while (initObject == null && i < INDEX_ACCESS_ATTEMPTS_NUMBER) {
      // pause increased from 0 up to 10 * 50 ms
      sleep(i * INDEX_ACCESS_BASE_PAUSE);
      i++;
      synchronized (_monitored) {
        try {
          initObject = condition.initialize();
        } catch (final IndexException e) {
          if (i == INDEX_ACCESS_ATTEMPTS_NUMBER) {
            throw e;
          }
          continue;
        }
        int j = 0;
        boolean isSuccessfully = false;
        while (!isSuccessfully && j < OPERATION_EXECUTE_ATTEMPTS_NUMBER) {
          // pause increased from 0 up to 10 * 50 ms
          sleep(j * OPERATION_EXECUTE_BASE_PAUSE);
          j++;
          final boolean isLast = (j == OPERATION_EXECUTE_ATTEMPTS_NUMBER);
          try {
            final ReturnedType result = operation.process(initObject);
            isSuccessfully = true;
            return result;
          } catch (final IndexException e) {
            if (isLast) {
              throw e;
            }
          }
        }
      }
    }
    throw new IndexException("Unable to initialize object");
  }

  /**
   * Sleep.
   * 
   * @param ms
   *          the ms
   */
  private void sleep(final long ms) {
    if (ms > 0) {
      try {
        Thread.sleep(ms);
      } catch (final InterruptedException e) {
        ;// nothing
      }
    }
  }

  /**
   * Close.
   * 
   * @param object
   *          the object
   * 
   * @throws IndexException
   *           the index exception
   */
  protected abstract void close(InitObject object) throws IndexException;

  /**
   * Execute.
   * 
   * @param operation
   *          the operation
   * 
   * @return the returned type
   * 
   * @throws IndexException
   *           the index exception
   */
  public abstract ReturnedType execute(ISynchronizedOperation<InitObject, ReturnedType> operation)
    throws IndexException;

}
