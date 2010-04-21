/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.util.test;

import java.io.PrintWriter;
import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.smila.processing.pipelets.xmlprocessing.util.NullPrintWriter;

/**
 * Test class for NullPrintWriter.
 */
public class TestNullPrintWriter extends TestCase {

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {

  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {

  }

  /**
   * Test methods with return values.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testRetvalMethods() throws Exception {
    final String stringValue = "a string value";
    final Object obj = new Object();
    final CharSequence charSeq = new StringBuffer();
    final Locale locale = Locale.GERMANY;

    final NullPrintWriter writer = new NullPrintWriter();
    assertNotNull(writer);

    PrintWriter retval = writer.append('a');
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.append(charSeq, 0, 1);
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.append(charSeq);
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.format(locale, stringValue, obj);
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.format(stringValue, obj);
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.printf(locale, stringValue, obj);
    assertNotNull(retval);
    assertEquals(writer, retval);

    retval = writer.printf(stringValue, obj);
    assertNotNull(retval);
    assertEquals(writer, retval);

    final boolean hasError = writer.checkError();
    assertEquals(false, hasError);
  }

  /**
   * Test methods without return values.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testVoideMethods() throws Exception {
    final boolean booleanValue = true;
    final char charValue = 'c';
    final char[] charArray = { 'a', 'b', 'c' };
    final double doubleValue = Double.MAX_VALUE;
    final float floateValue = Float.MAX_VALUE;
    final int intValue = Integer.MAX_VALUE;
    final long longValue = Long.MAX_VALUE;
    final String stringValue = "a string value";
    final Object obj = new Object();

    final NullPrintWriter writer = new NullPrintWriter();
    assertNotNull(writer);

    writer.close();
    writer.flush();

    writer.print(booleanValue);
    writer.print(charValue);
    writer.print(charArray);
    writer.print(doubleValue);
    writer.print(floateValue);
    writer.print(intValue);
    writer.print(longValue);
    writer.print(obj);
    writer.print(stringValue);
    
    writer.println();
    writer.println(booleanValue);
    writer.println(charValue);
    writer.println(charArray);
    writer.println(doubleValue);
    writer.println(floateValue);
    writer.println(intValue);
    writer.println(longValue);
    writer.println(obj);
    writer.println(stringValue);
    
    writer.write(charArray, 0, 1);
    writer.write(charArray);
    writer.write(intValue);
    writer.write(stringValue, 0, 1);
    writer.write(stringValue);
  }
}
