/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.datamodel.tools.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.test.RecordCreator;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterHelper;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterLoadSaveException;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterNotFoundException;

/**
 * The Class TestRecortFilterHelper.
 */
public final class TestRecortFilterHelper extends AbstractDatamodelToolsTestCase {

  /**
   * The _helper.
   */
  private RecordFilterHelper _helper;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _helper = new RecordFilterHelper(TestRecortFilterHelper.class.getResourceAsStream("RecordFilters.xml"));
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _helper = null;
    super.tearDown();
  }

  /**
   * Test filtering.
   * 
   * @throws RecordFilterLoadSaveException
   *           the record filter load exception
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testFiltering() throws RecordFilterLoadSaveException, RecordFilterNotFoundException,
    ParserConfigurationException, IOException {
    Record record = RecordCreator.createTestRecord1();
    record.removeAttachments();

    Record record1 = _helper.filter(record, "no-filter");
    assertEquals(false, record == record1);
    assertEquals(record, record1);

    record = _helper.filter(record, "only-attribute1");
    _log.info("record1=" + transformRecordToString(record1));
    assertEquals(1, record1.getMetadata().size());
    final Attribute attribute1 = record1.getMetadata().getAttribute("attribute1");
    assertNotNull(attribute1);
    assertEquals(false, attribute1.hasAnnotations());

    // other source record
    record = RecordCreator.createRecordLiteralAttributes();
    record.removeAttachments();
    assertEquals(2 + 2 + 2 + 2, record.getMetadata().size());
    _log.info("record1=" + transformRecordToString(record));
    record1 = _helper.filter(record, "filter-single-and-datetime");
    assertEquals(2, record1.getMetadata().size());
    _log.info("record1=" + transformRecordToString(record1));

  }
}
