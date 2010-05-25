/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.stax;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.dom.IdParser;
import org.eclipse.smila.datamodel.id.stax.IdReader;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.dom.RecordParser;

/**
 * StAX based Id reader. Should give better performance than the DOM based IdParser.
 * 
 * @author jschumacher
 * 
 */
public class RecordReader {

  /**
   * my object factory.
   */
  private RecordFactory _recordFactory;

  /**
   * my Id reader.
   */
  private IdReader _idReader;

  /**
   * local helper for parsing literals.
   */
  private final LiteralFormatHelper _literalHelper = new LiteralFormatHelper();

  /**
   * create default instance.
   */
  public RecordReader() {
    this(RecordFactory.DEFAULT_INSTANCE, IdFactory.DEFAULT_INSTANCE);
  }

  /**
   * @param recordFactory
   *          record factory to use.
   * @param idFactory
   *          Id factory to use.
   */
  public RecordReader(final RecordFactory recordFactory, final IdFactory idFactory) {
    _recordFactory = recordFactory;
    _idReader = new IdReader(idFactory);
  }

  /**
   * read Record list from the XML stream. The stream must be currently at the RecordList start tag.
   * 
   * @param staxReader
   *          source XML stream
   * @return Record list read from stream or an empty list, if stream is not currently at a RecordList start tag.
   * @throws XMLStreamException
   *           StAX error.
   */
  public List<Record> readRecords(final XMLStreamReader staxReader) throws XMLStreamException {
    final List<Record> records = new ArrayList<Record>();
    if (isStartTag(staxReader, RecordParser.TAG_RECORDLIST)) {
      staxReader.nextTag();
      while (isStartTag(staxReader, RecordParser.TAG_RECORD)) {
        records.add(readRecord(staxReader));
        staxReader.nextTag();
      }
    }
    return records;
  }

  /**
   * read Record from the XML stream. The stream must be currently at the Record start tag.
   * 
   * @param staxReader
   *          source XML stream
   * @return Record read from stream or null, if stream is not currently at a Record start tag.
   * @throws XMLStreamException
   *           StAX error.
   */
  public Record readRecord(final XMLStreamReader staxReader) throws XMLStreamException {
    Record record = null;
    if (isStartTag(staxReader, RecordParser.TAG_RECORD)) {
      record = _recordFactory.createRecord();
      staxReader.nextTag(); // go to next element (eventually Id)
      if (isStartTag(staxReader, IdParser.TAG_ID)) {
        record.setId(_idReader.readId(staxReader));
        staxReader.nextTag(); // move beyond </Id>
      }
      record.setMetadata(readMetadata(staxReader));
      readAttachments(staxReader, record);
    }
    return record;
  }

  /**
   * read MObject from the XML stream. The stream must be currently at the first attribute or annotation tag of the
   * MObject.
   * 
   * @param staxReader
   *          source XML stream
   * @return Record read from stream or null, if stream is not currently at a Record start tag.
   * @throws XMLStreamException
   *           StAX error.
   */
  private MObject readMetadata(final XMLStreamReader staxReader) throws XMLStreamException {
    final MObject mobject = _recordFactory.createMetadataObject();
    while (isStartTag(staxReader, RecordParser.TAG_ATTRIBUTE)) {
      readAttribute(staxReader, mobject);
      staxReader.nextTag();
    }
    readAnnotations(staxReader, mobject);
    return mobject;
  }

