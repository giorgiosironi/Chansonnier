/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.dom;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.dom.IdParser;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for creating SMILA records from DOM elements.
 * 
 * @author jschumacher
 * 
 */
public class RecordParser {
  /**
   * SMILA Record XML namespace URI, "http://www.eclipse.org/smila/record".
   */
  public static final String NAMESPACE_RECORD = "http://www.eclipse.org/smila/record";

  /**
   * local name of Record element: "Record".
   */
  public static final String TAG_RECORD = "Record";

  /**
   * local name of RecordList element: "RecordList".
   */
  public static final String TAG_RECORDLIST = "RecordList";

  /**
   * local name of Attribute element: "A".
   */

  public static final String TAG_ATTRIBUTE = "A";

  /**
   * local name of Annotation element: "An".
   */
  public static final String TAG_ANNOTATION = "An";

  /**
   * local name of Value element: "L".
   */
  public static final String TAG_LITERAL = "L";

  /**
   * local name of Value element: "O".
   */
  public static final String TAG_OBJECT = "O";

  /**
   * local name of Value element: "V".
   */
  public static final String TAG_VALUE = "V";

  /**
   * local name of Attachment element: "Attachment".
   */
  public static final String TAG_ATTACHMENT = "Attachment";

  /**
   * attribute name of version attribute: "version".
   */
  public static final String ATTRIBUTE_VERSION = "version";

  /**
   * attribute name of name attribute: "n".
   */
  public static final String ATTRIBUTE_NAME = "n";

  /**
   * attribute name of type attribute: "type".
   */
  public static final String ATTRIBUTE_TYPE = "t";

  /**
   * attribute name of semantic type attribute: "st".
   */
  public static final String ATTRIBUTE_SEMANTICTYPE = "st";

  /**
   * Id parser for creating Id objects from DOM elements.
   */
  private final IdParser _idParser;

  /**
   * Record factory for creating the record objects.
   */
  private final RecordFactory _recordFactory;

  /**
   * local helper for parsing literals.
   */
  private final LiteralFormatHelper _literalHelper = new LiteralFormatHelper();

  /**
   * Create parser using default factories for records and IDs.
   */
  public RecordParser() {
    _idParser = new IdParser();
    _recordFactory = RecordFactory.DEFAULT_INSTANCE;
  }

  /**
   * Create parser with custom factories for records and IDs.
   * 
   * @param recordFactory
   *          custom record factory.
   * @param idFactory
   *          custom id factory.
   */
  public RecordParser(final RecordFactory recordFactory, final IdFactory idFactory) {
    _idParser = new IdParser(idFactory);
    _recordFactory = recordFactory;
  }

