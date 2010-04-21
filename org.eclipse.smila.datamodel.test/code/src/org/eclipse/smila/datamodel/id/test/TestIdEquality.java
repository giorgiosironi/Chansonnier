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

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.id.Id;

/**
 * Test cases for Id equals, hashcode and id hash methods.
 * 
 * @author jschumacher
 * 
 */
public class TestIdEquality extends TestCase {
  /**
   * test if the two ids are equals, have same hashcode and id hash.
   * 
   * @param id1
   *          id1
   * @param id2
   *          id2
   */
  public static void checkIdEquality(Id id1, Id id2) {
    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(id1.getIdHash(), id2.getIdHash());
    System.out.println("IdString: " + id1.toString());
    System.out.println("IdHash: " + id1.getIdHash());
  }

  /**
   * test if the two ids are not equals, and have different id hashes.
   * 
   * @param id1
   *          id1
   * @param id2
   *          id2
   */
  public static void checkIdInequality(Id id1, Id id2) {
    assertFalse(id1.equals(id2));
    assertFalse(id1.getIdHash().equals(id2.getIdHash()));
  }

  /**
   * test SourceObjectIdSimpleKey.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdSimpleKey() throws Exception {
    final Id id1 = IdCreator.createSourceObjectIdSimpleKey();
    checkIdEquality(id1, IdCreator.createSourceObjectIdSimpleKey());

    checkIdInequality(id1, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id1, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id1, IdCreator.createElement1Id());
    checkIdInequality(id1, IdCreator.createElement2Id());
    checkIdInequality(id1, IdCreator.createFragment1Id());
    checkIdInequality(id1, IdCreator.createFragment2Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id1, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id1, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test SourceObjectIdSimpleNamedKey.
   * 
   * @throws Exception
   *           test fails
   */

  public void testSourceObjectIdSimpleNamedKey() throws Exception {
    final Id id = IdCreator.createSourceObjectIdSimpleNamedKey();
    checkIdEquality(id, IdCreator.createSourceObjectIdSimpleNamedKey());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test SourceObjectIdCompositeKey.
   * 
   * @throws Exception
   *           test fails
   */
  public void testSourceObjectIdCompositeKey() throws Exception {
    final Id id = IdCreator.createSourceObjectIdCompositeKey();
    checkIdEquality(id, IdCreator.createSourceObjectIdCompositeKey());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element1Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement1Id() throws Exception {
    final Id id = IdCreator.createElement1Id();
    checkIdEquality(id, IdCreator.createElement1Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element2Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement2Id() throws Exception {
    final Id id = IdCreator.createElement2Id();
    checkIdEquality(id, IdCreator.createElement2Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Fragment1Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testFragment1Id() throws Exception {
    final Id id = IdCreator.createFragment1Id();
    checkIdEquality(id, IdCreator.createFragment1Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Fragment2Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testFragment2Id() throws Exception {
    final Id id1 = IdCreator.createFragment2Id();
    checkIdEquality(id1, IdCreator.createFragment2Id());

    checkIdInequality(id1, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id1, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id1, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id1, IdCreator.createElement1Id());
    checkIdInequality(id1, IdCreator.createElement2Id());
    checkIdInequality(id1, IdCreator.createFragment1Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id1, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id1, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element1Fragment1Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement1Fragment1Id() throws Exception {
    final Id id = IdCreator.createElement1Fragment1Id();
    checkIdEquality(id, IdCreator.createElement1Fragment1Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element1Fragment2Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement1Fragment2Id() throws Exception {
    final Id id = IdCreator.createElement1Fragment2Id();
    checkIdEquality(id, IdCreator.createElement1Fragment2Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment1Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element2Fragment1Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement2Fragment1Id() throws Exception {
    final Id id = IdCreator.createElement2Fragment1Id();
    checkIdEquality(id, IdCreator.createElement2Fragment1Id());

    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id, IdCreator.createElement1Id());
    checkIdInequality(id, IdCreator.createElement2Id());
    checkIdInequality(id, IdCreator.createFragment1Id());
    checkIdInequality(id, IdCreator.createFragment2Id());
    checkIdInequality(id, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id, IdCreator.createElement2Fragment2Id());
  }

  /**
   * test Element2Fragment2Id.
   * 
   * @throws Exception
   *           test fails
   */
  public void testElement2Fragment2Id() throws Exception {
    final Id id1 = IdCreator.createElement2Fragment2Id();
    checkIdEquality(id1, IdCreator.createElement2Fragment2Id());

    checkIdInequality(id1, IdCreator.createSourceObjectIdSimpleKey());
    checkIdInequality(id1, IdCreator.createSourceObjectIdSimpleNamedKey());
    checkIdInequality(id1, IdCreator.createSourceObjectIdCompositeKey());
    checkIdInequality(id1, IdCreator.createElement1Id());
    checkIdInequality(id1, IdCreator.createElement2Id());
    checkIdInequality(id1, IdCreator.createFragment1Id());
    checkIdInequality(id1, IdCreator.createFragment2Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment1Id());
    checkIdInequality(id1, IdCreator.createElement1Fragment2Id());
    checkIdInequality(id1, IdCreator.createElement2Fragment1Id());
  }

}
