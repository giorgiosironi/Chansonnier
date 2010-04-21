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

package org.eclipse.smila.datamodel.record;

/**
 * thrown by Literals when trying to set value objects of invalid types.
 * 
 * @author jschumacher
 * 
 */
public class InvalidTypeException extends Exception {

  /**
   * exceptions are serializable ...
   */
  private static final long serialVersionUID = 1L;

  /**
   * create exception with description.
   * 
   * @param message
   *          desciption.
   */
  public InvalidTypeException(String message) {
    super(message);
  }
}
