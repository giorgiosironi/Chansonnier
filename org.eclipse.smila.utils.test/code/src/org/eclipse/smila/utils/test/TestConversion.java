/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConversionException;
import org.eclipse.smila.utils.conversion.DefaultConversionUtils;

/**
 * The Class TestConversion.
 */
public class TestConversion extends TestCase {

  /**
   * Test integer conversion.
   */
  public void testIntegerConversion() {
    final String source = "1";
    final Object o = DefaultConversionUtils.convert(source, Integer.class);
    assertNotNull(o);
    assertEquals(o.getClass(), Integer.class);
    assertEquals(o.equals(1), true);
    final String backSource = DefaultConversionUtils.convert(o);
    assertEquals(source, backSource);
  }

  /**
   * Test date conversion null.
   */
  public void testDateConversionNull() {
    final Date date = (Date) DefaultConversionUtils.convert(null, Date.class);
    assertNull(date);
  }

  /**
   * Test string conversion null.
   */
  public void testStringConversionNull() {
    assertNull(DefaultConversionUtils.convert(null));
  }

  /**
   * Test date conversion1.
   */
  public void testDateConversion1() {
    // yyyy-MM-dd HH:mm:ss.SSS
    final String source = "2009-01-01 01:02:01.120";
    final Date date = (Date) DefaultConversionUtils.convert(source, Date.class);
    assertNotNull(date);
    final Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    assertEquals(calendar.get(Calendar.YEAR), 2009);
    assertEquals(calendar.get(Calendar.MONTH), 0);
    assertEquals(calendar.get(Calendar.DATE), 1);
    assertEquals(calendar.get(Calendar.HOUR), 1);
    assertEquals(calendar.get(Calendar.MINUTE), 2);
    assertEquals(calendar.get(Calendar.SECOND), 1);
    assertEquals(calendar.get(Calendar.MILLISECOND), 120);
    assertEquals(source, DefaultConversionUtils.convert(date));
  }

  /**
   * Test date conversion2.
   */
  public void testDateConversion2() {
    // yyyy-MM-dd HH:mm:ss
    final String source = "2009-01-01 01:02:01";
    final Date date = (Date) DefaultConversionUtils.convert(source, Date.class);
    assertNotNull(date);
    final Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    assertEquals(calendar.get(Calendar.YEAR), 2009);
    assertEquals(calendar.get(Calendar.MONTH), 0);
    assertEquals(calendar.get(Calendar.DATE), 1);
    assertEquals(calendar.get(Calendar.HOUR), 1);
    assertEquals(calendar.get(Calendar.MINUTE), 2);
    assertEquals(calendar.get(Calendar.SECOND), 1);
    assertEquals(calendar.get(Calendar.MILLISECOND), 0);
  }

  /**
   * Test date conversion3.
   */
  public void testDateConversion3() {
    // yyyy-MM-dd HH:mm
    final String source = "2009-01-01 01:02";
    final Date date = (Date) DefaultConversionUtils.convert(source, Date.class);
    assertNotNull(date);
    final Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    assertEquals(calendar.get(Calendar.YEAR), 2009);
    assertEquals(calendar.get(Calendar.MONTH), 0);
    assertEquals(calendar.get(Calendar.DATE), 1);
    assertEquals(calendar.get(Calendar.HOUR), 1);
    assertEquals(calendar.get(Calendar.MINUTE), 2);
    assertEquals(calendar.get(Calendar.SECOND), 0);
    assertEquals(calendar.get(Calendar.MILLISECOND), 0);
  }

  /**
   * Test date conversion4.
   */
  public void testDateConversion4() {
    // yyyy-MM-dd HH:mm
    final String source = "2009-01-01";
    final Date date = (Date) DefaultConversionUtils.convert(source, Date.class);
    assertNotNull(date);
    final Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    assertEquals(calendar.get(Calendar.YEAR), 2009);
    assertEquals(calendar.get(Calendar.MONTH), 0);
    assertEquals(calendar.get(Calendar.DATE), 1);
    assertEquals(calendar.get(Calendar.HOUR), 0);
    assertEquals(calendar.get(Calendar.MINUTE), 0);
    assertEquals(calendar.get(Calendar.SECOND), 0);
    assertEquals(calendar.get(Calendar.MILLISECOND), 0);
  }

  /**
   * Test date conversion error.
   */
  public void testDateConversionError() {
    final String source = "QQ";
    Date date = null;
    try {
      date = (Date) DefaultConversionUtils.convert(source, Date.class);
      throw new AssertionError("QQ was parsed as date");
    } catch (final ConversionException e) {
      ;// ok
    }
    assertNull(date);
  }

}
