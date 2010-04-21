/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *               Andreas Weber (empolis GmbH) - remove characters violating XML 1.1 spec when creating documents.
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.dom;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.id.dom.IdBuilder;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A utility class to create DOM representations of SMILA records.
 *
 * @author jschumacher
 */
public class RecordBuilder {

  /** SMILA Recird XML namespace URI, "http://www.eclipse.org/smila/record". */
  public static final String NAMESPACE_RECORD = RecordParser.NAMESPACE_RECORD;

  /** attribute "xmlns:rec" for specification if SMILA Id XML namespace URI. */
  public static final String ATTRIBUTE_XMLNSREC = XMLConstants.XMLNS_ATTRIBUTE; // + ":rec";

  /** namespace prefix "rec:" for Id XML elements. */
  public static final String PREFIX_REC = ""; // "rec:";

  /** qualified name of Record element: "rec:Record". */
  public static final String TAG_RECORD = PREFIX_REC + RecordParser.TAG_RECORD;

  /** qualified name of RecordList element: "rec:RecordList". */
  public static final String TAG_RECORDLIST = PREFIX_REC + RecordParser.TAG_RECORDLIST;

  /** qualified name of Attribute element: "rec:A". */

  public static final String TAG_ATTRIBUTE = PREFIX_REC + RecordParser.TAG_ATTRIBUTE;

  /** qualified name of Annotation element: "rec:An". */
  public static final String TAG_ANNOTATION = PREFIX_REC + RecordParser.TAG_ANNOTATION;

  /** qualified name of Value element: "rec:V". */
  public static final String TAG_VALUE = PREFIX_REC + RecordParser.TAG_VALUE;

  /** qualified name of Value element: "rec:L". */
  public static final String TAG_LITERAL = PREFIX_REC + RecordParser.TAG_LITERAL;

  /** qualified name of Value element: "rec:O". */
  public static final String TAG_OBJECT = PREFIX_REC + RecordParser.TAG_OBJECT;

  /** qualified name of Attachment element: "rec:Attachment". */
  public static final String TAG_ATTACHMENT = PREFIX_REC + RecordParser.TAG_ATTACHMENT;

  /** attribute name of version attribute: "version". */
  public static final String ATTRIBUTE_VERSION = RecordParser.ATTRIBUTE_VERSION;

  /** attribute name of name attribute: "n". */
  public static final String ATTRIBUTE_NAME = RecordParser.ATTRIBUTE_NAME;

  /** attribute name of type attribute: "type". */
  public static final String ATTRIBUTE_TYPE = RecordParser.ATTRIBUTE_TYPE;

  /** attribute name of semantic type attribute: "st". */
  public static final String ATTRIBUTE_SEMANTICTYPE = RecordParser.ATTRIBUTE_SEMANTICTYPE;

  /** version of Record XMLs created by this builder: "1.0". */
  public static final String SCHEMA_VERSION_RECORD = "1.0";

  /** Id builder to create id:ID elements. */
  private final IdBuilder _idBuilder;

  /** switch to true to add newlines for better readability, but poorer performance. */
  private final boolean _printPretty;

  /**
   * create new RecordBuilder.
   */
  public RecordBuilder() {
    this(false);
  }

  /**
   * create new RecordBuilder with custom printPretty flag.
   *
   * @param printPretty
   *          printPretty flag
   */
  public RecordBuilder(final boolean printPretty) {
    _printPretty = printPretty;
    _idBuilder = new IdBuilder(printPretty);
  }

  /**
   * Append a rec:Record element describing the given record to the given parent element. The record element is appended
   * as a new last child to the parent element. The element contains a namespace declaration for "xmlns:rec".
   *
   * @param parent
   *          the parent element to append to.
   * @param record
   *          the record to transform.
   *
   * @return the appended element
   */
  public Element appendRecord(final Element parent, final Record record) {
    final Document factory = parent.getOwnerDocument();
    newline(factory, parent);
    final Element recordElement = buildRecord(factory, record);
    recordElement.setAttribute(ATTRIBUTE_XMLNSREC, NAMESPACE_RECORD);
    parent.appendChild(recordElement);
    newline(factory, parent);
    return recordElement;

  }

