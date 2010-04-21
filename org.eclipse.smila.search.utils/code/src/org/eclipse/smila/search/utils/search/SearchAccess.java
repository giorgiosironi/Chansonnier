/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class SearchAccess {

  /**
   * Extension point name for Search Access.
   */
  public static final String EXTENSION_POINT_NAME_SEARCH_ACCESS = "org.eclipse.smila.search.utils.search.access";

  /**
   * Cached IRM types.
   */
  private static SearchAccess[] s_cachedSearchAccess;

  public static SearchAccess getInstance() {

    final Log log = LogFactory.getLog(SearchAccess.class);

    SearchAccess[] types;
    try {
      types = getTypes();
      if (types.length != 1) {
        if (log.isWarnEnabled()) {
          log.warn("invalid index structure access count [" + types.length + "]");
        }
        return null;
      }
      return types[0];
    } catch (final DSearchException e) {
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
   */
  public static SearchAccess[] getTypes() throws DSearchException {

    if (s_cachedSearchAccess != null) {
      return s_cachedSearchAccess;
    }

    final Log log = LogFactory.getLog(SearchAccess.class);

    final List<SearchAccess> found = new ArrayList<SearchAccess>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_NAME_SEARCH_ACCESS).getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        final String typeName = parseType(configurationElement, found.size());

        SearchAccess clazz = null;
        try {
          final Object obj = configurationElement.createExecutableExtension("Clazz");
          clazz = (SearchAccess) obj;
        } catch (final Exception exception) {
          if (log.isErrorEnabled()) {
            if (configurationElement != null) {
              log.error("Failed to instantiate search access");
            } else {
              log.error("Unknown!");
            }
          }
          throw new DSearchException("unable to load search access", exception);
        }

        if (clazz != null) {
          found.add(clazz);
        }
      }
    }

    s_cachedSearchAccess = found.toArray(new SearchAccess[0]);
    return s_cachedSearchAccess;
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

    if (!configurationElement.getName().equals("SearchAccess")) {
      return null;
    }

    final Log log = LogFactory.getLog(SearchAccess.class);

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
          "Failed to load search type named " + name + " in "
            + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        log.error(msg, e);
      }
      return null;
    }
  }

  public SearchAccess() {
  }

  public abstract Class getCodecClass(IDFParameter parameter);

  public abstract Class getCodecClass(INFParameter parameter);

  public abstract Class getCodecClass(ITFParameter parameter);

  public abstract Class getCodecClass(String className) throws ClassNotFoundException;

}
