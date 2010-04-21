/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.test;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.eclipse.smila.processing.pipelets.ATransformationPipelet;
import org.eclipse.smila.processing.pipelets.xmlprocessing.XPathFilterPipelet;

/**
 * Test the XPathExtractorPipelet.
 */
public class TestXPathFilterPipelet extends AXPathTest {

  /**
   * Constant for the xml document to extract values from.
   */
  private static final String AUTHOR_XML = "./configuration/data/author.xml";

  /**
   * Expected result for include filter mode.
   */
  private static final String AUTHOR_INCLUDE_XML = "./configuration/data/author_include.xml";

  /**
   * Expected result for exclude filter mode.
   */
  private static final String AUTHOR_EXCLUDE_XML = "./configuration/data/author_exclude.xml";

  /**
   * The XPathFilterPipelet.
   */
  private XPathFilterPipelet _pipelet;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.pipelets.test.APipeletTest#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    _pipelet = new XPathFilterPipelet();
  }

  /**
   * Test XPathFilterPipelet include with Attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testIncludeFilterByAttribute() throws Exception {
    final String xmlString = filterByAttribute(_pipelet, "TestXPathFilterPipeletIncByAttribute.xml", AUTHOR_XML);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_INCLUDE_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(xmlString, expected);
    assertTrue(diff.identical());
  }

  /**
   * Test XPathFilterPipelet include with Attachments.
   * 
   * @throws Exception
   *           test fails
   */
  public void testIncludeFilterByAttachment() throws Exception {
    final String xmlString = filterByAttachment(_pipelet, "TestXPathFilterPipeletIncByAttachment.xml", AUTHOR_XML);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_INCLUDE_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(xmlString, expected);
    assertTrue(diff.identical());
  }

  /**
   * Test XPathFilterPipelet exclude with Attributes.
   * 
   * @throws Exception
   *           test fails
   */
  public void testExcludeFilterbyAttribute() throws Exception {
    final String xmlString = filterByAttribute(_pipelet, "TestXPathFilterPipeletExByAttribute.xml", AUTHOR_XML);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_EXCLUDE_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(xmlString, expected);
    assertTrue(diff.identical());
  }

  /**
   * Test XPathFilterPipelet exclude with Attachments.
   * 
   * @throws Exception
   *           test fails
   */
  public void testExcludeFilterByAttachment() throws Exception {
    final String xmlString = filterByAttachment(_pipelet, "TestXPathFilterPipeletExByAttachment.xml", AUTHOR_XML);
    final String expected =
      removeBOM(IOUtils.toString(new FileInputStream(AUTHOR_EXCLUDE_XML),
        ATransformationPipelet.ENCODING_ATTACHMENT));
    final Diff diff = XMLUnit.compareXML(xmlString, expected);
    assertTrue(diff.identical());
  }
}
