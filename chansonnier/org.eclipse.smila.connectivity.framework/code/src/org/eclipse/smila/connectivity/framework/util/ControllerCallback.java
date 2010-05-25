/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util;

import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;

/**
 * Interface for callbacks on Controllers. This interface is used Agents and CrawlThreads to check operations for delta
 * indexing.
 */
public interface ControllerCallback {

  /**
   * Checks if delta indexing is enabled or disabled. Some delta indexing methods provide methods separate methods.
   * 
   * @param deltaIndexingType
   *          the delta indexing type
   * @return true if it is enabled, false otherwise.
   */
  boolean doDeltaIndexing(final DeltaIndexingType deltaIndexingType);

  /**
   * Checks if delta indexing checkForUpdate should be executed.
   * 
   * @param deltaIndexingType
   *          the delta indexing type
   * @return true if it should be executed, false otherwise.
   */
  boolean doCheckForUpdate(final DeltaIndexingType deltaIndexingType);

  /**
   * Checks if delta indexing delete delta should be executed.
   * 
   * @param deltaIndexingType
   *          the delta indexing type
   * @return true if it should be executed, false otherwise.
   */
  boolean doDeltaDelete(final DeltaIndexingType deltaIndexingType);
}
