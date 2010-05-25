/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.scriptexecution;

import java.io.File;
import java.io.IOException;

/**
 * Interface for script execution.
 */
public interface ScriptExecutor {

  /**
   * Executes script.
   * 
   * @param file
   *          script file
   * 
   * @return result code
   * 
   * @throws IOException
   *           IOException
   * @throws InterruptedException
   *           InterruptedException
   */
  int execute(File file) throws IOException, InterruptedException;
}
