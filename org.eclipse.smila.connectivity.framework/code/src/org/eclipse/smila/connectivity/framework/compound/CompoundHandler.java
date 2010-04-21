/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound;

import java.util.Collection;

import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Interface CompoundHandler.
 */
public interface CompoundHandler {

  /**
   * Gets the mime types the CompoundHandler is capable to extract.
   * @return a Collection of mime types the CompoundHandler is capable to extract.
   */
  Collection<String> getSupportedMimeTypes();

  /**
   * Extracts the elements of the given record and returns a Crawler over the extracted elements.
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return a Crawler interface over the extracted elements
   * @throws CompoundException
   *           if any error occurs
   */
  Crawler extract(final Record record, final DataSourceConnectionConfig config) throws CompoundException;
}
