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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.dom.RecordBuilder;
import org.eclipse.smila.datamodel.record.dom.RecordParser;
import org.eclipse.smila.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Test DOM transformation of Records.
 *
 * @author jschumacher
 *
 */
public class TestXmlDomUtilities extends ARecordTestCase {

  /**
   * used for creating Record DOM elements.
   */
  private final RecordBuilder _builder = new RecordBuilder(true);

  /**
   * used for creating Records from DOM elements.
   */
  private final RecordParser _parser = new RecordParser();

  /**
   * build Record DOM, print it to a string, and parse it again.
   *
   * @param record
   *          record to transform
   * @return parsed record
   * @throws Exception
   *           error in transformation
   */
  private Record buildAndParseDOM(final Record record) throws Exception {
    final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    final Document document = createDOMRecord(builderFactory, record);
    final String xmlRecord = XmlHelper.toString(document);
    System.out.println("XML Record: " + xmlRecord);

    final DocumentBuilder domBuilder = builderFactory.newDocumentBuilder();
    final Document parsedDocument = domBuilder.parse(new InputSource(new StringReader(xmlRecord)));
    final Record parsedRecord = _parser.parseRecordIn(parsedDocument.getDocumentElement());
    assertNotNull(parsedRecord);

    final Document recreatedDocument = createDOMRecord(builderFactory, parsedRecord);
    final String recreatedXmlRecord = XmlHelper.toString(recreatedDocument);
    System.out.println("Recreated XML Record: " + recreatedXmlRecord);
    return parsedRecord;
  }

  /**
   * build RecordList DOM, print it to a string, and parse it again.
   *
   * @param records
   *          record list to transform
   * @return parsed record list
   * @throws Exception
   *           error in transformation
   */
  private List<Record> buildAndParseDOM(final List<Record> records) throws Exception {
    final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);

    final Document document = builderFactory.newDocumentBuilder().newDocument();
    final Element rootElement = document.createElement("test");
    document.appendChild(rootElement);
    _builder.appendRecordList(rootElement, records);

    final String xmlRecord = XmlHelper.toString(document);
    System.out.println("XML Record: " + xmlRecord);

    final DocumentBuilder domBuilder = builderFactory.newDocumentBuilder();
    final Document parsedDocument = domBuilder.parse(new InputSource(new StringReader(xmlRecord)));
    final Element parsedRootElement = parsedDocument.getDocumentElement();
    final Element parsedRecordList =
      (Element) parsedRootElement.getElementsByTagName(RecordParser.TAG_RECORDLIST).item(0);
    final List<Record> parsedRecords = _parser.parseRecordsIn(parsedRecordList);
    assertNotNull(parsedRecords);
    return parsedRecords;
  }

  /**
   * create DOM from record.
   *
   * @param builderFactory
   *          DOM build factory to use
   * @param record
   *          record to transform
   * @return corresponding DOM element
   * @throws ParserConfigurationException
   *           error
   */
  private Document createDOMRecord(final DocumentBuilderFactory builderFactory, final Record record)
    throws ParserConfigurationException {
    final Document document = builderFactory.newDocumentBuilder().newDocument();
    final Element rootElement = document.createElement("test");
    document.appendChild(rootElement);
    _builder.appendRecord(rootElement, record);
    return document;
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
    final Record resultRecord = buildAndParseDOM(testRecord);
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
    final Record resultRecord = buildAndParseDOM(testRecord);
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
    final Record resultRecord = buildAndParseDOM(testRecord);
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
    final List<Record> resultRecords = buildAndParseDOM(testRecords);
    assertEquals(testRecords.size(), resultRecords.size());
    for (int i = 0; i < testRecords.size(); i++) {
      checkRecordEquality(testRecords.get(i), resultRecords.get(i));
    }
  }
}
