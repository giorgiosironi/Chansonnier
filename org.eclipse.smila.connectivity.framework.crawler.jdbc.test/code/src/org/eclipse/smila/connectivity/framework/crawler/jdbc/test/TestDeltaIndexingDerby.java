/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc.test;

import java.util.ArrayList;

import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.connectivity.framework.crawler.jdbc.JdbcCrawler;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Testcase for {@link JdbcCrawler}.
 */
public class TestDeltaIndexingDerby extends AbstractDataEnabledJdbcCrawlerTestCase {

  /**
   * The Constant EXPECTED_RECORDS.
   */
  private static final int EXPECTED_RECORDS = AbstractDataEnabledJdbcCrawlerTestCase.RECORDS_TO_INSERT;

  /**
   * Tests {@link JdbcCrawler#getNext()}.
   * 
   * @throws Exception
   *           If exceptions occur ...
   */
  public void testDeltaIndexing() throws Exception {
    assertNotNull("Crawler instance should not be null after initializing TestCase", _crawler);
    final DataSourceConnectionConfig config =
      ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("JdbcDerbyWithGrouping.xml"));
    assertNotNull("DataSourceConnection Config Config should not be null after unmarshalling", config);

    _crawler.initialize(config);
    final long start = System.currentTimeMillis();
    Thread.sleep(WAIT_FOR_CRAWLER_INIT);

    final java.util.List<Record> records = new ArrayList<Record>();
    DataReference[] currentReferences = null;
    _log.info("Beginning to retrieve DeltaIndexingData...");
    while ((currentReferences = _crawler.getNext()) != null) {
      if (currentReferences.length == 0) {
        break;
      }
      for (int i = 0; i < currentReferences.length; i++) {
        records.add(currentReferences[i].getRecord());
      }
    }
    assertEquals("The current DB configuration should result in the creation of " + EXPECTED_RECORDS + " records",
      EXPECTED_RECORDS, records.size());
    final long stop = System.currentTimeMillis();
    _log.info("Elapsed Time:" + (stop - start));
    _log.info("MObjects created: " + records.size());
  }

}
