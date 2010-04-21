/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class LoggedComponent.
 */
public abstract class AbstractLoggedComponent {

  /**
   * The _log.
   */
  protected final Log _log = LogFactory.getLog(getClass());

  /**
   * The _id.
   */
  protected final String _id;

  /**
   * Instantiates a new abstract logged component.
   * 
   * @param id
   *          the id
   */
  public AbstractLoggedComponent(final String id) {
    _id = id;
  }

  /**
   * Format message.
   * 
   * @param message
   *          the message
   * 
   * @return the string
   */
  protected String msg(final String message) {
    return String.format("[%s] %s", _id, message);
  }
}
