/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing;

import java.util.Map;

/**
 * service interface for tracker of SimplePipelets.
 * 
 * @author jschumacher
 * 
 */
public interface PipeletTracker {

  /**
   * get all currently registered pipelets.
   * 
   * @return map of class names to pipelet classes.
   */
  Map<String, Class<? extends IPipelet>> getRegisteredPipelets();

  /**
   * add a listener that will be notified about new or deactivated pipelet classes on bundle changes. This is also set
   * as a service reference bind method in component descriptor, so the suggested way to use this is to register
   * SimplePipeletListener service instead of calling the method directly.
   * 
   * @param listener
   *          a new listener for SimplePipeletTracker events.
   */
  void addListener(PipeletTrackerListener listener);

  /**
   * remove a listener that will be notified about new or deactivated pipelet classes on bundle changes. This is also
   * set as a service reference unbind method in component descriptor, so the suggested way to use this is to register
   * SimplePipeletListener service instead of calling the method directly.
   * 
   * @param listener
   *          an obsolete listener for SimplePipeletTracker events.
   */
  void removeListener(PipeletTrackerListener listener);
}
