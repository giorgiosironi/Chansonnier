/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * The Class DateConverter.
 */
public class DateConverter implements Converter {

  /**
   * The Constant DATE_PATTERN.
   */
  public static final String DATE_PATTERN = "dd.MM.yyyy HH:mm";

  /**
   * The Constant SHORT_DATE_PATTERN.
   */
  public static final String SHORT_DATE_PATTERN = "dd.MM.yyyy";

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  public Object convert(final Class type, final Object value) {
    if (value == null) {
      return null;
    } else if (type == Date.class) {
      return convertToDate(type, value);
    } else if (type == String.class) {
      return convertToString(type, value);
    }
    throw new ConversionException(String.format("Could not convert %s to %s", value.getClass().getName(), type
      .getName()));
  }

  /**
   * Convert to date.
   * 
   * @param type
   *          the type
   * @param value
   *          the value
   * 
   * @return the object
   */
  @SuppressWarnings("unchecked")
  protected Object convertToDate(final Class type, final Object value) {
    if (value instanceof String) {
      if ("".equals(value.toString())) {
        return null;
      }
      try {
        return (new SimpleDateFormat(DATE_PATTERN)).parse((String) value);
      } catch (final ParseException e) {
        try {
          return (new SimpleDateFormat(SHORT_DATE_PATTERN)).parse((String) value);
        } catch (final ParseException e1) {
          throw new ConversionException("Error converting String to Date");
        }
      }
    }
    throw new ConversionException(String.format("Could not convert %s to %s", value.getClass().getName(), type
      .getName()));
  }

  /**
   * Convert to string.
   * 
   * @param type
   *          the type
   * @param value
   *          the value
   * 
   * @return the object
   */
  @SuppressWarnings("unchecked")
  protected Object convertToString(final Class type, final Object value) {
    if (value instanceof Date) {
      final DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
      return dateFormat.format(value);
    } else {
      return value.toString();
    }
  }
}
