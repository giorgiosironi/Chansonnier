/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.jpa.impl.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.deltaindexing.jpa.impl.DeltaIndexingDao;
import org.eclipse.smila.connectivity.deltaindexing.jpa.impl.IdIterator;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;

/**
 * Test class for IdIterator.
 */
public class TestIdIterator extends TestCase {

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
   * Test IdIterator.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testIdIterator() throws Exception {
    try {
      new IdIterator(null);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("parameter daoIterator is null", e.getMessage());
    } catch (Exception e) {
      fail("expected IllegalArgumentException");
    }

    final Id id1 = IdFactory.DEFAULT_INSTANCE.createId("testSource", "testKey1");
    final Id id2 = IdFactory.DEFAULT_INSTANCE.createId("testSource", "testKey2");
    final DeltaIndexingDao dao1 = new DeltaIndexingDao(id1, "0815", false, false);
    final DeltaIndexingDao dao2 = new DeltaIndexingDao(id2, "0815", false, false);
    final ArrayList<DeltaIndexingDao> daos = new ArrayList<DeltaIndexingDao>();
    daos.add(dao1);
    daos.add(dao2);
    daos.add(null);

    final IdIterator it = new IdIterator(daos.iterator());
    assertNotNull(it);

    // 1st object
    assertTrue(it.hasNext());
    try {
      it.remove();
      fail("expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
      assertEquals("use delete(Id) method of delta indexing manager instead", e.getMessage());
    } catch (Exception e) {
      fail("expected UnsupportedOperationException");
    }
    final Id itId1 = it.next();
    assertNotNull(itId1);
    assertEquals(id1, itId1);

    // 2nd object
    assertTrue(it.hasNext());
    try {
      it.remove();
      fail("expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
      assertEquals("use delete(Id) method of delta indexing manager instead", e.getMessage());
    } catch (Exception e) {
      fail("expected UnsupportedOperationException");
    }
    final Id itId2 = it.next();
    assertNotNull(itId2);
    assertEquals(id2, itId2);

    // 3rd object (null)
    assertTrue(it.hasNext());
    try {
      it.remove();
      fail("expected UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
      assertEquals("use delete(Id) method of delta indexing manager instead", e.getMessage());
    } catch (Exception e) {
      fail("expected UnsupportedOperationException");
    }
    final Id nullId = it.next();
    assertNull(nullId);

    // finished iteration
    assertFalse(it.hasNext());
  }
}
