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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Abstract base class for record test cases. Provides methods for asserting record equality.
 * 
 * @author jschumacher
 * 
 */
public abstract class ARecordTestCase extends TestCase {

  /**
   * check if two records have the same content.
   * 
   * @param record1
   *          expected record
   * @param record2
   *          record to test
   */
  protected void checkRecordEquality(Record record1, Record record2) {
    checkMObjectEquality(record1.getMetadata(), record2.getMetadata());

    assertEquals("number of attachments differs", record1.attachmentSize(), record2.attachmentSize());
    if (record1.hasAttachments()) {
      final Iterator<String> names = record1.getAttachmentNames();
      while (names.hasNext()) {
        final String name = names.next();
        assertTrue("missing attachment", record2.hasAttachment(name));
        // equality of attachment value not checked because attachment values do not go into XML.
      }
    }

    assertEquals(record1.getId(), record2.getId());
  }

  /**
   * check if two records have different content.
   * 
   * @param record1
   *          one record
   * @param record2
   *          another record
   */
  protected void checkRecordInequality(Record record1, Record record2) {
    boolean recordsEqual = false;
    try {
      checkRecordEquality(record1, record2);
      recordsEqual = true;
    } catch (AssertionFailedError ex) {
      System.out.println("Difference found: " + ex.toString());
    } catch (Exception ex) {
      fail("wrong exception caught: " + ex.toString());
    }
    if (recordsEqual) {
      fail("a difference should have been found");
    }
  }

  /**
   * check if two metadata objects have the same content.
   * 
   * @param mobject1
   *          expected metadata object
   * @param mobject2
   *          metadata object to test
   */
  private void checkMObjectEquality(MObject mobject1, MObject mobject2) {
    if (mobject1 != null || mobject2 != null) {
      assertNotNull(mobject1);
      assertNotNull(mobject2);
      assertEquals("number of attributes differs", mobject1.size(), mobject2.size());

      final Iterator<String> expectedAttributeNames = mobject1.getAttributeNames();
      while (expectedAttributeNames.hasNext()) {
        final String name = expectedAttributeNames.next();
        assertTrue("missing attribute " + name, mobject2.hasAttribute(name));
        checkAttributeEquality(mobject1.getAttribute(name), mobject2.getAttribute(name));
      }

      checkAnnotatableEquality(mobject1, mobject2);
    }
  }

  /**
   * check if two attributes have the same content.
   * 
   * @param attribute1
   *          expected attribute
   * @param attribute2
   *          attribute to test
   */
  private void checkAttributeEquality(Attribute attribute1, Attribute attribute2) {
    if (attribute1 != null || attribute2 != null) {
      assertNotNull(attribute1);
      assertNotNull(attribute2);
      checkValueEquality(attribute1, attribute2);
      checkSubobjectEquality(attribute1, attribute2);
      checkAnnotatableEquality(attribute1, attribute2);
    }
  }

  /**
   * check if two attribute have the same values.
   * 
   * @param attribute1
   *          expected attribute
   * @param attribute2
   *          attribute to test
   */
  private void checkValueEquality(Attribute attribute1, Attribute attribute2) {
    assertEquals("number of values differs", attribute1.literalSize(), attribute2.literalSize());
    if (attribute1.hasLiterals()) {
      final List<Literal> values2 = new ArrayList<Literal>(attribute2.getLiterals());
      for (Literal value1 : attribute1.getLiterals()) {
        final Iterator<Literal> iter2 = values2.iterator();
        while (iter2.hasNext()) {
          final Literal value2 = iter2.next();
          try {
            assertEquals(value1, value2);
            checkAnnotatableEquality(value1, value2);
            iter2.remove();
            break;
          } catch (AssertionFailedError ex) {
            ex = null;
          }
        }
      }
      assertTrue("values not equal", values2.isEmpty());
    }
  }

  /**
   * check if two attribute have the same sub objects.
   * 
   * @param attribute1
   *          expected attribute
   * @param attribute2
   *          attribute to test
   */
  private void checkSubobjectEquality(Attribute attribute1, Attribute attribute2) {
    assertEquals("number of objects differs", attribute1.objectSize(), attribute2.objectSize());
    if (attribute1.hasObjects()) {
      final List<MObject> mobjects2 = new ArrayList<MObject>(attribute2.getObjects());
      for (MObject mobject1 : attribute1.getObjects()) {
        final Iterator<MObject> iter2 = mobjects2.iterator();
        while (iter2.hasNext()) {
          final MObject mobject2 = iter2.next();
          try {
            checkMObjectEquality(mobject1, mobject2);
            iter2.remove();
            break;
          } catch (AssertionFailedError ex) {
            ex = null;
          }
        }
      }
      assertTrue("subobjects not equal", mobjects2.isEmpty());
    }
  }

  /**
   * check if two object have same annotations.
   * 
   * @param annotatable1
   *          expected object
   * @param annotatable2
   *          object to test
   */
  private void checkAnnotatableEquality(Annotatable annotatable1, Annotatable annotatable2) {
    assertEquals(annotatable1.annotationsSize(), annotatable2.annotationsSize());
    if (annotatable1.hasAnnotations()) {
      final Iterator<String> names = annotatable1.getAnnotationNames();
      while (names.hasNext()) {
        final String name = names.next();
        assertTrue("missing annotation " + name, annotatable2.hasAnnotation(name));
        final Collection<Annotation> annotations1 = annotatable1.getAnnotations(name);
        final Collection<Annotation> annotations2 = new ArrayList<Annotation>(annotatable2.getAnnotations(name));
        assertEquals("number of annotations of name " + name + " differs", annotations1.size(), annotations2.size());
        for (Annotation annotation1 : annotations1) {
          final Iterator<Annotation> iter2 = annotations2.iterator();
          while (iter2.hasNext()) {
            final Annotation annotation2 = iter2.next();
            try {
              checkAnnotationEquality(annotation1, annotation2);
              iter2.remove();
            } catch (AssertionFailedError ex) {
              ex = null;
            }

          }
        }
        assertTrue("annotations not equal", annotations2.isEmpty());
      }
    }
  }

  /**
   * check if two annotations have same content.
   * 
   * @param annotation1
   *          expected annotation
   * @param annotation2
   *          annotation to test
   */
  private void checkAnnotationEquality(Annotation annotation1, Annotation annotation2) {
    assertEquals(annotation1.anonValuesSize(), annotation2.anonValuesSize());
    assertEquals(new HashSet<String>(annotation1.getAnonValues()), new HashSet<String>(annotation2.getAnonValues()));

    assertEquals(annotation1.namedValuesSize(), annotation2.namedValuesSize());
    if (annotation1.hasNamedValues()) {
      final Iterator<String> names = annotation1.getValueNames();
      while (names.hasNext()) {
        final String name = names.next();
        final String value1 = annotation1.getNamedValue(name);
        final String value2 = annotation2.getNamedValue(name);
        assertEquals("wrong value for name " + name, value1, value2);
      }
    }
    checkAnnotatableEquality(annotation1, annotation2);
  }

}
