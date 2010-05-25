/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Standard-DOMErrorHandler in AnyFinder. Only the error()-methods are implemented which throw an SAXParseException.
 */
public class DOMErrorHandler implements ErrorHandler {
  /**
   * This method throws an SAXParseException to detect validation errors. The message-text is cuntructed as this:
   * 
   * <pre>
   * e.getMessage() + &quot;\n\t\tPublicId:\t&quot; + e.getPublicId() + &quot;\n\t\tSystemId:\t&quot; + e.getSystemId()
   *   + &quot;\n\t\tLineNumber:\t&quot; + e.getLineNumber() + &quot;\n\t\tColumnNumber:\t&quot; + e.getColumnNumber()
   * </pre>
   * 
   * @param e -
   * @throws SAXException -
   * 
   * @see description in interface
   */
  public void error(SAXParseException e) throws SAXException {
    throw new SAXParseException(
      e.getMessage() + "\n\t\tPublicId:\t" + e.getPublicId() + "\n\t\tSystemId:\t" + e.getSystemId()
        + "\n\t\tLineNumber:\t" + e.getLineNumber() + "\n\t\tColumnNumber:\t" + e.getColumnNumber(), e
        .getPublicId(), e.getSystemId(), e.getLineNumber(), e.getColumnNumber());
  }

  /**
   * This method throws an SAXParseException to detect wellformattedness-errors. The message-text is cuntructed as this:
   * 
   * <pre>
   * e.getMessage() + &quot;\n\t\tPublicId:\t&quot; + e.getPublicId() + &quot;\n\t\tSystemId:\t&quot; + e.getSystemId()
   *   + &quot;\n\t\tLineNumber:\t&quot; + e.getLineNumber() + &quot;\n\t\tColumnNumber:\t&quot; + e.getColumnNumber()
   * </pre>
   * 
   * @param e -
   * @throws SAXException -
   */
  public void fatalError(SAXParseException e) throws SAXException {
    throw new SAXParseException(
      e.getMessage() + "\n\t\tPublicId:\t" + e.getPublicId() + "\n\t\tSystemId:\t" + e.getSystemId()
        + "\n\t\tLineNumber:\t" + e.getLineNumber() + "\n\t\tColumnNumber:\t" + e.getColumnNumber(), e
        .getPublicId(), e.getSystemId(), e.getLineNumber(), e.getColumnNumber());
  }

  /**
   * This method does nothing.
   * 
   * @param e -
   */
  public void warning(SAXParseException e) {
  }

}
