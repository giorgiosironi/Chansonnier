/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * Factory class providing with appropriate implementation of binary storage : file system or binary object persistence.
 * 
 * @author mcimpean
 */
public class BinaryPersistenceFactory {

  /**
   * Creates concrete BinaryPersistence implementation.
   * 
   * @param configuration
   * @return BinaryPersistence
   * @throws BinaryStorageException -
   *           in case BinaryPersistence implementation is not configured
   */
  @SuppressWarnings("unchecked")
  public static BinaryPersistence newImplInstance(BinaryStorageConfiguration configuration)
    throws BinaryStorageException {
    try {
      final Class clazz = Class.forName(configuration.getImplementationClass());
      final Constructor implConstructor = clazz.getConstructor(new Class[] { BinaryStorageConfiguration.class });

      return (BinaryPersistence) implConstructor.newInstance(new Object[] { configuration });
    } catch (final Exception exception) {
      throw new IllegalArgumentException("Invalid BinaryPersistence configuration", exception);
    } finally {
      final Log log = LogFactory.getLog(BinaryPersistenceFactory.class);
      if (log.isInfoEnabled()) {
        log
          .info("Created new BinaryPersistence implementation instance :" + configuration.getImplementationClass());
      }
    }
  }

  /**
   * Prevent instantiation outside of this class.
   */
  private BinaryPersistenceFactory() {
  }
}
