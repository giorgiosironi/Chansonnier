/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH) - initial API and implementation (based on aperture test by DS)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.common.mimetype.MimeTypeIdentifier;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.pipelets.MimeTypeIdentifyService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestConnectivity.
 */
public class TestMimeTypeIdentifyService extends DeclarativeServiceTestCase {

  /** the BlackboardService. */
  private Blackboard _blackboard;

  /** the MimeTypeIdentifyService. */
  private MimeTypeIdentifyService _mis;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);    
    final MimeTypeIdentifier mti = getService(MimeTypeIdentifier.class);
    assertNotNull("no MimeTypeIdentifier service found.", mti);    
    _mis = new MimeTypeIdentifyService();
    assertNotNull(_mis);
    _mis.setMimeTypeIdentifier(mti);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _blackboard = null;
    _mis = null;
  }

  /**
   * Test the process method with a known extension.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testProcess() throws Exception {
    final String idValue = "testId";
    final String content = "This is a simple test document. It contains no special format.";
    final String extension = "txt";
    final String expectedMimeType = "text/plain";

    final Record record = createRecord(idValue, content, extension, null);
    _blackboard.setRecord(record);

    final Id[] recordIds = new Id[] { record.getId() };
    final Id[] result = _mis.process(_blackboard, recordIds);
    assertNotNull(result);
    assertEquals(recordIds.length, result.length);

    final Literal literal = _blackboard.getLiteral(record.getId(), new Path("MimeType"));
    assertNotNull(literal);
    final String mimeType = literal.getStringValue();
    assertNotNull(mimeType);
    assertEquals(expectedMimeType, mimeType);
  }

  /**
   * Test the process method with an unknown extension.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testProcessUnknownExtension() throws Exception {
    final String idValue = "testId";
    final String content = "This is a simple test document. It contains no special format.";
    final String extension = "dummy";

    final Record record = createRecord(idValue, content, extension, null);
    _blackboard.setRecord(record);

    final Id[] recordIds = new Id[] { record.getId() };
    final Id[] result = _mis.process(_blackboard, recordIds);
    assertNotNull(result);
    assertEquals(recordIds.length, result.length);

    final Literal literal = _blackboard.getLiteral(record.getId(), new Path("MimeType"));
    assertNull(literal);
  }

  /**
   * Test the process method without an extension but with metadata.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testProcessMetadata() throws Exception {
    final String idValue = "testId";
    final String content = "This is a simple test document. It contains no special format.";
    final String expectedMimeType = "text/plain";
    final ArrayList<String> metadata = new ArrayList<String>();
    metadata.add("someproperty:somevalue");
    metadata.add("someotherproperty:somedifferentvalue");
    metadata.add("Content-Type:" + expectedMimeType + ";charset=UTF-8");
    metadata.add("yap:yav");

    final Record record = createRecord(idValue, content, null, metadata);
    _blackboard.setRecord(record);

    final Id[] recordIds = new Id[] { record.getId() };
    final Id[] result = _mis.process(_blackboard, recordIds);
    assertNotNull(result);
    assertEquals(recordIds.length, result.length);

    final Literal literal = _blackboard.getLiteral(record.getId(), new Path("MimeType"));
    assertNotNull(literal);
    final String mimeType = literal.getStringValue();
    assertNotNull(mimeType);
    assertEquals(expectedMimeType, mimeType);
  }

  /**
   * Creates a Record object for testing.
   * 
   * @param idValue
   *          the idValue
   * @param content
   *          the content
   * @param extension
   *          the file extension
   * @param metadata
   *          the metadata
   * @return a Record
   * @throws InvalidTypeException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           if any error occurs
   */
  private Record createRecord(String idValue, String content, String extension, List<String> metadata)
    throws InvalidTypeException, UnsupportedEncodingException {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("testDataSource", idValue);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);
    final MObject mobject = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(mobject);

    if (extension != null) {
      final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
      final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
      literal.setStringValue(extension);
      attribute.setLiteral(literal);
      mobject.setAttribute("FileExtension", attribute);
    }

    if (metadata != null) {
      final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
      for (final String stringValue : metadata) {
        final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
        literal.setStringValue(stringValue);
        attribute.addLiteral(literal);
      }
      mobject.setAttribute("MetaData", attribute);
    }
    record.setAttachment("Content", content.getBytes());
    return record;
  }
}
