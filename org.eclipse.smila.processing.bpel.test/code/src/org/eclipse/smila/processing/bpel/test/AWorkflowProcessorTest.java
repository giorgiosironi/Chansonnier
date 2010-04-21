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

package org.eclipse.smila.processing.bpel.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Base class for WorkflowProcessor tests.
 * 
 * @author jschumacher
 * 
 */
public abstract class AWorkflowProcessorTest extends DeclarativeServiceTestCase {
  /**
   * WorkflowProcessor instance to test.
   */
  private WorkflowProcessor _processor;

  /**
   * Blackboard service to use in test.
   */
  private Blackboard _blackboard;

  /** The log. */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Check if WorkflowProcessor service is active. Wait up to 30 seconds for start. Fail, if no service is starting.
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    forceStartBundle("org.eclipse.smila.blackboard");
    forceStartBundle("org.eclipse.smila.processing");
    forceStartBundle("org.eclipse.smila.processing.bpel");
    _processor = getService(WorkflowProcessor.class);
    assertNotNull("no WorkflowProcessor service found.", _processor);
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
    assertTrue(getPipelineName() + " not active", getProcessor().getWorkflowNames().contains(getPipelineName()));
    _log.info("*** TESTING PIPELINE " + getPipelineName() + " ***");
  }

  /**
   * @return the processor
   */
  public WorkflowProcessor getProcessor() {
    return _processor;
  }

  /**
   * @return the blackboard
   */
  public Blackboard getBlackboard() {
    return _blackboard;
  }

  /**
   * @return name of test pipeline
   */
  protected abstract String getPipelineName();

  /**
   * create a new record on the blackboard.
   * 
   * @param source
   *          source value of ID
   * @param key
   *          key value of ID
   * @return id of created record.
   */
  protected Id createBlackboardRecord(String source, String key) {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
    _log.info("Invalidating and re-creating test record on blackboard.");
    _log.info("This may cause an exception to be logged that can be safely ignored.");
    getBlackboard().invalidate(id);
    getBlackboard().create(id);
    return id;
  }

}
