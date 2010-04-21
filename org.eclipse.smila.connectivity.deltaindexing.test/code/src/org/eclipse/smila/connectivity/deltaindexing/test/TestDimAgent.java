/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.test;

import java.util.Map;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager.LockState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Base test class for DeltaIndexingManager implementations.
 */
public class TestDimAgent extends DeclarativeServiceTestCase {

  /**
   * The Constant DATA_SOURCE_ID.
   */
  private static final String DATA_SOURCE_ID = "some_datasource_id";

  /**
   * The _dima.
   */
  protected DeltaIndexingManagerAgent _dima;

  /**
   * The _dim.
   */
  protected DeltaIndexingManager _dim;

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _dima = getService(DeltaIndexingManagerAgent.class);
    if (_dima == null) {
      throw new DeltaIndexingException("Unable to find Delta Indexing Manager Agent reference!");
    }
    _dim = getService(DeltaIndexingManager.class, "(smila.connectivity.deltaindexing.impl=memory)");
    if (_dim == null) {
      throw new DeltaIndexingException("Unable to find Delta Indexing Manager reference!");
    }
    _dim.clear();
  }

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _dima = null;
  }

  /**
   * Test DeltaIndexingManagerAgent.
   *
   * @throws DeltaIndexingSessionException
   *           on invalid sessions
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void testAgent() throws DeltaIndexingSessionException, DeltaIndexingException {
    String message = _dima.clear(null);
    assertEquals("Error: Unable to find index for datasource [null]", message);
    message = _dima.clear(DATA_SOURCE_ID);
    assertEquals("Error: Unable to find index for datasource [" + DATA_SOURCE_ID + "]", message);

    Object result = _dima.getEntryCount(DATA_SOURCE_ID);
    assertTrue(result instanceof String);
    assertEquals("Error: Unable to find index for datasource [" + DATA_SOURCE_ID + "]", result);

    Map<String, Long> entries = _dima.getEntryCounts();
    assertNotNull(entries);
    assertEquals(0, entries.size());

    Map<String, String> locks = _dima.getLockStates();
    assertNotNull(locks);
    assertEquals(0, locks.size());

    final String sessionId = _dim.init(DATA_SOURCE_ID);

    locks = _dima.getLockStates();
    assertNotNull(locks);
    assertEquals(1, locks.size());
    assertEquals(LockState.LOCKED.name(), locks.get(DATA_SOURCE_ID));

    _dim.finish(sessionId);

    result = _dima.getEntryCount(DATA_SOURCE_ID);
    assertTrue(result instanceof Long);
    assertEquals(0L, ((Long) result).longValue());
    entries = _dima.getEntryCounts();
    assertNotNull(entries);
    assertEquals(1, entries.size());
    final Map.Entry<String, Long> entry = entries.entrySet().iterator().next();
    assertNotNull(entry);
    assertEquals(DATA_SOURCE_ID, entry.getKey());
    assertEquals(0L, entry.getValue().longValue());

    locks = _dima.getLockStates();
    assertNotNull(locks);
    assertEquals(1, locks.size());
    assertEquals(LockState.UNLOCKED.name(), locks.get(DATA_SOURCE_ID));

    // check unlocking
    final String newSessionId = _dim.init(DATA_SOURCE_ID);
    locks = _dima.getLockStates();
    assertNotNull(locks);
    assertEquals(1, locks.size());
    assertEquals(LockState.LOCKED.name(), locks.get(DATA_SOURCE_ID));

    _dima.unlock(DATA_SOURCE_ID);
    locks = _dima.getLockStates();
    assertNotNull(locks);
    assertEquals(1, locks.size());
    assertEquals(LockState.UNLOCKED.name(), locks.get(DATA_SOURCE_ID));

    try {
      _dim.finish(newSessionId);
    } catch (final DeltaIndexingSessionException e) {
      assertNotNull(e);
      assertEquals("Invalid session id: " + newSessionId, e.getMessage());
    }

    _dima.unlockAll();
    _dima.clear(DATA_SOURCE_ID);
    _dima.clearAll();
  }
}
