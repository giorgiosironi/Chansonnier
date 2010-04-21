/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.indexstructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Element;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class IndexStructureAccess {

  /**
   * Extension point name for Index Structure Access.
   */
  public static final String EXTENSION_POINT_NAME_INDEX_STRUCTURE_ACCESS =
    "org.eclipse.smila.search.utils.indexstructure.access";

  /**
   * Cached IRM types.
   */
  private static IndexStructureAccess[] s_cachedIndexStructureAccess;

  /**
   * Constructor.
   */
  protected IndexStructureAccess() {
  }

  public static IndexStructureAccess getInstance() {

    final Log log = LogFactory.getLog(IndexStructureAccess.class);

    IndexStructureAccess[] types;
    try {
      types = getTypes();
      if (types.length != 1) {
        if (log.isWarnEnabled()) {
          log.warn("invalid index structure access count [" + types.length + "]");
        }
        return null;
      }
      return types[0];
    } catch (final ISException e) {
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
   * @throws ISException
   *           -
   */
  public static IndexStructureAccess[] getTypes() throws ISException {

    if (s_cachedIndexStructureAccess != null) {
      return s_cachedIndexStructureAccess;
    }

    final Log log = LogFactory.getLog(IndexStructureAccess.class);

    final List<IndexStructureAccess> found = new ArrayList<IndexStructureAccess>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_NAME_INDEX_STRUCTURE_ACCESS)
        .getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        final String typeName = parseType(configurationElement, found.size());

        IndexStructureAccess clazz = null;
        try {
          final Object obj = configurationElement.createExecutableExtension("Clazz");
          clazz = (IndexStructureAccess) obj;
        } catch (final Exception exception) {
          if (log.isErrorEnabled()) {
            if (configurationElement != null) {
              log.error("Failed to instantiate index structure access");
            } else {
              log.error("Unknown!");
            }
          }
          throw new ISException("unable to load index structure access", exception);
        }

        if (clazz != null) {
          found.add(clazz);
        }
      }
    }

    s_cachedIndexStructureAccess = found.toArray(new IndexStructureAccess[0]);
    return s_cachedIndexStructureAccess;
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

    if (!configurationElement.getName().equals("IndexStructureAccess")) {
      return null;
    }

    final Log log = LogFactory.getLog(IndexStructureAccess.class);

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
          "Failed to load StrategyType named " + name + " in "
            + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        log.error(msg, e);
      }
      return null;
    }
  }

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

  public abstract DIndexStructure decode(Element eIndexStructure) throws ISException;

  public abstract Element encode(DIndexStructure dIndexStructure, Element element) throws ISException;

}
