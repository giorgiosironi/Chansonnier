/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.search.EIFActivator;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PluginFactory {

  /**
   * Extension point name for Plugin Access.
   */
  public static final String EXTENSION_POINT_NAME_PLUGIN = "org.eclipse.smila.search.plugin";

  /**
   * s_object.
   */
  private static Object s_object = new Object();

  /**
   * s_plugin.
   */
  private static Plugin s_plugin;

  /**
   * Constructor.
   */
  private PluginFactory() {

  }

  /**
   * @throws PluginException -
   */
  public static void initialize() throws PluginException {
    synchronized (s_object) {
      final String pluginName = null;
      try {
        EIFActivator.registerSchemas();
        // pluginName = ConfigManager
        // .getString(ConfigManager.AFSDK_PLUGIN);
        s_plugin = getInstance();
      } catch (final Throwable e) {
        throw new PluginException("unable to instanciate retrieval plugin [" + pluginName + "]", e);
      }
    }
  }

  public static Plugin getPlugin() {
    synchronized (s_object) {
      final Log log = LogFactory.getLog(PluginFactory.class);
      if (s_plugin == null) {
        try {
          initialize();
        } catch (final PluginException exception) {
          if (log.isErrorEnabled()) {
            log.error(exception);
          }
        }
      }

      return s_plugin;
    }
  }

  public static Plugin getInstance() {

    // TODO: implement correctly
    final Log log = LogFactory.getLog(PluginFactory.class);
    Plugin[] types;
    try {
      types = getTypes();
      if (types.length != 1) {
        if (log.isWarnEnabled()) {
          log.warn("invalid plugin count [" + types.length + "]");
        }
        return null;
      }
      return types[0];
    } catch (final PluginException e) {
      if (log.isErrorEnabled()) {
        log.error(e);
      }
      return null;
    }
  }

  /**
   * Get all available IRM types.
   * 
   * @return IRM types.
   * @throws PluginException -
   */
  public static Plugin[] getTypes() throws PluginException {
    final Log log = LogFactory.getLog(PluginFactory.class);
    final List<Plugin> found = new ArrayList<Plugin>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_NAME_PLUGIN).getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        final String typeName = parseType(configurationElement, found.size());

        Plugin clazz = null;
        try {
          final Object obj = configurationElement.createExecutableExtension("Clazz");
          clazz = (Plugin) obj;
        } catch (final Exception exception) {
          if (log.isErrorEnabled()) {
            if (configurationElement != null) {
              log.error("Failed to instantiate plugin");
            } else {
              log.error("Unknown!");
            }
          }
          throw new PluginException("unable to load plugin", exception);
        }

        if (clazz != null) {
          found.add(clazz);
        }
      }
    }

    return found.toArray(new Plugin[0]);
  }

  /**
   * Parse configuration and return according IRMType.
   * 
   * @param configurationElement
   *          Configuration element.
   * @param ordinal
   *          Ordinal.
   * @return Type name.
   */
  public static String parseType(IConfigurationElement configurationElement, int ordinal) {

    final Log log = LogFactory.getLog(PluginFactory.class);

    if (!configurationElement.getName().equals("Plugin")) {
      return null;
    }

    try {
      String name = configurationElement.getAttribute("Clazz");
      if (name == null) {
        name = "[missing attribute name]";
      }
      return name;
    } catch (final Exception e) {
      if (log.isErrorEnabled()) {
        String name = configurationElement.getAttribute("Clazz");
        if (name == null) {
          name = "[missing attribute name]";
        }
        final String msg =
          "Failed to load plugin named " + name + " in "
            + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        log.error(msg, e);
      }
      return null;
    }
  }

}
