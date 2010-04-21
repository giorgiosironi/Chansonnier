/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.connectivity.queue.worker.Listener;
import org.eclipse.smila.connectivity.queue.worker.RecordRecycler;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.JmxTestCase;

/**
 * The Class TestWorkerBase.
 */
public abstract class AbstractTestWorker extends JmxTestCase {

  /**
   * The Constant SMALL_PAUSE.
   */
  protected static final int SMALL_PAUSE = 100;

  /**
   * The Constant PAUSE.
   */
  protected static final int PAUSE = 5000;

  /**
   * The Constant MAX_PAUSE.
   */
  protected static final int MAX_PAUSE = 180000;

  /**
   * The _blackboard.
   */
  protected Blackboard _blackboard;

  /**
   * The _router.
   */
  protected Router _router;

  /**
   * The _listener.
   */
  protected Listener _listener;

  /**
   * The _recycler.
   */
  protected RecordRecycler _recycler;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _log.info("SETUP getting BlackboardService...");
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull(factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull(_blackboard);
    _log.info("SETUP getting Router...");
    _router = getService(Router.class);
    assertNotNull(_router);
    _log.info("SETUP getting Listener...");
    _listener = getService(Listener.class);
    assertNotNull(_listener);
    _log.info("SETUP getting Recycler...");
    _recycler = getService(RecordRecycler.class);
    assertNotNull(_recycler);
    _log.info("SETUP Worker OK...");
    super.setUp();
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _blackboard = null;
    _router = null;
    _listener = null;
    _recycler = null;
    super.tearDown();
  }

  /**
   * Creates the test record.
   * 
   * @param source
   *          the source
   * @param key
   *          the key
   * 
   * @return a test record.
   */
  protected Record createTestRecord(final String source, final String key) {
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(IdFactory.DEFAULT_INSTANCE.createId(source, key));
    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);
    final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    final Literal value = RecordFactory.DEFAULT_INSTANCE.createLiteral();
    value.setStringValue("value1");
    attribute.addLiteral(value);
    metadata.setAttribute("attribute1", attribute);
    record.setAttachment("attachment1", "bytes attachment".getBytes());
    return record;
  }
}
