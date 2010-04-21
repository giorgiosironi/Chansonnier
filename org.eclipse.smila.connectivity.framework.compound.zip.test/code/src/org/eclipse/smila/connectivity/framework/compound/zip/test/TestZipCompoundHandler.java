/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.compound.zip.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.compound.CompoundException;
import org.eclipse.smila.connectivity.framework.compound.CompoundHandler;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The Class TestZipCompoundHandler.
 */
public class TestZipCompoundHandler extends DeclarativeServiceTestCase {

  /**
   * The Zip CompoundHandler.
   */
  private CompoundHandler _zipHandler;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _zipHandler = getService(CompoundHandler.class, "(component.name=ZipCompoundHandler)");
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _zipHandler = null;
  }

  /**
   * Test getSupportedMimeTypes.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetSupportedMimeTypes() throws Exception {
    assertNotNull(_zipHandler);

    final Collection<String> supportedMimeTypes = _zipHandler.getSupportedMimeTypes();
    assertNotNull(supportedMimeTypes);
    assertEquals(2, supportedMimeTypes.size());
    assertTrue(supportedMimeTypes.contains("application/zip"));
    assertTrue(supportedMimeTypes.contains("application/java-archive"));
  }

  /**
   * Test extract exceptions.
   * 
   * @throws Exception
   *           the exception
   */
  public void testExceptions() throws Exception {
    assertNotNull(_zipHandler);
    try {
      _zipHandler.extract(null, null);
    } catch (CompoundException e) {
      assertNotNull(e);
      assertEquals("error during initialization of ZipCompoundCrawler", e.getMessage());
      assertEquals("parameter record is null", e.getCause().getMessage());
    }

    try {
      final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
      _zipHandler.extract(record, null);
    } catch (CompoundException e) {
      assertNotNull(e);
      assertEquals("error during initialization of ZipCompoundCrawler", e.getMessage());
      assertEquals("parameter config is null", e.getCause().getMessage());
    }
  }

  /**
   * Test extract.
   * 
   * @throws Exception
   *           the exception
   */
  public void testExtract() throws Exception {
    assertNotNull(_zipHandler);

    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
        "ConfigExample.xml"));
    assertNotNull(config);

    final Crawler zipCrawler = _zipHandler.extract(createRecord(), config);
    assertNotNull(zipCrawler);
    zipCrawler.close();
  }

  /**
   * Creates a record containing a zip as attachment.
   * 
   * @return a Record object
   * @throws IOException
   *           if any error occurs
   */
  private Record createRecord() throws IOException {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("FileSystem_ZipTest", "test.zip");
    record.setId(id);
    record.setAttachment("Content", FileUtils.readFileToByteArray(new File("configuration/test.zip")));
    return record;
  }
}