  /**
   * Append a rec:RecordList element describing the given record list to the given parent element. The record list
   * element is appended as a new last child to the parent element. The element contains a namespace declaration for
   * "xmlns:rec".
   *
   * @param parent
   *          the parent element to append to.
   * @param records
   *          the record list to transform.
   *
   * @return the appended element
   */
  public Element appendRecordList(final Element parent, final Iterable<Record> records) {
    final Document factory = parent.getOwnerDocument();
    newline(factory, parent);
    final Element recordListElement = appendElement(factory, parent, TAG_RECORDLIST);
    for (final Record record : records) {
      final Element recordElement = buildRecord(factory, record);
      recordListElement.appendChild(recordElement);
      newline(factory, parent);
    }
    return recordListElement;
  }

  /**
   * Append a rec:RecordList element describing the given record list to the given parent element. The record list
   * element is appended as a new last child to the parent element. The element contains a namespace declaration for
   * "xmlns:rec".
   *
   * @param parent
   *          the parent element to append to.
   * @param records
   *          the record list to transform.
   *
   * @return the appended element
   */
  public Element appendRecordList(final Element parent, final Record[] records) {
    final Document factory = parent.getOwnerDocument();
    newline(factory, parent);
    final Element recordListElement = appendElement(factory, parent, TAG_RECORDLIST);
    for (final Record record : records) {
      final Element recordElement = buildRecord(factory, record);
      recordListElement.appendChild(recordElement);
      newline(factory, parent);
    }
    return recordListElement;
  }

  /**
   * build rec:Record element without namespace declaration.
   *
   * @param factory
   *          factory for creating DOM elements
   * @param record
   *          the record to transform
   *
   * @return the created element.
   */
  public Element buildRecord(final Document factory, final Record record) {
    final Element recordElement = factory.createElementNS(NAMESPACE_RECORD, TAG_RECORD);
    recordElement.setAttribute(ATTRIBUTE_VERSION, SCHEMA_VERSION_RECORD);
    if (record.getId() != null) {
      _idBuilder.appendId(recordElement, record.getId());
    }

    if (record.getMetadata() != null) {
      appendMetadata(factory, recordElement, record.getMetadata());
    }

    if (record.hasAttachments()) {
      final Iterator<String> attachmentNames = record.getAttachmentNames();
      while (attachmentNames.hasNext()) {
        final String attachmentName = attachmentNames.next();
        appendTextElement(factory, recordElement, TAG_ATTACHMENT, attachmentName);
      }
    }

    return recordElement;
  }

