/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.advsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public abstract class AdvSearchAccess {

  /**
   * Extension point name for Search Access.
   */
  public static final String EXTENSION_POINT_NAME_ADV_SEARCH_ACCESS =
    "org.eclipse.smila.search.utils.advsearch.access";

  /**
   * Namespace.
   */
  public static String NS = "http://www.anyfinder.de/AdvancedSearch";

  /**
   * Cached IRM types.
   */
  private static AdvSearchAccess[] s_cachedAdvSearchAccess;

  public static AdvSearchAccess getInstance() {
    final Log log = LogFactory.getLog(AdvSearchAccess.class);
    AdvSearchAccess[] types;
    try {
      types = getTypes();
      if (types.length != 1) {
        if (log.isWarnEnabled()) {
          log.warn("invalid index structure access count [" + types.length + "]");
        }
        return null;
      }
      return types[0];
    } catch (final AdvSearchException e) {
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
  public static AdvSearchAccess[] getTypes() throws AdvSearchException {

    if (s_cachedAdvSearchAccess != null) {
      return s_cachedAdvSearchAccess;
    }

    final Log log = LogFactory.getLog(AdvSearchAccess.class);

    final List<AdvSearchAccess> found = new ArrayList<AdvSearchAccess>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_NAME_ADV_SEARCH_ACCESS).getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        final String typeName = parseType(configurationElement, found.size());

        AdvSearchAccess clazz = null;
        try {
          final Object obj = configurationElement.createExecutableExtension("Clazz");
          clazz = (AdvSearchAccess) obj;
        } catch (final Exception exception) {
          if (log.isErrorEnabled()) {
            if (configurationElement != null) {
              log.error("Failed to instantiate adv. search access");
            } else {
              log.error("Unknown!");
            }
          }
          throw new AdvSearchException("unable to load adv. search access", exception);
        }

        if (clazz != null) {
          found.add(clazz);
        }
      }
    }

    s_cachedAdvSearchAccess = found.toArray(new AdvSearchAccess[0]);
    return s_cachedAdvSearchAccess;
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

    final Log log = LogFactory.getLog(AdvSearchAccess.class);

    if (!configurationElement.getName().equals("SearchAdvAccess")) {
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
          "Failed to load adv. search type named " + name + " in "
            + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
        log.error(msg, e);
      }
      return null;
    }
  }

  protected AdvSearchAccess() {
  }

  /**
   * Decode advanced search.
   * 
   * @param eAdvSearch
   *          Advanced search element.
   * @return Decoded advanced search.
   * @throws AdvSearchException
   *           Unable to decode advanced search.
   */
  public abstract IAdvSearch decode(Element eAdvSearch) throws AdvSearchException;

  /**
   * Decode term.
   * 
   * @param eTerm
   *          Term element.
   * @return Decoded term.
   * @throws AdvSearchException
   *           Unable to decode term.
   */
  public abstract ITerm decodeTerm(Element eTerm) throws AdvSearchException;

  /**
   * Encode advanced search.
   * 
   * @param dAnyFinderAdvSearch
   *          Advanced search.
   * @return Encoded advanced search.
   * @throws AdvSearchException
   *           Unable to encode advanced search.
   */
  public abstract Document encode(IAdvSearch dAnyFinderAdvSearch) throws AdvSearchException;

  /**
   * Encode advanced search.
   * 
   * @param dAnyFinderAdvSearch
   *          Advanced search.
   * @param element
   *          Parent element.
   * @return Encoded advanced search.
   * @throws AdvSearchException
   *           Unable to encode advanced search.
   */
  public abstract Element encode(IAdvSearch dAnyFinderAdvSearch, Element element) throws AdvSearchException;

  /**
   * Encode term.
   * 
   * @param dTerm
   *          Term to encode.
   * @param element
   *          Parent element.
   * @return Encoded element.
   * @throws AdvSearchException
   *           Unable to encode element.
   */
  public abstract Element encodeTerm(ITerm dTerm, Element element) throws AdvSearchException;
}
