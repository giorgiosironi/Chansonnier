/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.crawler.filesystem.test;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.FileSystemCrawler;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.compression.CompressionHelper;

/**
 * The Class TestCrawler.
 */
public class TestCrawler extends DeclarativeServiceTestCase {

  /**
   * timeout for service detection.
   */
  private static final long WAIT_FOR_SERVICE_DELAY = 30000;

  /**
   * The Constant FILES_FILTERED.
   */
  private static final int FILES_FILTERED = 4;

  /**
   * The Constant PAUSE.
   */
  private static final int PAUSE = 1000;

  /**
   * the Crawler.
   */
  private FileSystemCrawler _crawler;

  /**
   * Crawling folder.
   */
  private File _crawlingFolder;

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
    forceStartBundle("org.eclipse.smila.connectivity.impl");
    // register the service, because it's configuration uses immediate="false"
    _crawler = registerService(new FileSystemCrawler(), null, FileSystemCrawler.class, WAIT_FOR_SERVICE_DELAY);
    _crawlingFolder = File.createTempFile("SMILA", "crawler");
    final String path = _crawlingFolder.getPath();
    _crawlingFolder.delete();
    _crawlingFolder = new File(path);
    Thread.sleep(PAUSE);
    InputStream inputStream = null;
    try {
      inputStream = TestCrawler.class.getResourceAsStream("testFolder.zip");
      CompressionHelper.unzip(_crawlingFolder, inputStream);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
    Thread.sleep(PAUSE);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    Thread.sleep(PAUSE);
    try {
      FileUtils.deleteDirectory(_crawlingFolder);
    } catch (final Throwable e) {
      ;// nothing
    }
  }

  /**
   * Test crawler.
   * 
   * @throws Exception
   *           the exception
   */
  public void testCrawler() throws Exception {
    assertNotNull(_crawler);
    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("ConfigExample.xml"));
    assertNotNull(config);
    // patch configuration to crawling folder
    final Process process = (Process) config.getProcess();
    process.getBaseDirAndFilter().set(0, _crawlingFolder.getPath());
    // System.out.println(_crawlingFolder.getPath());

    _crawler.initialize(config);
    final long start = System.currentTimeMillis();
    int counter = 0;
    DataReference[] objects;
    while ((objects = _crawler.getNext()) != null) {
      if (objects.length == 0) {
        break;
      }
      for (int i = 0; i < objects.length; i++) {
        final DataReference object = objects[i];
        final Record record = object.getRecord();
        counter++;
        _log.info(record.getMetadata().getAttribute("Path").getLiteral());
        _log.info(DatamodelSerializationUtils.serialize2string(record));
      }
    }
    _crawler.close();
    assertEquals(FILES_FILTERED, counter);
    _log.info("Time:" + (System.currentTimeMillis() - start));
    _log.info("Count: " + counter);
  }
}
