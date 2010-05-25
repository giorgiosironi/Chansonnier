/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.smila.lucene.LuceneIndexService;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IndexAdmin extends org.eclipse.smila.search.index.IndexAdmin {

  /**
   * Logging.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexAdmin
   *      #createIndex(org.eclipse.smila.search.utils.indexstructure.DIndexStructure)
   */
  @Override
  protected void createIndex(final DIndexStructure dIndexStructure) throws IndexException {

    // get Analyzer
    final Analyzer analyzer = AnalyzerFactory.getAnalyzer(dIndexStructure);

    // create lucene index
    try {
      final File dataFolder =
        WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, dIndexStructure.getName());
      final IndexWriter iw = new IndexWriter(dataFolder.getAbsolutePath(), analyzer, true);
      iw.close();
    } catch (final Throwable e) {
      final String msg = "unable to create index [" + dIndexStructure.getName() + "]";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new IndexException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexAdmin#indexExists(java.lang.String)
   */
  @Override
  protected boolean indexExists(final String indexName) throws IndexException {
    try {
      return WorkspaceHelper.existsWorkingDir(LuceneIndexService.BUNDLE_NAME, indexName);
    } catch (final IOException e) {
      if (_log.isErrorEnabled()) {
        _log.error(e.getMessage(), e);
      }
      throw new IndexException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexAdmin#deleteIndex(java.lang.String)
   */
  @Override
  protected void deleteIndex(final String indexName) throws IndexException {
    try {
      final File dataFolder = WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, indexName);
      FileUtils.deleteDirectory(dataFolder);
    } catch (final IOException e) {
      throw new IndexException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexAdmin#renameIndex(java.lang.String, java.lang.String)
   */
  @Override
  protected void renameIndex(final String indexName, final String newIndexName) throws IndexException {
    try {
      final File dataFolder = WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, indexName);
      final File newDataFolder = new File(dataFolder.getParentFile(), newIndexName);
      FileUtils.moveDirectory(dataFolder, newDataFolder);
    } catch (final IOException e) {
      throw new IndexException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see com.brox.anyfinder.index.IndexAdmin#reorganizeIndex(java.lang.String)
   */
  @Override
  protected void reorganizeIndex(final String indexName) throws IndexException {
    Analyzer analyzer = null;
    try {
      // get Analyzer
      analyzer = AnalyzerFactory.getAnalyzer(DataDictionaryController.getIndex(indexName));
    } catch (final DataDictionaryException e) {
      throw new IndexException("unable to reorganize index [" + indexName + "]", e);
    }
    // reorganize lucene index
    IndexWriter iw = null;
    try {
      final File indexFolder = WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, indexName);
      iw = new IndexWriter(indexFolder, analyzer, false);
      iw.optimize();
    } catch (final Throwable e) {
      throw new IndexException("unable to reorganize index [" + indexName + "]", e);
    } finally {
      if (iw != null) {
        try {
          iw.close();
        } catch (final Throwable e) {
          ;//
        }
      }
    }
  }

  /**
   * throw new RuntimeException("saveIndex is not implemented!").
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.index.IndexAdmin#saveIndex(java.lang.String)
   */
  @Override
  public void saveIndex(final String indexName) throws IndexException {
    // TODO: implement it.
    throw new RuntimeException("saveIndex is not implemented!");
  }
}
