/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker;

import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Interface Router.
 */
public interface Router extends QueueWorker {

  /**
   * Route record.
   * 
   * @param record
   *          the record
   * @param operation
   *          the operation
   * @return a Map of Id and exceptions if certain records could not be routed
   * 
   * @throws RouterException
   *           the router exception
   */
  Map<Id, Exception> route(Record[] record, Operation operation) throws RouterException;
}
