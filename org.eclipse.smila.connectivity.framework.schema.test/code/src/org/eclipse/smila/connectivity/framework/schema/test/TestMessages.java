/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.OriginalAttribute;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.OriginalProcess;
import org.eclipse.smila.connectivity.framework.schema.config.AttributeAdapter;
import org.eclipse.smila.connectivity.framework.schema.config.CompoundHandling;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionIDAdapter;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfigSimple;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IProcess;

/**
 * The Class TestMessages.
 * 
 * @author Alexander Eliseyev
 */
public class TestMessages extends TestCase {

  /**
   * Test configuration simple.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDataSourceConnectionConfigSimple() throws Exception {
    final DataSourceConnectionConfigSimple configurationSimple = new DataSourceConnectionConfigSimple();
    configurationSimple.setDataSourceID("dataSourceId");
    configurationSimple.setSchemaID("schemaId");

    assertEquals("dataSourceId", configurationSimple.getDataSourceID());
    assertEquals("schemaId", configurationSimple.getSchemaID());
  }

  /**
   * Gets the data connection id.
   * 
   * @return the data connection id
   */
  private DataConnectionID getDataConnectionID() {
    final DataConnectionID connectionID = new DataConnectionID();
    connectionID.setId("Id");
    connectionID.setType(DataConnectionType.AGENT);
    return connectionID;
  }

  /**
   * Test attribute adapter exception handle.
   * 
   * @throws Exception
   *           the exception
   */
  public void testAttributeAdapterException() throws Exception {
    final AttributeAdapter attributeAdapter = new AttributeAdapter();
    final WrongClass wrongClass = new WrongClass();
    try {
      attributeAdapter.unmarshal(wrongClass);
      fail();
    } catch (final Exception e) {
      ; // ok
    }
  }

  /**
   * The Class WrongClass.
   */
  private static class WrongClass {

  }

  /**
   * Test data connection id adapter.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDataConnectionIDAdapter() throws Exception {
    final DataConnectionIDAdapter adapter = new DataConnectionIDAdapter();
    final DataConnectionID dataConnectionID = getDataConnectionID();

    try {
      adapter.unmarshal(adapter.marshal(dataConnectionID));
    } catch (final Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test configuration.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDataSourceConnectionConfig() throws Exception {
    final DataSourceConnectionConfig configuration = new DataSourceConnectionConfig();

    final DataSourceConnectionConfig.Attributes attributes = new DataSourceConnectionConfig.Attributes();
    final IAttribute attribute = new OriginalAttribute();
    attributes.getAttribute().add(attribute);

    final DataSourceConnectionConfig.RecordBuffer recordBuffer = new DataSourceConnectionConfig.RecordBuffer();
    recordBuffer.setSize(30);
    recordBuffer.setFlushInterval(5000);
    configuration.setRecordBuffer(recordBuffer);

    configuration.setDeltaIndexing(DeltaIndexingType.DISABLED);

    final IProcess process = new OriginalProcess();

    final CompoundHandling compoundHandling = new CompoundHandling();
    compoundHandling.setMimeTypeAttribute("MimeType");
    compoundHandling.setExtensionAttribute("Extension");
    compoundHandling.setContentAttachment("Content");
    final CompoundHandling.CompoundAttributes compoundAttributes = new CompoundHandling.CompoundAttributes();
    final CompoundHandling.CompoundAttribute compoundAttribute = new CompoundHandling.CompoundAttribute();
    compoundAttributes.getCompoundAttributes().add(compoundAttribute);
    compoundHandling.setCompoundAttributes(compoundAttributes);
    configuration.setCompoundHandling(compoundHandling);

    configuration.setAttributes(attributes);
    configuration.setDataConnectionID(getDataConnectionID());
    configuration.setDataSourceID("dataSourceId");
    configuration.setProcess(process);
    configuration.setSchemaID("schemaId");

    assertNotNull(configuration.getAttributes());
    assertTrue(configuration.getAttributes().getAttribute().contains(attribute));

    assertNotNull(configuration.getRecordBuffer());
    assertEquals(30, configuration.getRecordBuffer().getSize());
    assertEquals(5000, configuration.getRecordBuffer().getFlushInterval());

    assertNotNull(configuration.getDeltaIndexing());
    assertEquals(DeltaIndexingType.DISABLED, configuration.getDeltaIndexing());

    final CompoundHandling returnedCompoundHandling = configuration.getCompoundHandling();
    assertNotNull(returnedCompoundHandling);
    assertEquals("MimeType", returnedCompoundHandling.getMimeTypeAttribute());
    assertEquals("Extension", returnedCompoundHandling.getExtensionAttribute());
    assertEquals("Content", returnedCompoundHandling.getContentAttachment());
    assertNotNull(returnedCompoundHandling.getCompoundAttributes());
    assertTrue(returnedCompoundHandling.getCompoundAttributes().getCompoundAttributes().contains(compoundAttribute));

    assertNotNull(configuration.getDataConnectionID());
    assertEquals("Id", configuration.getDataConnectionID().getId());
    assertEquals(DataConnectionType.AGENT, configuration.getDataConnectionID().getType());
    assertEquals("dataSourceId", configuration.getDataSourceID());
    assertTrue(process == configuration.getProcess());
    assertEquals("schemaId", configuration.getSchemaID());
  }

}
