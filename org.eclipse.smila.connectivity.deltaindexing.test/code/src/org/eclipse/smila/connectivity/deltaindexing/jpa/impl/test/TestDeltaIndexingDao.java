/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.jpa.impl.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.deltaindexing.jpa.impl.DeltaIndexingDao;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;

/**
 * Test class for DeltaIndexingDao.
 */
public class TestDeltaIndexingDao extends TestCase {

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
   * Test DeltaIndexingDao.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testDeltaIndexingDao() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("testSource", "testKey1");
    final String hash = "12345";

    try {
      new DeltaIndexingDao(null, null, true, true);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("parameter id is null", e.getMessage());
    } catch (Exception e) {
      fail("expected IllegalArgumentException");
    }

    try {
      new DeltaIndexingDao(id, null, true, true);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("parameter hash is null", e.getMessage());
    } catch (Exception e) {
      fail("expected IllegalArgumentException");
    }

    final DeltaIndexingDao dao = new DeltaIndexingDao(id, hash, true, false);
    assertNotNull(dao);

    assertEquals(id.getSource(), dao.getDataSourceId());
    assertEquals(hash, dao.getHash());
    assertEquals(id.getIdHash(), dao.getIdHash());
    assertNull(dao.getParentIdHash());
    assertTrue(dao.isCompound());
    assertFalse(dao.isModified());
    assertFalse(dao.isVisited());

    dao.modifyAndVisit();
    assertTrue(dao.isModified());
    assertTrue(dao.isVisited());
    
    final  Id serializedId = dao.toId();
    assertNotNull(serializedId);
    assertEquals(id.getSource(), serializedId.getSource());
    assertEquals(id.getIdHash(), serializedId.getIdHash());
    assertEquals(id.hasElementKeys(), serializedId.hasElementKeys());
    assertEquals(id.hasFragmentNames(), serializedId.hasFragmentNames());    
  }
}
