/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.efs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * EFS utility class.
 *
 * @author mcimpean
 */
public class EFSUtils {
  /** Binary Storage Service configured root location */
  private static IPath _root = null;

  private static IFileStore _store = null;

  /**
   * Binary Storage persistence location initialization.
   *
   * @param binaryStorageConfig
   */
  public static void init(final BinaryStorageConfiguration binaryStorageConfig) {
    _root = getCanonicalPath(binaryStorageConfig.getPath());
    _store = EFS.getLocalFileSystem().getStore(_root);
  }

  /**
   * Gets canonical file.
   *
   * @param filePath
   *          String - file path
   * @return file File
   */
  private static Path getCanonicalPath(final String filePath) {
    final File file = new File(filePath);
    try {
      return new Path(file.getCanonicalPath());
    } catch (final IOException e) {
      return new Path(file.getAbsolutePath());
    }
  }

  /**
   * Writes input stream to file.
   *
   * @param path
   * @param stream
   * @throws BinaryStorageException
   */
  public static void writeInputStreamToFile(final String path, final InputStream stream)
    throws BinaryStorageException {
    final IFileStore record = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
    OutputStream output = null;
    try {
      output = record.openOutputStream(EFS.NONE, null);
      IOUtils.copy(stream, output);
    } catch (final CoreException exception) {
      throw new BinaryStorageException(exception, "Could not write binary record to :" + path);
    } catch (final IOException exception) {
      throw new BinaryStorageException(exception, "IO stream exception. Could not write binary record to :" + path);
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  /**
   * @param key
   * @param content
   * @throws BinaryStorageException
   */
  public static void writeByteArrayToFile(final String path, final byte[] data) throws BinaryStorageException {
    final IFileStore store = mkdirForFileRecord(path);

    BufferedOutputStream output = null;
    try {
      // Buffering should be applied if desired.
      output = new BufferedOutputStream(store.openOutputStream(EFS.NONE, null));
      output.write(data);
      output.flush();
    } catch (final CoreException exception) {
      throw new BinaryStorageException(exception, "Could not write binary record to :" + path);
    } catch (final IOException exception) {
      throw new BinaryStorageException(exception, "IO stream exception. Could not write binary record to :" + path);
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  /**
   * Make necessary directories for record storing.
   *
   * @param path
   * @return IFileStore record file
   * @throws BinaryStorageException
   */
  private static IFileStore mkdirForFileRecord(final String path) throws BinaryStorageException {
    final IFileStore record = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
    try {
      if (!isRecorParentRoot(record)) {
        // Hierarchical structure
        record.getParent().mkdir(EFS.NONE, null);
      }
      return record;
    } catch (final CoreException exception) {
      throw new BinaryStorageException(exception, "Could not write binary record to :" + path);
    }
  }

  /**
   * Check if current record's parent is identically with the binary storage persistence configured-root-location.
   *
   * @param record
   * @return boolean true in case parent location is identically with root; false otherwise.
   */
  private static boolean isRecorParentRoot(final IFileStore record) {
    return record.getParent().equals(_store);
  }

  /**
   * Reads a file, filling and return a byte array.
   *
   * @param path
   * @return byte[]
   * @throws BinaryStorageException
   */
  public static byte[] readFileToByteArray(final String path) throws BinaryStorageException {
    InputStream input = null;
    try {
      final IFileStore file = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
      input = file.openInputStream(EFS.NONE, null);
      return IOUtils.toByteArray(input);
    } catch (final CoreException exception) {
      throw new BinaryStorageException(exception, "Could not read binary record from :" + path);
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "IO stream sxception. Could not read binary record from :" + path);
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Reads a file, filling a byte array. The caller is responsible for closing the returned input stream when is no
   * longer needed.
   *
   * @param path
   * @return InputStream
   * @throws BinaryStorageException
   */
  public static InputStream readFileToInputStream(final String path) throws BinaryStorageException {
    final IFileStore record = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
    try {
      return record.openInputStream(EFS.NONE, null);
    } catch (final CoreException exception) {
      throw new BinaryStorageException(exception, "Could not read binary record from :" + path);
    }
  }

  /**
   * Delete file.
   *
   * @param key
   * @throws BinaryStorageException
   */
  public static void deleteFile(final String path) throws BinaryStorageException {
    final IFileStore file = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
    if (file.fetchInfo().exists()) {
      try {
        file.delete(EFS.NONE, null);
      } catch (final CoreException exception) {
        throw new BinaryStorageException(exception, "Could not delte binary record from :" + path);
      }
    }
    // TODO Prune empty directories too if not storage persistence root
  }

  /**
   * get file size
   *
   * @param path
   * @throws BinaryStorageException
   *           file does not exist
   */
  public static long fetchSize(final String path) throws BinaryStorageException {
    final IFileStore file = EFS.getLocalFileSystem().getStore(_root.append(new Path(path)));
    final IFileInfo info = file.fetchInfo();
    if (info.exists()) {
      return info.getLength();
    }
    throw new BinaryStorageException("file does not exist: " + path);
  }
}