  /**
   * Builds the record as document element.
   *
   * @param factory
   *          the factory
   * @param record
   *          the record
   */
  public void buildRecordAsDocumentElement(final Document factory, final Record record) {
    final Element recordElement = buildRecord(factory, record);
    recordElement.setAttribute(ATTRIBUTE_VERSION, SCHEMA_VERSION_RECORD);
    recordElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, ATTRIBUTE_XMLNSREC, NAMESPACE_RECORD);
    factory.appendChild(recordElement);
  }

  /**
   * append Attribute and Annotation elements to parent to describe the specified metadata obejct.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          the parent element to append to.
   * @param metadata
   *          the metadata object to describe.
   */
  private void appendMetadata(final Document factory, final Element parentElement, final MObject metadata) {
    if (metadata.hasAttributes()) {
      final Iterator<String> names = metadata.getAttributeNames();
      while (names.hasNext()) {
        final String name = names.next();
        final Attribute attribute = metadata.getAttribute(name);
        appendAttribute(factory, parentElement, attribute);
      }
    }
    appendAnnotations(factory, parentElement, metadata);
  }

  /**
   * append Attribute to parent element.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          the parent element to append to.
   * @param attribute
   *          the possibly attribute object.
   */
  private void appendAttribute(final Document factory, final Element parentElement, final Attribute attribute) {
    final Element attributeElement = appendElement(factory, parentElement, TAG_ATTRIBUTE);
    attributeElement.setAttribute(ATTRIBUTE_NAME, attribute.getName());
    if (attribute.hasAnnotations()) {
      appendAnnotations(factory, attributeElement, attribute);
    }
    if (attribute.hasLiterals()) {
      Element unannotatedValuesElement = null;
      if (attribute.hasLiterals()) {
        for (final Literal literal : attribute.getLiterals()) {
          if (literal.hasAnnotations()) {
            final Element valueElement = appendElement(factory, attributeElement, TAG_LITERAL);
            appendLiteral(factory, valueElement, literal);
          } else {
            if (unannotatedValuesElement == null) {
              unannotatedValuesElement = appendElement(factory, attributeElement, TAG_LITERAL);
            }
            appendLiteral(factory, unannotatedValuesElement, literal);
          }
        }
      }
    }
    if (attribute.hasObjects()) {
      for (final MObject object : attribute.getObjects()) {
        final Element objectElement = appendElement(factory, attributeElement, TAG_OBJECT);
        if (object.getSemanticType() != null) {
          objectElement.setAttribute(ATTRIBUTE_SEMANTICTYPE, object.getSemanticType());
        }
        appendMetadata(factory, objectElement, object);
      }
    }
  }

  /**
   * append Literal to parent element.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          the parent element to append to.
   * @param literal
   *          the possibly attribute object.
   */
  private void appendLiteral(final Document factory, final Element parentElement, final Literal literal) {
    if (literal.getValue() != null) {
      final Element valueElement = appendTextElement(factory, parentElement, TAG_VALUE, literal.getStringValue());
      switch (literal.getDataType()) {
        case INT:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "int");
          break;
        case FP:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "fp");
          break;
        case BOOL:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "bool");
          break;
        case DATE:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "date");
          break;
        case TIME:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "time");
          break;
        case DATETIME:
          valueElement.setAttribute(ATTRIBUTE_TYPE, "datetime");
          break;
        default: // write nothing for strings
      }
      if (literal.getSemanticType() != null) {
        valueElement.setAttribute(ATTRIBUTE_SEMANTICTYPE, literal.getSemanticType());
      }
      appendAnnotations(factory, parentElement, literal);
    }
  }

  /**
   * append Annotations of specified object to parent element.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          the parent element to append to.
   * @param annotated
   *          the possibly annotated object.
   */
  private void appendAnnotations(final Document factory, final Element parentElement, final Annotatable annotated) {
    if (annotated.hasAnnotations()) {
      final Iterator<String> names = annotated.getAnnotationNames();
      while (names.hasNext()) {
        final String name = names.next();
        final Collection<Annotation> annotations = annotated.getAnnotations(name);
        for (final Annotation annotation : annotations) {
          appendAnnotation(factory, parentElement, name, annotation);
        }
      }
    }
  }

  /**
   * append Annotation to parent element.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          the parent element to append to.
   * @param name
   *          name of annotation
   * @param annotation
   *          the annotation to append.
   */
  private void appendAnnotation(final Document factory, final Element parentElement, final String name,
    final Annotation annotation) {
    final Element annotationElement = appendElement(factory, parentElement, TAG_ANNOTATION);
    annotationElement.setAttribute(ATTRIBUTE_NAME, name);
    if (annotation.hasAnonValues()) {
      for (final String value : annotation.getAnonValues()) {
        appendTextElement(factory, annotationElement, TAG_VALUE, value);
      }
    }
    if (annotation.hasNamedValues()) {
      final Iterator<String> valueNames = annotation.getValueNames();
      while (valueNames.hasNext()) {
        final String valueName = valueNames.next();
        final Element valueElement =
          appendTextElement(factory, annotationElement, TAG_VALUE, annotation.getNamedValue(valueName));
        valueElement.setAttribute(ATTRIBUTE_NAME, valueName);
      }
    }
    appendAnnotations(factory, annotationElement, annotation);
  }

  /**
   * create an empty element and append it to the parent element.
   *
   * @param factory
   *          factory for creating DOM elements.
   * @param parentElement
   *          element to append to
   * @param tag
   *          name of element
   *
   * @return new element
   */
  private Element appendElement(final Document factory, final Element parentElement, final String tag) {
    final Element element = factory.createElementNS(NAMESPACE_RECORD, tag);
    newline(factory, element);
    parentElement.appendChild(element);
    newline(factory, parentElement);
    return element;
  }

  /**
   * append an element containing a text node.
   *
   * @param factory
   *          the DOM element factory to use
   * @param element
   *          the element to append to
   * @param name
   *          the qualified name of the new element
   * @param text
   *          the text content to add
   *
   * @return the new element
   */
  private Element appendTextElement(final Document factory, final Element element, final String name,
    final String text) {
    final Element textElement = factory.createElementNS(NAMESPACE_RECORD, name);
    textElement.appendChild(factory.createTextNode(cleanText(text)));
    element.appendChild(textElement);
    newline(factory, element);
    return textElement;
  }

  /**
   * append a newline text element if printPretty is activated.
   *
   * @param factory
   *          factory to use.
   * @param element
   *          element to append to.
   */
  private void newline(final Document factory, final Element element) {
    if (_printPretty) {
      element.appendChild(factory.createTextNode("\n"));
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
