/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker;

/**
 * The Enum RecordRecyclerStatus.
 */
public enum RecordRecyclerStatus {

  /**
   * The STARTED.
   */
  STARTED,

  /**
   * The I n_ progress.
   */
  IN_PROGRESS,

  /**
   * The STOPPING.
   */
  STOPPING,

  /**
   * The STOPPED.
   */
  STOPPED,

  /**
   * The FINISHED.
   */
  FINISHED,

  /**
   * The EXCEPTION.
   */
  EXCEPTION
}
