/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.config.ConfigurationLoadException;

/**
 * The Class TestConfigUtils.
 */
public class TestConfigUtils extends TestCase {

  /**
   * Test root folder.
   */
  public void testRootFolder() {
    final File file = ConfigUtils.getConfigurationFolder();
    assertNotNull(file);
  }

  /**
   * Test entries.
   */
  public void testEntries() {
    final List<String> entries = ConfigUtils.getConfigEntries(AllTests.BUNDLE_ID, "res");
    assertNotNull(entries);
    assertTrue(entries.size() > 0);
  }

  /**
   * Test null folder.
   */
  public void testNullFolder() {
    assertNull(ConfigUtils.getConfigFolder(AllTests.BUNDLE_ID, "res2"));
  }

  /**
   * Test entries no folder ex.
   */
  public void testEntriesNoFolderEx() {
    List<String> entries = null;
    try {
      entries = ConfigUtils.getConfigEntries(AllTests.BUNDLE_ID, "res2");
      throw new AssertionError();
    } catch (final ConfigurationLoadException e) {
      ;// ok
    }
    assertNull(entries);
  }

  /**
   * Test entries no bundle ex.
   */
  public void testEntriesNoBundleEx() {
    List<String> entries = null;
    try {
      entries = ConfigUtils.getConfigEntries("qq", "res");
      throw new AssertionError();
    } catch (final ConfigurationLoadException e) {
      ;// ok
    }
    assertNull(entries);
  }

  /**
   * Test stream no bundle ex.
   */
  public void testStreamNoBundleEx() {
    try {
      ConfigUtils.getConfigStream("qq", "res");
      throw new AssertionError();
    } catch (final ConfigurationLoadException e) {
      ;// ok
    }
  }

  /**
   * Test stream no file ex.
   */
  public void testStreamNoFileEx() {
    try {
      ConfigUtils.getConfigStream(AllTests.BUNDLE_ID, "q");
      throw new AssertionError();
    } catch (final ConfigurationLoadException e) {
      ;// ok
    }
  }

  /**
   * Test config load exception.
   */
  public void testConfigLoadException() {
    boolean isThrown = false;
    try {
      throw new ConfigurationLoadException("My exception", new ConfigurationLoadException(
        new ConfigurationLoadException()));
    } catch (final ConfigurationLoadException e) {
      assertNotNull(e.getCause());
      assertNotNull(e.getCause().getCause());
      isThrown = true;
    }
    if (!isThrown) {
      throw new RuntimeException("It was unsuccessfully thrown exception before ;)");
    }
  }
}
