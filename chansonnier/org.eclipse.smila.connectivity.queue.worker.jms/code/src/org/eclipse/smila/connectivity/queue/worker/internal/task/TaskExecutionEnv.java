/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.task;

import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.blackboard.Blackboard;

/**
 * The Class TaskExecutionEnv.
 */
public class TaskExecutionEnv {

  /**
   * The _services access point.
   */
  private final ServicesAccessPoint _servicesAccessPoint;

  /**
   * The _blackboard.
   */
  private final Blackboard _blackboard;

  /**
   * The _commit required.
   */
  private boolean _commitRequired;

  /**
   * Instantiates a new task execution env.
   * 
   * @param accessPoint
   *          the access point
   * @param blackboard
   *          the blackboard to use
   */
  public TaskExecutionEnv(final ServicesAccessPoint accessPoint, final Blackboard blackboard) {
    _servicesAccessPoint = accessPoint;
    _blackboard = blackboard;
  }

  /**
   * Checks if is commit required.
   * 
   * @return true, if is commit required
   */
  public boolean isCommitRequired() {
    return _commitRequired;
  }

  /**
   * Sets the commit required.
   * 
   * @param commitRequired
   *          the new commit required
   */
  public void setCommitRequired(final boolean commitRequired) {
    _commitRequired = commitRequired;
  }

  /**
   * Gets the services.
   * 
   * @return the services
   */
  public ServicesAccessPoint getServices() {
    return _servicesAccessPoint;
  }

  /**
   * Gets the blackboard holding the records to process.
   * 
   * @return blackboard instance
   */
  public Blackboard getBlackboard() {
    return _blackboard;
  }

}
