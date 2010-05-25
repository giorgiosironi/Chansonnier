/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage;

import java.util.Iterator;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;

/**
 * RecordStorage service interface.
 */
public interface RecordStorage {
  
  /**
   * Load the record with the given Id.
   * @param id Id of the record
   * @return a Record object or null, if no record with the given Id exists
   * @throws RecordStorageException if any error occurs
   */
  Record loadRecord(Id id) throws RecordStorageException;

  /**
   * Stores the given Record object. An existing Record with the same ID is overwritten by the given record.
   * @param record the Record object
   * @throws RecordStorageException if any error occurs
   */
  void storeRecord(Record record) throws RecordStorageException;

  /**
   * Removes the record with the given Id.
   * @param id Id of the record
   * @throws RecordStorageException if any error occurs
   */
  void removeRecord(Id id) throws RecordStorageException;

  /**
   * Checks if a Record with the given Id exists in the storeage.
   * @param id Id of the record
   * @return true if a record with the given Id exists, false otherwise
   * @throws RecordStorageException if any error occurs
   */
  boolean existsRecord(Id id) throws RecordStorageException;
  
  /**
   * Loads all records of the given source.
   * @param source the name of the data source
   * @return an Iterator over the Record objects
   * @throws RecordStorageException if any error occurs
   */
  Iterator<Record> loadRecords(String source) throws RecordStorageException;
}
