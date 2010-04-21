/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.eclipse.smila.utils.xml.stax.MarkerTag;
import org.eclipse.smila.utils.xml.stax.XmlSnippetHandler;
import org.eclipse.smila.utils.xml.stax.XmlSnippetSplitter;

/**
 * The Class TestXmlSnippetSplitter.
 */
public class TestXmlSnippetSplitter extends TestCase implements XmlSnippetHandler {

  /**
   * boolean flag.
   */
  private boolean _testTitle;

  /**
   * {@inheritDoc}
   */
  public void handleSnippet(final byte[] snippet) {
    try {
      assertNotNull(snippet);
      final String xml = new String(snippet);
      assertNotNull(xml);
      if (_testTitle) {
        xml.startsWith("<title>");
        xml.endsWith("</title>");
      } else {
        xml.startsWith("<page>");
        xml.endsWith("</page>");
      }
    } catch (Exception e) {
      fail("unexpected exception " + e.getMessage());
    }
  }

  /**
   * Test XmlSnippetSplitter splitting by "page".
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testReadPages() throws Exception {
    final MarkerTag begin = new MarkerTag("page", false);
    final MarkerTag end = new MarkerTag("page", true);
    final XmlSnippetSplitter reader = new XmlSnippetSplitter(this, begin, end);
    final FileInputStream in = new FileInputStream("configuration/data/samplexmldump.xml");
    reader.read(in);
  }

  /**
   * Test XmlSnippetSplitter splitting by "title".
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testReadTitles() throws Exception {
    _testTitle = true;
    final MarkerTag begin = new MarkerTag("title", false);
    final MarkerTag end = new MarkerTag("title", true);
    final XmlSnippetSplitter reader = new XmlSnippetSplitter(this, begin, end);
    final FileInputStream in = new FileInputStream("configuration/data/samplexmldump.xml");
    reader.read(in);
  }
}
