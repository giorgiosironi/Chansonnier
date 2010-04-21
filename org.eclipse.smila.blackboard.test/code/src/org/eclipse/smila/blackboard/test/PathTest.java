/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.blackboard.test;

import junit.framework.TestCase;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;

/**
 * The Class PathTest.
 */
public class PathTest extends TestCase {

  /**
   * Constant for number 3.
   */
  private static final int NUMBER_3 = 3;
  
  /**
   * Test path.
   */
  public void testPath() {    
    final Path path = new Path();
    final PathStep pathStep = new PathStep("attributeName");
    path.add(pathStep);
    assertFalse(path.isEmpty());
    assertEquals("attributeName[0]/", path.toString());
    
    // Chech equals() and hashCode() overriden methods
    assertEquals(path, new Path().add(new PathStep("attributeName", 0)));
    assertEquals(path, new Path().add("attributeName"));
    assertEquals(path.hashCode(), new Path().add(new PathStep("attributeName", 0)).hashCode());
    assertEquals(path, path);
    assertEquals(pathStep, new PathStep("attributeName", 0));
    assertEquals(pathStep.hashCode(), new PathStep("attributeName", 0).hashCode());
    
    final Path path1 = new Path();
    final PathStep pathStep1 = new PathStep("attributeName1", 1);
    final PathStep pathStep2 = new PathStep("attributeName2", 2);
    final PathStep pathStep3 = new PathStep("attributeName3", 3);
    assertTrue(path1.isEmpty());
    assertEquals(path1.add(pathStep1).toString(), "attributeName1[1]/");
    assertEquals(path1.add(pathStep2).toString(), "attributeName1[1]/attributeName2[2]/");
    assertEquals(path1.add(pathStep3).toString(), "attributeName1[1]/attributeName2[2]/attributeName3[3]/");
    assertEquals(path1.length(), NUMBER_3);
    assertEquals(path1.getName(1), "attributeName2");
    final Path path2 = new Path(path1);
    assertEquals(path2, path1);
  }

}
