/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.FileInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.utils.xml.stax.SimpleTagExtractor;

/**
 * The Class TestXmlSnippetSplitter.
 */
public class TestSimpleTagExtractor extends TestCase {

  /**
   * Test XmlSnippetSplitter splitting by "title".
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testSimpleTagExtractor() throws Exception {
    FileInputStream inputStream = new FileInputStream("configuration/data/samplexmldump.xml");
    SimpleTagExtractor extractor = new SimpleTagExtractor(true);
    List<String> titles = extractor.getTags("title", inputStream);
    assertNotNull(titles);
    assertEquals(5, titles.size());
    for (String title : titles) {
      assertNotNull(title);
      assertTrue(title.indexOf("<") < 0);
      assertTrue(title.indexOf(">") < 0);
    }

    inputStream = new FileInputStream("configuration/data/samplexmldump.xml");
    extractor = new SimpleTagExtractor(false);
    titles = extractor.getTags("title", inputStream);
    assertNotNull(titles);
    assertEquals(5, titles.size());
    for (String title : titles) {
      assertNotNull(title);
      assertTrue(title.indexOf("<") >= 0);
      assertTrue(title.indexOf(">") > 0);
    }
  }
}
