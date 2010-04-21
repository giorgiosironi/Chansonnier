/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.security.test;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.security.SecurityAnnotation;
import org.eclipse.smila.security.SecurityAnnotations.AccessRightType;
import org.eclipse.smila.security.SecurityAnnotations.EntityType;

/**
 * The Class TestSecurityResolver.
 */
public class TestSecurityAnnotation extends TestCase {

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
   * Test creation of access rights annotations.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testSecurityAnnotations() throws Exception {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final SecurityAnnotation sa = new SecurityAnnotation(record);

    final Annotation acessRights = sa.getAccessRights();
    assertNotNull(acessRights);
    assertFalse(acessRights.hasAnnotations());
    assertFalse(acessRights.hasAnonValues());

    final Annotation writeRights = sa.getAccessRights(AccessRightType.WRITE);
    assertNotNull(writeRights);
    assertFalse(writeRights.hasAnnotations());
    assertFalse(writeRights.hasAnonValues());

    final Annotation readRights = sa.getAccessRights(AccessRightType.READ);
    assertNotNull(readRights);
    assertFalse(readRights.hasAnnotations());
    assertFalse(readRights.hasAnonValues());

    final Annotation readPrincipals = sa.getAccessRights(AccessRightType.READ, EntityType.PRINCIPALS);
    assertNotNull(readPrincipals);
    assertFalse(readPrincipals.hasAnnotations());
    assertFalse(readPrincipals.hasAnonValues());

    final Annotation readGroups = sa.getAccessRights(AccessRightType.READ, EntityType.GROUPS);
    assertNotNull(readGroups);
    assertFalse(readGroups.hasAnnotations());
    assertFalse(readGroups.hasAnonValues());

    // now the annotations must contain sub annotations
    assertNotNull(readRights);
    assertTrue(readRights.hasAnnotations());
    assertFalse(readRights.hasAnonValues());

    assertNotNull(acessRights);
    assertTrue(acessRights.hasAnnotations());
    assertFalse(acessRights.hasAnonValues());

    // remove sub annotations
    sa.remove(AccessRightType.READ, EntityType.PRINCIPALS);
    sa.remove(AccessRightType.READ, EntityType.GROUPS);
    assertNotNull(readRights);
    assertFalse(readRights.hasAnnotations());
    assertFalse(readRights.hasAnonValues());

    sa.remove(AccessRightType.READ);
    sa.remove(AccessRightType.WRITE);
    assertNotNull(acessRights);
    assertFalse(acessRights.hasAnnotations());
    assertFalse(acessRights.hasAnonValues());

    sa.remove();
    assertFalse(record.getMetadata().hasAnnotations());
  }

  /**
   * Test creation of access rights annotations.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testSecurityAnnotationValues() throws Exception {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final SecurityAnnotation sa = new SecurityAnnotation(record);
    final String[] users = { "user1", "user2", "user3" };
    final String[] groups = { "group1", "group2", "group3" };

    // add users and groups
    for (int i = 0; i < users.length; i++) {
      sa.add(AccessRightType.READ, EntityType.PRINCIPALS, users[i]);
      sa.add(AccessRightType.READ, EntityType.GROUPS, groups[i]);
    }

    // check annotations and values
    final Annotation readRights = sa.getAccessRights(AccessRightType.READ);
    assertNotNull(readRights);
    assertTrue(readRights.hasAnnotations());
    assertFalse(readRights.hasAnonValues());

    final Annotation readPrincipals = sa.getAccessRights(AccessRightType.READ, EntityType.PRINCIPALS);
    assertNotNull(readPrincipals);
    assertFalse(readPrincipals.hasAnnotations());
    assertTrue(readPrincipals.hasAnonValues());
    Collection<String> userValues = readPrincipals.getAnonValues();
    assertEquals(users.length, userValues.size());
    for (String user : users) {
      assertTrue(userValues.contains(user));
    }

    final Annotation readGroups = sa.getAccessRights(AccessRightType.READ, EntityType.GROUPS);
    assertNotNull(readGroups);
    assertFalse(readGroups.hasAnnotations());
    assertTrue(readGroups.hasAnonValues());
    Collection<String> groupValues = readGroups.getAnonValues();
    assertEquals(groups.length, groupValues.size());
    for (String group : groups) {
      assertTrue(groupValues.contains(group));
    }

    // remove
    sa.remove(AccessRightType.READ, EntityType.PRINCIPALS, users[0]);
    userValues = readPrincipals.getAnonValues();
    assertEquals(users.length - 1, userValues.size());
    assertFalse(userValues.contains(users[0]));

    sa.remove(AccessRightType.READ, EntityType.GROUPS, groups[0]);
    groupValues = readGroups.getAnonValues();
    assertEquals(groups.length - 1, groupValues.size());
    assertFalse(groupValues.contains(groups[0]));

    sa.remove(AccessRightType.READ, EntityType.PRINCIPALS);
    sa.remove(AccessRightType.READ, EntityType.GROUPS);
    assertFalse(readRights.hasAnnotations());
  }

}
