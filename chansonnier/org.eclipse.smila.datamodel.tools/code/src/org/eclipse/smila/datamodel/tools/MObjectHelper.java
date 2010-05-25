/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.tools;

import java.util.Collection;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * Connectivity MObject Helper.
 */
public final class MObjectHelper {

  /**
   * dummy constructor.
   */
  private MObjectHelper() {

  }

   /**
   * Adds simple literal attribute.
   * 
   * @param factory
   *          the record factory
   * @param mObject
   *          the m object
   * @param attributeName
   *          the attribute name
   * @param attributeValue
   *          the attribute value
   * 
   * @throws InvalidTypeException
   *           the invalid type exception
   */
  public static void addSimpleLiteralAttribute(final RecordFactory factory, final MObject mObject,
    final String attributeName, final Object attributeValue) throws InvalidTypeException {
    final Attribute attribute = factory.createAttribute();
    attribute.setName(attributeName);
    final Literal literal = factory.createLiteral();
    literal.setValue(attributeValue);
    attribute.addLiteral(literal);
    mObject.setAttribute(attributeName, attribute);
  }

  /**
   * Adds the list of literals to attribute.
   * 
   * @param factory
   *          the factory
   * @param mObject
   *          the m object
   * @param attributeName
   *          the attribute name
   * @param values
   *          the values
   * 
   * @throws InvalidTypeException
   *           the invalid type exception
   */
  public static void addLiteralArrayAttribute(final RecordFactory factory, final MObject mObject,
    final String attributeName, final Object[] values) throws InvalidTypeException {
    final Attribute attribute = factory.createAttribute();
    attribute.setName(attributeName);
    for (Object value : values) {
      final Literal literal = factory.createLiteral();
      literal.setValue(value);
      attribute.addLiteral(literal);
    }
    mObject.setAttribute(attributeName, attribute);
  }

  /**
   * Adds the MObject containing attributes with names and literal values from given NameValuePairs array.
   * 
   * @param factory
   *          the factory
   * @param mObject
   *          the m object
   * @param attributeName
   *          the attribute name
   * @param nameValuePairs
   *          the name value pairs
   * @throws InvalidTypeException
   *           InvalidTypeException
   */
  public static void addNameValuePairsAttribute(final RecordFactory factory, final MObject mObject,
    final String attributeName, final NameValuePair[] nameValuePairs) throws InvalidTypeException {
    final Attribute attribute = factory.createAttribute();
    attribute.setName(attributeName);
    final MObject attributeMObject = factory.createMetadataObject();
    for (NameValuePair nameValuePair : nameValuePairs) {
      final Attribute valueAttribute = factory.createAttribute();
      valueAttribute.setName(nameValuePair.getName());
      final Literal literal = factory.createLiteral();
      literal.setValue(nameValuePair.getValue());
      valueAttribute.addLiteral(literal);
      attributeMObject.setAttribute(nameValuePair.getName(), valueAttribute);
    }
    attribute.setObject(attributeMObject);
    mObject.setAttribute(attributeName, attribute);
  }

  /**
   * Glue literals.
   * 
   * @param recordAttribute
   *          the record attribute
   * 
   * @return the string
   */
  public static String glueLiterals(final Attribute recordAttribute) {
    final StringBuilder sb = new StringBuilder();
    if (recordAttribute != null) {
      final Collection<Literal> values = recordAttribute.getLiterals();
      if (!values.isEmpty()) {
        boolean notFirst = false;
        for (final Literal literal : values) {
          if (notFirst) {
            sb.append(',');
          } else {
            notFirst = true;
          }
          sb.append(literal.getStringValue());
        }
      }
    }
    return sb.toString();
  }

  // private static final void processAnnotations(final Annotatable annotatable) {
  // final Iterator<String> annotationNameIterator = annotatable.getAnnotationNames();
  // if (annotationNameIterator == null) {
  // return;
  // }
  // // sort
  // final List<String> annotationNames = new ArrayList<String>();
  // while (annotationNameIterator.hasNext()) {
  // annotationNames.add(annotationNameIterator.next());
  // }
  // Collections.sort(annotationNames);
  // // sorted by name
  // for (final String annotationName : annotationNames) {
  // final Annotation annotation = annotatable.getAnnotation(annotationName);
  // processAnnotations(annotation);
  // }
  // }
}
