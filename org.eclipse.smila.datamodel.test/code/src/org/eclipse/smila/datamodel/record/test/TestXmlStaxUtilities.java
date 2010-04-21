/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.stax.RecordReader;
import org.eclipse.smila.datamodel.record.stax.RecordWriter;

/**
 * Test StAX transformation of Records.
 *
 * @author jschumacher
 *
 */
public class TestXmlStaxUtilities extends ARecordTestCase {

  /**
   * StAX writer factory.
   */
  private XMLOutputFactory _outputFactory = XMLOutputFactory.newInstance();

  /**
   * StAX reader factory.
   */
  private XMLInputFactory _inputFactory = XMLInputFactory.newInstance();

  /**
   * Id Writer.
   */
  private RecordWriter _recordWriter = new RecordWriter(true);

  /**
   * Id Reader.
   */
  private RecordReader _recordReader = new RecordReader();

  /**
   * write and read record using StAX.
   *
   * @param record
   *          record to transform
   * @return parsed record
   * @throws Exception
   *           error in transformation
   */
  private Record writeAndRead(final Record record) throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final XMLStreamWriter writer = _outputFactory.createXMLStreamWriter(out, "utf-8");
    writer.writeStartDocument("utf-8", "1.1");
    _recordWriter.writeRecord(writer, record);
    writer.writeEndDocument();
    writer.close();
    out.close();

    final String xmlRecord = out.toString("utf-8");
    System.out.println("XML Record: " + xmlRecord);

    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    final XMLStreamReader reader = _inputFactory.createXMLStreamReader(in);
    reader.nextTag(); // to Id tag.
    final Record parsedRecord = _recordReader.readRecord(reader);
    assertNotNull(parsedRecord);
    return parsedRecord;
  }

  /**
   * write and read record using StAX.
   *
   * @param records
   *          record to transform
   * @return parsed record
   * @throws Exception
   *           error in transformation
   */
  private List<Record> writeAndRead(final List<Record> records) throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final XMLStreamWriter writer = _outputFactory.createXMLStreamWriter(out, "utf-8");
    writer.writeStartDocument("utf-8", "1.1");
    _recordWriter.writeRecordList(writer, records);
    writer.writeEndDocument();
    writer.close();
    out.close();

    final String xmlRecord = out.toString("utf-8");
    System.out.println("XML Record List: " + xmlRecord);

    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    final XMLStreamReader reader = _inputFactory.createXMLStreamReader(in);
    reader.nextTag(); // to Id tag.
    final List<Record> parsedRecords = _recordReader.readRecords(reader);
    assertNotNull(parsedRecords);
    return parsedRecords;
  }

  /**
   * just a test to check the record comparison code...
   */
  public void testCheck() {
    final Record record1 = RecordCreator.createTestRecord1();
    final Record record2 = RecordCreator.createRecordLiteralAttributes();
    final Record record3 = RecordCreator.createRecordObjectAttributes();
    checkRecordEquality(record1, record1);
    checkRecordEquality(record2, record2);
    checkRecordEquality(record3, record3);
    checkRecordInequality(record1, record2);
    checkRecordInequality(record1, record3);
    checkRecordInequality(record2, record1);
    checkRecordInequality(record2, record3);
    checkRecordInequality(record3, record1);
    checkRecordInequality(record3, record2);

    Record modifiedRecord = RecordCreator.createTestRecord1();
    Literal literal = modifiedRecord.getFactory().createLiteral();
    literal.setStringValue("additional string value");
    final String attributeName = modifiedRecord.getMetadata().getAttributeNames().next();
    modifiedRecord.getMetadata().getAttribute(attributeName).addLiteral(literal);
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createTestRecord1();
    modifiedRecord.getMetadata().getAttribute(attributeName).getLiteral().setStringValue("modified string value");
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createTestRecord1();
    final String annotationName = modifiedRecord.getMetadata().getAnnotationNames().next();
    modifiedRecord.getMetadata().getAnnotation(annotationName).addAnonValue("additional anon value");
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createTestRecord1();
    modifiedRecord.getMetadata().getAnnotation(annotationName).setNamedValue("additional name",
      "additional anon value");
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createTestRecord1();
    modifiedRecord.setAttachment("additional attachment", "test".getBytes());
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createTestRecord1();
    modifiedRecord.removeAttachment(modifiedRecord.getAttachmentNames().next());
    modifiedRecord.setAttachment("modified attachment", "test".getBytes());
    checkRecordInequality(record1, modifiedRecord);
    checkRecordInequality(modifiedRecord, record1);

    modifiedRecord = RecordCreator.createRecordObjectAttributes();
    List<MObject> objects = modifiedRecord.getMetadata().getAttribute("multi object").getObjects();
    objects.remove(0);
    modifiedRecord.getMetadata().getAttribute("multi object").setObjects(objects);
    checkRecordInequality(record3, modifiedRecord);
    checkRecordInequality(modifiedRecord, record3);

    modifiedRecord = RecordCreator.createRecordObjectAttributes();
    objects = modifiedRecord.getMetadata().getAttribute("multi object").getObjects();
    literal = modifiedRecord.getFactory().createLiteral();
    literal.setStringValue("modified string value");
    objects.get(0).getAttribute("value").setLiteral(literal);
    modifiedRecord.getMetadata().getAttribute("multi object").setObjects(objects);
    checkRecordInequality(record3, modifiedRecord);
    checkRecordInequality(modifiedRecord, record3);

  }

  /**
   * simple first test.
   *
   * @throws Exception
   *           test fails.
   */
  public void testRecord1() throws Exception {
    final Record testRecord = RecordCreator.createTestRecord1();
    final Record resultRecord = writeAndRead(testRecord);
    checkRecordEquality(testRecord, resultRecord);
  }

  /**
   * test record with some literal attributes.
   *
   * @throws Exception
   *           test fails
   */
  public void testRecordLiteralAttributes() throws Exception {
    final Record testRecord = RecordCreator.createRecordLiteralAttributes();
    final Record resultRecord = writeAndRead(testRecord);
    checkRecordEquality(testRecord, resultRecord);
  }

  /**
   * test record with some literal attributes.
   *
   * @throws Exception
   *           test fails
   */
  public void testRecordObjectAttributes() throws Exception {
    final Record testRecord = RecordCreator.createRecordObjectAttributes();
    final Record resultRecord = writeAndRead(testRecord);
    checkRecordEquality(testRecord, resultRecord);
  }

  /**
   * test list of records.
   *
   * @throws Exception
   *           test fails
   */
  public void testRecordList() throws Exception {
    final List<Record> testRecords = new ArrayList<Record>();
    testRecords.add(RecordCreator.createTestRecord1());
    testRecords.add(RecordCreator.createRecordLiteralAttributes());
    testRecords.add(RecordCreator.createRecordObjectAttributes());
    final List<Record> resultRecords = writeAndRead(testRecords);
    assertEquals(testRecords.size(), resultRecords.size());
    for (int i = 0; i < testRecords.size(); i++) {
      checkRecordEquality(testRecords.get(i), resultRecords.get(i));
    }
  }
}
