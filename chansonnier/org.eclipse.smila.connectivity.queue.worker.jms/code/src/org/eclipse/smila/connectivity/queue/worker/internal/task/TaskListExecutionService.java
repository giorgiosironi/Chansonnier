/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * Andreas Weber, Juergen Schumacher (empolis GmbH) - fix for 269967
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerTaskListType;
import org.eclipse.smila.connectivity.queue.worker.config.ProcessType;
import org.eclipse.smila.connectivity.queue.worker.config.SendType;
import org.eclipse.smila.connectivity.queue.worker.config.TaskListType;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.connectivity.queue.worker.internal.task.impl.Process;
import org.eclipse.smila.connectivity.queue.worker.internal.task.impl.Send;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Class TaskListExecutionService.
 */
public class TaskListExecutionService {

  /**
   * The _tasks.
   */
  @SuppressWarnings("unchecked")
  private final Map<Class, TaskExecutionService> _tasks = new HashMap<Class, TaskExecutionService>();

  /**
   * Instantiates a new task list execution service.
   */
  public TaskListExecutionService() {
    _tasks.put(ProcessType.class, new Process());
    _tasks.put(SendType.class, new Send());
  }

  /**
   * Execute.
   * 
   * @param accessPoint
   *          the access point
   * @param taskList
   *          the task list
   * @param recordPropertyMap
   *          a map of records and jms message properties
   * 
   * @throws TaskExecutionException
   *           the task execution exception
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  @SuppressWarnings("unchecked")
  public void execute(final ServicesAccessPoint accessPoint, final TaskListType taskList,
    final Map<Record, Properties> recordPropertyMap) throws TaskExecutionException, BlackboardAccessException {
    if (taskList != null) {
      // reinterpret "blackboardSync = false" as "use transient blackboard, do not write to storages".
      final Blackboard taskBlackboard = accessPoint.getBlackboardService(taskList.isBlackboardSync());
      final TaskExecutionEnv env = new TaskExecutionEnv(accessPoint, taskBlackboard);
      try {
        env.setCommitRequired(false);

        final HashMap<Id, Properties> idPropertyMap = new HashMap<Id, Properties>();
        final Iterator<Record> it = recordPropertyMap.keySet().iterator();
        while (it.hasNext()) {
          final Record record = it.next();
          idPropertyMap.put(record.getId(), recordPropertyMap.get(record));
          if (isInitiallySet(taskList)) {
            env.getBlackboard().setRecord(record);
          } else {
            env.getBlackboard().synchronize(record);
          }
        }

        for (final Object object : taskList.getProcessOrSend()) {
          final TaskExecutionService taskExecutionService = _tasks.get(object.getClass());
          if (taskExecutionService == null) {
            throw new RuntimeException("Unknown task " + object.getClass().getName());
          }
          taskExecutionService.execute(env, object, idPropertyMap);
        }
      } finally {
        // release records on blackboard.
        if (env.isCommitRequired()) {
          env.getBlackboard().commit();
        } else {
          env.getBlackboard().invalidate();
        }
      }
    }
  }

  /**
   * determine if record is initial and overwrites old version in storage, or must be synced.
   * 
   * @param taskList
   *          taskList to process
   * @return true if record can be written to blackboard without syncing
   */
  private boolean isInitiallySet(final TaskListType taskList) {
    // initially set is always true for router and configurable for listener
    boolean isInitiallySet = true;
    if (taskList instanceof ListenerTaskListType) {
      final ListenerTaskListType listenerTaskList = (ListenerTaskListType) taskList;
      isInitiallySet = listenerTaskList.isInitiallySet();
    }
    return isInitiallySet;
  }
}
