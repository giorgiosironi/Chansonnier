/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary;

// data dictionary classes
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DAnyFinderDataDictionary;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DAnyFinderDataDictionaryCodec;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DDException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndexCodec;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DConfiguration;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DDateField;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DField;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DFieldConfig;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DNumberField;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DTextField;
import org.eclipse.smila.search.utils.indexstructure.DIndexField;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess;
import org.eclipse.smila.search.utils.search.IParameter;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.eclipse.smila.utils.xml.XMLUtilsConfig;
import org.eclipse.smila.utils.xml.XMLUtilsException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public abstract class DataDictionaryController {

  /**
   * The Constant BUNDLE.
   */
  private static final String BUNDLE = "org.eclipse.smila.search.datadictionary";

  /**
   * The Constant CONFIG_NAME.
   */
  private static final String CONFIG_NAME = "DataDictionary.xml";

  /**
   * The _data dictionary types.
   */
  private static DAnyFinderDataDictionary _dataDictionaryTypes;

  /**
   * The dd.
   */
  private static DAnyFinderDataDictionary dd;

  /**
   * The mutex.
   */
  private static Object mutex = new Object();

  /**
   * This method adds an index entry to the data dictionary and saves it.
   * 
   * @param dIndex
   *          the d index
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static void addIndex(final DIndex dIndex) throws DataDictionaryException {
    final Log log = LogFactory.getLog(DataDictionaryController.class);
    synchronized (mutex) {
      ensureLoaded();
      if (hasIndexIgnoreCase(dIndex.getName())) {
        throw new DataDictionaryException("index already exists in data dictionary ["
          + getExistingIndexName(dIndex.getName()) + "]");
      }

      final DConfiguration dConfig = dIndex.getConfiguration();
      if (dConfig != null) {
        validateConfiguration(dConfig, dIndex.getIndexStructure());
      }

      dd.addIndex(dIndex);

      // validate data dictionary
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", new ByteArrayOutputStream());
      } catch (final DDException e) {
        log.error("unable to save data dictionary", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary");
      } catch (final XMLUtilsException e) {
        log.error("Unable to stream DataDictionary!", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary while streaming");
      }

      save();
    }
  }

  /**
   * Adds the index.
   * 
   * @param indexTypeName
   *          the index type name
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static void addIndex(final String indexTypeName) throws DataDictionaryException {
    synchronized (dd) {
      ensureLoaded();
      final Enumeration<DIndex> indiceTypes = _dataDictionaryTypes.getIndices();
      while (indiceTypes.hasMoreElements()) {
        final DIndex dIndexType = indiceTypes.nextElement();
        if (dIndexType.getName().equalsIgnoreCase(indexTypeName)) {
          // CLONE
          // encode to XML
          final Document doc = XMLUtils.getDocument();
          final Element rootElement =
            doc.createElementNS(DAnyFinderDataDictionaryCodec.NS, "AnyFinderDataDictionary");
          Attr attr = null;
          attr = doc.createAttribute("xmlns:xsi");
          attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
          rootElement.setAttributeNode(attr);
          attr = doc.createAttribute("xsi:schemaLocation");
          attr.setValue(DAnyFinderDataDictionaryCodec.NS + " ../xml/AnyFinderDataDictionary.xsd");
          rootElement.setAttributeNode(attr);
          doc.appendChild(rootElement);
          final DIndex dIndex;
          try {
            DIndexCodec.encode(dIndexType, rootElement);
          } catch (final DDException e) {
            throw new DataDictionaryException(e);
          }
          final Document doc2;
          try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", bos);
            doc2 = XMLUtils.parse(bos.toByteArray(), true);
          } catch (final XMLUtilsException e) {
            throw new DataDictionaryException(e);
          }
          final DAnyFinderDataDictionary dictionary;
          // decode again to DIndex
          try {
            dictionary = DAnyFinderDataDictionaryCodec.decode(doc2.getDocumentElement());
          } catch (final DDException e) {
            throw new DataDictionaryException(e);
          }
          dIndex = dictionary.getIndex(dIndexType.getName());
          addIndex(dIndex);
          return;
        }
      }
    }
    throw new DataDictionaryException(String.format("Index type [%s] was not found!", indexTypeName));
  }

  /**
   * Rename index.
   * 
   * @param indexName
   *          the index name
   * @param newIndexName
   *          the new index name
   * 
   * @return true, if successful
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static boolean renameIndex(final String indexName, final String newIndexName)
    throws DataDictionaryException {
    final Log log = LogFactory.getLog(DataDictionaryController.class);

    synchronized (mutex) {
      ensureLoaded();
      if (dd.getIndex(newIndexName) != null) {
        throw new DataDictionaryException(String.format("Cannot rename index to [%s] because it's already exists!",
          newIndexName));
      }
      log.debug("Updating datadictionary...");
      final DIndex index = dd.getIndex(indexName);
      if (index == null) {
        return false; // no index entry
      }
      dd.removeIndex(index);
      index.setName(newIndexName);
      final DIndexStructure indexStructure = index.getIndexStructure();
      if (indexStructure != null) {
        indexStructure.setName(newIndexName);
      }
      dd.addIndex(index);
      // validate data dictionary
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", new ByteArrayOutputStream());
      } catch (final DDException e) {
        log.error("unable to save data dictionary", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary");
      } catch (final XMLUtilsException e) {
        log.error("Unable to stream DataDictionary!", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary while streaming");
      }

      save();
      return true;
    }

  }

  /**
   * This method removes an index entry from the data dictionary. It returnes true, when the index entry has exists.
   * 
   * @param indexName
   *          the index name
   * 
   * @return true, if delete index
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static boolean deleteIndex(final String indexName) throws DataDictionaryException {
    final Log log = LogFactory.getLog(DataDictionaryController.class);

    synchronized (mutex) {
      ensureLoaded();
      log.debug("Updating datadictionary...");
      final DIndex index = dd.getIndex(indexName);
      if (index == null) {
        return false; // no index entry
      }
      dd.removeIndex(index);

      // validate data dictionary
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", new ByteArrayOutputStream());
      } catch (final DDException e) {
        log.error("unable to save data dictionary", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary");
      } catch (final XMLUtilsException e) {
        log.error("Unable to stream DataDictionary!", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary while streaming");
      }

      save();
      return true;
    }
  }

  /**
   * Gets the data dictionary types.
   * 
   * @return the data dictionary types
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static DAnyFinderDataDictionary getDataDictionaryTypes() throws DataDictionaryException {
    ensureLoaded();
    return _dataDictionaryTypes;
  }

  /**
   * Ensure loaded.
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  private static void ensureLoaded() throws DataDictionaryException {
    if (dd == null) {
      synchronized (mutex) {
        if (dd == null) {
          loadDataDictionary();
        }
      }
    }
  }

  /**
   * Gets the data dictionary.
   * 
   * @return the data dictionary
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static DAnyFinderDataDictionary getDataDictionary() throws DataDictionaryException {
    ensureLoaded();
    return dd;
  }

  /**
   * Checks whether an index with a given name exists and returns that name if such an index is found. If
   * <code>hasIndexIgnoreCase()</code> returns true, this method can be used to obtain the name of the index in the
   * same writing as is used in the data dictionary.
   * 
   * @param indexName -
   *          The name of the index to search for
   * 
   * @return The name of the index as it is stored in the data dictionary
   * 
   * @throws DataDictionaryException
   *           if the data dictionary cannot be loaded
   */
  public static String getExistingIndexName(final String indexName) throws DataDictionaryException {
    ensureLoaded();
    for (final Enumeration e = dd.getIndices(); e.hasMoreElements();) {
      final String name = ((DIndex) e.nextElement()).getName();
      if (name.equalsIgnoreCase(indexName)) {
        return name;
      }
    }
    return null;
  }

  /**
   * Gets the index.
   * 
   * @param indexName
   *          the index name
   * 
   * @return the index
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static DIndex getIndex(final String indexName) throws DataDictionaryException {
    ensureLoaded();
    final DIndex dIndex = dd.getIndex(indexName);
    if (dIndex == null) {
      throw new DataDictionaryException("index does not exist in data dictionary [" + indexName + "]");
    }

    return dIndex;
  }

  /**
   * Checks for index.
   * 
   * @param indexName
   *          the index name
   * 
   * @return true, if successful
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static boolean hasIndex(final String indexName) throws DataDictionaryException {
    ensureLoaded();
    return (dd.getIndex(indexName) != null) ? true : false;
  }

  /**
   * This method checks whether an index with the same name already exists in the data dictionary. The check is
   * performed in a case-insensitive manner.
   * 
   * @param indexName -
   *          The name of the index to check
   * 
   * @return boolean
   * 
   * @throws DataDictionaryException
   *           if the data dictionary cannot be loaded
   */

  public static boolean hasIndexIgnoreCase(final String indexName) throws DataDictionaryException {
    ensureLoaded();
    for (final Enumeration e = dd.getIndices(); e.hasMoreElements();) {
      final String name = ((DIndex) e.nextElement()).getName();
      if (name.equalsIgnoreCase(indexName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Load data dictionary.
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  private static void loadDataDictionary() throws DataDictionaryException {
    synchronized (mutex) {
      final Log log = LogFactory.getLog(DataDictionaryController.class);
      // load data dictionary types from configuration folder
      InputStream is = ConfigUtils.getConfigStream(BUNDLE, CONFIG_NAME);
      _dataDictionaryTypes = parseDataDictionary(is, log);
      // load data dictionary from workspace folder
      File workspace;
      try {
        workspace = WorkspaceHelper.createWorkingDir(BUNDLE);
      } catch (final IOException e) {
        throw new DataDictionaryException(e);
      }
      final File ddFile = new File(workspace, CONFIG_NAME);
      if (!ddFile.exists()) {
        dd = new DAnyFinderDataDictionary();
      } else {
        try {
          is = new FileInputStream(ddFile);
        } catch (final FileNotFoundException e) {
          throw new DataDictionaryException(e);
        }
        dd = parseDataDictionary(is, log);
      }
    }
  }

  /**
   * Parses the data dictionary.
   * 
   * @param is
   *          the is
   * @param log
   *          the log
   * 
   * @return the d any finder data dictionary
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  private static DAnyFinderDataDictionary parseDataDictionary(final InputStream is, final Log log)
    throws DataDictionaryException {
    try {
      final Document doc = XMLUtils.parse(is, new XMLUtilsConfig());
      return DAnyFinderDataDictionaryCodec.decode(doc.getDocumentElement());
    } catch (final XMLUtilsException e) {
      log.error("Unable parse DataDictionary!", e);
      throw new DataDictionaryException("Unable parse DataDictionary!");
    } catch (final DDException e) {
      log.error("unable to load data dictionary", e);
      throw new DataDictionaryException("Unable to decode XML into DataDictionary");
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * Save.
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  private static void save() throws DataDictionaryException {
    final Log log = LogFactory.getLog(DataDictionaryController.class);

    // resolve datadictionary name
    File workspaceFolder;
    try {
      workspaceFolder = WorkspaceHelper.createWorkingDir(BUNDLE);
    } catch (final IOException e) {
      throw new DataDictionaryException(e);
    }

    // save datadictionary
    try {
      final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
      XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", new File(workspaceFolder, CONFIG_NAME));
    } catch (final DDException e) {
      log.error("unable to save data dictionary", e);
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

      } catch (final Throwable ex) {
        ; // do nothing
      }
      throw new DataDictionaryException("unable to update data dictionary");
    } catch (final XMLUtilsException e) {
      log.error("Unable to stream DataDictionary!", e);
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

      } catch (final Throwable ex) {
        ; // do nothing
      }
      throw new DataDictionaryException("Unable to stream DataDictionary!");
    }
  }

  /**
   * Sets the index configuration.
   * 
   * @param indexName
   *          the index name
   * @param dConfiguration
   *          the d configuration
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static void setIndexConfiguration(final String indexName, final DConfiguration dConfiguration)
    throws DataDictionaryException {
    final Log log = LogFactory.getLog(DataDictionaryController.class);
    synchronized (mutex) {
      ensureLoaded();

      if (!hasIndex(indexName)) {
        throw new DataDictionaryException("index does not exist in data dictionary [" + indexName + "]");
      }

      // check validity of configuration
      final DIndex dIndex = dd.getIndex(indexName);
      final DIndexStructure dIS = dIndex.getIndexStructure();

      validateConfiguration(dConfiguration, dIS);
      dIndex.setConfiguration(dConfiguration);

      // validate data dictionary
      try {
        final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
        XMLUtils.stream(doc.getDocumentElement(), true, "UTF-8", new ByteArrayOutputStream());
      } catch (final DDException e) {
        log.error("unable to save data dictionary", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary");
      } catch (final XMLUtilsException e) {
        log.error("Unable to stream DataDictionary!", e);
        try {
          final Document doc = DAnyFinderDataDictionaryCodec.encode(dd);
          log.debug("invalid data dictionary\n" + new String(XMLUtils.stream(doc.getDocumentElement(), false)));

        } catch (final Throwable ex) {
          ; // do nothing
        }
        throw new DataDictionaryException("invalid data dictionary while streaming");
      }

      save();
    }
  }

 
  /**
   * Validate configuration.
   * 
   * @param dConfiguration
   *          the d configuration
   * @param dIS
   *          the d is
   * 
   * @throws DataDictionaryException
   *           the data dictionary exception
   */
  public static void validateConfiguration(final DConfiguration dConfiguration, final DIndexStructure dIS)
    throws DataDictionaryException {

    if (dConfiguration == null) {
      throw new DataDictionaryException("No default configuration defined for index");
    }

    // test field count match
    if (dConfiguration.getDefaultConfig().getFieldCount() != dIS.getFieldCount()) {
      throw new DataDictionaryException("Invalid default configuration. Field counts in DefaultConfig "
        + "and IndexStructure do not match: [" + dConfiguration.getDefaultConfig().getFieldCount() + "/"
        + dIS.getFieldCount() + "]");
    }

    final IndexStructureAccess indexStructureAccess = IndexStructureAccess.getInstance();
    for (int i = 0; i < dIS.getFieldCount(); i++) {
      final DField field = dConfiguration.getDefaultConfig().getField(i);
      if (field == null) {
        throw new DataDictionaryException("Default configuration missing for field " + i);
      }
      final DFieldConfig configField = field.getFieldConfig();

      final DIndexField dIF = dIS.getField(field.getFieldNo());

      if (dIF != null && !indexStructureAccess.dataTypeMatches(dIF.getType(), configField.getType())) {
        throw new DataDictionaryException("Type of field '" + field.getFieldNo()
          + "' in DefaultConfig does not match type of index field");
      }

      if (configField.getConstraint() == null) {
        throw new DataDictionaryException("'Constraint' parameter missing in DefaultConfig for field [" + i + "]");
      }
      if (configField.getWeight() == null) {
        throw new DataDictionaryException("'Weight' parameter missing in DefaultConfig for field [" + i + "]");
      }

      // check completeness of search technology dependant parameters
      IParameter param = null;
      if (configField instanceof DTextField) {
        param = ((DTextField) configField).getParameter();
      }
      if (configField instanceof DNumberField) {
        param = ((DNumberField) configField).getParameter();
      }
      if (configField instanceof DDateField) {
        param = ((DDateField) configField).getParameter();
      }

      if (param == null) {
        throw new DataDictionaryException("'Parameter' parameter missing in DefaultConfig for field [" + i + "]");
      }

      if (!param.isComplete()) {
        throw new DataDictionaryException("'Parameter' parameter is incomplete in DefaultConfig for field [" + i
          + "]");
      }

    }
  }

}
