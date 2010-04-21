/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

/**
 * XmlException.
 */
public class XmlException extends Exception {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default Constructor.
   */
  public XmlException() {
    super();
  }

  /**
   * Conversion Constructor.
   * @param msg the message
   * @param t the Throwable
   */
  public XmlException(String msg, Throwable t) {
    super(msg, t);
  }

  /**
   * Conversion Constructor.
   * @param msg the message
   */
  public XmlException(String msg) {
    super(msg);
  }

  /**
   * Conversion Constructor.
   * @param t the Throwable
   */
  public XmlException(Throwable t) {
    super(t);
  }
}
