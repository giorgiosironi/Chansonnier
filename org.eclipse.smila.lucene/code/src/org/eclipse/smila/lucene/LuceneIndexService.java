/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.lucene;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DAnyFinderDataDictionary;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.index.IndexAdmin;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.index.IndexManager;
import org.eclipse.smila.search.lucene.index.access.IndexWriterPool;
import org.eclipse.smila.search.plugin.IIndexAccess;
import org.eclipse.smila.search.plugin.Plugin;
import org.eclipse.smila.search.plugin.PluginFactory;
import org.eclipse.smila.utils.log.RecordLifecycleLogHelper;
import org.osgi.service.component.ComponentContext;

/**
 * Lucene Index Service.
 */
public class LuceneIndexService extends LuceneServie implements ProcessingService {

  /**
   * Configuration property for the index directory - if true, it will be generated and PROPERTY_INDEX_DIR will be
   * ignored.
   */
  public static final String PROPERTY_TEMPORARY_INDEX_DIR = "temporaryIndexDir";

  /**
   * Configuration property if to force to unlock a locked index on service activation. Unlocking may corrupt an
   * existing index. This option is useful for tests. If not set the default is false.
   */
  public static final String PROPERTY_FORCE_UNLOCK_INDEX = "forceUnlockIndex";

  /**
   * name of annotation configuring if doublets are allowed in index.
   */
  public static final String ALLOW_DOUBLETS = "allowDoublets";

  /**
   * name of annotation configuring the mode of execution.
   */
  public static final String EXECUTION_MODE = "executionMode";

  /**
   * Types of errors this pipelet can produce.
   */
  public enum ExecutionMode {

    /**
     * Add the record to the index.
     */
    ADD,

    /**
     * Delete the id from the index.
     */
    DELETE
  };

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(LuceneIndexService.class);

  /**
   * The index admin.
   */
  private IndexAdmin _indexAdmin;

  /**
   * The _indices.
   */
  private Set<String> _indices = Collections.synchronizedSet(new HashSet<String>());

  /**
   * process methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * DS activate method.
   *
   * @param context
   *          ComponentContext
   *
   * @throws Exception
   *           if any error occurs
   */
  protected void activate(final ComponentContext context) throws Exception {
    synchronized (_indices) {
      try {
        loadMappings();

        final Plugin plugin = PluginFactory.getPlugin();
        final IIndexAccess indexAccess = plugin.getIndexAccess();
        _indexAdmin = indexAccess.getIndexAdmin();
        final DAnyFinderDataDictionary dic = DataDictionaryController.getDataDictionary();
        final Enumeration<DIndex> indices = dic.getIndices();
        while (indices.hasMoreElements()) {
          final DIndex dIndex = indices.nextElement();
          if (!_indexAdmin.exists(dIndex.getName())) {
            if (_log.isInfoEnabled()) {
              _log.info("Physical Index [" + dIndex.getName() + "] doesn´t exist. Adapt DataDictionary.");
            }
            DataDictionaryController.deleteIndex(dIndex.getName());
          } else {
            _indices.add(dIndex.getName());
          }
        }
      } catch (final Exception e) {
        if (_log.isErrorEnabled()) {
          _log.error("error initializing LuceneIndexService", e);
        }
        throw e;
      }
    }
  }

  /**
   * DS deactivate method.
   *
   * @param context
   *          the ComponentContext
   *
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    _lock.writeLock().lock();
    try {
      _indices = null;
      _indexAdmin = null;
      unloadMappings();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error deactivating LuceneIndexService", e);
      }
      throw e;
    } finally {
      _lock.writeLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.ProcessingService#process(Blackboard, Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    _lock.readLock().lock();
    try {
      for (int i = 0; i < recordIds.length; i++) {
        try {
          final Annotation pipeletAnnotation = blackboard.getAnnotation(recordIds[i], null, getClass().getName());
          if (pipeletAnnotation != null) {
            final String executionModeValue = pipeletAnnotation.getNamedValue(EXECUTION_MODE);
            final String indexName = pipeletAnnotation.getNamedValue(INDEX_NAME);
            final boolean allowDoublets = Boolean.valueOf(pipeletAnnotation.getNamedValue(ALLOW_DOUBLETS));

            final ExecutionMode executionMode = ExecutionMode.valueOf(executionModeValue);
            switch (executionMode) {
              case ADD:
                addRecord(blackboard, recordIds[i], indexName, allowDoublets);
                if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {
                  RecordLifecycleLogHelper.logRecordState("Record added to lucene index", recordIds[i].getIdHash());
                }
                break;
              case DELETE:
                deleteRecord(recordIds[i], indexName);
                if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {
                  RecordLifecycleLogHelper.logRecordState("Record deleted from lucene index", recordIds[i]
                    .getIdHash());
                }
                break;
              default:
                break;
            }
          }
        } catch (final Exception ex) {
          if (_log.isErrorEnabled()) {
            _log.error("error processing record " + recordIds[i], ex);
          }
        }
      } // for
      return recordIds;
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * Add a record to the Lucene index.
   *
   * @param blackboard
   *          reference to the BlackboardService
   * @param id
   *          Id of the record
   * @param indexName
   *          name of the index <<<<<<< .mine
   *
   *          =======
   * @param allowDoublets
   *          boolean flag if doublets of documents are allowed in the index
   *
   *          >>>>>>> .r475
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws IndexException
   *           if any error occurs
   * @throws IOException
   *           if any error occurs
   * @throws ParserConfigurationException
   *           if any error occurs
   */
  private void addRecord(final Blackboard blackboard, final Id id, final String indexName,
    final boolean allowDoublets) throws BlackboardAccessException, IndexException, IOException,
    ParserConfigurationException {
    if (!isIndexExists(indexName)) {
      synchronized (_indices) {
        if (!isIndexExists(indexName)) {
          createIndex(indexName);
        }
      }
    }
    IndexConnection indexConnection = null;
    try {
      indexConnection = IndexManager.getInstance(indexName);
      if (indexConnection != null) {
        if (!allowDoublets && indexConnection.docExists(id)) {
          indexConnection.deleteDocument(id);
        }
        final HashMap<String, HashMap<String, Integer>> indexMap = getMappings().get(indexName);
        if (indexMap != null) {
          indexConnection.learnDocument(blackboard, id, indexMap.get(MappingsLoader.ATTRIBUTES), indexMap
            .get(MappingsLoader.ATTACHMENTS));
          if (_log.isInfoEnabled()) {
            _log.info("adding record " + id + " to Lucene index");
          }
        } else {
          throw new IndexException("Could not find a mapping in file " + CONFIG_FILE_MAPPINGS + " for index "
            + indexName);
        }
      } else {
        throw new IndexException("Could not open connection to index " + indexName);
      }
    } finally {
      if (indexConnection != null) {
        IndexManager.releaseInstance(indexConnection, false);
      }
    }
  }

