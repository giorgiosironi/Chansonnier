/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.index.IndexException;

/**
 * A Pool of IndexWriter objects.
 */
public final class IndexWriterPool {

  /**
   * Internal map of index names to IndexWriter objects.
   */
  private static final HashMap<String, IndexWriter> POOL = new HashMap<String, IndexWriter>();

  /**
   * local logger.
   */
  private static final Log LOG = LogFactory.getLog(IndexWriterPool.class);

  /**
   * Private default constructor to prevent instance creation.
   */
  private IndexWriterPool() {
  }

  /**
   * Gets a IndexWriter object from the pool of IndexWriters or creates a new one if for the given index none exists.
   * 
   * @param indexName
   *          name of the index
   * @param storage
   *          the path to the index
   * @param analyzer
   *          Analyzer used for IndexWriter creation
   * @return a IndexWriter
   * @throws IndexException
   *           if any error occurs
   */
  public static synchronized IndexWriter getIndexWriter(final String indexName, final String storage,
    final Analyzer analyzer) throws IndexException {
    IndexWriter indexWriter = POOL.get(indexName);
    if (indexWriter == null) {
      try {
        if (IndexReader.isLocked(storage)) {
          if (LOG.isWarnEnabled()) {
            LOG.warn("Lucene index " + indexName
              + " was locked. Perhaps SMILA process was killed or another error happened.");
          }
          IndexReader.unlock(FSDirectory.getDirectory(storage));
          if (LOG.isWarnEnabled()) {
            LOG.warn("Removed lock on Lucene index " + indexName);
          }
        }

        final DIndex indexConfig = DataDictionaryController.getIndex(indexName);

        indexWriter = new IndexWriter(storage, analyzer, false);

        // set flush buffer values if configured, else use lucene defaults
        if (indexConfig.getRamBufferSize() != null) {
          indexWriter.setRAMBufferSizeMB(indexConfig.getRamBufferSize());
        }
        if (indexConfig.getMaxBufferedDocs() != null) {
          indexWriter.setMaxBufferedDocs(indexConfig.getMaxBufferedDocs());
        }
        if (indexConfig.getMaxBufferedDeleteTerms() != null) {
          indexWriter.setMaxBufferedDeleteTerms(indexConfig.getMaxBufferedDeleteTerms());
        }

        POOL.put(indexName, indexWriter);

        if (LOG.isInfoEnabled()) {
          LOG.info("Created new IndexWriter for Lucene index " + indexName);
        }
      } catch (final Exception e) {
        throw new IndexException(e);
      }
    }
    return indexWriter;
  }

  /**
   * Flushes the IndexWriter for the given indexName. Quietly ignores any not existing index names.
   * 
   * @param indexName
   *          the name of the index
   * @throws IndexException
   *           if any error occurs
   */
  public static void flushIndexWriter(final String indexName) throws IndexException {
    final IndexWriter indexWriter = POOL.get(indexName);
    if (indexWriter != null) {
      try {
        indexWriter.flush();
        if (LOG.isInfoEnabled()) {
          LOG.info("Flushed Lucene index " + indexName);
        }
      } catch (final Exception e) {
        throw new IndexException(e);
      }
    }
  }

  /**
   * Closes the IndexWriter for the given index name.
   * 
   * @param indexName
   *          name of the index
   * @throws IndexException
   *           if any error occurs
   */
  public static void closeIndexWriter(final String indexName) throws IndexException {
    final IndexWriter indexWriter = POOL.get(indexName);
    if (indexWriter != null) {
      try {
        indexWriter.close();
        if (LOG.isInfoEnabled()) {
          LOG.info("Closed IndexWriter for Lucene index " + indexName);
        }
      } catch (final Exception e) {
        throw new IndexException(e);
      }
      POOL.remove(indexName);
    }
  }

  /**
   * Closes all created IndexWriter objects.
   * 
   * @throws IndexException
   *           if any error occurs
   */
  public static synchronized void closeAll() throws IndexException {
    Exception exception = null;
    final Iterator<IndexWriter> it = POOL.values().iterator();
    while (it.hasNext()) {
      final IndexWriter indexWriter = it.next();
      if (indexWriter != null) {
        try {
          indexWriter.close();
          if (LOG.isInfoEnabled()) {
            LOG.info("Closed IndexWriter for Lucene index " + indexWriter.getDirectory());
          }
        } catch (final Exception e) {
          // save exception to be thrown after close was called on all indexWriters
          if (exception == null) {
            exception = e;
          }
        }
      } // if
    } // while

    POOL.clear();

    if (exception != null) {
      throw new IndexException(exception);
    }
  }
}
