/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.test;

import org.eclipse.smila.processing.pipelets.xmlprocessing.XPathExtractorPipelet;

/**
 * Test the XPathExtractorPipelet.
 */
public class TestXPathExtractorPipelet extends AXPathTest {

  /**
   * Constant for the xml document to extract values from.
   */
  private static final String AUTHOR_XML = "./configuration/data/author.xml";

  /**
   * Expected result.
   */
  private static final String AUTHOR_EMAIL = "redaktion@devmag.net";

  /**
   * The XPathExtractorPipelet.
   */
  private XPathExtractorPipelet _pipelet;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.pipelets.test.APipeletTest#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    _pipelet = new XPathExtractorPipelet();
  }

  /**
   * Test XPathExtractorPipelet with Attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testXSLTbyAttribute() throws Exception {
    final String xmlString = filterByAttribute(_pipelet, "TestXPathExtractorPipeletByAttribute.xml", AUTHOR_XML);
    assertNotNull(xmlString);
    assertEquals(AUTHOR_EMAIL, xmlString);
  }

  /**
   * Test XPathExtractorPipelet with Attachments.
   * 
   * @throws Exception
   *           test fails
   */
  public void testXSLTbyAttachment() throws Exception {
    final String xmlString = filterByAttachment(_pipelet, "TestXPathExtractorPipeletByAttachment.xml", AUTHOR_XML);
    assertNotNull(xmlString);
    assertEquals(AUTHOR_EMAIL, xmlString);
  }
}
