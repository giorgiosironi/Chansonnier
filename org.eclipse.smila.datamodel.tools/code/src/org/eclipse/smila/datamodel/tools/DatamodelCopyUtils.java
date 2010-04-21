/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.datamodel.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * The Class CloneUtils.
 */
public final class DatamodelCopyUtils {

  /**
   * Does not instantiates.
   */
  private DatamodelCopyUtils() {

  }

  /**
   * Clone m object.
   * 
   * @param source
   *          the source
   * @param factory
   *          the factory
   * 
   * @return the m object
   */
  public static MObject cloneMObject(final MObject source, final RecordFactory factory) {
    if (source == null) {
      return null;
    }
    final MObject destination = factory.createMetadataObject();
    destination.setSemanticType(source.getSemanticType());
    copyAnnotations(source, destination, factory);
    copyAttributes(source, destination, factory);
    return destination;
  }

  /**
   * Copy attributes.
   * 
   * @param source
   *          the source
   * @param destination
   *          the destination
   * @param factory
   *          the factory
   */
  public static void copyAttributes(final MObject source, final MObject destination, final RecordFactory factory) {
    final Iterator<String> attributeNameIterator = source.getAttributeNames();
    while (attributeNameIterator.hasNext()) {
      final String name = attributeNameIterator.next();
      final Attribute sourceAttribute = source.getAttribute(name);
      final Attribute attribute = cloneAttribute(sourceAttribute, factory);
      destination.setAttribute(name, attribute);
    }
  }

  /**
   * Copy annotations.
   * 
   * @param source
   *          the source
   * @param destination
   *          the destination
   * @param factory
   *          the record factory
   */
  public static void copyAnnotations(final Annotatable source, final Annotatable destination,
    final RecordFactory factory) {
    final Iterator<String> annotationNameIterator = source.getAnnotationNames();
    while (annotationNameIterator.hasNext()) {
      final String annotationName = annotationNameIterator.next();
      final Collection<Annotation> sourceAnnotations = source.getAnnotations(annotationName);
      for (Annotation sourceAnnotation : sourceAnnotations) {
        final Annotation annotation = cloneAnnotation(sourceAnnotation, factory);
        destination.addAnnotation(annotationName, annotation);
      }
    }
  }

  /**
   * Copy literals.
   * 
   * @param source
   *          the source
   * @param destination
   *          the destination
   * @param factory
   *          the factory
   */
  public static void copyLiterals(final Attribute source, final Attribute destination, final RecordFactory factory) {
    if (source == null || destination == null) {
      throw new IllegalArgumentException("Source and destination cannot be null!");
    }
    destination.removeLiterals();
    List<Literal> destinationLiterals = null;
    if (source.hasLiterals()) {
      destinationLiterals = new ArrayList<Literal>();
      for (final Literal literal : source.getLiterals()) {
        destinationLiterals.add(cloneLiteral(literal, factory));
      }
    }
    if (destinationLiterals != null) {
      destination.setLiterals(destinationLiterals);
    }
  }

  /**
   * Clone annotation.
   * 
   * @param source
   *          the source annotation
   * @param factory
   *          the record factory
   * 
   * @return the annotation
   */
  public static Annotation cloneAnnotation(final Annotation source, final RecordFactory factory) {
    if (source == null) {
      throw new IllegalArgumentException("annotation source cannot be null!");
    }
    if (factory == null) {
      throw new IllegalArgumentException("record factory cannot be null!");
    }
    final Annotation destination = factory.createAnnotation();
    // copy named values
    if (source.hasNamedValues()) {
      final Iterator<String> namesIterator = source.getValueNames();
      while (namesIterator.hasNext()) {
        final String name = namesIterator.next();
        final String value = source.getNamedValue(name);
        destination.setNamedValue(name, value);
      }
    }
    // copy anonymous values
    if (source.hasAnonValues()) {
      for (final String anonymousValue : source.getAnonValues()) {
        destination.addAnonValue(anonymousValue);
      }
    }
    copyAnnotations(source, destination, factory);
    return destination;
  }

  /**
   * Clone attribute.
   * 
   * @param source
   *          the source attribute
   * @param factory
   *          the factory
   * 
   * @return the attribute
   */
  public static Attribute cloneAttribute(final Attribute source, final RecordFactory factory) {
    return cloneAttribute(source, factory, true);
  }

  /**
   * Clone attribute.
   * 
   * @param source
   *          the source
   * @param factory
   *          the factory
   * @param doCopyAnnotation
   *          the do copy annotation
   * 
   * @return the attribute
   */
  public static Attribute cloneAttribute(final Attribute source, final RecordFactory factory,
    final boolean doCopyAnnotation) {
    if (source == null) {
      return null;
    }
    final Attribute destination = factory.createAttribute();
    destination.setName(source.getName());
    if (doCopyAnnotation) {
      copyAnnotations(source, destination, factory);
    }
    copyLiterals(source, destination, factory);
    final List<MObject> sourceMObjects = source.getObjects();
    destination.removeObjects();
    if (sourceMObjects != null && !sourceMObjects.isEmpty()) {
      final List<MObject> destinationMObjects = new ArrayList<MObject>();
      for (final MObject object : sourceMObjects) {
        destinationMObjects.add(cloneMObject(object, factory));
      }
      destination.setObjects(destinationMObjects);
    }
    return destination;
  }

  /**
   * Clone literal.
   * 
   * @param source
   *          the source
   * @param factory
   *          the factory
   * 
   * @return the literal
   */
  public static Literal cloneLiteral(final Literal source, final RecordFactory factory) {
    if (source == null) {
      return null;
    }
    final Literal destination = factory.createLiteral();
    destination.setSemanticType(source.getSemanticType());
    switch (source.getDataType()) {
      case BOOL:
        destination.setBoolValue(source.getBoolValue());
        break;
      case DATE:
        destination.setDateValue(source.getDateValue());
        break;
      case DATETIME:
        destination.setDateTimeValue(source.getDateTimeValue());
        break;
      case TIME:
        destination.setTimeValue(source.getTimeValue());
        break;
      case FP:
        destination.setFpValue(source.getFpValue());
        break;
      case STRING:
        destination.setStringValue(source.getStringValue());
        break;
      case INT:
        destination.setIntValue(source.getIntValue());
        break;
      default:
        throw new RuntimeException("There is no copy procedure described for the type " + source.getDataType());
    }
    copyAnnotations(source, destination, factory);
    return destination;

  }
}
