/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.internal;

import java.io.InputStream;

import org.apache.commons.io.input.NullInputStream;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;

/**
 * implementation that will not store anything and hence returns always 0 length content which can be used for
 * lightweight testing.
 *
 * @author tmenzel
 */
public class NilBinaryPersistenceImpl extends BinaryPersistence {

  /** NULL_INPUT_STREAM. */
  private static final NullInputStream NULL_INPUT_STREAM = new NullInputStream(0);

  /** NULL_BYTES. */
  private static final byte[] NULL_BYTES = new byte[0];

  public NilBinaryPersistenceImpl(final BinaryStorageConfiguration binaryStorageConfig)
    throws BinaryStorageException {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#cleanup()
   */
  @Override
  public void cleanup() throws BinaryStorageException {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#deleteBinary(java.lang.String)
   */
  @Override
  public void deleteBinary(String key) throws BinaryStorageException {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#fetchSize(java.lang.String)
   */
  @Override
  public long fetchSize(String key) throws BinaryStorageException {
    return 0;

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#loadBinaryAsByteArray(java.lang.String)
   */
  @Override
  public byte[] loadBinaryAsByteArray(String key) throws BinaryStorageException {
    return NULL_BYTES;

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#loadBinaryAsInputStream(java.lang.String)
   */
  @Override
  public InputStream loadBinaryAsInputStream(String key) throws BinaryStorageException {
    return NULL_INPUT_STREAM;

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#storeBinary(java.lang.String,
   *      java.io.InputStream)
   */
  @Override
  public void storeBinary(String key, InputStream stream) throws BinaryStorageException {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.persistence.BinaryPersistence#storeBinary(java.lang.String, byte[])
   */
  @Override
  public void storeBinary(String key, byte[] content) throws BinaryStorageException {
  }

}
