/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.collections.MultiValueMap;

/**
 * The Class TestCollections.
 */
public class TestCollections extends DeclarativeServiceTestCase {

  /**
   * The Constant VALUE_1.
   */
  private static final String VALUE_1 = "two";

  /**
   * The Constant VALUE_0.
   */
  private static final String VALUE_0 = "one";

  /**
   * The Constant KEY.
   */
  private static final String KEY = "key";

  /**
   * Test multi value map.
   */
  public void testMultiValueMap() {
    final MultiValueMap<String, String> map = new MultiValueMap<String, String>();
    final List<String> values = new ArrayList<String>();
    values.add(VALUE_0);
    values.add(VALUE_1);
    map.put(KEY, values);
    List<String> returnedValues = map.get(KEY);
    assertNotNull(returnedValues);
    assertEquals(returnedValues.size(), 2);
    assertTrue(Arrays.equals(values.toArray(), returnedValues.toArray()));
    map.addKey(KEY);
    returnedValues = map.getValues(KEY);
    assertNotNull(returnedValues);
    assertEquals(returnedValues.size(), 0);
  }

  /**
   * Test multi value map2.
   */
  public void testMultiValueMapWithInitialSize() {
    final MultiValueMap<String, String> map = new MultiValueMap<String, String>(2);
    final List<String> values = new ArrayList<String>();
    values.add(VALUE_0);
    values.add(VALUE_1);
    map.put(KEY, values);
    List<String> returnedValues = map.get(KEY);
    assertNotNull(returnedValues);
    assertEquals(returnedValues.size(), 2);
    assertTrue(Arrays.equals(values.toArray(), returnedValues.toArray()));
    map.addKey(KEY);
    returnedValues = map.getValues(KEY);
    assertNotNull(returnedValues);
    assertEquals(returnedValues.size(), 0);
  }

}
