/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence;

import java.io.InputStream;

import org.eclipse.smila.binarystorage.BinaryStorageException;

/**
 * Abstract binary persistence class. This class shall be extended by all concrete binary storage implementation
 * classes.
 *
 * @author mcimpean
 */
public abstract class BinaryPersistence {
  /**
   * Store binary data in binary storage.
   *
   * @param key
   *          String - unique identifier inside of binary storage
   * @param stream
   *          InputStream - binary data input stream
   * @throws BinaryStorageException -
   *           in case of any exception occurs
   */
  public abstract void storeBinary(String key, InputStream stream) throws BinaryStorageException;

  /**
   * Store binary data in binary storage.
   *
   * @param key
   *          String - unique identifier inside of binary storage
   * @param content
   *          byte[] - binary content
   * @throws BinaryStorageException -
   *           in case of any exception occurs
   */
  public abstract void storeBinary(String key, byte[] content) throws BinaryStorageException;

  /**
   * Fetch binary data by key from binary storage.
   *
   * @param key
   *          String - unique identifier inside of binary storage
   * @return byte[] - binary data
   * @throws BinaryStorageException -
   *           in case of any exception occurs
   */
  public abstract byte[] loadBinaryAsByteArray(String key) throws BinaryStorageException;

  /**
   * Fetch binary data by key from binary storage.
   *
   * @param key
   *          String - unique identifier inside of binary storage
   * @return InputStream - binary input stream
   * @throws BinaryStorageException -
   *           in case of any exception occurs
   */
  public abstract InputStream loadBinaryAsInputStream(String key) throws BinaryStorageException;

  /**
   * Delete binary data by key from binary storage.
   *
   * @param key
   *          String - unique identifier inside of binary storage
   * @throws BinaryStorageException -
   *           in case of any exception occurs
   */
  public abstract void deleteBinary(String key) throws BinaryStorageException;

  /**
   * Fetch record size.
   * 
   * @param key
   *          String - unique identifier inside of binary storage
   * @return long - size of record
   * @throws BinaryStorageException
   *           - in case of any exception occurs
   */
  public abstract long fetchSize(String key) throws BinaryStorageException;

  /**
   * Release resources and performs cleanup actions.
   *
   * @throws BinaryStorageException
   *           in case of any exception
   */
  public abstract void cleanup() throws BinaryStorageException;

}
