/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;

/**
 * test elements of the Literal API default implementation.
 * 
 * @author jschumacher
 * 
 */
public class TestLiterals extends TestCase {
  /**
   * helper for parsing date/time values.
   */
  private final LiteralFormatHelper _literalHelper = new LiteralFormatHelper();

  /**
   * test literal value access methods.
   */
  public void testGetMethods() {
    final Record record = RecordCreator.createRecordLiteralAttributes();
    final MObject metadata = record.getMetadata();

    Literal literal = metadata.getAttribute("single value").getLiteral();
    assertEquals(literal.getValue(), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getTimeValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("int value").getLiteral();
    assertEquals(literal.getValue(), literal.getIntValue());
    assertEquals(literal.getValue().toString(), literal.getStringValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getTimeValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("fp value").getLiteral();
    assertEquals(literal.getValue(), literal.getFpValue());
    assertEquals(literal.getValue().toString(), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getTimeValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("bool value").getLiteral();
    assertEquals(literal.getValue(), literal.getBoolValue());
    assertEquals(literal.getValue().toString(), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getTimeValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("date value").getLiteral();
    assertEquals(literal.getValue(), literal.getDateValue());
    assertEquals(_literalHelper.formatDate(literal.getDateValue()), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getTimeValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("time value").getLiteral();
    assertEquals(literal.getValue(), literal.getTimeValue());
    assertEquals(_literalHelper.formatTime(literal.getTimeValue()), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getDateTimeValue());

    literal = metadata.getAttribute("datetime value").getLiteral();
    assertEquals(literal.getValue(), literal.getDateTimeValue());
    assertEquals(_literalHelper.formatDateTime(literal.getDateTimeValue()), literal.getStringValue());
    assertNull(literal.getIntValue());
    assertNull(literal.getFpValue());
    assertNull(literal.getBoolValue());
    assertNull(literal.getDateValue());
    assertNull(literal.getTimeValue());
  }

  /**
   * test literal used as hash keys.
   */
  public void testLiteralForHash() {
    final Record record = RecordCreator.createRecordLiteralAttributes();
    final MObject metadata = record.getMetadata();

    final HashMap<Literal, Object> map = new HashMap<Literal, Object>();

    Literal literal = metadata.getAttribute("single value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("int value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("fp value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("bool value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("date value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("time value").getLiteral();
    map.put(literal, literal.getValue());
    literal = metadata.getAttribute("datetime value").getLiteral();
    map.put(literal, literal.getValue());

    for (Literal key : map.keySet()) {
      assertEquals(key.getValue(), map.get(key));
    }

    for (Literal key : map.keySet()) {
      map.put(key, key.toString());
    }

    for (Literal key : map.keySet()) {
      assertEquals(key.toString(), map.get(key));
    }

  }

  /**
   * test some special cases about equality of literals.
   */
  public void testLiteralEquality() {
    final Literal l1 = RecordCreator.FACTORY.createLiteral();
    final Literal l2 = RecordCreator.FACTORY.createLiteral();
    assertEquals(l1, l2);
  }

  /**
   * test generic value setting method.
   * 
   * @throws InvalidTypeException
   *           error
   */
  public void testValueSetting() throws InvalidTypeException {
    final Literal literal = RecordCreator.FACTORY.createLiteral();
    literal.setValue("test");
    assertEquals(Literal.DataType.STRING, literal.getDataType());
    literal.setValue(1L);
    assertEquals(Literal.DataType.INT, literal.getDataType());
    literal.setValue(Short.valueOf((short) 1));
    assertEquals(Literal.DataType.INT, literal.getDataType());
    literal.setValue(Math.PI);
    assertEquals(Literal.DataType.FP, literal.getDataType());
    literal.setValue((float) Math.PI);
    assertEquals(Literal.DataType.FP, literal.getDataType());
    literal.setValue(new BigDecimal(Math.PI));
    assertEquals(Literal.DataType.FP, literal.getDataType());
    literal.setValue(true);
    assertEquals(Literal.DataType.BOOL, literal.getDataType());
    literal.setValue(new Date());
    assertEquals(Literal.DataType.DATETIME, literal.getDataType());
    try {
      literal.setValue(new byte[0]);
      fail("exception expected");
    } catch (InvalidTypeException ex) {
      ex = null;
    } catch (Exception ex) {
      fail("wrong exception caught");
    }
  }
}
