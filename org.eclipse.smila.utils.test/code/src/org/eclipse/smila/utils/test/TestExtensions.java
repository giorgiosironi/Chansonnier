/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import junit.framework.TestCase;

import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.utils.extensions.AbstractCollectionPluginRegistry;
import org.eclipse.smila.utils.extensions.AbstractSinglePluginRegistry;
import org.eclipse.smila.utils.test.interfaces.TestPluginInterface;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The Class TestExtensions.
 */
public class TestExtensions extends TestCase {

  /**
   * The Constant PAUSE.
   */
  private static final int PAUSE = 500;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // to increase coverage
    LogFactory.getFactory().setAttribute("log4j.logger.org.eclipse.smila", "TRACE, file");
  }

  /**
   * Test registry.
   * 
   * @throws BundleException
   *           the bundle exception
   */
  public void testRegistry() throws BundleException {
    final TestCollectionRegistry collectionRegistry = new TestCollectionRegistry();
    final TestSingleRegistry singleRegistry = new TestSingleRegistry();
    TestPluginInterface[] plugins = collectionRegistry.getPlugins();
    assertEquals(plugins.length, 1);
    TestPluginInterface plugin = singleRegistry.getPlugin();
    assertNotNull(plugin);
    final Bundle bundle = Platform.getBundle("org.eclipse.smila.utils.test.plugin");
    assertNotNull(bundle);
    bundle.uninstall();
    try {
      Thread.sleep(PAUSE);
    } catch (final InterruptedException e) {
      ;// nothing
    }
    plugins = collectionRegistry.getPlugins();
    assertEquals(plugins.length, 0);
    plugin = singleRegistry.getPlugin();
    assertNull(plugin);
  }

  /**
   * The Class TestRegistry.
   */
  private static class TestCollectionRegistry extends AbstractCollectionPluginRegistry<TestPluginInterface> {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractCollectionPluginRegistry#createEmptyArray(int)
     */
    @Override
    protected TestPluginInterface[] createEmptyArray(final int size) {
      return new TestPluginInterface[size];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointLocalName()
     */
    @Override
    protected String getExtensionPointLocalName() {
      return "plugin";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointNameSpace()
     */
    @Override
    protected String getExtensionPointNameSpace() {
      return "org.eclipse.smila.utils.test";
    }

  }

  /**
   * The Class TestSingleRegistry.
   */
  private static class TestSingleRegistry extends AbstractSinglePluginRegistry<TestPluginInterface> {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointLocalName()
     */
    @Override
    protected String getExtensionPointLocalName() {
      return "plugin";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointNameSpace()
     */
    @Override
    protected String getExtensionPointNameSpace() {
      return "org.eclipse.smila.utils.test";
    }

  }

}
