/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.test;

import org.eclipse.smila.datamodel.id.test.IdCreator;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.impl.DefaultRecordFactoryImpl;

/**
 * utility class to create record objects for tests.
 * 
 * @author jschumacher
 * 
 */
public final class RecordCreator {

  /**
   * Factory to create test records.
   */
  public static final RecordFactory FACTORY = new DefaultRecordFactoryImpl();

  /**
   * helper for parsing date/time values.
   */
  private static final LiteralFormatHelper LITERAL_HELPER = new LiteralFormatHelper();

  /**
   * id factory used to create Ids.
   */
  private RecordCreator() {
  }

  /**
   * Creates the test record.
   * 
   * @param dataSourceID
   *          the data source id
   * @param keyvalue
   *          the keyvalue
   * 
   * @return the record
   */
  public static Record createTestRecord(final String dataSourceID, final String keyvalue) {
    final Record record = FACTORY.createRecord();
    record.setId(IdCreator.createSourceObjectIdSimpleKey(dataSourceID, keyvalue));
    final MObject metadata = record.getMetadata();

    final Attribute attribute = FACTORY.createAttribute();
    Literal value = FACTORY.createLiteral();
    value.setStringValue("my first value");
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    final int intValue = 42;
    value.setIntValue(intValue);
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setFpValue(Math.PI);
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setBoolValue(true);
    attribute.addLiteral(value);

    metadata.setAttribute("attribute1", attribute);

    // final Annotation annotation = FACTORY.createAnnotation();
    // annotation.addAnonValue("my first anonymous value");
    // annotation.addAnonValue("my second anonymous value");
    // annotation.setNamedValue("name1", "my first named value");
    // annotation.setNamedValue("name2", "my second named value");
    // metadata.addAnnotation("annotation1", annotation);

    // record.setAttachment("attachment1", "my very first attachment".getBytes());
    return record;

  }

  /**
   * @return a test record.
   */
  public static Record createTestRecord1() {
    final Record record = FACTORY.createRecord();
    record.setId(IdCreator.createSourceObjectIdSimpleKey());
    final MObject metadata = FACTORY.createMetadataObject();
    record.setMetadata(metadata);

    final Attribute attribute = FACTORY.createAttribute();
    Literal value = FACTORY.createLiteral();
    value.setStringValue("my first value");
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    final int intValue = 42;
    value.setIntValue(intValue);
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setFpValue(Math.PI);
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setBoolValue(true);
    attribute.addLiteral(value);
    try {
      value = FACTORY.createLiteral();
      value.setDateValue(LITERAL_HELPER.parseDate("2000-01-01"));
      attribute.addLiteral(value);
    } catch (Exception ex) {
      ex = null;
    }
    try {
      value = FACTORY.createLiteral();
      value.setTimeValue(LITERAL_HELPER.parseTime("12:34:56.789"));
      attribute.addLiteral(value);
    } catch (Exception ex) {
      ex = null;
    }
    try {
      value = FACTORY.createLiteral();
      value.setDateTimeValue(LITERAL_HELPER.parseDateTime("2000-01-01 12:34:56.789"));
      attribute.addLiteral(value);
    } catch (Exception ex) {
      ex = null;
    }
    metadata.setAttribute("attribute1", attribute);

    final Annotation annotation = FACTORY.createAnnotation();
    annotation.addAnonValue("my first anonymous value");
    annotation.addAnonValue("my second anonymous value");
    annotation.setNamedValue("name1", "my first named value");
    annotation.setNamedValue("name2", "my second named value");
    metadata.addAnnotation("annotation1", annotation);

    record.setAttachment("attachment1", "my very first attachment".getBytes());

    return record;
  }