  /**
   * read an attribute from the XML stream and set it on the given {@link MObject}.
   * 
   * @param staxReader
   *          XML stream
   * @param mobject
   *          metadata object
   * @throws XMLStreamException
   *           StAX error.
   */
  private void readAttribute(final XMLStreamReader staxReader, final MObject mobject) throws XMLStreamException {
    final Attribute attribute = _recordFactory.createAttribute();
    final String attributeName = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_NAME);
    attribute.setName(attributeName);
    mobject.setAttribute(attributeName, attribute);
    staxReader.nextTag();
    readAnnotations(staxReader, attribute);
    while (isStartTag(staxReader, RecordParser.TAG_LITERAL)) {
      readLiteralElement(staxReader, attribute);
      staxReader.nextTag();
    }
    while (isStartTag(staxReader, RecordParser.TAG_OBJECT)) {
      staxReader.nextTag();
      attribute.addObject(readMetadata(staxReader));
      staxReader.nextTag();
    }
  }

  /**
   * read an L element and add {@link Literal}s to the given {@link Attribute}.
   * 
   * @param staxReader
   *          XML stream
   * @param attribute
   *          current attribute
   * @throws XMLStreamException
   *           StAX error
   */
  private void readLiteralElement(final XMLStreamReader staxReader, final Attribute attribute)
    throws XMLStreamException {
    final String defaultSemanticType = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_SEMANTICTYPE);
    staxReader.nextTag();
    Literal literal = null;
    while (isStartTag(staxReader, RecordParser.TAG_VALUE)) {
      literal = _recordFactory.createLiteral();
      String semanticType = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_SEMANTICTYPE);
      if (semanticType == null) {
        semanticType = defaultSemanticType;
      }
      literal.setSemanticType(semanticType);
      final String dataType = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_TYPE);
      final String stringValue = staxReader.getElementText();
      setLiteralValue(literal, dataType, stringValue);
      attribute.addLiteral(literal);
      staxReader.nextTag();
    }
    if (isStartTag(staxReader, RecordParser.TAG_ANNOTATION)) {
      if (literal == null) { // literal without value, this is actually allowed by the schema.
        literal = _recordFactory.createLiteral();
      }
      readAnnotations(staxReader, attribute);
    }
  }

  /**
   * read an annotations from the stream and add them to the given {@link Annotatable}.
   * 
   * @param staxReader
   *          XML stream
   * @param annotatable
   *          object to annotate
   * @throws XMLStreamException
   *           StAX error
   */
  private void readAnnotations(final XMLStreamReader staxReader, final Annotatable annotatable)
    throws XMLStreamException {
    while (isStartTag(staxReader, RecordParser.TAG_ANNOTATION)) {
      readAnnotation(staxReader, annotatable);
      staxReader.nextTag();
    }
  }

  /**
   * read an annotation from the stream and add it to the given {@link Annotatable}.
   * 
   * @param staxReader
   *          XML stream
   * @param annotatable
   *          object to annotate
   * @throws XMLStreamException
   *           StAX error
   */
  private void readAnnotation(final XMLStreamReader staxReader, final Annotatable annotatable)
    throws XMLStreamException {
    final Annotation annotation = _recordFactory.createAnnotation();
    final String annotationName = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_NAME);
    staxReader.nextTag();
    while (isStartTag(staxReader, RecordParser.TAG_VALUE)) {
      final String valueName = staxReader.getAttributeValue(null, RecordParser.ATTRIBUTE_NAME);
      final String value = staxReader.getElementText();
      if (valueName == null) {
        annotation.addAnonValue(value);
      } else {
        annotation.setNamedValue(valueName, value);
      }
      staxReader.nextTag();
    }
    while (isStartTag(staxReader, RecordParser.TAG_ANNOTATION)) {
      readAnnotation(staxReader, annotation);
      staxReader.nextTag();
    }
    annotatable.addAnnotation(annotationName, annotation);
  }

  /**
   * read attachment names from the XML stream.
   * 
   * @param staxReader
   *          source XML stream param record Record to add the attachments to.
   * @param record
   *          record to add attachments too.
   * @throws XMLStreamException
   *           StAX error.
   */
  private void readAttachments(final XMLStreamReader staxReader, final Record record) throws XMLStreamException {
    while (isStartTag(staxReader, RecordParser.TAG_ATTACHMENT)) {
      final String attachmentName = staxReader.getElementText();
      if (attachmentName != null && attachmentName.length() > 0) {
        record.setAttachment(attachmentName, null);
      }
      staxReader.nextTag();
    }
  }

  /**
   * set literal value from string according to datatype.
   * 
   * @param literal
   *          literal to write to.
   * @param dataType
   *          datatype
   * @param stringValue
   *          string value.
   */
  private void setLiteralValue(final Literal literal, final String dataType, final String stringValue) {
    if (StringUtils.isBlank(dataType)) {
      literal.setStringValue(stringValue);
    } else {
      try {
        if ("int".equals(dataType)) {
          literal.setIntValue(Long.valueOf(stringValue));
        } else if ("fp".equals(dataType)) {
          literal.setFpValue(Double.valueOf(stringValue));
        } else if ("bool".equals(dataType)) {
          literal.setBoolValue(Boolean.valueOf(stringValue));
        } else if ("date".equals(dataType)) {
          literal.setDateValue(_literalHelper.parseDate(stringValue));
        } else if ("time".equals(dataType)) {
          literal.setTimeValue(_literalHelper.parseTime(stringValue));
        } else if ("datetime".equals(dataType)) {
          literal.setDateTimeValue(_literalHelper.parseDateTime(stringValue));
        }
      } catch (final ParseException ex) { // forget about the datatype, use just string.
        literal.setStringValue(stringValue);
      }
    }
  }

  /**
   * 
   * @param staxReader
   *          source XML stream
   * @param tagName
   *          tag name
   * @return true if we are currently at a start tag with the specificied name
   */
  private boolean isStartTag(final XMLStreamReader staxReader, final String tagName) {
    return staxReader.isStartElement() && tagName.equals(staxReader.getLocalName());
  }

}
