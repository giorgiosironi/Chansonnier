/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;

/**
 * Binary Storage Service utility i/o class.
 *
 * @author mcimpean
 */
public class BssIOUtils {
  /** Binary Storage Service configured root location */
  private static File _root = null;

  /** Constant file size for 64 MB */
  private final static int FILE_SIZE_64MB = 64 * 1024 * 1024;

  /** File size transfer : 64MB - 32KB */
  private final static int FILE_SIZE_TRANSFER = FILE_SIZE_64MB - (32 * 1024);

  /**
   * Binary Storage persistence location initialization.
   *
   * @param binaryStorageConfig
   */
  public static void init(final BinaryStorageConfiguration binaryStorageConfig) {
    _root = getCanonicalFile(binaryStorageConfig.getPath());
  }

  /**
   * Gets canonical file.
   *
   * @param filePath
   *          String - file path
   * @return file File
   */
  private static File getCanonicalFile(final String filePath) {
    final File file = new File(filePath);
    try {
      return file.getCanonicalFile();
    } catch (final IOException e) {
      return file.getAbsoluteFile();
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

    final File record = mkdirForFileRecord(path);
    if (stream instanceof FileInputStream) {
      try {
        writeCopyByChannels(record.getCanonicalPath(), stream);
      } catch (final IOException ioe) {
        throw new BinaryStorageException(ioe, "Could not write from input stream to record :" + path);
      }
    } else {
      FileOutputStream output = null;
      try {
        output = FileUtils.openOutputStream(record);
        IOUtils.copy(stream, output);
      } catch (final IOException ioe) {
        throw new BinaryStorageException(ioe, "Could not write from input stream to record :" + path);
      } finally {
        IOUtils.closeQuietly(output);
      }
    }
  }

  /**
   * Copies data from input stream into persistence location, by using channels.
   *
   * @param path
   * @param stream
   * @throws BinaryStorageException
   */
  private static void writeCopyByChannels(final String path, final InputStream stream)
    throws BinaryStorageException {
    FileChannel inChannel = null;
    FileChannel outChannel = null;
    try {
      inChannel = ((FileInputStream) stream).getChannel();
      outChannel = new FileOutputStream(path).getChannel();
      if (inChannel.size() < FILE_SIZE_64MB) {
        outChannel.transferFrom(inChannel, 0, inChannel.size());
      } else {
        // the transferTo() does not transfer files > than 2^31-1 bytes
        final long size = inChannel.size();
        long position = 0;
        while (position < size) {
          position += inChannel.transferTo(position, FILE_SIZE_TRANSFER, outChannel);
        }
      }
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "Could not write binary record to :" + path);
    } finally {
      // Close the channels
      closeChannel(inChannel);
      closeChannel(outChannel);
    }
  }

  /**
   * Utility method to close a file channel.
   *
   * @param channel
   * @throws BinaryStorageException
   */
  private static void closeChannel(final FileChannel channel) throws BinaryStorageException {
    if (channel != null) {
      try {
        channel.close();
      } catch (final IOException exception) {
        throw new BinaryStorageException(exception, "Could not close stream channel.");
      }
    }
  }

  /**
   * Saves array of bytes into file.
   *
   * @param path
   * @param data
   * @throws BinaryStorageException
   */
  public static void writeByteArrayToFile(final String path, final byte[] data) throws BinaryStorageException {

    final File record = mkdirForFileRecord(path);
    try {
      FileUtils.writeByteArrayToFile(record, data);
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "Could not write binary record to :" + path);
    }
  }

  /**
   * Make necessary directories for record storing.
   *
   * @param path
   * @return File record file
   * @throws BinaryStorageException
   */
  private synchronized static File mkdirForFileRecord(final String path) throws BinaryStorageException {
    final File record = getFile(path);
    try {
      if (!isRecordParentRoot(record)) {
        // Hierarchical structure
        FileUtils.forceMkdir(record.getParentFile());
      }
      return record;
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "Could not write binary record to :" + path);
    }
  }

  /**
   * Check if current record's parent is identically with the binary storage persistence configured-root-location.
   *
   * @param record
   * @return boolean true in case parent location is identically with root; false otherwise.
   */
  private static boolean isRecordParentRoot(final File record) {
    return record.getParentFile().equals(_root);
  }

  /**
   * Reads a file, filling and return a byte array.
   *
   * @param path
   * @return byte[]
   * @throws BinaryStorageException
   */
  public static byte[] readFileToByteArray(final String path) throws BinaryStorageException {
    try {
      return FileUtils.readFileToByteArray(getFile(path));
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "Could not read binary record from :" + path);
    }
  }

  /**
   * Reads a file, filling a byte array.
   *
   * @throws BinaryStorageException
   */
  public static InputStream readFileToInputStream(final String path) throws BinaryStorageException {
    final File source = getFile(path);
    try {
      return new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
    } catch (final IOException ioe) {
      throw new BinaryStorageException(ioe, "Could not read binary record from :" + path);
    }
  }

  /**
   * Delete file.
   *
   * @param key
   */
  public static void deleteFile(final String key) {
    FileUtils.deleteQuietly(getFile(key));
    /*
     * note | TM | this needs to be impled in each impl.as this might be specific to each implementation | TM @ Jun 25,
     * 2009
     */
  }

  /**
   * @param key
   * @return
   */
  public static File getFile(final String key) {
    return new File(_root, key);
  }

  /**
   * get file size.
   *
   * @param key
   * @throws BinaryStorageException
   *           file does not exist
   */
  public static long fetchSize(final String key) throws BinaryStorageException {
    final File file = getFile(key);
    if (file.exists()) {
      return file.length();
    }
    throw new BinaryStorageException("file does not exist: " + key);
  }

  /**
   * Delete empty parent folders.
   *
   * @param leafFolder
   *          the leaf folder
   *
   * @return the file
   */
  public static void deleteEmptyParentFolders(File leafFolder) {
    while (leafFolder != null && leafFolder.list().length < 1 && !leafFolder.equals(_root)) {
      leafFolder.delete();
      leafFolder = leafFolder.getParentFile();
    }
  }
}
