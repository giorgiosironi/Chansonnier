/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DConnection;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DDException;
import org.w3c.dom.Element;

/**
 * @author brox IT-Solutions GmbH
 */
public abstract class DataDictionaryAccess {

  /**
   * Extension point name for Data Dictionary Accesss.
   */
  public static final String EXTENSION_POINT_NAME_DATA_DICTIONARY_ACCESS =
    "org.eclipse.smila.search.datadictionary.access";

  /**
   * Cached Data Dictionary Access types.
   */
  private static DataDictionaryAccess[] s_cachedDataDictionaryAccess;
  
  /**
   * DataDicitonaryAccess.
   * 
   * @return - an access object.
   */
  public static DataDictionaryAccess getInstance() {

    final Log log = LogFactory.getLog(DataDictionaryAccess.class);    
    
    DataDictionaryAccess[] types;
    try {
      types = getTypes();
      if (types.length != 1) {
        if (log.isWarnEnabled()) {
          log.warn("invalid data dictionary access count [" + types.length + "]");
        }
        return null;
      }
      return types[0];
    } catch (final DataDictionaryException e) {
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
   * @throws DataDictionaryException
   *           DataDictionaryException.
   */
  public static DataDictionaryAccess[] getTypes() throws DataDictionaryException {

    if (s_cachedDataDictionaryAccess != null) {
      return s_cachedDataDictionaryAccess;
    }

    final Log log = LogFactory.getLog(DataDictionaryAccess.class);    
    
    final List<DataDictionaryAccess> found = new ArrayList<DataDictionaryAccess>();
    // TODO: Check why the next line is needed.
    // found.add(UNKNOWN);
    final IExtension[] extensions =
      Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_NAME_DATA_DICTIONARY_ACCESS)
        .getExtensions();
    for (int i = 0; i < extensions.length; i++) {
      final IExtension extension = extensions[i];
      final IConfigurationElement[] configElements = extension.getConfigurationElements();

      for (int j = 0; j < configElements.length; j++) {
        final IConfigurationElement configurationElement = configElements[j];
        DataDictionaryAccess clazz = null;
        try {
          final Object obj = configurationElement.createExecutableExtension("Clazz");
          clazz = (DataDictionaryAccess) obj;
        } catch (final Exception exception) {
          if (log.isErrorEnabled()) {
            if (configurationElement != null) {
              log.error("Failed to instantiate data dictionary access");
            } else {
              log.error("Unknown!");
            }
          }
          throw new DataDictionaryException("unable to load data dictionary access", exception);
        }

        if (clazz != null) {
          found.add(clazz);
        }
      }
    }

    s_cachedDataDictionaryAccess = found.toArray(new DataDictionaryAccess[0]);
    return s_cachedDataDictionaryAccess;
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

    if (!configurationElement.getName().equals("DataDictionaryAccess")) {
      return null;
    }

    final Log log = LogFactory.getLog(DataDictionaryAccess.class);    
    
    try {
      String name = configurationElement.getAttribute("Clazz");
      if (name == null) {
        name = "[missing attribute name]";
      }
      return name;
    } catch (final Exception e) {
      String name = configurationElement.getAttribute("Clazz");
      if (name == null) {
        name = "[missing attribute name]";
      }
      final String msg =
        "Failed to load StrategyType named " + name + " in "
          + configurationElement.getDeclaringExtension().getNamespaceIdentifier();
      if (log.isErrorEnabled()) {
       log.error(msg, e); 
      }
      return null;
    }
  }

  /**
   * Constructor.
   */
  public DataDictionaryAccess() {
  }

  /**
   * Connection object.
   * 
   * @param eConnection -
   *          element connection.
   * @return - DConnection object.
   * @throws DDException -
   *           a possible exception.
   */
  public abstract DConnection decodeConnection(Element eConnection) throws DDException;

 
  /**
   * @param dConnection -
   *          dConnection object.
   * @param element -
   *          a connection element.
   * @return - the element.
   * @throws DDException -
   *           a exception.
   */
  public abstract Element encodeConnection(DConnection dConnection, Element element) throws DDException;
}
