/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.File;
import java.net.URL;

import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.scriptexecution.ScriptExecutor;
import org.eclipse.smila.utils.scriptexecution.ScriptExecutorFactory;
import org.eclipse.smila.utils.scriptexecution.UnixScriptExecutor;
import org.eclipse.smila.utils.scriptexecution.WindowsScriptExecutor;

/**
 * The Class TestScriptExecutor.
 */
public class TestScriptExecutor extends DeclarativeServiceTestCase {

  /**
   * Test factory.
   */
  public void testFactory() {
    final ScriptExecutor scriptExecutor = ScriptExecutorFactory.getScriptExecutor();
    assertNotNull(scriptExecutor);
  }

  /**
   * Test windows executor.
   */
  public void testWindowsExecutor() {
    final WindowsScriptExecutor executor = new WindowsScriptExecutor();
    // in reality do nothing
    try {
      final URL url = this.resolveResourceURL("/res/test.bat");
      executor.execute(new File(url.getFile()));
    } catch (final Throwable e) {
      // do nothing
      _log.error(e);
    }
  }

  /**
   * Test windows executor.
   */
  public void testWindowsExecutorWithWrongExt() {
    final WindowsScriptExecutor executor = new WindowsScriptExecutor();
    // in reality do nothing
    try {
      final URL url = this.resolveResourceURL("/res/test");
      executor.execute(new File(url.getFile()));
    } catch (final Throwable e) {
      // do nothing
      _log.error(e);
    }
  }

  /**
   * Test linux executor.
   */
  public void testLinuxExecutor() {
    final UnixScriptExecutor executor = new UnixScriptExecutor();
    // in reality do nothing
    try {
      final URL url = this.resolveResourceURL("/res/test.sh");
      executor.execute(new File(url.getFile()));
    } catch (final Throwable e) {
      // do nothing
      _log.error(e);
    }
  }

}
