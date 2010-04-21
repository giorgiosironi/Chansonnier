/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.stax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.id.stax.IdWriter;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.dom.RecordBuilder;

/**
 * StAX based Id writer. Should give better performance than the DOM based IdBuilder.
 *
 * @author jschumacher
 *
 */
public class RecordWriter {

  /**
   * if true "print pretty", i.e. add newlines after each tag.
   */
  private boolean _printPretty;

  /**
   * to use for Id writing.
   */
  private IdWriter _idWriter;

  /**
   * create default instance.
   */
  public RecordWriter() {
    this(false);
  }

  /**
   * @param printPretty
   *          set to true to add newlines after each element tag.
   */
  public RecordWriter(final boolean printPretty) {
    _printPretty = printPretty;
    _idWriter = new IdWriter(printPretty);

  }

  /**
   * Append a rec:Record element describing the given record to the given XML stream. The element contains a namespace
   * declaration for xmlns:rec.
   *
   * @param staxWriter
   *          target XML stream
   * @param record
   *          the record to write
   * @throws XMLStreamException
   *           StAX error
   */
  public void writeRecord(final XMLStreamWriter staxWriter, final Record record) throws XMLStreamException {
    writeRecord(staxWriter, record, true);
  }

  /**
   * Append a rec:RecordList element describing the given record list to the given XML stream. The element contains a
   * namespace declaration for xmlns:rec.
   *
   * @param staxWriter
   *          target XML stream
   * @param records
   *          the record list to transform.
   * @throws XMLStreamException
   *           StAX error
   *
   */
  public void writeRecordList(final XMLStreamWriter staxWriter, final Iterable<Record> records)
    throws XMLStreamException {
    staxWriter.setDefaultNamespace(RecordBuilder.NAMESPACE_RECORD);
    writeStartElement(staxWriter, RecordBuilder.TAG_RECORDLIST);
    staxWriter.writeDefaultNamespace(RecordBuilder.NAMESPACE_RECORD);
    newline(staxWriter);
    for (final Record record : records) {
      writeRecord(staxWriter, record, false);
    }
    writeEndElement(staxWriter);
  }

