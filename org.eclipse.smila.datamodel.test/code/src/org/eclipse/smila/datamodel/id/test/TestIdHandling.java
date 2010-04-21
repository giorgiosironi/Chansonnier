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

package org.eclipse.smila.datamodel.id.test;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdHandlingException;

/**
 * some Id functionality tests.
 * 
 * @author jschumacher
 * 
 */
public class TestIdHandling extends TestCase {

  /**
   * check if using Ids as hash keys works correctly.
   * 
   * @throws Exception
   *           test fails.
   */
  public void testHashMapping() throws Exception {
    final HashMap<Id, String> testMap = new HashMap<Id, String>();
    testMap.put(IdCreator.createSourceObjectIdSimpleKey(), "createSourceObjectIdSimpleKey");
    testMap.put(IdCreator.createSourceObjectIdSimpleNamedKey(), "createSourceObjectIdSimpleNamedKey");
    testMap.put(IdCreator.createSourceObjectIdCompositeKey(), "createSourceObjectIdCompositeKey");
    testMap.put(IdCreator.createElement1Id(), "createElementId_1");
    testMap.put(IdCreator.createElement2Id(), "createElementId_2");
    testMap.put(IdCreator.createFragment1Id(), "createFragmentId_1");
    testMap.put(IdCreator.createFragment2Id(), "createFragmentId_2");
    testMap.put(IdCreator.createElement1Fragment1Id(), "createElementFragmentId_1_1");
    testMap.put(IdCreator.createElement1Fragment2Id(), "createElementFragmentId_1_2");
    testMap.put(IdCreator.createElement2Fragment1Id(), "createElementFragmentId_2_1");
    testMap.put(IdCreator.createElement2Fragment2Id(), "createElementFragmentId_2_2");

    assertEquals(testMap.get(IdCreator.createSourceObjectIdSimpleKey()), "createSourceObjectIdSimpleKey");
    assertEquals(testMap.get(IdCreator.createSourceObjectIdSimpleNamedKey()), "createSourceObjectIdSimpleNamedKey");
    assertEquals(testMap.get(IdCreator.createSourceObjectIdCompositeKey()), "createSourceObjectIdCompositeKey");
    assertEquals(testMap.get(IdCreator.createElement1Id()), "createElementId_1");
    assertEquals(testMap.get(IdCreator.createElement2Id()), "createElementId_2");
    assertEquals(testMap.get(IdCreator.createFragment1Id()), "createFragmentId_1");
    assertEquals(testMap.get(IdCreator.createFragment2Id()), "createFragmentId_2");
    assertEquals(testMap.get(IdCreator.createElement1Fragment1Id()), "createElementFragmentId_1_1");
    assertEquals(testMap.get(IdCreator.createElement1Fragment2Id()), "createElementFragmentId_1_2");
    assertEquals(testMap.get(IdCreator.createElement2Fragment1Id()), "createElementFragmentId_2_1");
    assertEquals(testMap.get(IdCreator.createElement2Fragment2Id()), "createElementFragmentId_2_2");
  }

  /**
   * test if unallowed splitting operations throw correct exceptions.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSplittingExceptions() throws Exception {
    Id id = IdCreator.createFragment1Id();
    tryForbiddenElementSplit(id);
    id = IdCreator.createFragment2Id();
    tryForbiddenElementSplit(id);
    id = IdCreator.createElement1Fragment1Id();
    tryForbiddenElementSplit(id);
    id = IdCreator.createElement2Fragment1Id();
    tryForbiddenElementSplit(id);
    id = IdCreator.createElement1Fragment2Id();
    tryForbiddenElementSplit(id);
    id = IdCreator.createElement2Fragment2Id();
    tryForbiddenElementSplit(id);
  }

  /**
   * split id by element and check exceptions.
   * 
   * @param id
   *          id to test
   */
  private void tryForbiddenElementSplit(Id id) {
    try {
      id.createElementId("bla");
      fail("exception expected");
    } catch (IdHandlingException ex) {
      ex = null; // fine
    } catch (Exception ex) {
      fail("wrong exception");
    }
  }

  /**
   * test creating of compound ids.
   * 
   * @throws Exception
   *           test fails
   */
  public void testCompounding() throws Exception {
    Id id = IdCreator.createElement2Fragment2Id();
    id = id.createCompoundId();
    assertEquals(IdCreator.createElement2Fragment1Id(), id);
    id = id.createCompoundId();
    assertEquals(IdCreator.createElement2Id(), id);
    id = id.createCompoundId();
    assertEquals(IdCreator.createElement1Id(), id);
    id = id.createCompoundId();
    assertEquals(IdCreator.createSourceObjectIdSimpleKey(), id);
    try {
      id.createCompoundId();
      fail("exception expected");
    } catch (Exception ex) {
      assertTrue(ex instanceof IdHandlingException);
    }
  }
}
