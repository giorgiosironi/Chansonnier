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

import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.test.RecordCreator;
import org.eclipse.smila.datamodel.tools.DatamodelCopyUtils;

/**
 * The Class TestRecordFilters.
 */
public class TestDatamodelCopyUtils extends AbstractDatamodelToolsTestCase {

  /**
   * Test clone record1.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testCloneRecord1() throws ParserConfigurationException, IOException {
    final Record record = RecordCreator.createTestRecord1();
    compareRecordWithClone(record);
  }

  /**
   * Test clone record literal attributes.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testCloneRecordLiteralAttributes() throws ParserConfigurationException, IOException {
    final Record record = RecordCreator.createRecordLiteralAttributes();
    compareRecordWithClone(record);
  }

  /**
   * Test clone record object attributes.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testCloneRecordObjectAttributes() throws ParserConfigurationException, IOException {
    final Record record = RecordCreator.createRecordObjectAttributes();
    compareRecordWithClone(record);
  }

  /**
   * Test clone record annotations.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testCloneRecordAnnotations() throws ParserConfigurationException, IOException {
    final Record record = RecordCreator.createRecordAnnotations();
    compareRecordWithClone(record);
  }

  /**
   * Compare record with clone.
   * 
   * @param record
   *          the record
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void compareRecordWithClone(final Record record) throws ParserConfigurationException, IOException {
    final RecordFactory factory = record.getFactory();
    assertNotNull(record);
    final MObject source = record.getMetadata();
    assertNotNull(source);

    final String result = transformRecordToString(record);
    _log.info("source record:" + result);
    final MObject object = DatamodelCopyUtils.cloneMObject(source, factory);
    assertEquals(false, object == source);
    record.setMetadata(object);
    final String result2 = transformRecordToString(record);
    _log.info("cloned record:" + result2);
    assertEquals(result, result2);
  }
}
