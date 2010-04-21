/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT-Solutions GmbH) - initial creator
 **********************************************************************************************************************/
/**
 * 
 */
package org.eclipse.smila.product;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The Application.
 * 
 * @author August Georg Schmidt (brox IT-Solutions GmbH)
 */
public class Application implements IApplication {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
   */
  public Object start(final IApplicationContext context) throws Exception {

    final String[] arguments = Platform.getApplicationArgs();

    for (final String arg : arguments) {
      System.out.println(arg);
    }

    return IApplication.EXIT_OK;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  public void stop() {
    // do nothing
  }

}
