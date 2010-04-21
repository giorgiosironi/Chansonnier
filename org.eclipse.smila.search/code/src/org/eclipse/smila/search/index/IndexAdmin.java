/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;

/**
 * The Class IndexAdmin.
 * 
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class IndexAdmin {

  /**
   * Delemiter for separating key values.
   */
  public static final String DELIMITER = "##";

  /**
   * Default sleep time.
   */
  protected static final long SLEEP_TIME = 2000;

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Create index.
   * 
   * @param dIndexStructure
   *          Index structure.
   * 
   * @throws IndexException
   *           Unable to create index.
   */
  public void create(final DIndexStructure dIndexStructure) throws IndexException {
    createIndex(dIndexStructure);
  }

  /**
   * Delete.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  public void delete(final String indexName) throws IndexException {
    checkIndexExist(indexName);
    checkIndexNotBusy(indexName);
    IndexManager.noUse(indexName);
    IndexManager.deleteIndexUsage(indexName);
    // remove dd entry
    try {
      DataDictionaryController.deleteIndex(indexName);
    } catch (final DataDictionaryException e) {
      throw new IndexException(e);
    }
    // remove physical index
    deleteIndex(indexName);
  }

  /**
   * Rename.
   * 
   * @param indexName
   *          the index name
   * @param newIndexName
   *          the new index name
   * 
   * @throws IndexException
   *           the index exception
   */
  public void rename(final String indexName, final String newIndexName) throws IndexException {
    checkIndexExist(indexName);
    checkIndexNotBusy(indexName);
    checkIndexNotExist(newIndexName);
    try {
      IndexManager.noUse(indexName);
      DataDictionaryController.renameIndex(indexName, newIndexName);
      renameIndex(indexName, newIndexName);
      IndexManager.deleteIndexUsage(indexName);
    } catch (final DataDictionaryException e) {
      throw new IndexException(e);
    } finally {
      IndexManager.multiUse(newIndexName);
    }
  }

  /**
   * Save.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  public void save(final String indexName) throws IndexException {
    checkIndexExist(indexName);
    checkIndexNotBusy(indexName);
    try {
      IndexManager.noUse(indexName);
      saveIndex(indexName);
    } finally {
      IndexManager.multiUse(indexName);
    }
  }

  /**
   * Save index.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  public abstract void saveIndex(final String indexName) throws IndexException;

  /**
   * Create index.
   * 
   * @param dIndexStructure
   *          Index structure.
   * 
   * @throws IndexException
   *           Unable to create index.
   */
  protected abstract void createIndex(DIndexStructure dIndexStructure) throws IndexException;

  /**
   * Delete index.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  protected abstract void deleteIndex(String indexName) throws IndexException;

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
  protected abstract void renameIndex(final String indexName, String newIndexName) throws IndexException;

  /**
   * Checks existence of a given index. Note that if this method returns <code>false</code>, that does not
   * necessarily mean that an index with that name can be created. AnyFinder does not allow indexes that differ only in
   * capitalization of names. If an index with name <i>Test</i> exists, <code>exists("test")</code> will return
   * <code>false</code>, but <code>create("test")</code> will fail nevertheless. If you wish to check whether it is
   * legal to create an index, call <code>existsIgnoreCase()</code> instead.
   * 
   * @param indexName -
   *          index name to be checked
   * 
   * @return true if an index with that name exists, false otherwise
   * 
   * @throws IndexException
   *           If any other error occurs.
   */
  public boolean exists(final String indexName) throws IndexException {
    try {
      if (!DataDictionaryController.hasIndex(indexName)) {
        return false;
      }
    } catch (final DataDictionaryException e) {
      throw new IndexException(e);
    }
    return indexExists(indexName);
    /*
     * try { if (exists) { if (!DataDictionaryController.hasIndex(indexName)) { throw new IndexException("physical index
     * exists, but there is " + "no data dictionary entry [" + indexName + "]"); } } else { if
     * (DataDictionaryController.hasIndex(indexName)) { throw new IndexException("physical index does not exists, but
     * there is " + "an data dictionary entry [" + indexName + "]"); } } } catch (final DataDictionaryException e) {
     * throw new IndexException(e.getMessage()); }
     */
  }

  /**
   * Checks whether an index name exists in the data dictionary. The check is performed in a case-insensitive manner. If
   * no index can be found, this method returns <code>null</code>. If an index corresponding to
   * <code>indexName</code> is found, this method calls <code>exists()</code> to check additional constraints that
   * may be imposed by the retrieval plugin.
   * 
   * @param indexName -
   *          The index name to search for
   * 
   * @return The name of the index as found in the data dictionary or <code>null</code> if such an index does not
   *         exist
   * 
   * @throws IndexException -
   */
  public String existsIgnoreCase(final String indexName) throws IndexException {
    try {
      final String name = DataDictionaryController.getExistingIndexName(indexName);
      if (name == null) {
        return null;
      }

      if (exists(name)) {
        return name;
      }
      return null;

    } catch (final DataDictionaryException e) {
      throw new IndexException(e.getMessage());
    }
  }

  /**
   * Index exists.
   * 
   * @param indexName
   *          the index name
   * 
   * @return true, if successful
   * 
   * @throws IndexException
   *           the index exception
   */
  protected abstract boolean indexExists(String indexName) throws IndexException;

  /**
   * This method is responsible for reorganizeing the index structure of a given retrieval system. It synchronize the
   * access to the retrieval system during the reorganization process. The reorganization process itself is done by the
   * delegate method reorganizeIndex(indexName). As part of the reorganization process the method checks, wether the
   * index, which should be reorganized, exists.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  public void reorganize(final String indexName) throws IndexException {
    checkIndexExist(indexName);
    checkIndexNotBusy(indexName);
    // reorganize pool
    try {
      IndexManager.noUse(indexName);
      // ????
      try {
        Thread.sleep(SLEEP_TIME);
      } catch (final InterruptedException e) {
        ;// nothing
      }
      reorganizeIndex(indexName);
    } catch (final Throwable e) {
      throw new IndexException("unable to reorganize index [" + indexName + "]");
    } finally {
      IndexManager.multiUse(indexName);
    }
    // clearCache(indexName);
    if (_log.isInfoEnabled()) {
      _log.info("index reorganized [" + indexName + "]");
    }
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
  protected abstract void reorganizeIndex(String indexName) throws IndexException;

  /**
   * Check index exist.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  private void checkIndexExist(final String indexName) throws IndexException {
    if (!exists(indexName)) {
      throw new IndexException("index does not exist [" + indexName + "]");
    }
  }

  /**
   * Check index not exist.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  private void checkIndexNotExist(final String indexName) throws IndexException {
    if (exists(indexName)) {
      throw new IndexException("index [" + indexName + "] already exists!");
    }
  }

  /**
   * Check index not busy.
   * 
   * @param indexName
   *          the index name
   * 
   * @throws IndexException
   *           the index exception
   */
  private void checkIndexNotBusy(final String indexName) throws IndexException {
    if (IndexManager.isIndexBusy(indexName)) {
      throw new IndexException(String.format("Index [%s] is busy!", indexName));
    }
  }

}
