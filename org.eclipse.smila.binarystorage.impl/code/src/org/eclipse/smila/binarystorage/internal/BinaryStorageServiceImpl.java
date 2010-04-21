/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistenceFactory;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.service.component.ComponentContext;

/**
 * Binary storage service implementation.
 *
 * @author mcimpean
 */
public class BinaryStorageServiceImpl implements BinaryStorageService {

  /** Path to binary storage configuration file. */
  public static final String CONFIGURATION_DIR = "configuration/";

  /** Binary storage configuration file name. */
  public static final String CONFIGURATION_FILE = "BinaryStorageConfiguration.xml";

  /** bundle name holding Configuration. */
  private static final String CONFIGURATION_BUNDLE = "org.eclipse.smila.binarystorage.impl";

  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(BinaryStorageServiceImpl.class);

  /** The _bundle id. */
  private String _bundleId;

  /** Binary Storage Service instance */
  private BinaryPersistence _bssPersistence;

  /** Binary storage configuration basket object. */
  private BinaryStorageConfiguration _binaryStorageConfiguration;

  /** The Constant JAXB_PACKAGE. */
  private static final String JAXB_PACKAGE = "org.eclipse.smila.binarystorage.config";

  /**
   * Default constructor.
   */
  public BinaryStorageServiceImpl() {
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#store(java.lang.String, java.io.InputStream)
   */
  public void store(final String id, final InputStream stream) throws BinaryStorageException {
    _bssPersistence.storeBinary(id, stream);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#createStorageFile(java.lang.String, java.lang.String,
   *      byte[])
   */
  public void store(final String id, final byte[] blob) throws BinaryStorageException {
      if (StringUtils.isBlank(id)) {
        throw new BinaryStorageException("id must not be blank");
      }
      _bssPersistence.storeBinary(id, blob);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#fetchAsByte(java.lang.String)
   */
  public byte[] fetchAsByte(final String id) throws BinaryStorageException {
    return _bssPersistence.loadBinaryAsByteArray(id);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#fetchAsStream(java.lang.String)
   */
  public InputStream fetchAsStream(final String id) throws BinaryStorageException {
    return _bssPersistence.loadBinaryAsInputStream(id);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#removeRecordAttachment(java.lang.String)
   */
  public void remove(final String id) throws BinaryStorageException {
    _bssPersistence.deleteBinary(id);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.BinaryStorageService#fetchSize(java.lang.String)
   */
  public long fetchSize(final String id) throws BinaryStorageException {
    return _bssPersistence.fetchSize(id);
  }

  /**
   * Perform binary storage activation.
   *
   * @param context
   *          ComponentContext
   */
  protected void activate(final ComponentContext context) {
    _bundleId = context.getBundleContext().getBundle().getSymbolicName();
    try {
      initialize();
    } catch (final BinaryStorageException bsex) {
      if (_log.isErrorEnabled()) {
        _log.error(bsex.getMessage());
      }
      throw new RuntimeException(bsex);
    }
  }

  /**
   * Release binary storage resources.
   *
   * @param context
   *          ComponentContext.
   * @throws BinaryStorageException
   */
  protected void deactivate(final ComponentContext context) throws BinaryStorageException {
    _log.info("deactivating bundle: bin storage");
    _bssPersistence.cleanup();
  }

  /**
   * Perform binary storage initialization.
   */
  private void initialize() throws BinaryStorageException {
    // Load configuration properties
    loadBinaryStorageConfigurationProperties();

    // Setup working directory
    setupWorkingDirectoryPath(_binaryStorageConfiguration);

    // Configure Virtual File System
    initializeBSS();
  }

  /**
   * read properties for configuration.
   *
   * @throws BinaryStorageException
   */
  private void loadBinaryStorageConfigurationProperties() throws BinaryStorageException {
    _binaryStorageConfiguration = new BinaryStorageConfiguration();
    InputStream configurationFileStream;

    try {
      configurationFileStream = ConfigUtils.getConfigStream(CONFIGURATION_BUNDLE, CONFIGURATION_FILE);

      final javax.xml.validation.Schema schema = BinaryStorageSchemaProvider.CONFIGURATION_SCHEMA;

      _binaryStorageConfiguration =
        (BinaryStorageConfiguration) JaxbUtils.unmarshall(JAXB_PACKAGE, BinaryStorageConfiguration.class
          .getClassLoader(), schema, configurationFileStream);

    } catch (final Exception e) {
      throw new BinaryStorageException(e, "Could not load configuration properties for " + _bundleId
        + " bundle; from " + CONFIGURATION_DIR + CONFIGURATION_FILE);
    }
  }

  /**
   * Setup BinaryStorage working directory.
   *
   * @param _binaryStorageConfiguration
   * @throws BinaryStorageException
   */
  private void setupWorkingDirectoryPath(final BinaryStorageConfiguration _binaryStorageConfiguration)
    throws BinaryStorageException {
    String path = _binaryStorageConfiguration.getPath();
    String tempPath = _binaryStorageConfiguration.getTempPath();
    if (StringUtils.isBlank(path)) {
      File file = buildFile();
      if (!file.exists()) {
        file.mkdir();
      }
      if (_log.isDebugEnabled()) {
        _log.debug("Binstorage location path :" + file.getPath());
      }
      path = file.getPath();
      file = new File(file.getParentFile(), "temp");
      if (!file.exists()) {
        file.mkdir();
      }
      tempPath = file.getPath();
    } else {
      tempPath = _binaryStorageConfiguration.getTempPath();
    }

    _binaryStorageConfiguration.setPath(path);
    _binaryStorageConfiguration.setTempPath(tempPath);
  }

  /**
   * Creates the binary storage working directory.
   *
   * @return File - binary storage bundle working directory
   * @throws BinaryStorageException
   *           - in case of any IO exception
   */
  private File buildFile() throws BinaryStorageException {
    try {
      final File file = WorkspaceHelper.createWorkingDir(_bundleId);
      return new File(file, "storage");
    } catch (final IOException ioex) {
      if (_log.isErrorEnabled()) {
        _log.error(ioex.getMessage());
      }
      throw new BinaryStorageException(ioex, "Could not setup binary-storage working directory for " + _bundleId
        + " bundle");
    }
  }

  /**
   * Initialize binary storage persistence file system.
   *
   * @throws BinaryStorageException
   */
  private void initializeBSS() throws BinaryStorageException {
    _bssPersistence = BinaryPersistenceFactory.newImplInstance(_binaryStorageConfiguration);
  }
}
