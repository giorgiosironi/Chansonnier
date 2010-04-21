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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.Annotation;

/**
 * Tests for Annotation API.
 * 
 * @author jschumacher
 * 
 */
public class TestAnnotations extends TestCase {

  /**
   * check handling of anonymous values.
   */
  public void testAnonValues() {
    int expectedSize = 0;
    final Annotation anno = RecordCreator.FACTORY.createAnnotation();
    assertEquals(expectedSize, anno.anonValuesSize());
    assertFalse(anno.hasAnonValues());
    assertFalse(anno.hasValues());
    assertNull(anno.getAnonValues());

    anno.addAnonValue("1");
    expectedSize++;
    anno.addAnonValue("2");
    expectedSize++;
    anno.addAnonValue("3");
    expectedSize++;
    assertEquals(expectedSize, anno.anonValuesSize());
    assertTrue(anno.hasAnonValues());
    assertTrue(anno.hasValues());
    assertNotNull(anno.getAnonValues());
    assertEquals(anno.getAnonValues().size(), anno.anonValuesSize());
    assertTrue(anno.getAnonValues().contains("1"));
    assertTrue(anno.getAnonValues().contains("2"));
    assertTrue(anno.getAnonValues().contains("3"));

    anno.addAnonValue("1");
    expectedSize++;
    assertEquals(expectedSize, anno.anonValuesSize());

    final List<String> values = Arrays.asList("4", "5");
    expectedSize = values.size();
    anno.setAnonValues(values);
    assertEquals(expectedSize, anno.anonValuesSize());
    assertTrue(anno.hasAnonValues());
    assertTrue(anno.hasValues());
    assertNotNull(anno.getAnonValues());
    assertEquals(anno.getAnonValues().size(), anno.anonValuesSize());
    assertTrue(anno.getAnonValues().contains("4"));
    assertTrue(anno.getAnonValues().contains("5"));

    anno.removeAnonValues();
    expectedSize = 0;
    assertEquals(expectedSize, anno.anonValuesSize());
    assertFalse(anno.hasAnonValues());
    assertFalse(anno.hasValues());
    assertNull(anno.getAnonValues());
  }

  /**
   * check handling of named values.
   */
  public void testNamedValues() {
    int expectedSize = 0;
    final Annotation anno = RecordCreator.FACTORY.createAnnotation();
    assertEquals(expectedSize, anno.namedValuesSize());
    assertFalse(anno.hasNamedValues());
    assertFalse(anno.hasValues());
    assertFalse(anno.getValueNames().hasNext());

    anno.setNamedValue("1", "value 1");
    expectedSize++;
    anno.setNamedValue("2", "value 2");
    expectedSize++;
    anno.setNamedValue("3", "value 3");
    expectedSize++;
    assertEquals(expectedSize, anno.namedValuesSize());
    assertTrue(anno.hasNamedValues());
    assertTrue(anno.hasValues());
    Iterator<String> names = anno.getValueNames();
    while (names.hasNext()) {
      final String name = names.next();
      assertEquals("value " + name, anno.getNamedValue(name));
    }

    anno.setNamedValue("1", "Value 1");
    anno.setNamedValue("2", "Value 2");
    anno.setNamedValue("3", "Value 3");
    assertEquals(expectedSize, anno.namedValuesSize());
    assertTrue(anno.hasNamedValues());
    assertTrue(anno.hasValues());
    names = anno.getValueNames();
    while (names.hasNext()) {
      final String name = names.next();
      assertEquals("Value " + name, anno.getNamedValue(name));
    }

    anno.removeNamedValue("1");
    expectedSize--;
    assertNull(anno.getNamedValue("1"));
    assertEquals(expectedSize, anno.namedValuesSize());
    assertTrue(anno.hasNamedValues());
    assertTrue(anno.hasValues());
    names = anno.getValueNames();
    while (names.hasNext()) {
      final String name = names.next();
      assertEquals("Value " + name, anno.getNamedValue(name));
    }

    anno.removeNamedValues();
    expectedSize = 0;
    assertEquals(expectedSize, anno.namedValuesSize());
    assertFalse(anno.hasNamedValues());
    assertFalse(anno.hasValues());
    assertFalse(anno.getValueNames().hasNext());
  }

