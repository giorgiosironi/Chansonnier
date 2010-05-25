/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util;

/**
 * A Enum defining the states a Agent internal thread can be in.
 */
public enum AgentThreadState {

  /**
   * The different states. Running - the Agent is actively running Stopped - the Agent was stopped by user interaction
   * Aborted - the Agent aborted because of some fatal error
   */
  Running, Stopped, Aborted
}
