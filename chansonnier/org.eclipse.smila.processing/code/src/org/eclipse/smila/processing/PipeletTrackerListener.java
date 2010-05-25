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
 * Interface for listeners of pipelet class change events from SimplePipeletTracker.
 * 
 * @author jschumacher
 * 
 */
public interface PipeletTrackerListener {
  /**
   * event: the given pipelets are now available for instantiation.
   * 
   * @param pipeletClasses
   *          map of pipelet class names to new pipelet classes.
   */
  void pipeletsAdded(Map<String, Class<? extends IPipelet>> pipeletClasses);

  /**
   * event: the given pipelets are not available anymore for instantiation. Current instances should be removed
   * immediately.
   * 
   * @param pipeletClasses
   *          map of pipelet class names to removed pipelet classes.
   */

  void pipeletsRemoved(Map<String, Class<? extends IPipelet>> pipeletClasses);

}