  /**
   * find the first child rec:Record element the specified parent element and create a Record from it.
   * 
   * @param parentElement
   *          parent element of rec:Record
   * @return the record described by the first rec:Record element under the parent.
   */
  public Record parseRecordIn(final Element parentElement) {
    final NodeList children = parentElement.getChildNodes();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element recordElement = (Element) childNode;
          if (TAG_RECORD.equals(recordElement.getLocalName())) {
            return parseRecordFrom(recordElement);
          }
        }
      }
    }
    return null;
  }

  /**
   * Find all child rec:Record elements that are child of this specified element and parse the record it describes. If
   * no record element is found, null is returned.
   * 
   * @param parentElement
   *          the element under which to search for recordss
   * @return all records found under the parentElement.
   */
  public List<Record> parseRecordsIn(final Element parentElement) {
    final NodeList children = parentElement.getChildNodes();
    final List<Record> records = new ArrayList<Record>();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element recordElement = (Element) childNode;
          if (TAG_RECORD.equals(recordElement.getLocalName())) {
            records.add(parseRecordFrom(recordElement));
          }
        }
      }
    }
    return records;
  }

  /**
   * create a record from a rec:Record element.
   * 
   * @param recordElement
   *          an rec:Record element.
   * @return the record described by this element.
   */
  public Record parseRecordFrom(final Element recordElement) {
    final Record record = _recordFactory.createRecord();
    record.setId(_idParser.parseIdIn(recordElement));
    record.setMetadata(parseMetadataObject(recordElement));
    parseAttachments(record, recordElement);
    return record;
  }

  /**
   * parse an rec:Annotation element and create an Annotation object.
   * 
   * @param annotatable
   *          object to annotate.
   * @param annotationElement
   *          the element to parse
   */
  public void parseAnnotation(final Annotatable annotatable, final Element annotationElement) {
    final String annotationName = annotationElement.getAttribute(ATTRIBUTE_NAME);
    final Annotation annotation = _recordFactory.createAnnotation();

    final NodeList children = annotationElement.getChildNodes();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element childElement = (Element) childNode;
          if (TAG_VALUE.equals(childElement.getLocalName())) {
            final String value = childElement.getTextContent();
            final String name = childElement.getAttribute(ATTRIBUTE_NAME);
            if (StringUtils.isBlank(name)) {
              annotation.addAnonValue(value);
            } else {
              annotation.setNamedValue(name, value);
            }
          } else if (TAG_ANNOTATION.equals(childElement.getLocalName())) {
            parseAnnotation(annotation, childElement);
          }
        }
      }
    }
    annotatable.addAnnotation(annotationName, annotation);
  }

  /**
   * parse a metadata object from the given element.
   * 
   * @param element
   *          element to parse.
   * @return parsed metadata object, if appropriate objects could be found. else null.
   */
  private MObject parseMetadataObject(final Element element) {
    // fixed to always create metadata object
    final MObject mObject = _recordFactory.createMetadataObject();
    final NodeList children = element.getChildNodes();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element childElement = (Element) childNode;
          if (TAG_ATTRIBUTE.equals(childElement.getLocalName())) {
            parseAttribute(mObject, childElement);
          } else if (TAG_ANNOTATION.equals(childElement.getLocalName())) {
            parseAnnotation(mObject, childElement);
          }
        }
      }
    }
    return mObject;
  }

  /**
   * parse Attribute from given rec:Attribute element and attach to given metadata object.
   * 
   * @param mObject
   *          metadata to add to
   * @param attributeElement
   *          rec:Attribute element to parse.
   */
  private void parseAttribute(final MObject mObject, final Element attributeElement) {
    final String name = attributeElement.getAttribute(ATTRIBUTE_NAME);
    Attribute attribute = null;
    if (mObject.hasAttribute(name)) {
      attribute = mObject.getAttribute(name);
    } else {
      attribute = _recordFactory.createAttribute();
    }
    final NodeList children = attributeElement.getChildNodes();
    if (children != null && children.getLength() > 0) {
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element childElement = (Element) childNode;
          if (TAG_LITERAL.equals(childElement.getLocalName())) {
            parseLiterals(attribute, childElement);
          } else if (TAG_OBJECT.equals(childElement.getLocalName())) {
            parseAttributeObject(attribute, attributeElement, childElement);
          } else if (TAG_ANNOTATION.equals(childElement.getLocalName())) {
            parseAnnotation(attribute, childElement);
          }
        }
      }
    }
    mObject.setAttribute(name, attribute);
  }

  /**
   * parse metadata object as an attribute value.
   * 
   * @param attribute
   *          attribute to add to.
   * @param attributeElement
   *          XML element of attribute.
   * @param childElement
   *          XML element of metadata object.
   */
  private void parseAttributeObject(final Attribute attribute, final Element attributeElement,
    final Element childElement) {
    final MObject attributeMObject = parseMetadataObject(childElement);
    final String semanticType = attributeElement.getAttribute(ATTRIBUTE_SEMANTICTYPE);
    if (!StringUtils.isBlank(semanticType)) {
      attributeMObject.setSemanticType(semanticType);
    }
    attribute.addObject(attributeMObject);
  }

  /**
   * parse literals from L element to attribute.
   * 
   * @param attribute
   *          target attribute
   * @param literalsElement
   *          L element
   */
  private void parseLiterals(final Attribute attribute, final Element literalsElement) {
    String defaultSemanticType = literalsElement.getAttribute(ATTRIBUTE_SEMANTICTYPE);
    if (StringUtils.isBlank(defaultSemanticType)) {
      defaultSemanticType = null;
    }
    final NodeList children = literalsElement.getChildNodes();
    if (children != null && children.getLength() > 0) {
      Literal annotationLiteral = null;
      for (int i = 0; i < children.getLength(); i++) {
        final Node childNode = children.item(i);
        if (childNode instanceof Element) {
          final Element childElement = (Element) childNode;
          if (TAG_VALUE.equals(childElement.getLocalName())) {
            final Literal newLiteral = parseAttributeLiteral(attribute, childElement, defaultSemanticType);
            if (annotationLiteral == null) {
              annotationLiteral = newLiteral;
            }
          } else if (annotationLiteral != null && TAG_ANNOTATION.equals(childElement.getLocalName())) {
            parseAnnotation(annotationLiteral, childElement);
          }
        }
      }
    }
  }

  /**
   * create literal object as an attribute value.
   * 
   * @param attribute
   *          attribute to add to
   * @param childElement
   *          XML element of literal
   * @param defaultSemanticType
   *          default semantic type set in literal
   * @return created literal
   */
  private Literal parseAttributeLiteral(final Attribute attribute, final Element childElement,
    final String defaultSemanticType) {
    final Literal literal = parseLiteralValue(childElement);
    if (literal.getSemanticType() == null) {
      literal.setSemanticType(defaultSemanticType);
    }
    attribute.addLiteral(literal);
    return literal;
  }

  /**
   * parse a Literal from the given rec:V element.
   * 
   * @param literalElement
   *          rec:Literal element
   * @return the literal parsed from this element
   */
  private Literal parseLiteralValue(final Element literalElement) {
    final Literal literal = _recordFactory.createLiteral();
    final String semanticType = literalElement.getAttribute(ATTRIBUTE_SEMANTICTYPE);
    if (!StringUtils.isBlank(semanticType)) {
      literal.setSemanticType(semanticType);
    }
    final String dataType = literalElement.getAttribute(ATTRIBUTE_TYPE);
    final String stringValue = literalElement.getTextContent();
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
    return literal;

  }

  /**
   * parse attachment elements.
   * 
   * @param record
   *          record to attach to
   * @param recordElement
   *          element ro parse from.
   */
  private void parseAttachments(final Record record, final Element recordElement) {
    final NodeList attachmentElements = recordElement.getElementsByTagNameNS(NAMESPACE_RECORD, TAG_ATTACHMENT);
    if (attachmentElements != null && attachmentElements.getLength() > 0) {
      for (int i = 0; i < attachmentElements.getLength(); i++) {
        final Element attachmentElement = (Element) attachmentElements.item(i);
        final String attachmentName = attachmentElement.getTextContent();
        record.setAttachment(attachmentName, null);
      }
    }
  }

}
