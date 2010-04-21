/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.conversion;

import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

/**
 * The Class DefaultConversion.
 */
public final class DefaultConversionUtils {

  /**
   * The Constant CUB.
   */
  private static final ConvertUtilsBean CUB = new ConvertUtilsBean();

  static {
    CUB.deregister(String.class);
    CUB.register(new StringConverter(), String.class);
    CUB.deregister(java.util.Date.class);
    CUB.register(new DateConverter(), java.util.Date.class);
  }

  /**
   * formatter to create and parse standard string representations of DateTime values.
   */
  private static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * The Constant FORMAT_DATETIME2.
   */
  private static final String FORMAT_DATETIME2 = "yyyy-MM-dd HH:mm:ss";

  /**
   * The Constant FORMAT_DATETIME3.
   */
  private static final String FORMAT_DATETIME3 = "yyyy-MM-dd HH:mm";

  /**
   * The Constant FORMAT_DATETIME4.
   */
  private static final String FORMAT_DATETIME4 = "yyyy-MM-dd";

  /**
   * prevents instantiating default conversion utils.
   */
  private DefaultConversionUtils() {

  }

  /**
   * Convert.
   * 
   * @param source
   *          the source
   * @param clazz
   *          the clazz
   * 
   * @return the object
   */
  @SuppressWarnings("unchecked")
  public static Object convert(final String source, final Class clazz) {
    return CUB.convert(source, clazz);
  }

  /**
   * Convert.
   * 
   * @param value
   *          the value
   * 
   * @return the string
   */
  public static String convert(final Object value) {
    return CUB.convert(value);
  }

  /**
   * The Class DateConverter.
   */
  private static class DateConverter implements Converter {
    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Object convert(final Class class1, final Object obj) {
      if (obj == null) {
        return null;
      }
      final String v = obj.toString();
      try {
        return new SimpleDateFormat(FORMAT_DATETIME).parse(v);
      } catch (final Throwable e) {
        try {
          return new SimpleDateFormat(FORMAT_DATETIME2).parse(v);
        } catch (final Throwable e1) {
          try {
            return new SimpleDateFormat(FORMAT_DATETIME3).parse(v);
          } catch (final Throwable e2) {
            try {
              return new SimpleDateFormat(FORMAT_DATETIME4).parse(v);
            } catch (final Throwable e3) {
              throw new ConversionException("Unable to parse date " + v);
            }
          }
        }
      }
    }
  }

  /**
   * The Class StringConverter.
   */
  private static class StringConverter implements Converter {

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Object convert(final Class class1, final Object obj) {
      if (obj == null) {
        return null;
      }
      if (java.util.Date.class.isAssignableFrom(obj.getClass())) {
        return new SimpleDateFormat(FORMAT_DATETIME).format(obj);
      }
      return obj.toString();
    }

  }

}
