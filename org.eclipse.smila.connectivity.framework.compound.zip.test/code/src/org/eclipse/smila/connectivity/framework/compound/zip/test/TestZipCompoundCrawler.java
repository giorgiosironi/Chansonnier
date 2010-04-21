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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.compound.zip.ZipCompoundCrawler;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.CompoundHandling;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The Class TestZipCompoundCrawler.
 */
public class TestZipCompoundCrawler extends DeclarativeServiceTestCase {

  /**
   * The ZipCompoundCrawler.
   */
  private ZipCompoundCrawler _zipCrawler;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _zipCrawler = new ZipCompoundCrawler();
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _zipCrawler = null;
  }

  /**
   * Test not initialized exceptions.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNotInitializedionExceptions() throws Exception {
    assertNotNull(_zipCrawler);

    try {
      _zipCrawler.getAttachment(null, null);
      fail("expected CompoundException");
    } catch (CrawlerCriticalException e) {
      assertEquals("ZipCompoundCrawler was not initialized", e.getMessage());
    }

    try {
      _zipCrawler.getAttachmentNames(null);
      fail("expected CompoundException");
    } catch (CrawlerCriticalException e) {
      assertEquals("ZipCompoundCrawler was not initialized", e.getMessage());
    }

    try {
      _zipCrawler.getMObject(null);
      fail("expected CompoundException");
    } catch (CrawlerCriticalException e) {
      assertEquals("ZipCompoundCrawler was not initialized", e.getMessage());
    }

    try {
      _zipCrawler.getNext();
      fail("expected CompoundException");
    } catch (CrawlerCriticalException e) {
      assertEquals("ZipCompoundCrawler was not initialized", e.getMessage());
    }
  }

  /**
   * Test initialization exceptions.
   * 
   * @throws Exception
   *           the exception
   */
  public void testInitializationExceptions() throws Exception {
    assertNotNull(_zipCrawler);
    try {
      try {
        _zipCrawler.initialize(null);
        fail("expected CrawlerCriticalException");
      } catch (CrawlerCriticalException e) {
        assertEquals("parameter config is null", e.getMessage());
      }

      final DataSourceConnectionConfig config =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
          "ConfigExample.xml"));
      assertNotNull(config);

      try {
        _zipCrawler.initialize(config);
        fail("expected CrawlerCriticalException");
      } catch (CrawlerCriticalException e) {
        assertEquals("the compound record was not set", e.getMessage());
      }

      // try to initialize with an empty attachment
      Record compoundRecord = createRecord(config);
      _zipCrawler.setCompoundRecord(compoundRecord);
      try {
        _zipCrawler.initialize(config);
        fail("expected CrawlerCriticalException");
      } catch (CrawlerCriticalException e) {
        assertEquals("Error during initialization", e.getMessage());
        assertEquals("error in opening zip file", e.getCause().getMessage());
      }

      // try to initialize with an zip containing non utf-8 filenames
      compoundRecord = createRecord(config);
      compoundRecord.setAttachment(config.getCompoundHandling().getContentAttachment(), FileUtils
        .readFileToByteArray(new File("configuration/umlaut_test.zip")));
      _zipCrawler.setCompoundRecord(compoundRecord);
      _zipCrawler.initialize(config);
      final DataReference[] dataReferences = _zipCrawler.getNext();
      assertNotNull(dataReferences);
      try {
        dataReferences[0].getRecord();
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertTrue(e.getMessage().startsWith("Error reading content of ZipEntry"));
        assertTrue(e.getCause() instanceof NullPointerException);
      }
    } finally {
      _zipCrawler.close();
    }
  }

  /**
   * Test {@link ZipCompoundCrawler#getAttachment(Id, String)}.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetAttachment() throws Exception {
    assertNotNull(_zipCrawler);
    try {
      final DataSourceConnectionConfig config =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
          "ConfigExample.xml"));
      assertNotNull(config);

      final Record compoundRecord = createRecord(config);
      compoundRecord.setAttachment(config.getCompoundHandling().getContentAttachment(), FileUtils
        .readFileToByteArray(new File("configuration/test.zip")));
      _zipCrawler.setCompoundRecord(compoundRecord);
      _zipCrawler.initialize(config);

      final Id nullId = null;
      final Id dummyId = IdFactory.DEFAULT_INSTANCE.createId("FileSystem_ZipTest", "test.zip");
      try {
        _zipCrawler.getAttachment(nullId, null);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Could not find ZipEntry for id " + nullId, e.getMessage());
      }
      try {
        _zipCrawler.getAttachment(dummyId, null);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Could not find ZipEntry for id " + dummyId, e.getMessage());
      }

      final DataReference[] dataReferences = _zipCrawler.getNext();
      final String nullAttachmentName = null;
      final String dummyAttachmentName = "dummyAttachment";
      try {
        _zipCrawler.getAttachment(dataReferences[0].getId(), nullAttachmentName);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Unable to find attachment definition for [" + nullAttachmentName + "]", e.getMessage());
      }
      try {
        _zipCrawler.getAttachment(dataReferences[0].getId(), dummyAttachmentName);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Unable to find attachment definition for [" + dummyAttachmentName + "]", e.getMessage());
      }

      final byte[] content =
        _zipCrawler.getAttachment(dataReferences[0].getId(), config.getCompoundHandling().getContentAttachment());
      assertNotNull(content);
    } finally {
      _zipCrawler.close();
    }
  }

  /**
   * Test {@link ZipCompoundCrawler#getAttachmentNames(Id)}.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetAttachmentNames() throws Exception {
    assertNotNull(_zipCrawler);
    try {
      final DataSourceConnectionConfig config =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
          "ConfigExample.xml"));
      assertNotNull(config);

      final Record compoundRecord = createRecord(config);
      compoundRecord.setAttachment(config.getCompoundHandling().getContentAttachment(), FileUtils
        .readFileToByteArray(new File("configuration/test.zip")));
      _zipCrawler.setCompoundRecord(compoundRecord);
      _zipCrawler.initialize(config);

      final Id dummyId = IdFactory.DEFAULT_INSTANCE.createId("FileSystem_ZipTest", "test.zip");
      final String[] names = _zipCrawler.getAttachmentNames(dummyId);
      assertNotNull(names);
      assertEquals(1, names.length);
    } finally {
      _zipCrawler.close();
    }
  }

  /**
   * Test {@link ZipCompoundCrawler#getMObject(Id)}.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetMObject() throws Exception {
    assertNotNull(_zipCrawler);
    try {
      final DataSourceConnectionConfig config =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
          "ConfigExample.xml"));
      assertNotNull(config);

      final Record compoundRecord = createRecord(config);
      compoundRecord.setAttachment(config.getCompoundHandling().getContentAttachment(), FileUtils
        .readFileToByteArray(new File("configuration/test.zip")));
      _zipCrawler.setCompoundRecord(compoundRecord);
      _zipCrawler.initialize(config);

      final Id nullId = null;
      final Id dummyId = IdFactory.DEFAULT_INSTANCE.createId("FileSystem_ZipTest", "test.zip");
      try {
        _zipCrawler.getMObject(nullId);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Could not find ZipEntry for id " + nullId, e.getMessage());
      }
      try {
        _zipCrawler.getMObject(dummyId);
        fail("expected CrawlerException");
      } catch (CrawlerException e) {
        assertEquals("Could not find ZipEntry for id " + dummyId, e.getMessage());
      }

      final DataReference[] dataReferences = _zipCrawler.getNext();
      final MObject mObject = _zipCrawler.getMObject(dataReferences[0].getId());
      assertNotNull(mObject);
    } finally {
      _zipCrawler.close();
    }
  }

  /**
   * Test a crawl run.
   * 
   * @throws Exception
   *           the exception
   */
  public void testCrawl() throws Exception {
    assertNotNull(_zipCrawler);
    try {
      final DataSourceConnectionConfig config =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
          "ConfigExample.xml"));
      assertNotNull(config);
      final Record compoundRecord = createRecord(config);
      compoundRecord.setAttachment(config.getCompoundHandling().getContentAttachment(), FileUtils
        .readFileToByteArray(new File("configuration/test.zip")));
      _zipCrawler.setCompoundRecord(compoundRecord);
      _zipCrawler.initialize(config);

      final int expectedEntries = 20;
      int counter = 0;
      DataReference[] dataReferences = _zipCrawler.getNext();
      while (dataReferences != null) {
        for (DataReference reference : dataReferences) {
          try {
            assertNotNull(reference.getId());
            assertNotNull(reference.getHash());
            final Record record = reference.getRecord();
            assertNotNull(record);
            assertNotNull(record.getMetadata());

            final List<CompoundHandling.CompoundAttribute> atts =
              config.getCompoundHandling().getCompoundAttributes().getCompoundAttributes();
            for (CompoundHandling.CompoundAttribute att : atts) {
              if (att.isAttachment()) {
                assertNotNull(record.getAttachment(att.getName()));
              } else {
                assertNotNull(record.getMetadata().getAttribute(att.getName()).getLiteral());
              }
            }
            counter++;
          } finally {
            reference.dispose();
          }
        }
        dataReferences = _zipCrawler.getNext();
      }

      assertEquals(expectedEntries, counter);
    } finally {
      _zipCrawler.close();
    }
  }

  /**
   * Creates a record containing a zip as attachment.
   * 
   * @param config
   *          the DataSourceConnectionConfig
   * 
   * @return a Record object
   * @throws IOException
   *           if any error occurs
   */
  private Record createRecord(final DataSourceConnectionConfig config) throws IOException {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("FileSystem_ZipTest", "test.zip");
    record.setId(id);
    return record;
  }
}
