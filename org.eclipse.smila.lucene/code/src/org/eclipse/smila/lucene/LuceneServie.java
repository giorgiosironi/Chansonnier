/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.lucene;

import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract base class for services dealing with Lucene.
 */
public abstract class LuceneServie {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.lucene";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIG_FILE_MAPPINGS = "Mappings.xml";

  /**
   * name of annotation configuring the index to work with.
   */
  public static final String INDEX_NAME = "indexName";

  /**
   * Name of the Lucene index field containing the id value.
   */
  public static final String ID_FIELD = "ID";

  /**
   * The mappings in a multi map format.
   */
  private HashMap<String, HashMap<String, HashMap<String, Integer>>> _mappings;

  /**
   * Loads the mappings.
   */
  protected void loadMappings() {
    _mappings = MappingsLoader.loadMappingsMap(CONFIG_FILE_MAPPINGS);
  }

  /**
   * Unloads the mappings.
   */
  protected void unloadMappings() {
    if (_mappings != null) {
      final Collection<HashMap<String, HashMap<String, Integer>>> collection = _mappings.values();
      for (HashMap<String, HashMap<String, Integer>> map : collection) {
        if (map != null) {
          final Collection<HashMap<String, Integer>> values = map.values();
          for (HashMap<String, Integer> submap : values) {
            if (submap != null) {
              submap.clear();
            }
          }
          map.clear();
        }
      }
      _mappings.clear();
      _mappings = null;
    }
  }

  /**
   * Returns the mappings.
   * 
   * @return the mappings.
   */
  protected HashMap<String, HashMap<String, HashMap<String, Integer>>> getMappings() {
    return _mappings;
  }

}