  /**
   * check function of annotatables.
   */
  public void testSubAnnotations() {
    int expectedSize = 0;
    final Annotation anno = RecordCreator.FACTORY.createAnnotation();
    assertEquals(expectedSize, anno.annotationsSize());
    assertFalse(anno.hasAnnotations());
    assertFalse(anno.getAnnotationNames().hasNext());

    final Annotation subAnno1 = RecordCreator.FACTORY.createAnnotation();
    subAnno1.addAnonValue("subanno 1");
    final Annotation subAnno2 = RecordCreator.FACTORY.createAnnotation();
    subAnno2.addAnonValue("subanno 2");
    final Annotation subAnno3 = RecordCreator.FACTORY.createAnnotation();
    subAnno3.addAnonValue("subanno 3");

    anno.setAnnotation("1", subAnno1);
    expectedSize++;
    anno.setAnnotation("2", subAnno2);
    expectedSize++;
    anno.setAnnotation("3", subAnno3);
    expectedSize++;
    int expectedListSize = 1;

    assertEquals(expectedSize, anno.annotationsSize());
    assertTrue(anno.hasAnnotations());
    Iterator<String> names = anno.getAnnotationNames();
    while (names.hasNext()) {
      final String name = names.next();
      final Collection<Annotation> annotations = anno.getAnnotations(name);
      assertEquals(expectedListSize, annotations.size());
      for (Annotation annotation : annotations) {
        assertEquals("subanno " + name, annotation.getAnonValues().iterator().next());
      }
    }

    anno.addAnnotation("1", subAnno1);
    anno.addAnnotation("2", subAnno2);
    anno.addAnnotation("3", subAnno3);
    expectedListSize++;
    assertEquals(expectedSize, anno.annotationsSize());
    assertTrue(anno.hasAnnotations());
    names = anno.getAnnotationNames();
    while (names.hasNext()) {
      final String name = names.next();
      final Collection<Annotation> annotations = anno.getAnnotations(name);
      assertEquals(expectedListSize, annotations.size());
      for (Annotation annotation : annotations) {
        assertEquals("subanno " + name, annotation.getAnonValues().iterator().next());
      }
    }

    anno.setAnnotations("1", Arrays.asList(subAnno1, subAnno1, subAnno1));
    anno.setAnnotations("2", Arrays.asList(subAnno2, subAnno2, subAnno2));
    anno.setAnnotations("3", Arrays.asList(subAnno3, subAnno3, subAnno3));
    expectedListSize++;
    assertEquals(expectedSize, anno.annotationsSize());
    assertTrue(anno.hasAnnotations());
    names = anno.getAnnotationNames();
    while (names.hasNext()) {
      final String name = names.next();
      final Collection<Annotation> annotations = anno.getAnnotations(name);
      assertEquals(expectedListSize, annotations.size());
      for (Annotation annotation : annotations) {
        assertEquals("subanno " + name, annotation.getAnonValues().iterator().next());
      }
    }

    anno.removeAnnotations("1");
    expectedSize--;
    assertEquals(expectedSize, anno.annotationsSize());
    assertNull(anno.getAnnotation("1"));
    assertTrue(anno.hasAnnotations());
    names = anno.getAnnotationNames();
    while (names.hasNext()) {
      final String name = names.next();
      final Collection<Annotation> annotations = anno.getAnnotations(name);
      assertEquals(expectedListSize, annotations.size());
      for (Annotation annotation : annotations) {
        assertEquals("subanno " + name, annotation.getAnonValues().iterator().next());
      }
    }

    anno.removeAnnotations();
    expectedSize = 0;
    assertEquals(expectedSize, anno.annotationsSize());
    assertFalse(anno.hasAnnotations());
    assertFalse(anno.getAnnotationNames().hasNext());
  }

}
