/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.wrongconfig.test;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.connectivity.queue.worker.RecordRecycler;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.JmxTestCase;

public class TestWrongRecyclerConfig extends JmxTestCase {

  /**
   * The Constant AGENT.
   */
  private static final String AGENT = "QueueWorker/Recycler";

  /**
   * The Constant MAIN_SOURCE.
   */
  private static final String MAIN_SOURCE = "TestWrongRecyclerConfig_source";

  /**
   * The _blackboard.
   */
  protected Blackboard _blackboard;

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
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
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
    _recycler = null;
    super.tearDown();
  }

  /**
   * Test recycler.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecycler() throws Exception {
    final String status = (String) methodArgumentString(AGENT, "getStatus", MAIN_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.toString().equals(status)
      || RecordRecyclerStatus.FINISHED.toString().equals(status));

    final Record record = createTestRecord(MAIN_SOURCE, "key22");
    _blackboard.setRecord(record);
    _blackboard.commit(record.getId());
    try {
      _recycler.recycle("Recycler1", MAIN_SOURCE);
      throw new AssertionError("Recycler service should not process recycling because wrong config!");
    } catch (final Exception e) {
      assertNotNull(e);
    }
    assertEquals(_recycler.getRecordsRecycled(MAIN_SOURCE), 0);
    try {
      method(AGENT, "startRecycle", new Object[] { "Recycler1", MAIN_SOURCE }, new String[] {
        String.class.getName(), String.class.getName() });
      throw new AssertionError("Recycler service should not process recycling because wrong config!");
    } catch (final Exception e) {
      assertNotNull(e);
    }
    assertEquals(_recycler.getRecordsRecycled(MAIN_SOURCE), 0);
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
