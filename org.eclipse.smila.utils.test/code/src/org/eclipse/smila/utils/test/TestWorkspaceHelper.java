/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.framework.Bundle;

/**
 * The Class TestWorkspaceHelper.
 */
public class TestWorkspaceHelper extends TestCase {

  /**
   * Test main workspace folder.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testMainWorkspaceFolder() throws IOException {
    final File file = WorkspaceHelper.createWorkingDir(AllTests.BUNDLE_ID);
    assertNotNull(file);
    assertTrue(file.exists());
  }

  /**
   * Test sub workspace folder.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testSubWorkspaceFolder() throws IOException {
    assertFalse(WorkspaceHelper.existsWorkingDir(AllTests.BUNDLE_ID, "my_folder"));
    final File file = WorkspaceHelper.createWorkingDir(AllTests.BUNDLE_ID, "my_folder");
    assertNotNull(file);
    assertTrue(file.exists());
    assertTrue(WorkspaceHelper.existsWorkingDir(AllTests.BUNDLE_ID, "my_folder"));
  }

  /**
   * Test main workspace folder by bundle.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testMainWorkspaceFolderByBundle() throws IOException {
    final Bundle bundle = Platform.getBundle(AllTests.BUNDLE_ID);
    final File file = WorkspaceHelper.createWorkingDirByBundle(bundle);
    assertNotNull(file);
    assertTrue(file.exists());
  }

}
