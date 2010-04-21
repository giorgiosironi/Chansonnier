/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.efs;

import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * EFS Flat structure implementation.
 * 
 * @author mcimpean
 */
public class EFSFlatManager extends EFSBinaryPersistence {
  /**
   * Basic constructor.
   * 
   * @param binaryStorageConfig
   * @throws BinaryStorageException
   */
  public EFSFlatManager(BinaryStorageConfiguration binaryStorageConfig) throws BinaryStorageException {
    super(binaryStorageConfig);
  }
}
