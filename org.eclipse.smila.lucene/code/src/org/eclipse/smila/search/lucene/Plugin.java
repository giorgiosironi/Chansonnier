/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.smila.lucene.MappingsLoader;
import org.eclipse.smila.search.datadictionary.DataDictionaryAccess;
import org.eclipse.smila.search.plugin.IIndexAccess;
import org.eclipse.smila.search.plugin.ITemplateAccess;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Plugin extends org.eclipse.smila.search.plugin.Plugin {

  private final IIndexAccess _indexAccess;

  private final org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess _indexStructureAccess;

  private final org.eclipse.smila.search.utils.advsearch.AdvSearchAccess _advSearchAccess;

  private final ITemplateAccess _templateAccess;

  private final DataDictionaryAccess _dataDictionaryAccess;

  private final org.eclipse.smila.search.utils.search.SearchAccess _searchAccess;

  /**
   * 
   */
  public Plugin() {
    super();

    _indexAccess = new IndexAccess();
    _indexStructureAccess = org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess.getInstance();
    _advSearchAccess = org.eclipse.smila.search.utils.advsearch.AdvSearchAccess.getInstance();
    _templateAccess = new TemplateAccess();
    _dataDictionaryAccess = DataDictionaryAccess.getInstance();
    _searchAccess = org.eclipse.smila.search.utils.search.SearchAccess.getInstance();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getAdvSearchAccess()
   */
  @Override
  public org.eclipse.smila.search.utils.advsearch.AdvSearchAccess getAdvSearchAccess() {
    return _advSearchAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getIndexStructureAccess()
   */
  @Override
  public org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess getIndexStructureAccess() {
    return _indexStructureAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getIndexAccess()
   */
  @Override
  public IIndexAccess getIndexAccess() {
    return _indexAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getTemplateAccess()
   */
  @Override
  public ITemplateAccess getTemplateAccess() {
    return _templateAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getDataDictionaryAccess()
   */
  @Override
  public DataDictionaryAccess getDataDictionaryAccess() {
    return _dataDictionaryAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getSearchAccess()
   */
  @Override
  public org.eclipse.smila.search.utils.search.SearchAccess getSearchAccess() {
    return _searchAccess;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#dataTypeMatches(java.lang.String, java.lang.String)
   */
  @Override
  public boolean dataTypeMatches(String indexDataType, String searchDataType) {
    if ("FTText".equals(searchDataType) && "Text".equals(indexDataType)) {
      return true;
    }
    if ("FTNumber".equals(searchDataType) && "Number".equals(indexDataType)) {
      return true;
    }
    if ("FTDate".equals(searchDataType) && "Date".equals(indexDataType)) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getConfigurationFileForEngineData(java.lang.String)
   */
  @Override
  public byte[] getConfigurationFileForEngineData(String fileName) throws IOException {

    final File xmlFolder = ConfigUtils.getConfigFolder(MappingsLoader.BUNDLE_ID, "xml");
    final File file = new File(xmlFolder, fileName);
    final byte[] bytes = FileUtils.readFileToByteArray(file);
    return bytes;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#getConfigurationBundleForEngineData()
   */
  @Override
  public String getConfigurationBundleForEngineData() {
    return MappingsLoader.BUNDLE_ID;
  }
}