  /**
   * write a record to an XML stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param record
   *          the record to write
   * @param addNamespace
   *          add xmlns attribute
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeRecord(final XMLStreamWriter staxWriter, final Record record, final boolean addNamespace)
    throws XMLStreamException {
    staxWriter.setDefaultNamespace(RecordBuilder.NAMESPACE_RECORD);
    writeStartElement(staxWriter, RecordBuilder.TAG_RECORD);
    if (addNamespace) {
      staxWriter.writeDefaultNamespace(RecordBuilder.NAMESPACE_RECORD);
    }
    staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_VERSION, RecordBuilder.SCHEMA_VERSION_RECORD);
    newline(staxWriter);
    if (record.getId() != null) {
      _idWriter.writeId(staxWriter, record.getId());
      staxWriter.setDefaultNamespace(RecordBuilder.NAMESPACE_RECORD);
    }

    if (record.getMetadata() != null) {
      writeMetadata(staxWriter, record.getMetadata());
    }
    if (record.hasAttachments()) {
      final Iterator<String> attachmentNames = record.getAttachmentNames();
      while (attachmentNames.hasNext()) {
        final String attachmentName = attachmentNames.next();
        writeTextElement(staxWriter, RecordBuilder.TAG_ATTACHMENT, attachmentName);
      }
    }
    writeEndElement(staxWriter);
  }

  /**
   * append Attribute and Annotation elements to stream to describe the specified metadata object.
   *
   * @param staxWriter
   *          target XML stream
   * @param metadata
   *          the metadata object to describe.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeMetadata(final XMLStreamWriter staxWriter, final MObject metadata) throws XMLStreamException {
    if (metadata.hasAttributes()) {
      final Iterator<String> names = metadata.getAttributeNames();
      while (names.hasNext()) {
        final String name = names.next();
        final Attribute attribute = metadata.getAttribute(name);
        writeAttribute(staxWriter, attribute);
      }
    }
    writeAnnotations(staxWriter, metadata);
  }

  /**
   * append Attribute to stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param attribute
   *          the possibly attribute object.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeAttribute(final XMLStreamWriter staxWriter, final Attribute attribute)
    throws XMLStreamException {
    writeStartElement(staxWriter, RecordBuilder.TAG_ATTRIBUTE);
    staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_NAME, attribute.getName());
    newline(staxWriter);
    if (attribute.hasAnnotations()) {
      writeAnnotations(staxWriter, attribute);
    }
    if (attribute.hasLiterals()) {
      List<Literal> annotatedLiterals = null;
      boolean unnanotatedLiteralsStarted = false;
      if (attribute.hasLiterals()) {
        for (final Literal literal : attribute.getLiterals()) {
          if (literal.hasAnnotations()) {
            if (annotatedLiterals == null) {
              annotatedLiterals = new ArrayList<Literal>();
            }
            annotatedLiterals.add(literal);
          } else {
            if (!unnanotatedLiteralsStarted) {
              writeStartElement(staxWriter, RecordBuilder.TAG_LITERAL);
              newline(staxWriter);
              unnanotatedLiteralsStarted = true;
            }
            writeLiteral(staxWriter, literal);
          }
        }
      }
      if (unnanotatedLiteralsStarted) {
        writeEndElement(staxWriter);
      }
      if (annotatedLiterals != null) {
        for (final Literal literal : attribute.getLiterals()) {
          writeStartElement(staxWriter, RecordBuilder.TAG_LITERAL);
          newline(staxWriter);
          writeLiteral(staxWriter, literal);
          writeEndElement(staxWriter);
        }
      }
    }
    if (attribute.hasObjects()) {
      for (final MObject object : attribute.getObjects()) {
        writeStartElement(staxWriter, RecordBuilder.TAG_OBJECT);
        if (object.getSemanticType() != null) {
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_SEMANTICTYPE, object.getSemanticType());
        }
        writeMetadata(staxWriter, object);
        writeEndElement(staxWriter);
      }
    }
    writeEndElement(staxWriter);
  }

  /**
   * append Literal to stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param literal
   *          the possibly attribute object.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeLiteral(final XMLStreamWriter staxWriter, final Literal literal) throws XMLStreamException {
    if (literal.getValue() != null) {
      writeStartElement(staxWriter, RecordBuilder.TAG_VALUE);
      switch (literal.getDataType()) {
        case INT:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "int");
          break;
        case FP:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "fp");
          break;
        case BOOL:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "bool");
          break;
        case DATE:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "date");
          break;
        case TIME:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "time");
          break;
        case DATETIME:
          staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_TYPE, "datetime");
          break;
        default: // write nothing for strings
      }
      if (literal.getSemanticType() != null) {
        staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_SEMANTICTYPE, literal.getSemanticType());
      }
      writeText(staxWriter, literal.getStringValue());
      writeEndElement(staxWriter);
      writeAnnotations(staxWriter, literal);
    }
  }

  /**
   * append Annotations of specified object to stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param annotated
   *          the possibly annotated object.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeAnnotations(final XMLStreamWriter staxWriter, final Annotatable annotated)
    throws XMLStreamException {
    if (annotated.hasAnnotations()) {
      final Iterator<String> names = annotated.getAnnotationNames();
      while (names.hasNext()) {
        final String name = names.next();
        final Collection<Annotation> annotations = annotated.getAnnotations(name);
        for (final Annotation annotation : annotations) {
          writeAnnotation(staxWriter, name, annotation);
        }
      }
    }
  }

  /**
   * append Annotation to stream.
   *
   * @param staxWriter
   *          target XML stream
   * @param name
   *          name of annotation
   * @param annotation
   *          the annotation to append.
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeAnnotation(final XMLStreamWriter staxWriter, final String name, final Annotation annotation)
    throws XMLStreamException {
    writeStartElement(staxWriter, RecordBuilder.TAG_ANNOTATION);
    staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_NAME, name);
    newline(staxWriter);
    if (annotation.hasAnonValues()) {
      for (final String value : annotation.getAnonValues()) {
        writeTextElement(staxWriter, RecordBuilder.TAG_VALUE, value);
      }
    }
    if (annotation.hasNamedValues()) {
      final Iterator<String> valueNames = annotation.getValueNames();
      while (valueNames.hasNext()) {
        final String valueName = valueNames.next();
        writeStartElement(staxWriter, RecordBuilder.TAG_VALUE);
        staxWriter.writeAttribute(RecordBuilder.ATTRIBUTE_NAME, valueName);
        writeText(staxWriter, annotation.getNamedValue(valueName));
        writeEndElement(staxWriter);
      }
    }
    writeAnnotations(staxWriter, annotation);
    writeEndElement(staxWriter);
  }

  /**
   * write a text element with given name and value.
   *
   * @param staxWriter
   *          target XML stream
   * @param tagName
   *          tag name
   * @param value
   *          content characters
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeTextElement(final XMLStreamWriter staxWriter, final String tagName, final String value)
    throws XMLStreamException {
    writeStartElement(staxWriter, tagName);
    writeText(staxWriter, value);
    writeEndElement(staxWriter);
  }

  /**
   * write a text content.
   *
   * @param staxWriter
   *          target XML stream
   * @param value
   *          content characters
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeText(final XMLStreamWriter staxWriter, final String value) throws XMLStreamException {
    staxWriter.writeCharacters(cleanText(value));
  }

  /**
   * start an element with the tag name and the default namespace.
   *
   * @param staxWriter
   *          target XML stream
   * @param tagName
   *          tag name
   * @throws XMLStreamException
   *           StAX error
   */
  private void writeStartElement(final XMLStreamWriter staxWriter, final String tagName) throws XMLStreamException {
    staxWriter.writeStartElement(RecordBuilder.NAMESPACE_RECORD, tagName);
  }

  /**
   * end the current element, optionally append a newline.
   *
   * @param staxWriter
   *          target XML stream
   * @throws XMLStreamException
   *           StAX error.
   */
  private void writeEndElement(final XMLStreamWriter staxWriter) throws XMLStreamException {
    staxWriter.writeEndElement();
    newline(staxWriter);
  }

  /**
   * append a newline text if printPretty is activated.
   *
   * @param staxWriter
   *          target XML stream
   * @throws XMLStreamException
   *           StAX error
   */
  private void newline(final XMLStreamWriter staxWriter) throws XMLStreamException {
    if (_printPretty) {
      staxWriter.writeCharacters("\n");
    }
  }

  /**
   * @param text
   *          input text (XML)
   * @return XML text cleaned from characters forbidden in XML 1.1 specification
   */
  protected static String cleanText(final String text) {
    // (Andreas Weber) from XML 1.1 spec:
    // Due to potential problems with APIs, #x0 is still forbidden both directly and as a character reference.
    // (jschumacher) this uses StringUtils.replace and not String.replace(char, char) because the latter always
    // creates a new string instance, while the first return the input string instance as result, if the string
    // to be replaced is not contained (at least in commons.lang 2.4). It's better to safe unnecessary instance
    // creations in the most cases (no replacement at all) than to have a bit faster replacement method in very
    // rare cases.
    return StringUtils.replace(text, "\u0000", " ");
  }

}
