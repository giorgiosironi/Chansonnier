/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * Andreas Weber, Juergen Schumacher (empolis GmbH) - fix for 269967
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.task.impl;

import java.util.Map;
import java.util.Properties;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.connectivity.queue.worker.config.ProcessType;
import org.eclipse.smila.connectivity.queue.worker.internal.task.AbstractTask;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskExecutionEnv;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;

/**
 * The Class Process.
 */
public class Process extends AbstractTask<ProcessType> {

  /**
   * Instantiates a new process.
   */
  public Process() {
    super("TASK/Process");
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public Id[] executeInternal(final TaskExecutionEnv env, final ProcessType config,
    final Map<Id, Properties> idPropertyMap) throws ProcessingException, BlackboardAccessException {
    final Blackboard bb = env.getBlackboard();
    final Id[] outputIds =
      env.getServices().getWorkflowProcessor().process(config.getWorkflow(), bb,
        idPropertyMap.keySet().toArray(new Id[idPropertyMap.size()]));
    env.setCommitRequired(true);
    return outputIds;
  }
}
