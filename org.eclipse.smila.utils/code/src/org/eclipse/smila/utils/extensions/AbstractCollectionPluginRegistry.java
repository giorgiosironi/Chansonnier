/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.extensions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;

/**
 * The Class AbstractCollectionPluginRegistry.
 * 
 * @param <T>
 *          plug-in interface class
 */
public abstract class AbstractCollectionPluginRegistry<T> extends AbstractPluginRegistryBase<T> {

  /** The plugins map. */
  protected Map<String, T> _pluginsMap;

  /** The plugins array. */
  protected T[] _pluginsArray;

  /** The Constant LOG. */
  private final Log _log = LogFactory.getLog(AbstractCollectionPluginRegistry.class);

  /**
   * Creates the empty array.
   * 
   * @param size
   *          the size
   * 
   * @return the t[]
   */
  protected abstract T[] createEmptyArray(int size);

  /**
   * Gets the plugins.
   * 
   * @return the plugins
   */
  public T[] getPlugins() {
    return _pluginsArray;
  }

  /**
   * Gets the plugin.
   * 
   * @param id
   *          the id
   * 
   * @return the plugin
   */
  public T getPlugin(final String id) {
    if (_pluginsMap.containsKey(id)) {
      return _pluginsMap.get(id);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#initialize()
   */
  @Override
  protected void initialize() {
    _pluginsMap = new HashMap<String, T>();
    super.initialize();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#extensionAdded(java.lang.String,
   *      org.eclipse.core.runtime.IExtension)
   */
  @Override
  protected void extensionAdded(final String id, final IExtension extension) throws CoreException {
    if (id != null) {
      extensionRemoved(id);
      _pluginsMap.put(id, createInstance(extension));
    } else {
      _log.error("plug-in id is NULL");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#extensionRemoved(java.lang.String)
   */
  @Override
  protected void extensionRemoved(final String id) {
    if (id != null) {
      if (_pluginsMap.containsKey(id)) {
        _pluginsMap.remove(id);
      }
    } else {
      _log.error("plug-in id is NULL");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#finalizeExtensionChanges()
   */
  @Override
  protected void finalizeExtensionChanges() {
    _pluginsArray = _pluginsMap.values().toArray(createEmptyArray(_pluginsMap.values().size()));
  }
}
