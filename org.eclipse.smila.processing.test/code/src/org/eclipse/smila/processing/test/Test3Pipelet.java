/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * SimplePipelet implementation for test. Just logs the given configuration and record IDs.
 *
 * @author jschumacher
 *
 */
public class Test3Pipelet implements SearchPipelet {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(Test3Pipelet.class);

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    _log.info("Test3Pipelet.configure():");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SearchPipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.processing.SearchMessage)
   */
  public SearchMessage process(final Blackboard blackboard, final SearchMessage message)
    throws ProcessingException {
    _log.info("Test3Pipelet.process(search):");
    return message;
  }
}
