/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.schema.test;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Attribute;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process.Filter;
import org.eclipse.smila.connectivity.framework.crawler.filesystem.messages.Process.Filter.Include;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.CompoundHandling;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig.RecordBuffer;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;

/**
 * The Class TestDataSourceConnectionConfigLoad.
 */
public class TestConfigLoad extends TestCase {

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Test load.
   * 
   * @throws Exception
   *           the exception
   */
  public void testLoad() throws Exception {
    final DataSourceConnectionConfig configuration =
      ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("ConfigExample.xml"));
    _log.info("DataSourceID=" + configuration.getDataSourceID());
    final DataConnectionID dataConnectionID = configuration.getDataConnectionID();
    assertEquals(true, (dataConnectionID.getType() == DataConnectionType.CRAWLER));
    _log.info("DataConnectionID TYPE=" + dataConnectionID.getType());
    assertEquals("org.eclipse.smila.connectivity.framework.crawler.filesystem", configuration.getSchemaID());
    assertEquals("MyCrawler", dataConnectionID.getId());
    _log.info("DataConnectionID ID=" + dataConnectionID.getId());

    // record buffer check
    final RecordBuffer recordBuffer = configuration.getRecordBuffer();
    assertNotNull(recordBuffer);
    assertEquals(40, recordBuffer.getSize());
    assertEquals(3000, recordBuffer.getFlushInterval());

    // delta indexing check
    final DeltaIndexingType deltaIndexing = configuration.getDeltaIndexing();
    assertNotNull(deltaIndexing);
    assertEquals(DeltaIndexingType.FULL, deltaIndexing);

    // Compound handling check
    final CompoundHandling ch = configuration.getCompoundHandling();
    assertNotNull(ch);
    assertEquals("MimeType", ch.getMimeTypeAttribute());
    _log.info("CompoundHandling MimeTypeAttribute=" + ch.getMimeTypeAttribute());
    assertEquals("Extension", ch.getExtensionAttribute());
    _log.info("CompoundHandling ExtensionAttribute=" + ch.getExtensionAttribute());
    assertEquals("Content", ch.getContentAttachment());
    _log.info("CompoundHandling ContentAttachment=" + ch.getContentAttachment());
    // compound attributes check
    final List<CompoundHandling.CompoundAttribute> cattrs = ch.getCompoundAttributes().getCompoundAttributes();
    assertNotNull(cattrs);
    assertEquals(2 + 2 + 2, cattrs.size());

    // attributes check
    final List<IAttribute> attrs = configuration.getAttributes().getAttribute();
    final String formatString = "[%s] %s Tag: %s";
    assertEquals(2 + 2 + 1, attrs.size());
    for (final IAttribute attribute : attrs) {
      final Attribute fileSystemAttribute = (Attribute) attribute;
      final String name = fileSystemAttribute.getName();

      if (fileSystemAttribute.getFileAttributes() != null) {
        _log.info(String.format(formatString, name, "FileAttribute", fileSystemAttribute.getFileAttributes()));
      }
    }
    // attributes check

    // process check
    final org.eclipse.smila.connectivity.framework.schema.config.interfaces.IProcess iProcess =
      configuration.getProcess();
    final Process process = (Process) iProcess;
    assertNotNull(process);
    int i = 0;
    final int size = process.getBaseDirAndFilter().size();
    assertEquals(2, size);
    while (i < size) {
      _log.info("**************************************************************");
      final String baseDir = (String) process.getBaseDirAndFilter().get(i++);
      assertEquals("c:\\test", baseDir);
      _log.info("baseDir=" + baseDir);
      final Filter filter = (Filter) process.getBaseDirAndFilter().get(i++);
      for (final Include include : filter.getInclude()) {
        _log.info("include=" + include.getName());
      }
      _log.info("**************************************************************");
    }
    // end of process check
  }
}
