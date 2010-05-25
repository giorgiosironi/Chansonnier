/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import java.io.IOException;

import org.eclipse.smila.search.datadictionary.DataDictionaryAccess;
import org.eclipse.smila.search.utils.advsearch.AdvSearchAccess;
import org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess;
import org.eclipse.smila.search.utils.search.SearchAccess;

/**
 * @author August Georg Schmidt (BROX)
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Plugin {

  /**
   * 
   */
  public Plugin() {
    super();
  }

  /**
   * Get advanced search interface.
   * 
   * @return Advanced search interface.
   */
  public abstract AdvSearchAccess getAdvSearchAccess();

  /**
   * Get search access interface.
   * 
   * @return Search access interface.
   */
  public abstract SearchAccess getSearchAccess();

  /**
   * Get index structure interface.
   * 
   * @return Index structure interface.
   */
  public abstract IndexStructureAccess getIndexStructureAccess();

  /**
   * Get index access interface.
   * 
   * @return Index access interface.
   */
  public abstract IIndexAccess getIndexAccess();

  /**
   * Get template access interface.
   * 
   * @return Template access interface.
   */
  public abstract ITemplateAccess getTemplateAccess();

  /**
   * Get data dictionary interface.
   * 
   * @return Data dictionary interface.
   */
  public abstract DataDictionaryAccess getDataDictionaryAccess();

  /**
   * Determines whether a given index data type and search data type are compatible.
   * 
   * @param indexDataType
   *          Data type in index structure.
   * @param searchDataType
   *          Data type in simple search.
   * @return <code>true</code> if the two types can be converted to each other, <code>false</code> otherwise
   */
  public abstract boolean dataTypeMatches(String indexDataType, String searchDataType);

  /**
   * Get configuration file for engine data.
   * 
   * @param fileName
   *          File name.
   * @return Configuration file as byte[].
   * @throws IOException
   *           Unable to load configuration file.
   */
  public abstract byte[] getConfigurationFileForEngineData(String fileName) throws IOException;

  /**
   * @return Configuration bundle.
   */
  public abstract String getConfigurationBundleForEngineData();

}
