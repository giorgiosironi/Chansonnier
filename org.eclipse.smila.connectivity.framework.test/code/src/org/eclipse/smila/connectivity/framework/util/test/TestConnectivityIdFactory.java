/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util.test;

import java.util.Map;

import org.eclipse.smila.connectivity.framework.util.ConnectivityIdFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Test case for ConnectivityIdFactory.
 */
public class TestConnectivityIdFactory extends DeclarativeServiceTestCase {

  /**
   * Reference to a ConnectivityIdFactory instance.
   */
  private ConnectivityIdFactory _cif;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _cif = ConnectivityIdFactory.getInstance();
    assertNotNull(_cif);
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
   * Test ConnectivityIdFactory.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testConnectivityIdFactory() throws Exception {
    // prepare parameters
    final Attribute att1 = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    att1.setName("att1");
    final Literal literal1 = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    literal1.setStringValue("value1");
    att1.addLiteral(literal1);

    final Attribute att2 = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    att2.setName("att2");
    final Literal lit1 = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    lit1.setStringValue("value1");
    att2.addLiteral(lit1);
    final Literal lit2 = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    lit2.setStringValue("value2");
    att2.addLiteral(lit2);

    final String dataSourceId = "testDataSource";
    final Attribute[] simpleIdAttributes = { att1 };
    final Attribute[] complexIdAttributes = { att1, att2 };

    // create and check Id with simple key
    final Id id1 = _cif.createId(dataSourceId, simpleIdAttributes);
    assertNotNull(id1);
    assertEquals(dataSourceId, id1.getSource());
    final Key simpleKey = id1.getKey();
    assertNotNull(simpleKey);
    assertFalse(simpleKey.isCompositeKey());
    assertEquals(att1.getName(), simpleKey.getKeyName());
    final String simpleKeyValue = simpleKey.getKey();
    assertNotNull(simpleKeyValue);
    assertEquals(literal1.getStringValue(), simpleKeyValue);

    // create and check Id with complex key
    final Id id2 = _cif.createId(dataSourceId, complexIdAttributes);
    assertNotNull(id2);
    assertEquals(dataSourceId, id2.getSource());
    final Key complexKey = id2.getKey();
    assertNotNull(simpleKey);
    assertTrue(complexKey.isCompositeKey());
    assertNull(complexKey.getKeyName());
    assertNull(complexKey.getKey());
    final Map<String, String> keyValues = complexKey.getKeyValues();
    assertNotNull(keyValues);
    assertEquals(2, keyValues.size());
    final String keyValue1 = complexKey.getKey(att1.getName());
    assertNotNull(keyValue1);
    assertEquals(literal1.getStringValue(), keyValue1);
    final String keyValue2 = complexKey.getKey(att2.getName());
    assertNotNull(keyValue2);
    assertEquals(lit1.getStringValue() + "," + lit2.getStringValue(), keyValue2);
  }

  /**
   * Test exception handling.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testExceptions() throws Exception {
    final Attribute att1 = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    att1.setName("att1");
    final Literal literal1 = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    literal1.setStringValue("value1");
    att1.addLiteral(literal1);
    final String dataSourceId = "test";
    final Attribute[] idAttributes = { att1 };

    try {
      _cif.createId(null, idAttributes);
      fail("expected IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("Parameter dataSourceId must not be null or empty", e.getMessage());
    } catch (final Exception e) {
      fail("expected IllegalArgumentException");
    }

    try {
      _cif.createId("", idAttributes);
      fail("expected IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("Parameter dataSourceId must not be null or empty", e.getMessage());
    } catch (final Exception e) {
      fail("expected IllegalArgumentException");
    }

    try {
      _cif.createId(dataSourceId, null);
      fail("expected IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("Parameter idAttributes must not be null or empty", e.getMessage());
    } catch (final Exception e) {
      fail("expected IllegalArgumentException");
    }

    try {
      _cif.createId(dataSourceId, new Attribute[] {});
      fail("expected IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("Parameter idAttributes must not be null or empty", e.getMessage());
    } catch (final Exception e) {
      fail("expected IllegalArgumentException");
    }
  }
}
