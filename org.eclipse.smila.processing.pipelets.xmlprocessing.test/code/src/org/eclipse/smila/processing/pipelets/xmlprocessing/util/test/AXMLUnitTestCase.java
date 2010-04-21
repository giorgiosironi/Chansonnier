/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.util.test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

/**
 * An abstract TestCase that uses XMLUnit. 
 */
public abstract class AXMLUnitTestCase extends TestCase {

  /**
   * The Logger.
   */
  protected final Log _log = LogFactory.getLog(getClass());

  /**
   * Compares 2 Documents.
   * 
   * @param first
   *          a Document
   * @param second
   *          a Document
   * @return true if the Documents are equal, false otherwise
   */
  public boolean compareXmlDocuments(Document first, Document second) {
    XMLUnit.setIgnoreWhitespace(true);
    final Diff diff = XMLUnit.compareXML(first, second);
    System.out.println("diff: " + diff);
    return diff.identical();
  }

  /**
   * Compares 2 byte[].
   * 
   * @param first
   *          a byte[]
   * @param second
   *          a byte[]
   * @return true if the byte[] are equal, false otherwise
   */
  public boolean compareBytes(byte[] first, byte[] second) {
    if (first.length != second.length) {
      return false;
    }

    for (int i = 0; i < first.length; i++) {
      if (first[i] != second[i]) {
        return false;
      }
    }
    return true;
  }
}
