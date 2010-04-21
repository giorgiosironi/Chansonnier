/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.test;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager.LockState;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Base test class for DeltaIndexingManager implementations.
 */
public abstract class AbstractDeltaIndexingTest extends DeclarativeServiceTestCase {

  /**
   * The Constant DATA_SOURCE_ID.
   */
  private static final String DATA_SOURCE_ID = "some_datasource_id";

  /**
   * The Constant HASH.
   */
  private static final String HASH = "some_hash";

  /**
   * The Constant ID.
   */
  private static final Id ID = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");

  /**
   * The Constant REPEATS.
   */
  private static final int REPEATS = 10;

  /**
   * The _dim.
   */
  protected DeltaIndexingManager _dim;


  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _dim = null;
  }

  /**
   * Test index simple.
   * 
   */
  public void testIndexSimple() throws Exception {
    //assert virgin dim 
    assertFalse(_dim.exists(DATA_SOURCE_ID));
    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));

    //
    final String sessionId = _dim.init(DATA_SOURCE_ID);
    assertEquals(true, _dim.checkForUpdate(sessionId, ID, HASH));
    _dim.visit(sessionId, ID, HASH, false);
    Iterator<Id> obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId, DATA_SOURCE_ID);
    assertNotNull(obsoleteIdIterator);
    assertFalse(obsoleteIdIterator.hasNext());
    _dim.finish(sessionId);

    assertEquals(1, _dim.getEntryCount(DATA_SOURCE_ID));

    // second time
    final String sessionId2 = _dim.init(DATA_SOURCE_ID);
    assertEquals(false, _dim.checkForUpdate(sessionId2, ID, HASH));
    obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId2, DATA_SOURCE_ID);
    assertNotNull(obsoleteIdIterator);
    assertFalse(obsoleteIdIterator.hasNext());
    _dim.finish(sessionId2);
    assertTrue(_dim.exists(DATA_SOURCE_ID));

    assertEquals(1, _dim.getEntryCount(DATA_SOURCE_ID));

    final String sessionId3 = _dim.init(DATA_SOURCE_ID);
    _dim.clear(sessionId3);
    _dim.finish(sessionId3);

    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
  }

  /**
   * Test index hard.
   * 
   */
  public void testIndexHard() throws Exception {
    final String sessionId = _dim.init(DATA_SOURCE_ID);
    try {
      assertEquals(true, _dim.checkForUpdate(sessionId, ID, HASH));
      _dim.visit(sessionId, ID, HASH, false);
    } finally {
      _dim.finish(sessionId);
    }

    // second time
    final String sessionId2 = _dim.init(DATA_SOURCE_ID);
    try {
      assertEquals(false, _dim.checkForUpdate(sessionId2, ID, HASH));
      final Iterator<Id> obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId2, DATA_SOURCE_ID);
      assertNotNull(obsoleteIdIterator);
      assertFalse(obsoleteIdIterator.hasNext());
    } finally {
      _dim.finish(sessionId2);
    }

    // third time
    final String sessionId3 = _dim.init(DATA_SOURCE_ID);
    try {
      boolean hasDeleted = false;
      final Iterator<Id> obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId3, DATA_SOURCE_ID);
      while (obsoleteIdIterator.hasNext()) {
        final Id id = obsoleteIdIterator.next();
        _dim.delete(sessionId3, id);
        hasDeleted = true;
      }
      assertEquals(true, hasDeleted);
    } finally {
      _dim.finish(sessionId3);
    }
  }

  /**
   * Test index compounds.
   * 
   */
  public void testIndexCompounds() throws Exception {
    final int documentCount = 7;

    final Id base = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "base");
    final Id baseElement1 = base.createElementId("element1");
    final Id baseElement2 = base.createElementId("element2");
    final Id baseElement3 = base.createElementId("element3");
    final Id baseElement3SubElement1 = baseElement3.createElementId("subElement1");
    final Id baseElement3SubElement2 = baseElement3.createElementId("subElement2");
    final Id baseElement3SubElement3 = baseElement3.createElementId("subElement3");

    assertFalse(_dim.exists(DATA_SOURCE_ID));

    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));

    final String sessionId = _dim.init(DATA_SOURCE_ID);
    assertEquals(true, _dim.checkForUpdate(sessionId, base, HASH));
    _dim.visit(sessionId, base, HASH, true);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement1, HASH));
    _dim.visit(sessionId, baseElement1, HASH, true);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement2, HASH));
    _dim.visit(sessionId, baseElement2, HASH, true);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement3, HASH));
    _dim.visit(sessionId, baseElement3, HASH, true);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement3SubElement1, HASH));
    _dim.visit(sessionId, baseElement3SubElement1, HASH, false);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement3SubElement2, HASH));
    _dim.visit(sessionId, baseElement3SubElement2, HASH, false);
    assertEquals(true, _dim.checkForUpdate(sessionId, baseElement3SubElement3, HASH));
    _dim.visit(sessionId, baseElement3SubElement3, HASH, false);
    Iterator<Id> obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId, DATA_SOURCE_ID);
    assertNotNull(obsoleteIdIterator);
    assertFalse(obsoleteIdIterator.hasNext());
    _dim.finish(sessionId);
    assertEquals(documentCount, _dim.getEntryCount(DATA_SOURCE_ID));

    // second time
    final String sessionId2 = _dim.init(DATA_SOURCE_ID);
    assertEquals(false, _dim.checkForUpdate(sessionId2, base, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement1, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement2, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement3, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement3SubElement1, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement3SubElement2, HASH));
    assertEquals(false, _dim.checkForUpdate(sessionId2, baseElement3SubElement3, HASH));
    obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId2, DATA_SOURCE_ID);
    assertNotNull(obsoleteIdIterator);
    assertFalse(obsoleteIdIterator.hasNext());
    _dim.finish(sessionId2);
    assertTrue(_dim.exists(DATA_SOURCE_ID));
    assertEquals(documentCount, _dim.getEntryCount(DATA_SOURCE_ID));

    // third time
    final String sessionId3 = _dim.init(DATA_SOURCE_ID);
    try {
      boolean hasDeleted = false;
      obsoleteIdIterator = _dim.obsoleteIdIterator(sessionId3, DATA_SOURCE_ID);
      while (obsoleteIdIterator.hasNext()) {
        final Id id = obsoleteIdIterator.next();
        _dim.delete(sessionId3, id);
        hasDeleted = true;
      }
      assertEquals(true, hasDeleted);
    } finally {
      _dim.finish(sessionId3);
    }
    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
  }

  /**
   * Test threads index.
   * 
   */
  public void testThreadsIndex() throws Exception {
    final Thread thread1 = new Thread(new DimAccessTask());
    final Thread thread2 = new Thread(new DimAccessTask());
    thread1.run();
    thread2.run();
    thread1.join();
    thread2.join();
  }

  /**
   * Test exception handling.
   * 
   */
  public void testInOutExceptions() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");
    final String hash = "testhash";

    try {
      _dim.init(null);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter dataSourceID is null", e.getMessage());
    }

    final String sessionId = _dim.init(DATA_SOURCE_ID);

    try {
      _dim.checkForUpdate(sessionId, null, hash);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter id is null", e.getMessage());
    }
    try {
      _dim.checkForUpdate(sessionId, id, null);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter hash is null", e.getMessage());
    }

    try {
      _dim.delete(sessionId, null);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter id is null", e.getMessage());
    }

    // not implemented yet
    /*
     * try { _dim.obsoleteIdIterator((Id) null); } catch (DeltaIndexingException e) { assertNotNull(e);
     * assertEquals("parameter id is null", e.getMessage()); }
     */

    try {
      _dim.obsoleteIdIterator(sessionId, (String) null);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter dataSourceID is null", e.getMessage());
    }

    // not implemented yet
    /*
     * try { _dim.rollback(null); } catch (DeltaIndexingException e) { assertNotNull(e);
     * assertEquals("parameter dataSourceID is null", e.getMessage()); }
     */

    try {
      _dim.visit(sessionId, null, hash, false);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter id is null", e.getMessage());
    }
    try {
      _dim.visit(sessionId, id, null, false);
    } catch (DeltaIndexingException e) {
      assertNotNull(e);
      assertEquals("parameter hash is null", e.getMessage());
    }

    _dim.finish(sessionId);
  }

  /**
   * Test locks.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testLocks() throws Exception {
    _dim.clear();

    Map<String, LockState> states = _dim.getLockStates();
    assertNotNull(states);
    assertEquals(0, states.size());

    _dim.init(DATA_SOURCE_ID);
    states = _dim.getLockStates();
    assertNotNull(states);
    assertEquals(1, states.size());
    assertEquals(LockState.LOCKED, states.get(DATA_SOURCE_ID));

    _dim.unlockDatasource(DATA_SOURCE_ID);
    states = _dim.getLockStates();
    assertNotNull(states);
    assertEquals(1, states.size());
    assertEquals(LockState.UNLOCKED, states.get(DATA_SOURCE_ID));
  }

  /**
   * The Class DimAccessTask.
   */
  private class DimAccessTask implements Runnable {

    /**
     * {@inheritDoc}
     * 
     * @throws DeltaIndexingException
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
      for (int i = 0; i < REPEATS; i++) {
        String sessionId = null;
        try {
          sessionId = _dim.init(DATA_SOURCE_ID);
        } catch (final DeltaIndexingException ex) {
          ex.printStackTrace();
          continue;
        }
        try {
          final boolean result = _dim.checkForUpdate(sessionId, ID, HASH);
          _log.info(result);
          if (result) {
            _dim.visit(sessionId, ID, HASH, false);
          }
        } catch (final DeltaIndexingSessionException ex) {
          ex.printStackTrace();
        } catch (final DeltaIndexingException ex) {
          ex.printStackTrace();
        } finally {
          try {
            _dim.finish(sessionId);
          } catch (final DeltaIndexingSessionException ex) {
            ex.printStackTrace();
          } catch (final DeltaIndexingException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

}
