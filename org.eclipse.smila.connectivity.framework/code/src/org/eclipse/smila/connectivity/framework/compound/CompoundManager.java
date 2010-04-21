/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound;

import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Interface CompoundManager.
 */
public interface CompoundManager {

  /**
   * Checks if a record is a compound object.
   * 
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return true if the record is a compound object and is extractable by this CompoundManager, false otherwise
   * @throws CompoundException
   *           if any error occurs
   */
  boolean isCompound(final Record record, final DataSourceConnectionConfig config) throws CompoundException;

  /**
   *    * Extracts the elements of the given record and returns a Crawler over the extracted elements.
   * 
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return a Crawler interface over the extracted elements
   * @throws CompoundException
   *           if any error occurs
   */
  Crawler extract(final Record record, final DataSourceConnectionConfig config) throws CompoundException;

  /**
   * Adapts the input record according to the given configuration. The record may be left unmodified, modified or even
   * set to null.
   * 
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return the adapted record
   * @throws CompoundException
   *           if any error occurs
   */
  Record adaptCompoundRecord(final Record record, final DataSourceConnectionConfig config) throws CompoundException;
}
