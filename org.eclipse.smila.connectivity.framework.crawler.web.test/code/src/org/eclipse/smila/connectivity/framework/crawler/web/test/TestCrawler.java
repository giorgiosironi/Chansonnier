/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.web.WebCrawler;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FieldAttributeType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.MetaType;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parser;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParserManager;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParserManagerImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HtmlParser;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.js.JavascriptParserImpl;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestCrawler.
 */
public class TestCrawler extends DeclarativeServiceTestCase {

  /**
   * The Crawler.
   */
  private WebCrawler _crawler;

  /**
   * Parser manager.
   */
  private ParserManager _parserManager;

  /**
   * Html Parser.
   */
  private Parser _htmlParser;

  /**
   * Javascript Parser.
   */
  private Parser _javascriptParser;

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    // register the service, because it's configuration uses immediate="false"
    _crawler = registerService(new WebCrawler(), null, WebCrawler.class, 0);
    _parserManager = registerService(new ParserManagerImpl(), null, ParserManagerImpl.class, 0);
    _htmlParser = registerService(new HtmlParser(), null, HtmlParser.class, 0);
    _javascriptParser = registerService(new JavascriptParserImpl(), null, JavascriptParserImpl.class, 0);

    assertNotNull(_crawler);
    assertNotNull(_parserManager);
    assertNotNull(_htmlParser);
    assertNotNull(_javascriptParser);

    _parserManager.addParser(_htmlParser);
    _parserManager.addParser(_javascriptParser);

    _crawler.setParserManager(_parserManager);

  }

  /**
   * Test crawler.
   *
   * @throws Exception
   *           the exception
   */
  public void testCrawler() throws Exception {
    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("ConfigExample.xml"));
    assertNotNull(config);

    _crawler.initialize(config);

    final long start = System.currentTimeMillis();
    int counter = 0;
    DataReference[] diObjects;
    while ((diObjects = _crawler.getNext()) != null) {
      if (diObjects.length == 0) {
        break;
      }
      for (int i = 0; i < diObjects.length; i++) {
        final Record record = diObjects[i].getRecord();
        assertNotNull(record);
        assertNotNull(record.getMetadata().getAttribute(FieldAttributeType.URL.value()));
        assertNotNull(record.getMetadata().getAttribute(FieldAttributeType.TITLE.value()));
        assertNotNull(record.getAttachment(FieldAttributeType.CONTENT.value()));
        assertNotNull(record.getMetadata().getAttribute(FieldAttributeType.MIME_TYPE.value()));
        assertNotNull(record.getMetadata().getAttribute(MetaType.META_DATA.value()));
        assertNotNull(record.getMetadata().getAttribute(MetaType.RESPONSE_HEADER.value()));
        assertNotNull(record.getMetadata().getAttribute(MetaType.META_DATA_WITH_RESPONSE_HEADER_FALL_BACK.value()));
        counter++;
      }
    }

    _crawler.close();
    _log.info("Total time:" + (System.currentTimeMillis() - start));
    _log.info("Total records: " + counter);
  }

}
