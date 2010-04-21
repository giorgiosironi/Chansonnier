/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.agent.jobfile.test;

import java.io.File;

import org.eclipse.smila.connectivity.framework.AgentState;
import org.eclipse.smila.connectivity.framework.agent.jobfile.JobFileAgent;
import org.eclipse.smila.connectivity.framework.agent.jobfile.messages.Process;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestCrawler.
 */
public class TestAgent extends DeclarativeServiceTestCase implements AgentControllerCallback {

  /**
   * timeout for service detection.
   */
  private static final long WAIT_FOR_SERVICE_DELAY = 30000;

  /**
   * The Constant PAUSE.
   */
  private static final int PAUSE = 1000;

  /**
   * the Crawler.
   */
  private JobFileAgent _agent;

  /**
   * The data source configuration.
   */
  private DataSourceConnectionConfig _config;

  /**
   * The unregistered flag.
   */
  private boolean _unregistered;

  /**
   * Add counter.
   */
  private int _addCount;

  /**
   * Delete counter.
   */
  private int _delCount;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    forceStartBundle("org.eclipse.smila.connectivity.framework.agent.jobfile");
    // register the service, because it's configuration uses immediate="false"
    _agent = registerService(new JobFileAgent(), null, JobFileAgent.class, WAIT_FOR_SERVICE_DELAY);
    assertNotNull(_agent);
    _config = ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("ConfigExample.xml"));
    assertNotNull(_config);
    // set path to configuration
    ((Process) _config.getProcess()).getJobFileUrl().clear();

    final File file = new File("configuration/data/testjobfile.xml");
    final String jobfile = "file://" + file.getAbsolutePath().replaceAll("\\\\", "/");
    ((Process) _config.getProcess()).getJobFileUrl().add(jobfile);
    Thread.sleep(PAUSE);

    _addCount = 0;
    _delCount = 0;
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
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#add(String, DeltaIndexingType, Record, String)
   */
  public void add(final String sessionId, final DeltaIndexingType deltaIndexingType, final Record record,
    final String hash) {
    assertNotNull(record);
    assertEquals(_config.getDataSourceID(), record.getId().getSource());
    _addCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#delete(String, DeltaIndexingType, Id)
   */
  public void delete(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id id) {
    // should never be called in this test
    assertNotNull(id);
    assertEquals(_config.getDataSourceID(), id.getSource());
    _delCount++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#unregister(String, DeltaIndexingType, String)
   */

  public void unregister(final String sessionId, final DeltaIndexingType deltaIndexingType,
    final String dataSourceId) {
    assertNotNull(dataSourceId);
    assertEquals(_config.getDataSourceID(), dataSourceId);
    _unregistered = true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#doCheckForUpdate(DeltaIndexingType)
   */

  public boolean doCheckForUpdate(final DeltaIndexingType deltaIndexingType) {
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#doDeltaIndexing(DeltaIndexingType)
   */
  public boolean doDeltaIndexing(final DeltaIndexingType deltaIndexingType) {
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see AgentControllerCallback#doDeltaDelete(DeltaIndexingType)
   */
  public boolean doDeltaDelete(final DeltaIndexingType deltaIndexingType) {
    return true;
  }

  /**
   * Test agent.
   * 
   * @throws Exception
   *           the exception
   */
  public void testAgent() throws Exception {
    assertEquals(0, _addCount);
    assertEquals(0, _delCount);
    assertFalse(_unregistered);

    _agent.start(this, new AgentState(), _config, "dummy_session_id");
    Thread.sleep(PAUSE);
    assertEquals(AgentThreadState.Running, _agent.getAgentState().getState());
    _agent.stop();
    Thread.sleep(PAUSE);
    assertEquals(AgentThreadState.Stopped, _agent.getAgentState().getState());

    assertEquals(1 + 2, _addCount);
    assertEquals(2, _delCount);
    assertTrue(_unregistered);
  }

  /**
   * Test agent with invalid config.
   * 
   * @throws Exception
   *           the exception
   */
  public void testInvalidAgentConfig() throws Exception {
    assertEquals(0, _addCount);
    assertEquals(0, _delCount);
    assertFalse(_unregistered);
    final String filename = "configuration/data/invalidjobfile.xml";
    final File file = new File(filename);
    final String jobfile = "file://" + file.getAbsolutePath().replaceAll("\\\\", "/");
    ((Process) _config.getProcess()).getJobFileUrl().clear();
    ((Process) _config.getProcess()).getJobFileUrl().add(jobfile);

    _agent.start(this, new AgentState(), _config, "dummy_session_id");
    Thread.sleep(PAUSE);
    assertEquals(AgentThreadState.Running, _agent.getAgentState().getState());
    _agent.stop();
    Thread.sleep(PAUSE);

    final AgentState agentState = _agent.getAgentState();
    assertNotNull(agentState);
    assertEquals(AgentThreadState.Stopped, agentState.getState());
    final Throwable t = agentState.getLastError();
    assertNotNull(t);
    assertNotNull(t.getMessage());
    assertTrue(t.getMessage().startsWith("Invalid document"));
    assertTrue(t.getMessage().indexOf(filename) > 0);
    assertTrue(t.getMessage().endsWith(". Must begin with tag <JobFile>"));

    assertEquals(0, _addCount);
    assertEquals(0, _delCount);
    assertTrue(_unregistered);
  }
}
