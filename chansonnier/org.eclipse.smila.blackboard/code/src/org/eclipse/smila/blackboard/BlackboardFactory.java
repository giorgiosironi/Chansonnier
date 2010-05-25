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

package org.eclipse.smila.blackboard;

/**
 * Factory service to create blackboard instances.
 * 
 * @author jschumacher
 * 
 */
public interface BlackboardFactory {
  /**
   * create a new non-persisting blackboard instance. This method must always return a valid empty blackboard instance.
   * 
   * @return blackboard instance that does not persist into storages.
   */
  Blackboard createTransientBlackboard();

  /**
   * create a blackboard able to persist records in storages.
   * 
   * @return blackboard instance that persist into configured storages.
   * @throws BlackboardAccessException
   *           no persisting blackboard can be created, because not even a binary storage service is available (record
   *           storage remains optional)
   */
  Blackboard createPersistingBlackboard() throws BlackboardAccessException;
}
