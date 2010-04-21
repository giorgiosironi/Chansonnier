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

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;

/**
 * Tests for Attribute API.
 * 
 * @author jschumacher
 * 
 */
public class TestAttributes extends TestCase {

  /**
   * test literal values of attributes.
   */
  public void testValues() {
    int expectedSize = 0;
    final Attribute attribute = RecordCreator.FACTORY.createAttribute();
    assertEquals(expectedSize, attribute.literalSize());
    assertFalse(attribute.hasLiterals());
    assertNull(attribute.getLiteral());
    assertEquals(0, attribute.getLiterals().size());

    final Literal lit1 = RecordCreator.FACTORY.createLiteral();
    lit1.setStringValue("value 1");
    attribute.addLiteral(lit1);
    expectedSize++;
    final Literal lit2 = RecordCreator.FACTORY.createLiteral();
    lit2.setStringValue("value 2");
    attribute.addLiteral(lit2);
    expectedSize++;
    final Literal lit3 = RecordCreator.FACTORY.createLiteral();
    lit3.setStringValue("value 3");
    attribute.addLiteral(lit3);
    expectedSize++;

    assertEquals(expectedSize, attribute.literalSize());
    assertTrue(attribute.hasLiterals());
    assertNotNull(attribute.getLiteral());
    List<Literal> values = attribute.getLiterals();
    assertNotNull(values);
    assertTrue(values.contains(lit1));
    assertTrue(values.contains(lit2));
    assertTrue(values.contains(lit3));

    values.remove(lit1);
    attribute.setLiterals(values);
    expectedSize = values.size();

    assertEquals(expectedSize, attribute.literalSize());
    assertTrue(attribute.hasLiterals());
    assertNotNull(attribute.getLiteral());
    values = attribute.getLiterals();
    assertNotNull(values);
    assertFalse(values.contains(lit1));
    assertTrue(values.contains(lit2));
    assertTrue(values.contains(lit3));

    attribute.setLiteral(lit1);
    expectedSize = 1;
    assertEquals(expectedSize, attribute.literalSize());
    assertEquals(lit1, attribute.getLiteral());
    assertTrue(attribute.hasLiterals());
    assertNotNull(attribute.getLiteral());
    values = attribute.getLiterals();
    assertNotNull(values);
    assertTrue(values.contains(lit1));
    assertFalse(values.contains(lit2));
    assertFalse(values.contains(lit3));

    attribute.removeLiterals();
    expectedSize = 0;
    assertEquals(expectedSize, attribute.literalSize());
    assertFalse(attribute.hasLiterals());
    assertNull(attribute.getLiteral());
    assertEquals(0, attribute.getLiterals().size());
  }

  /**
   * test literal values of attributes.
   */
  public void testObject() {
    int expectedSize = 0;
    final Attribute attribute = RecordCreator.FACTORY.createAttribute();
    assertEquals(expectedSize, attribute.objectSize());
    assertFalse(attribute.hasObjects());
    assertNull(attribute.getObject());
    assertEquals(0, attribute.getObjects().size());

    final MObject mob1 = RecordCreator.FACTORY.createMetadataObject();
    attribute.addObject(mob1);
    expectedSize++;
    final MObject mob2 = RecordCreator.FACTORY.createMetadataObject();
    attribute.addObject(mob2);
    expectedSize++;
    final MObject mob3 = RecordCreator.FACTORY.createMetadataObject();
    attribute.addObject(mob3);
    expectedSize++;

    assertEquals(expectedSize, attribute.objectSize());
    assertTrue(attribute.hasObjects());
    assertNotNull(attribute.getObject());
    List<MObject> objects = attribute.getObjects();
    assertNotNull(objects);
    assertTrue(objects.contains(mob1));
    assertTrue(objects.contains(mob2));
    assertTrue(objects.contains(mob3));

    objects.remove(mob1);
    expectedSize--;
    attribute.setObjects(objects);
    assertEquals(expectedSize, attribute.objectSize());
    assertTrue(attribute.hasObjects());
    assertNotNull(attribute.getObject());
    objects = attribute.getObjects();
    assertNotNull(objects);
    assertFalse(objects.contains(mob1));
    assertTrue(objects.contains(mob2));
    assertTrue(objects.contains(mob3));

    attribute.setObject(mob1);
    expectedSize = 1;
    assertEquals(expectedSize, attribute.objectSize());
    assertEquals(mob1, attribute.getObject());
    assertTrue(attribute.hasObjects());
    assertNotNull(attribute.getObject());
    objects = attribute.getObjects();
    assertNotNull(objects);
    assertTrue(objects.contains(mob1));
    assertFalse(objects.contains(mob2));
    assertFalse(objects.contains(mob3));

    attribute.removeObjects();
    expectedSize = 0;
    assertEquals(expectedSize, attribute.objectSize());
    assertFalse(attribute.hasObjects());
    assertNull(attribute.getObject());
    assertEquals(0, attribute.getObjects().size());
  }
}
