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

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.IdHandlingException;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Base test class for DeltaIndexer Workflow.
 */
public abstract class AbstractDeltaIndexerWorkflowTest extends DeclarativeServiceTestCase {

  /**
   * data_source id for test.
   */
  private static final String DATA_SOURCE_ID = "datasourceid";

  /**
   * the DeltaIndexingManager.
   */
  protected DeltaIndexingManager _dim;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    fail("setup method must be overwritten");
  }

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
   * test the workflow that will be used by the crawler controller.
   * 
   * @throws DeltaIndexingSessionException
   *           on session id errors
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void testWorkFlow() throws DeltaIndexingSessionException, DeltaIndexingException {
    // first index run
    final Id[] idFirstRun = new Id[2];
    idFirstRun[0] = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");
    idFirstRun[1] = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "two");
    final String[] hashFirstRun = new String[2];
    hashFirstRun[0] = "hash";
    hashFirstRun[1] = "hash";

    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
    final String sessionId = _dim.init(DATA_SOURCE_ID);
    for (int i = 0; i < 2; i++) {

      final boolean r = _dim.checkForUpdate(sessionId, idFirstRun[i], hashFirstRun[i]);
      if (r) {
        _dim.visit(sessionId, idFirstRun[i], hashFirstRun[i], false);
      } else {
        assertTrue(true);
      }
    }
    final Iterator<Id> itempty = _dim.obsoleteIdIterator(sessionId, DATA_SOURCE_ID);
    assertTrue(!itempty.hasNext());
    _dim.finish(sessionId);
    assertEquals(2, _dim.getEntryCount(DATA_SOURCE_ID));

    // second index run
    // the secound entry was deleted
    final Id[] idSecondRun = new Id[1];
    idSecondRun[0] = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");
    final String[] hashSecondRun = new String[1];
    hashSecondRun[0] = "hash";
    final String sessionId2 = _dim.init(DATA_SOURCE_ID);
    for (int x = 0; x < 1; x++) {

      final boolean r = _dim.checkForUpdate(sessionId2, idFirstRun[x], hashFirstRun[x]);
      if (r) {
        _dim.visit(sessionId2, idFirstRun[x], hashFirstRun[x], false);
      }
    }
    // the second dt was deleted in the data source
    // now the controller needs to know which entries was not "visited"
    // therefore it calls obsoleteIdIterator
    final Iterator<Id> it = _dim.obsoleteIdIterator(sessionId2, DATA_SOURCE_ID);
    assertTrue(it.hasNext()); // there must be the second entry
    final Id deleteID = it.next();
    assertEquals(deleteID, idFirstRun[1]);
    _dim.delete(sessionId2, deleteID);
    assertTrue(!it.hasNext());
    _dim.finish(sessionId2);
    assertEquals(1, _dim.getEntryCount(DATA_SOURCE_ID));

    // reset
    final String sessionId3 = _dim.init(DATA_SOURCE_ID);
    _dim.clear(sessionId3);
    _dim.finish(sessionId3);
    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
  }

  /**
   * Test the work flow that will be used by the crawler controller with compound objects.
   * 
   * @throws DeltaIndexingSessionException
   *           on session id errors
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   * @throws IdHandlingException
   *           the delta indexing exception
   */
  public void testWorkFlowCompounds() throws DeltaIndexingSessionException, DeltaIndexingException,
    IdHandlingException {
    // initial index run
    final Id[] idFirstRun = new Id[8];
    idFirstRun[0] = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");
    idFirstRun[1] = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "compound");
    idFirstRun[2] = idFirstRun[1].createElementId("element1");
    idFirstRun[3] = idFirstRun[1].createElementId("element2");
    idFirstRun[4] = idFirstRun[1].createElementId("subCompound");
    idFirstRun[5] = idFirstRun[4].createElementId("subElement1");
    idFirstRun[6] = idFirstRun[4].createElementId("subElement2");
    idFirstRun[7] = idFirstRun[4].createElementId("subElement3");

    final String[] hashFirstRun = new String[8];
    for (int i = 0; i < hashFirstRun.length; i++) {
      hashFirstRun[i] = "hash";
    }

    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
    final String sessionId = _dim.init(DATA_SOURCE_ID);
    for (int i = 0; i < idFirstRun.length; i++) {

      final boolean doUpdate = _dim.checkForUpdate(sessionId, idFirstRun[i], hashFirstRun[i]);
      if (doUpdate) {
        boolean isCompound = false;
        if (i == 1 || i == 4) {
          isCompound = true;
        }
        _dim.visit(sessionId, idFirstRun[i], hashFirstRun[i], isCompound);
      } else {
        assertTrue(true);
      }
    }
    final Iterator<Id> itempty = _dim.obsoleteIdIterator(sessionId, DATA_SOURCE_ID);
    assertNotNull(itempty);
    assertFalse(itempty.hasNext());
    _dim.finish(sessionId);
    assertEquals(8, _dim.getEntryCount(DATA_SOURCE_ID));

    // first run, no modifications, obsoleteIdIterator must not return any ids
    final String sessionId1 = _dim.init(DATA_SOURCE_ID);
    // only mark index 0 and 1 (the single record and the top level compound record) as visited
    for (int i = 0; i < 2; i++) {
      final boolean doUpdate = _dim.checkForUpdate(sessionId1, idFirstRun[i], hashFirstRun[i]);
      assertFalse(doUpdate);
    }
    final Iterator<Id> itempty1 = _dim.obsoleteIdIterator(sessionId1, DATA_SOURCE_ID);
    assertNotNull(itempty1);
    assertFalse(itempty1.hasNext());
    _dim.finish(sessionId1);
    assertEquals(8, _dim.getEntryCount(DATA_SOURCE_ID));

    // second index run
    // the last entry was deleted
    final Id[] idSecondRun = new Id[7];
    for (int i = 0; i < idSecondRun.length; i++) {
      idSecondRun[i] = idFirstRun[i];
    }
    final String[] hashSecondRun = new String[7];
    for (int i = 0; i < hashSecondRun.length; i++) {
      if (i == 1 || i == 4) {
        hashSecondRun[i] = "hash_changed";
      } else {
        hashSecondRun[i] = "hash";
      }
    }

    final String sessionId2 = _dim.init(DATA_SOURCE_ID);
    for (int x = 0; x < idSecondRun.length; x++) {
      final boolean doUpdate = _dim.checkForUpdate(sessionId2, idSecondRun[x], hashSecondRun[x]);
      if (doUpdate) {
        boolean isCompound = false;
        if (x == 1 || x == 4) {
          isCompound = true;
        }
        _dim.visit(sessionId2, idSecondRun[x], hashSecondRun[x], isCompound);
      }
    }

    // the last id was deleted in the data source
    // now the controller needs to know which entries was not "visited"
    // therefore it calls obsoleteIdIterator
    Iterator<Id> it = _dim.obsoleteIdIterator(sessionId2, DATA_SOURCE_ID);
    assertTrue(it.hasNext()); // there must be the second entry
    final Id deleteID = it.next();
    assertEquals(deleteID, idFirstRun[7]);
    _dim.delete(sessionId2, deleteID);
    assertFalse(it.hasNext());
    _dim.finish(sessionId2);
    assertEquals(7, _dim.getEntryCount(DATA_SOURCE_ID));

    // simulate delete of whole compound, obsoleteIdIterator has to return all elements
    final String sessionId3 = _dim.init(DATA_SOURCE_ID);
    it = _dim.obsoleteIdIterator(sessionId3, DATA_SOURCE_ID);
    assertNotNull(it);
    int count = 0;
    for (count = 0; count < idFirstRun.length - 1; count++) {
      assertTrue(it.hasNext());
      final Id deleteId = it.next();
      assertNotNull(deleteId);
      _dim.delete(sessionId3, deleteId);
    }
    assertFalse(it.hasNext());
    assertEquals(idFirstRun.length - 1, count);
    _dim.finish(sessionId3);
    assertEquals(0, _dim.getEntryCount(DATA_SOURCE_ID));
  }

  /**
   * Test exception handling.
   */
  public void testDeltaIndexingSessionException() throws DeltaIndexingException {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(DATA_SOURCE_ID, "one");
    final String hash = "testhash";
    final String sessionId = "invalid session";

    // illegal state, calling any of those methods before calling init()
    try {
      _dim.checkForUpdate(null, id, hash);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.checkForUpdate(sessionId, id, hash);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    try {
      _dim.clear(null);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.clear(sessionId);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    try {
      _dim.delete(null, id);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.delete(sessionId, id);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    try {
      _dim.finish(null);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }

    try {
      _dim.finish(sessionId);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    try {
      _dim.obsoleteIdIterator(null, id);
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    } catch (DeltaIndexingSessionException e) {
      fail("expected UnsupportedOperationException");
    } catch (DeltaIndexingException e) {
      fail("expected UnsupportedOperationException");
    }

    try {
      _dim.obsoleteIdIterator(sessionId, id);
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    } catch (DeltaIndexingSessionException e) {
      fail("expected UnsupportedOperationException");
    } catch (DeltaIndexingException e) {
      fail("expected UnsupportedOperationException");
    }

    try {
      _dim.obsoleteIdIterator(null, DATA_SOURCE_ID);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.obsoleteIdIterator(sessionId, DATA_SOURCE_ID);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    try {
      _dim.rollback(null);
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    } catch (DeltaIndexingSessionException e) {
      fail("expected UnsupportedOperationException");
    }
    try {
      _dim.rollback(sessionId);
    } catch (UnsupportedOperationException e) {
      assertNotNull(e);
    } catch (DeltaIndexingSessionException e) {
      fail("expected UnsupportedOperationException");
    }

    try {
      _dim.visit(null, id, hash, false);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.visit(sessionId, id, hash, false);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }

    // must not throw any exception
    _dim.unlockDatasources();
    _dim.clear();

    // establish session and call with invalid values
    final String validSessionId = _dim.init(DATA_SOURCE_ID);
    try {
      _dim.finish(null);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + null, e.getMessage());
    }
    try {
      _dim.finish(sessionId);
    } catch (DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + sessionId, e.getMessage());
    }
    try {
      _dim.finish(validSessionId);
    } catch (DeltaIndexingSessionException e) {
      fail("unexpected exception " + e.getMessage());
    }
  }
}
