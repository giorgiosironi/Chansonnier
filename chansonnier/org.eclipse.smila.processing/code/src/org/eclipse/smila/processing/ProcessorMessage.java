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

import java.util.Collection;

import org.eclipse.smila.datamodel.id.Id;

/**
 * Wrapper for the record ID arrays used in interfaces of SimplePipelet and ProcessingService. Maybe, these interfaces
 * should be changed to use this type instead of Id[] for consistency with SearchPipelet and SearchProcessingServices.
 * For now this class can be used in WorkflowProcessors to enable more uniform handling of ProcessorMessages and
 * SearchMessages.
 * 
 * @author jschumacher
 * 
 */
public class ProcessorMessage {

  /**
   * the records to process.
   */
  private Id[] _records;

  /**
   * default constructor.
   */
  public ProcessorMessage() {
    super();
  }

  /**
   * create instance from data.
   * 
   * @param recordIds
   *          the records to process.
   */
  public ProcessorMessage(Id[] recordIds) {
    _records = recordIds;
  }

  /**
   * 
   * @return the records to process.
   */
  public Id[] getRecords() {
    return _records;
  }

  /**
   * 
   * @param records
   *          the records to process.
   */
  public void setRecords(Id[] records) {
    _records = records;
  }

  /**
   * set records array from a list.
   * 
   * @param records
   *          record list.
   */
  public void setRecords(Collection<Id> records) {
    if (records != null) {
      _records = records.toArray(new Id[records.size()]);
    }
  }

  /**
   * 
   * @return true, if the message contains a ID list (even an empty one). false, if the list is actually null.
   */
  public boolean hasRecords() {
    return _records != null;
  }

}
