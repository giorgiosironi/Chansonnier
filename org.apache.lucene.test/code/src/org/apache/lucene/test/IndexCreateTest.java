/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.lucene.test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * The Class IndexCreateTest.
 */
public class IndexCreateTest extends TestCase {

  /**
   * The log.
   */
  private final Log _log = LogFactory.getLog(IndexCreateTest.class);

  /**
   * Test index creation.
   */
  @SuppressWarnings("deprecation")
  public void testIndexCreate() {
    final String tempFolder = System.getProperty("java.io.tmpdir");
    final UUID uuid = UUID.randomUUID();
    final File indexFolder = new File(tempFolder + File.separator + uuid.toString());
    _log.info("Creating lucene index " + indexFolder.getPath());
    try {

      final File lockDir = new File(FSDirectory.LOCK_DIR);
      if (!lockDir.exists()) {
        boolean created = true;
        try {
          created = lockDir.mkdirs();
        } catch (final Exception ex) {
          created = false;
        }
        if (!created) {
          throw new RuntimeException("Unable to create folder \"" + lockDir.getAbsolutePath()
            + "\" for lucene index lock files");
        }
      } else {
        if (!lockDir.canWrite() || !lockDir.canRead()) {
          throw new RuntimeException("There isn't write permission for lucene index lock folder \""
            + lockDir.getAbsolutePath() + "\"");
        }
      }

      if (indexFolder.exists()) {
        unlockIndex(indexFolder, lockDir);
      } else {
        createIndex(indexFolder, lockDir);
      }

    } catch (final Throwable ex) {
      _log.error(ex);
      fail(ex.getMessage());
    } finally {
      if (indexFolder.exists()) {
        _log.info("Removing lucene index " + indexFolder.getPath());
        for (final File file : indexFolder.listFiles()) {
          file.delete();
        }
        indexFolder.delete();
      }
    }
  }

  /**
   * Creates the index.
   * 
   * @param indexFolder
   *          the index folder
   * @param lockDir
   *          the lock dir
   */
  private void createIndex(final File indexFolder, final File lockDir) {
    boolean created = true;
    try {
      created = indexFolder.mkdirs();
      if (created) {
        final IndexWriter indexWriter =
          new IndexWriter(indexFolder, new SnowballAnalyzer("English", StopAnalyzer.ENGLISH_STOP_WORDS), true);
        indexWriter.close();
      }
    } catch (final Exception ex) {
      created = false;
    }
    if (!created) {
      throw new RuntimeException("Cannot create lucene index folder \"" + lockDir.getAbsolutePath() + "\"");
    }
  }

  /**
   * Unlock index.
   * 
   * @param indexFolder
   *          the index folder
   * @param lockDir
   *          the lock dir
   */
  @SuppressWarnings("deprecation")
  private void unlockIndex(final File indexFolder, final File lockDir) {
    try {
      final Directory index = FSDirectory.getDirectory(indexFolder, false);
      if (IndexReader.isLocked(indexFolder.getAbsolutePath())) {
        IndexReader.unlock(index);
      }
    } catch (final IOException ex) {
      throw new RuntimeException("Cannot remove lock from lucene index folder \"" + lockDir.getAbsolutePath()
        + "\"");
    }
  }
}
