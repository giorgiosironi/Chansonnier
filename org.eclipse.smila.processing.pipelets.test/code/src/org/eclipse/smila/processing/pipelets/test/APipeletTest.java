/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.test;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * Base class for Pipelet tests.
 */
public abstract class APipeletTest extends DeclarativeServiceTestCase {

  /**
   * Blackboard service to use in test.
   */
  private Blackboard _blackboard;

  /**
   * Check if BlackboardService service is active. Wait up to 30 seconds for start. Fail, if no service is starting.
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    forceStartBundle("org.eclipse.smila.blackboard");
    forceStartBundle("org.eclipse.smila.processing");
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
  }

  /**
   * @return the blackboard
   */
  public Blackboard getBlackboard() {
    return _blackboard;
  }

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
    _blackboard.invalidate(id);
    _blackboard.create(id);
    return id;
  }

  /**
   * Load a PipeletConfiguration.
   * 
   * @param bundleName
   *          the bundleName
   * @param configPath
   *          the configPath
   * @return a PipeletConfiguration
   * @throws JAXBException
   *           if any error occurs
   */
  protected PipeletConfiguration loadPipeletConfiguration(String bundleName, String configPath)
    throws JAXBException {
    final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller();
    final InputStream inputStream = ConfigUtils.getConfigStream(bundleName, configPath);
    try {
      final PipeletConfiguration configuration = (PipeletConfiguration) unmarshaller.unmarshal(inputStream);
      assertNotNull(configuration);
      return configuration;
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  /**
   * configure a pipelet.
   * 
   * @param pipelet
   *          pipelet to configure
   * @param bundleName
   *          the bundleName
   * @param configPath
   *          the configPath
   * @throws ProcessingException
   * @throws JAXBException
   *           error loading config
   * @throws ProcessingException
   *           pipelet config error
   */
  protected void configurePipelet(SimplePipelet pipelet, String bundleName, String configPath)
    throws ProcessingException, JAXBException {
    final PipeletConfiguration configuration = loadPipeletConfiguration(bundleName, configPath);
    pipelet.configure(configuration);
  }
}
