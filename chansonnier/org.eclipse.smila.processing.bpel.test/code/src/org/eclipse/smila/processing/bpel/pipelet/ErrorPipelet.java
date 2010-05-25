/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel.pipelet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * combined SimplePipelet and ProcessingService implementation for error handling tests. Simulates different error cases
 * depending on named value "mode" of annotation "org.eclipse.smila.processing.bpel.test.ErrorPipelet" of root metadata
 * object of first ID.
 * 
 * @author jschumacher
 * 
 */
public class ErrorPipelet implements SimplePipelet, ProcessingService {
  /**
   * name of annotation configuring the type of error.
   */
  public static final String NAME_MODE = "mode";

  /**
   * delay longer than timeout configured in processor.properties (currently 3 sec.).
   */
  private static final int DELAY_LONGER_THAN_TIMEOUT = 15000;

  /**
   * Types of errors this pipelet can produce.
   */
  public enum ErrorMode {
    /**
     * throw a ProcessingException.
     */
    PROCESSING,
    /**
     * throw a RuntimeException.
     */
    RUNTIME,
    /**
     * return an ID of a record not on blackboard.
     */
    INVALID_ID,
    /**
     * return a null entry in the result array.
     */
    NULL_ID,
    /**
     * return null as result array.
     */
    NULL_RESULT,
    /**
     * pipeline timeout.
     */
    TIMEOUT
  };

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(ErrorPipelet.class);

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    _log.info("ErrorPipelet.configure()");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    _log.info("ErrorPipelet.process():");
    try {
      final Annotation pipeletAnnotation = blackboard.getAnnotation(recordIds[0], null, getClass().getName());
      if (pipeletAnnotation != null) {
        final String errorModeValue = pipeletAnnotation.getNamedValue(NAME_MODE);
        final ErrorMode errorMode = ErrorMode.valueOf(errorModeValue);
        switch (errorMode) {
          case PROCESSING:
            throw new ProcessingException("This is a ProcessingException.");
          case RUNTIME:
            throw new NullPointerException("This is a NullPointerException.");
          case INVALID_ID:
            final Id invalid = IdFactory.DEFAULT_INSTANCE.createId("ErrorPipelet", "invalid");
            // just to be sure.
            blackboard.invalidate(invalid);
            return new Id[] { invalid };
          case NULL_ID:
            return new Id[1];
          case NULL_RESULT:
            return null;
          case TIMEOUT:
            try {
              // processor.properties configures a 3-second timeout, so we wait for 5 here.
              Thread.sleep(DELAY_LONGER_THAN_TIMEOUT);
            } catch (InterruptedException e) {
              _log.error("interrupted from generating a timeout error ... maybe tests fails.");
            }
            return recordIds;
          default:
            return recordIds;
        }
      }
    } catch (BlackboardAccessException ex) {
      _log.error("error accessing blackboard", ex);
    }
    return recordIds;
  }
}
