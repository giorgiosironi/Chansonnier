/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator Igor Novakovic (Empolis GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * Hierarchical structure implementation.
 *
 * @author mcimpean
 */
/**
 * @author nova03
 *
 */
public class IOHierarchicalManager extends IOBinaryPersistence {

  /** FORBIDDEN_CHARS. */
  private static final String FORBIDDEN_CHARS = ";/\\:";

  /* Default path depth is two. */
  private int _pathDepth = 2;

  /* Default path length is two. */
  private final byte _length = 2;

  /* The separator used for creating paths. */
  private static final String _separator = File.separator;

  /**
   * Basic constructor.
   *
   * @throws BinaryStorageException
   */
  public IOHierarchicalManager(final BinaryStorageConfiguration binaryStorageConfig) throws BinaryStorageException {
    super(binaryStorageConfig);
    final Integer pathDepth = binaryStorageConfig.getPathDepth();
    if (pathDepth != null) {
      final int pathDepthInt = pathDepth.intValue();
      if (pathDepthInt > 0) {
        _pathDepth = pathDepthInt;
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#storeBinary(java.lang.String,
   *      byte[])
   */
  @Override
  public void storeBinary(final String key, final byte[] content) throws BinaryStorageException {
    super.storeBinary(calculateDirectoryPath(key), content);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#storeBinary(java.lang.String,
   *      java.io.InputStream)
   */
  @Override
  public void storeBinary(final String key, final InputStream stream) throws BinaryStorageException {
    super.storeBinary(calculateDirectoryPath(key), stream);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#loadBinaryAsByteArray(java.lang.String)
   */
  @Override
  public byte[] loadBinaryAsByteArray(final String key) throws BinaryStorageException {
    return super.loadBinaryAsByteArray(calculateDirectoryPath(key));
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#loadBinaryAsInputStream(java.lang.String)
   */
  @Override
  public InputStream loadBinaryAsInputStream(final String key) throws BinaryStorageException {
    return super.loadBinaryAsInputStream(calculateDirectoryPath(key));
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#deleteBinary(java.lang.String)
   */
  @Override
  public void deleteBinary(final String key) throws BinaryStorageException {
    String path = calculateDirectoryPath(key);
    super.deleteBinary(path);
    File parentFolder = BssIOUtils.getFile(path).getParentFile();
    BssIOUtils.deleteEmptyParentFolders(parentFolder);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#fetchSize(java.lang.String)
   */
  @Override
  public long fetchSize(final String key) throws BinaryStorageException {
    return super.fetchSize(calculateDirectoryPath(key));
  }

  /**
   * Deterministically calculation of record internal path.
   *
   * @param id
   * @return String path
   * @throws BinaryStorageException
   */
  public String calculateDirectoryPath(final String id) throws BinaryStorageException {

    if (StringUtils.containsAny(id, FORBIDDEN_CHARS)) {
      throw new BinaryStorageException("id contains one of the forbidden chars " + FORBIDDEN_CHARS + " : " + id);
    }
    try {
      final StringBuffer internalPath = new StringBuffer();
      for (int i = 0; i < _pathDepth && i * _length + _length < id.length(); i++) {
        int offset = i * _length;
        internalPath.append(id.substring(offset, offset + _length));
        internalPath.append(_separator);
      }
      internalPath.append(id);
      return internalPath.toString();
    } catch (RuntimeException e) {
      throw new BinaryStorageException("unable to create path from id", e);
    }
  }
}
