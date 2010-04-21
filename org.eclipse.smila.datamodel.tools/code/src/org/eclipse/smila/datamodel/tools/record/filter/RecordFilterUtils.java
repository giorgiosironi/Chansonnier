/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.datamodel.tools.record.filter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.validation.Schema;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.DatamodelCopyUtils;
import org.eclipse.smila.datamodel.tools.record.filter.messages.Filter;
import org.eclipse.smila.datamodel.tools.record.filter.messages.RecordFilters;
import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.xml.SchemaUtils;
import org.xml.sax.SAXException;

/**
 * The Class RecordFilterHelper.
 */
public final class RecordFilterUtils {

  /** The Constant MASK_ANY. */
  private static final String MASK_ANY = "*";

  /** The Constant BUNDLE_ID. */
  private static final String BUNDLE_ID = "org.eclipse.smila.datamodel.tools";

  /** The Constant JAXB_PACKAGE. */
  private static final String JAXB_PACKAGE = "org.eclipse.smila.datamodel.tools.record.filter.messages";

  /** The Constant SCHEMA_LOCATION. */
  private static final String SCHEMA_LOCATION = "schemas/RecordFilters.xsd";

  /** The Constant SCHEMA. */
  private static final Schema SCHEMA;

  static {
    try {
      SCHEMA = SchemaUtils.loadSchema(BUNDLE_ID, SCHEMA_LOCATION);
    } catch (final SAXException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Does not instantiates a new record filter helper.
   */
  private RecordFilterUtils() {
  }

  /**
   * Unmarshall.
   * 
   * @param inputStream
   *          the input stream
   * 
   * @return the record filters
   * 
   * @throws RecordFilterLoadSaveException
   *           the record filter load exception
   */
  public static RecordFilters unmarshall(final InputStream inputStream) throws RecordFilterLoadSaveException {
    try {
      return (RecordFilters) JaxbUtils.unmarshall(JAXB_PACKAGE, RecordFilters.class.getClassLoader(), SCHEMA,
        inputStream);
    } catch (final Exception e) {
      throw new RecordFilterLoadSaveException(e);
    }

  }

  /**
   * Marshall.
   * 
   * @param recordFilters
   *          the record filters
   * @param outputStream
   *          the output stream
   * 
   * @throws RecordFilterLoadSaveException
   *           the record filter load save exception
   */
  public static void marshall(final RecordFilters recordFilters, final OutputStream outputStream)
    throws RecordFilterLoadSaveException {
    try {
      JaxbUtils.marshall(recordFilters, JAXB_PACKAGE, RecordFilters.class.getClassLoader(), SCHEMA, outputStream);
    } catch (final Throwable e) {
      throw new RecordFilterLoadSaveException(e);
    }
  }

  /**
   * Find filter.
   * 
   * @param filters
   *          the filters
   * @param name
   *          the name
   * 
   * @return the filter
   * 
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   */
  public static Filter findFilter(final RecordFilters filters, final String name)
    throws RecordFilterNotFoundException {
    if (name == null || "".equals(name)) {
      // get the first filter
      return filters.getFilter().get(0);
    }
    for (final Filter filter : filters.getFilter()) {
      if (filter.getName().equals(name)) {
        return filter;
      }
    }
    throw new RecordFilterNotFoundException(name);
  }

  /**
   * Filter.
   * 
   * @param filter
   *          the record filter
   * @param record
   *          the record
   * 
   * @return the record
   */
  public static Record filter(final Filter filter, final Record record) {
    final RecordFactory recordFactory = record.getFactory();
    final Record newRecord = recordFactory.createRecord();
    // clone id?
    newRecord.setId(record.getId());
    newRecord.setMetadata(filter(filter, record.getMetadata(), recordFactory));
    return newRecord;
  }

  /**
   * Filter.
   * 
   * @param filter
   *          the record filter
   * @param source
   *          the mobject
   * @param recordFactory
   *          the record factory
   * 
   * @return the m object
   */
  public static MObject filter(final Filter filter, final MObject source, final RecordFactory recordFactory) {
    if (source == null) {
      throw new IllegalArgumentException("source MObject cannot be null!");
    }
    if (recordFactory == null) {
      throw new IllegalArgumentException("recordFactory cannot be null!");
    }
    final MObject destination = recordFactory.createMetadataObject();
    destination.setSemanticType(source.getSemanticType());
    if (source.hasAttributes()) {
      final Iterator<String> iterator = source.getAttributeNames();
      while (iterator.hasNext()) {
        final String name = iterator.next();
        if (isAttributePassedThroughFilter(filter, name)) {
          final boolean doCopyAnnotations = isAttributeAnnotationsShouldBeCopied(filter, name);
          final Attribute attribute =
            DatamodelCopyUtils.cloneAttribute(source.getAttribute(name), recordFactory, doCopyAnnotations);
          destination.setAttribute(name, attribute);
        }
      }
    }
    if (source.hasAnnotations()) {
      final Iterator<String> iterator = source.getAnnotationNames();
      while (iterator.hasNext()) {
        final String name = iterator.next();
        if (isAnnotationPassedThroughFilter(filter, name)) {
          final Annotation annotation =
            DatamodelCopyUtils.cloneAnnotation(source.getAnnotation(name), recordFactory);
          destination.addAnnotation(name, annotation);
        }
      }
    }
    return destination;
  }

  /**
   * Checks if is attribute passed through filter.
   * 
   * @param recordFilter
   *          the record filter
   * @param name
   *          the name
   * 
   * @return true, if is attribute passed through filter
   */
  private static boolean isAttributePassedThroughFilter(final Filter recordFilter, final String name) {
    for (final org.eclipse.smila.datamodel.tools.record.filter.messages.Attribute filterAttribute : recordFilter
      .getAttribute()) {
      if (MASK_ANY.equals(filterAttribute.getName()) || filterAttribute.getName().equals(name)) {
        return true;
      }

    }
    return false;
  }

  /**
   * Checks if is attribute annotations should be copied.
   * 
   * @param recordFilter
   *          the record filter
   * @param name
   *          the name
   * 
   * @return true, if is attribute annotations should be copied
   */
  private static boolean isAttributeAnnotationsShouldBeCopied(final Filter recordFilter, final String name) {
    for (final org.eclipse.smila.datamodel.tools.record.filter.messages.Attribute filterAttribute : recordFilter
      .getAttribute()) {
      if (MASK_ANY.equals(filterAttribute.getName()) || filterAttribute.getName().equals(name)) {
        if (filterAttribute.isKeepAnnotations()) {
          return true;
        }
      }

    }
    return false;
  }

  /**
   * Checks if is annotation passed through filter.
   * 
   * @param recordFilter
   *          the record filter
   * @param name
   *          the name
   * 
   * @return true, if is annotation passed through filter
   */
  private static boolean isAnnotationPassedThroughFilter(final Filter recordFilter, final String name) {
    for (final org.eclipse.smila.datamodel.tools.record.filter.messages.Annotation filterAnnotation : recordFilter
      .getAnnotation()) {
      if (MASK_ANY.equals(filterAnnotation.getName()) || filterAnnotation.getName().equals(name)) {
        return true;
      }

    }
    return false;
  }

}