  /**
   * @return a test record with literal attribute values, no annotations, no attachments.
   */
  public static Record createRecordLiteralAttributes() {
    final Record record = FACTORY.createRecord();
    record.setId(IdCreator.createSourceObjectIdSimpleKey());
    final MObject metadata = FACTORY.createMetadataObject();

    Attribute attribute = FACTORY.createAttribute();
    Literal value = FACTORY.createLiteral();
    value.setStringValue("value");
    attribute.addLiteral(value);
    metadata.setAttribute("single value", attribute);

    attribute = FACTORY.createAttribute();
    value = FACTORY.createLiteral();
    value.setStringValue("first value");
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setStringValue("second value");
    attribute.addLiteral(value);
    value = FACTORY.createLiteral();
    value.setStringValue("third value");
    attribute.addLiteral(value);
    metadata.setAttribute("multi value", attribute);

    attribute = FACTORY.createAttribute();
    value = FACTORY.createLiteral();
    value.setIntValue(2);
    attribute.addLiteral(value);
    metadata.setAttribute("int value", attribute);

    attribute = FACTORY.createAttribute();
    value = FACTORY.createLiteral();
    value.setFpValue(Math.PI);
    attribute.addLiteral(value);
    metadata.setAttribute("fp value", attribute);

    attribute = FACTORY.createAttribute();
    value = FACTORY.createLiteral();
    value.setBoolValue(true);
    attribute.addLiteral(value);
    metadata.setAttribute("bool value", attribute);

    try {
      attribute = FACTORY.createAttribute();
      value = FACTORY.createLiteral();
      value.setDateValue(LITERAL_HELPER.parseDate("2000-01-01"));
      attribute.addLiteral(value);
      metadata.setAttribute("date value", attribute);
    } catch (Exception ex) {
      ex = null;
    }
    try {
      attribute = FACTORY.createAttribute();
      value = FACTORY.createLiteral();
      value.setTimeValue(LITERAL_HELPER.parseTime("12:34:56.789"));
      attribute.addLiteral(value);
      metadata.setAttribute("time value", attribute);
    } catch (Exception ex) {
      ex = null;
    }
    try {
      attribute = FACTORY.createAttribute();
      value = FACTORY.createLiteral();
      value.setDateTimeValue(LITERAL_HELPER.parseDateTime("2000-01-01 12:34:56.789"));
      attribute.addLiteral(value);
      metadata.setAttribute("datetime value", attribute);
    } catch (Exception ex) {
      ex = null;
    }

    record.setMetadata(metadata);
    return record;
  }

  /**
   * @return a record with several subobject attributes, no annotations, no attachments.
   */
  public static Record createRecordObjectAttributes() {
    final Record record = FACTORY.createRecord();
    record.setId(IdCreator.createSourceObjectIdSimpleKey());
    final MObject metadata = FACTORY.createMetadataObject();

    MObject subobject = FACTORY.createMetadataObject();
    Attribute attribute = FACTORY.createAttribute();
    Literal value = FACTORY.createLiteral();
    value.setStringValue("level 1.1 value");
    attribute.addLiteral(value);
    subobject.setAttribute("value", attribute);
    attribute = FACTORY.createAttribute();
    attribute.addObject(subobject);
    metadata.setAttribute("level 1.1", attribute);

    value = FACTORY.createLiteral();
    value.setStringValue("level 2.2 value");
    attribute = FACTORY.createAttribute();
    attribute.addLiteral(value);
    subobject = FACTORY.createMetadataObject();
    subobject.setAttribute("value", attribute);
    attribute = FACTORY.createAttribute();
    attribute.addObject(subobject);
    subobject = FACTORY.createMetadataObject();
    subobject.setAttribute("level 2.2", attribute);
    attribute = FACTORY.createAttribute();
    attribute.addObject(subobject);
    metadata.setAttribute("level 2.1", attribute);

    value = FACTORY.createLiteral();
    value.setStringValue("multi object 1 value");
    Attribute subattribute = FACTORY.createAttribute();
    subattribute.addLiteral(value);
    subobject = FACTORY.createMetadataObject();
    subobject.setAttribute("value", subattribute);
    attribute = FACTORY.createAttribute();
    attribute.addObject(subobject);
    value = FACTORY.createLiteral();
    value.setStringValue("multi object 2 value");
    subattribute = FACTORY.createAttribute();
    subattribute.addLiteral(value);
    subobject = FACTORY.createMetadataObject();
    subobject.setAttribute("value", subattribute);
    attribute.addObject(subobject);
    metadata.setAttribute("multi object", attribute);

    record.setMetadata(metadata);
    return record;
  }
  
  /**
   * @return a record with nested annotations.
   */
  public static Record createRecordAnnotations() {
    final Record record = FACTORY.createRecord();
    record.setId(IdCreator.createSourceObjectIdSimpleKey());
    final MObject metadata = FACTORY.createMetadataObject();
    
    Annotation annotation = RecordCreator.FACTORY.createAnnotation();
    annotation.addAnonValue("1");
    annotation.addAnonValue("2");
    metadata.setAnnotation("a1", annotation);
    
    annotation = RecordCreator.FACTORY.createAnnotation();
    annotation.addAnonValue("1");
    annotation.addAnonValue("2");
    annotation.addAnonValue("3");    
    metadata.addAnnotation("a1", annotation);
    
    final Annotation subAnnotation = RecordCreator.FACTORY.createAnnotation();
    subAnnotation.addAnonValue("1.1");
    subAnnotation.addAnonValue("1.2");
    
    annotation.addAnnotation("sub", subAnnotation);
    metadata.setAnnotation("a2", annotation);
    
    record.setMetadata(metadata);
    return record;
  }
}
