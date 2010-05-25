/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.extensions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;

/**
 * The Class AbstractSinglePluginRegistry.
 * 
 * @param <T>
 *          plug-in interface class
 */
public abstract class AbstractSinglePluginRegistry<T> extends AbstractPluginRegistryBase<T> {

  /** The plugin id. */
  protected String _pluginId;

  /** The plugin. */
  protected T _plugin;

  /**
   * Gets the plugin.
   * 
   * @return the plugin
   */
  public T getPlugin() {
    return _plugin;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#extensionAdded(java.lang.String,
   *      org.eclipse.core.runtime.IExtension)
   */
  @Override
  protected void extensionAdded(final String id, final IExtension extention) throws CoreException {
    _plugin = createInstance(extention);
    _pluginId = id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#extensionRemoved(java.lang.String)
   */
  @Override
  protected void extensionRemoved(final String id) {
    if (id.equals(_pluginId)) {
      _plugin = null;
      _pluginId = id;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#finalizeExtensionChanges()
   */
  @Override
  protected void finalizeExtensionChanges() {
    // nothing
  }

}
