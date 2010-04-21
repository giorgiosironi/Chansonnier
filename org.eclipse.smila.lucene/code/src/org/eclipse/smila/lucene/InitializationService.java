/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.lucene;

import org.eclipse.smila.search.EIFActivator;
import org.osgi.service.component.ComponentContext;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class InitializationService {

  public static String s_bundleName;

  protected void activate(ComponentContext componentContext) {

    s_bundleName = componentContext.getBundleContext().getBundle().getSymbolicName();

    EIFActivator.registerSchemas();
  }

}
