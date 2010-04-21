/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.processing.parameters;

import org.eclipse.smila.processing.ProcessingException;

/**
 * exception thrown by {@link ParameterAccessor} on access to missing required parameters.
 *
 * @author jschumacher
 *
 */
public class MissingParameterException extends ProcessingException {

  /**
   * exception are serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param message
   *          description
   */
  public MissingParameterException(final String message) {
    super(message);
  }

}
