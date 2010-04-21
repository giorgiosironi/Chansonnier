/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound.test;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.eclipse.smila.common.mimetype.MimeTypeParseException;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.compound.CompoundException;
import org.eclipse.smila.connectivity.framework.compound.CompoundManager;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The Class TestCompoundManager.
 */
public class TestCompoundManager extends DeclarativeServiceTestCase {

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
  }

  /**
   * Test parameter exceptions.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testParameterExceptions() throws Exception {
    final CompoundManager compoundManager = getService(CompoundManager.class);
    assertNotNull(compoundManager);

    final Record record = createRecord();

    try {
      compoundManager.isCompound(null, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter record is null", e.getMessage());
    }

    try {
      compoundManager.isCompound(record, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter config is null", e.getMessage());
    }

    try {
      compoundManager.extract(null, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter record is null", e.getMessage());
    }

    try {
      compoundManager.extract(record, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter config is null", e.getMessage());
    }

    try {
      compoundManager.adaptCompoundRecord(null, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter record is null", e.getMessage());
    }

    try {
      compoundManager.adaptCompoundRecord(record, null);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("parameter config is null", e.getMessage());
    }
  }

  /**
   * Test {@link CompoundManager#isCompound(Record, DataSourceConnectionConfig)}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testIsCompound() throws Exception {
    final CompoundManager compoundManager = getService(CompoundManager.class);
    assertNotNull(compoundManager);

    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
        "ZipConfigExample.xml"));
    assertNotNull(config);
  
    // check by extension
    Record record = createRecord();
    addAttribute(record, config.getCompoundHandling().getExtensionAttribute(), "html");
    assertFalse(compoundManager.isCompound(record, config));
    assertTrue(record.getMetadata().hasAttribute(config.getCompoundHandling().getMimeTypeAttribute()));
    assertEquals("text/html", record.getMetadata()
      .getAttribute(config.getCompoundHandling().getMimeTypeAttribute()).getLiteral().getStringValue());

    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getExtensionAttribute(), "zip");
    assertTrue(compoundManager.isCompound(record, config));
    assertTrue(record.getMetadata().hasAttribute(config.getCompoundHandling().getMimeTypeAttribute()));
    assertEquals("application/zip", record.getMetadata().getAttribute(
      config.getCompoundHandling().getMimeTypeAttribute()).getLiteral().getStringValue());

    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getExtensionAttribute(), "dummy");
    try {
      compoundManager.isCompound(record, config);
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("Error identifying mimetype for record " + record.getId(), e.getMessage());
      assertTrue(e.getCause() instanceof MimeTypeParseException);
    }

    // check by mime type
    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "text/html");
    assertFalse(compoundManager.isCompound(record, config));
    assertTrue(record.getMetadata().hasAttribute(config.getCompoundHandling().getMimeTypeAttribute()));
    assertEquals("text/html", record.getMetadata()
      .getAttribute(config.getCompoundHandling().getMimeTypeAttribute()).getLiteral().getStringValue());

    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "application/zip");
    assertTrue(compoundManager.isCompound(record, config));
    assertTrue(record.getMetadata().hasAttribute(config.getCompoundHandling().getMimeTypeAttribute()));
    assertEquals("application/zip", record.getMetadata().getAttribute(
      config.getCompoundHandling().getMimeTypeAttribute()).getLiteral().getStringValue());
  }

  /**
   * Test {@link CompoundManager#extract(Record, DataSourceConnectionConfig)}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testExtract() throws Exception {
    final CompoundManager compoundManager = getService(CompoundManager.class);
    assertNotNull(compoundManager);

    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
        "ZipConfigExample.xml"));
    assertNotNull(config);

    // try to extract a non compound
    Record record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "text/html");
    Crawler crawler = compoundManager.extract(record, config);
    assertNull(crawler);

    // try to extract an empty compound
    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "application/zip");
    try {
      crawler = compoundManager.extract(record, config);
      crawler.close();
      fail("expected CompoundException");
    } catch (CompoundException e) {
      assertEquals("record " + record.getId() + " does not contain attachment "
        + config.getCompoundHandling().getContentAttachment(), e.getMessage());
    }

    // try to extract compound
    record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "application/zip");
    record.setAttachment("Content", FileUtils.readFileToByteArray(new File("configuration/data/test.zip")));
    crawler = compoundManager.extract(record, config);
    assertNotNull(crawler);
    crawler.close();
  }

  /**
   * Test {@link CompoundManager#adaptCompoundRecord(Record, DataSourceConnectionConfig)}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testAdaptCompoundRecord() throws Exception {
    final CompoundManager compoundManager = getService(CompoundManager.class);
    assertNotNull(compoundManager);

    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream("org.eclipse.smila.connectivity.framework",
        "ZipConfigExample.xml"));
    assertNotNull(config);

    final Record record = createRecord();
    addAttribute(record, config.getCompoundHandling().getMimeTypeAttribute(), "application/zip");
    record.setAttachment("Content", FileUtils.readFileToByteArray(new File("configuration/data/test.zip")));
    final Record adaptedRecord = compoundManager.adaptCompoundRecord(record, config);

    // with the current implementation the record should not be changed
    assertNotNull(adaptedRecord);
    assertTrue(adaptedRecord == record);
    final Iterator<String> atts = record.getMetadata().getAttributeNames();
    final String att = atts.next();
    assertEquals(att, config.getCompoundHandling().getMimeTypeAttribute());
    assertFalse(atts.hasNext());
    final Iterator<String> attaches = record.getAttachmentNames();
    final String attach = attaches.next();
    assertEquals(attach, config.getCompoundHandling().getContentAttachment());
    assertFalse(attaches.hasNext());
  }

  /**
   * Creates a record for testing.
   * 
   * @return a record
   */
  private Record createRecord() {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("CompoundTest", "CompoundRecord");
    record.setId(id);
    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);

    return record;
  }

  /**
   * Adds an attribute with the given name and value to the record.
   * 
   * @param record
   *          the record
   * @param name
   *          the attribute name
   * @param value
   *          the attribute value
   */
  private void addAttribute(final Record record, final String name, final String value) {
    final Attribute attribute = record.getFactory().createAttribute();
    record.getMetadata().setAttribute(name, attribute);
    final Literal literal = record.getFactory().createLiteral();
    literal.setStringValue(value);
    attribute.addLiteral(literal);
  }
}
