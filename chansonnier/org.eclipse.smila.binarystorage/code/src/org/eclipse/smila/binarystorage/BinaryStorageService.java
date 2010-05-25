/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage;

import java.io.InputStream;

/**
 * Binary storage service interface.
 *
 * @author mcimpean
 */
public interface BinaryStorageService {

  /**
   * Save the attachment stream in the binary storage location.
   *
   * @param id
   *          String - calculated key (attachment id) from blackboard service based on record Id and file name. The key
   *          will serve to identify (persistence scope) current folder & file inside of binary storage.
   * @param stream
   *          InputStream - attachment stream
   * @throws BinaryStorageException
   *           in case of any exception
   */
  void store(String id, InputStream stream) throws BinaryStorageException;

  /**
   * Save the attachment byte array content in the binary storage location.
   *
   * @param id
   *          String - calculated key (attachment id) from blackboard service based on record Id and file name. The key
   *          will serve to identify (persistence scope) current folder & file inside of binary storage.
   * @param blob
   *          byte[] - file content to be stored in binary storage
   * @throws BinaryStorageException
   *           in case of any exception
   */
  void store(String id, byte[] blob) throws BinaryStorageException;

  /**
   * Fetch an already persisted attachment through ({@link BinaryStorageService.storeRecordAttachment}) from binary
   * storage as byte array.
   *
   * @param id
   *          String - calculated key (attachment id) from blackboard service based on record Id and file name. The key
   *          value passed by the Blackboard service shall be identically with the same one, passed one step previously
   *          (through the createStorageFile service) in order to get exactly the same file content.
   * @return byte[] - file content
   * @throws BinaryStorageException
   *           in case of any exception
   */
  byte[] fetchAsByte(String id) throws BinaryStorageException;

  /**
   * Fetch an already persisted attachment through ({@link BinaryStorageService.storeRecordAttachment}) from binary
   * storage as InputStream.
   *
   * @param id
   *          String - calculated key (attachment id) from blackboard service based on record Id and file name. The key
   *          value passed by the Blackboard service shall be identically with the same one, passed one step previously
   *          (through the createStorageFile service) in order to get exactly the same file content.
   * @return InputStream - file content
   * @throws BinaryStorageException
   *           in case of any exception
   */
  InputStream fetchAsStream(String id) throws BinaryStorageException;

  /**
   * Removes folder&file structure from the binary storage, based on the passed key.
   *
   * @param id
   *          String - calculated key (attachment id) from blackboard service based on record Id and file name.
   * @throws BinaryStorageException
   *           in case of any exception
   */
  void remove(String id) throws BinaryStorageException;

  /**
   * Fetch size of persisted attachment.
   *
   * @param id
   * @return int - attachment size
   * @throws BinaryStorageException
   *           in case of any exception
   */
  long fetchSize(String id) throws BinaryStorageException;
}
