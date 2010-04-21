/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.lucene;

import java.util.HashMap;
import java.util.List;

import org.eclipse.smila.lucene.config.Attachment;
import org.eclipse.smila.lucene.config.Attribute;
import org.eclipse.smila.lucene.config.Mapping;
import org.eclipse.smila.lucene.config.Mappings;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.config.ConfigurationLoadException;
import org.eclipse.smila.utils.jaxb.JaxbUtils;

/**
 * The Class MappingsLoader.
 * 
 * @author stuc07
 */
public final class MappingsLoader {

  /**
   * The Constant BUNDLE_ID.
   */
  public static final String BUNDLE_ID = "org.eclipse.smila.lucene";

  /**
   * The Constant XSD_FILE.
   */
  public static final String XSD_FILE = "schemas/Mappings.xsd";

  /**
   * Constant to access the attributes map.
   */
  public static final String ATTRIBUTES = "attributes";

  /**
   * Constant to access the attachments map.
   */
  public static final String ATTACHMENTS = "attachments";

  /**
   * Prevents instantiating of class.
   */
  private MappingsLoader() {

  }

  /**
   * Load mappings.
   * 
   * @param fileName
   *          the name of the mappings file
   * 
   * @return the object
   */
  public static Object loadMappings(final String fileName) {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(MappingsLoader.class.getClassLoader());
    try {
      return JaxbUtils.unmarshall(BUNDLE_ID, BUNDLE_ID + ".config", MappingsLoader.class.getClassLoader(),
        XSD_FILE, ConfigUtils.getConfigStream(BUNDLE_ID, fileName));
    } catch (final Throwable e) {
      throw new ConfigurationLoadException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * Loads the mappings in an easy to use HashMap format. The base HasMap contains the index name as key. As values it
   * contains another HashMap, that has as key "attribute" and/or "attachment". Again it's value is another HashMap of
   * attribute/attachment names and index filed number.
   * 
   * <pre>
   * |- index1 
   * |      |- attributes
   * |      |             |- Filename = 1 
   * |      |             |- Path = 2
   * |      |             |- ... 
   * |      |- attachments 
   * |                    |- Content = 0
   * |                    |- ...
   * |                      
   * |- index2
   * |      |- attributes
   * |      |             |-Filename = 11 
   * |      |             |- Path = 12
   * |      |             |- ...        
   * |      |- attachments
   * |                    |- Content = 10
   * |                    |- ...
   * |
   * |- ...                      
   * </pre>
   * 
   * @param fileName
   *          the name of the mappings file
   * @return a HashMap
   */
  public static HashMap<String, HashMap<String, HashMap<String, Integer>>> loadMappingsMap(final String fileName) {
    final HashMap<String, HashMap<String, HashMap<String, Integer>>> indexMap =
      new HashMap<String, HashMap<String, HashMap<String, Integer>>>();

    // load mappings file
    final Mappings mappings = (Mappings) MappingsLoader.loadMappings(fileName);
    if (mappings != null) {
      final List<Mapping> list = mappings.getMapping();
      for (Mapping mappingType : list) {
        final HashMap<String, HashMap<String, Integer>> mappingMap =
          new HashMap<String, HashMap<String, Integer>>();

        // iterate over attributes
        if (mappingType.getAttributes() != null) {
          final List<Attribute> attribList = mappingType.getAttributes().getAttribute();
          final HashMap<String, Integer> attribMap = new HashMap<String, Integer>();
          for (Attribute attribute : attribList) {
            attribMap.put(attribute.getName(), attribute.getFieldNo());
          }
          mappingMap.put(ATTRIBUTES, attribMap);
        }

        // iterate over attachments
        if (mappingType.getAttachments() != null) {
          final List<Attachment> attachList = mappingType.getAttachments().getAttachment();
          final HashMap<String, Integer> attachMap = new HashMap<String, Integer>();
          for (Attachment attachment : attachList) {
            attachMap.put(attachment.getName(), attachment.getFieldNo());
          }
          mappingMap.put(ATTACHMENTS, attachMap);
        }

        indexMap.put(mappingType.getIndexName(), mappingMap);
      } // for i
    } // if
    return indexMap;
  }

}