  /**
   * Delete a record from the Lucene index.
   *
   * @param id
   *          Id of the record
   * @param indexName
   *          name of the index
   *
   * @throws IndexException
   *           if any error occurs
   */
  private void deleteRecord(final Id id, final String indexName) throws IndexException {
    IndexConnection indexConnection = null;
    try {
      indexConnection = IndexManager.getInstance(indexName);
      if (indexConnection != null) {
        indexConnection.deleteDocument(id);
        if (_log.isInfoEnabled()) {
          _log.info("deleted record " + id + " from Lucene index");
        }
      } else {
        throw new IndexException("Could not open connection to index " + indexName);
      }
    } finally {
      if (indexConnection != null) {
        IndexManager.releaseInstance(indexConnection, false);
      }
    }
  }

  /**
   * Checks if is index exists.
   *
   * @param indexName
   *          the index name
   *
   * @return true, if is index exists
   *
   * @throws IndexException
   *           the index exception
   */
  public boolean isIndexExists(final String indexName) throws IndexException {
    // return _indexAdmin.exists(indexName);
    return _indices.contains(indexName);
  }

  /**
   * Reorganize index.
   *
   * @param indexName
   *          the index name
   *
   * @throws IndexException
   *           the index exception
   */
  public void reorganizeIndex(final String indexName) throws IndexException {
    _indexAdmin.reorganize(indexName);
  }

  /**
   * Creates the index.
   *
   * @param indexName
   *          the index name
   *
   * @throws IndexException
   *           the index exception
   */
  public void createIndex(final String indexName) throws IndexException {
    synchronized (_indices) {
      if (isIndexExists(indexName)) {
        throw new IndexException(String.format("Index [%s] already exists", indexName));
      }
      try {
        DataDictionaryController.addIndex(indexName);
        final DIndex dIndex = DataDictionaryController.getIndex(indexName);
        _indexAdmin.create(dIndex.getIndexStructure());
        _indices.add(dIndex.getName());
      } catch (final DataDictionaryException e) {
        throw new IndexException(e);
      }
    }
  }

  /**
   * Delete index.
   *
   * @param indexName
   *          the index name
   *
   * @throws IndexException
   *           the index exception
   */
  public void deleteIndex(final String indexName) throws IndexException {
    synchronized (_indices) {
      _indexAdmin.delete(indexName);
      _indices.remove(indexName);
    }
  }

  /**
   * Rename index.
   *
   * @param indexName
   *          the index name
   * @param newIndexName
   *          the new index name
   *
   * @throws IndexException
   *           the index exception
   */
  public void renameIndex(final String indexName, final String newIndexName) throws IndexException {
    synchronized (_indices) {
      _indexAdmin.rename(indexName, newIndexName);
      _indices.remove(indexName);
      _indices.add(newIndexName);
    }
  }

  /**
   * Returns the names of all available indexes.
   *
   * @return an iterator over index names
   */
  public Iterator<String> getIndexNames() {
    return _indices.iterator();
  }

  /**
   * Flushes the index with the given name.
   *
   * @param indexName
   *          name of the index to flush
   * @throws IndexException
   *           if any error occurs
   */
  public void flushIndex(final String indexName) throws IndexException {
    IndexWriterPool.flushIndexWriter(indexName);
  }

  /**
   * Removes the write.lock of the Lucene index if any exists.
   *
   * @param indexName
   *          the name of the index to remove the lock
   * @throws IndexException
   *           if any error occurs
   */
  public void removeWriteLock(final String indexName) throws IndexException {
    final IndexConnection indexConnection = IndexManager.getInstance(indexName);
    if (indexConnection != null) {
      indexConnection.unlock();
    }
  }
}
