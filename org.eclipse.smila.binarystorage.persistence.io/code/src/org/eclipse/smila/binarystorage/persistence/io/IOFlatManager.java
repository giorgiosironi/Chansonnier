/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io;

import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * Flat structure implementation.
 * 
 * @author mcimpean
 */
public class IOFlatManager extends IOBinaryPersistence {

  /**
   * Basic constructor.
   * 
   * @param binaryStorageConfig
   * @throws BinaryStorageException
   */
  public IOFlatManager(BinaryStorageConfiguration binaryStorageConfig) throws BinaryStorageException {
    super(binaryStorageConfig);
  }
}
