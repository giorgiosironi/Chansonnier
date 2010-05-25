/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.blackboard.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterHelper;
import org.eclipse.smila.recordstorage.RecordStorage;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.service.component.ComponentContext;

/**
 * @author jschumacher
 * 
 */
public class BlackboardFactoryImpl implements BlackboardFactory {
  /**
   * The Constant BUNDLE_ID.
   */
  private static final String BUNDLE_ID = "org.eclipse.smila.blackboard";

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * RecordStorage used by blackboard.
   */
  private RecordStorage _recordStorage;

  /**
   * BinaryStorage used by blackboard.
   */
  private BinaryStorageService _binaryStorage;

  /**
   * The record filter helper.
   */
  private RecordFilterHelper _filterHelper;

  /**
   * Directory where cached attachments are stored.
   */
  private File _attachmentsTempDir;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.blackboard.BlackboardFactory#createPersistingBlackboard()
   */
  public Blackboard createPersistingBlackboard() throws BlackboardAccessException {
    if (_binaryStorage == null) {
      throw new BlackboardAccessException("no binary storage available, cannot create persisting blackboard.");
    }
    final PersistingBlackboardImpl blackboard = new PersistingBlackboardImpl(_filterHelper, _attachmentsTempDir);
    blackboard.setRecordStorage(_recordStorage);
    blackboard.setBinaryStorage(_binaryStorage);
    return blackboard;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.blackboard.BlackboardFactory#createTransientBlackboard()
   */
  public Blackboard createTransientBlackboard() {
    return new TransientBlackboardImpl(_filterHelper, _attachmentsTempDir);
  }

  /**
   * {@inheritDoc}
   */
  protected void activate(final ComponentContext context) {
    if (_log.isDebugEnabled()) {
      _log.debug("Activating " + getClass());
    }
    // TODO: remove it when Declarative Services will set it correctly
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    try {
      _attachmentsTempDir = WorkspaceHelper.createWorkingDir(BUNDLE_ID, "attachments");
      _filterHelper = new RecordFilterHelper(ConfigUtils.getConfigStream(BUNDLE_ID, "RecordFilters.xml"));
      if (_log.isInfoEnabled()) {
        _log.info("BlackboardService started");
      }
    } catch (final Exception exception) {
      if (_log.isErrorEnabled()) {
        _log.error("Activation of " + getClass() + " failed");
        throw new RuntimeException(exception);
      }
    } finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }

  /**
   * Set the record service for blackboard. To be used by Declarative Services as the bind method.
   * 
   * @param recordStorage
   *          RecordStorage - the record storage service interface
   */
  public void setRecordStorage(final RecordStorage recordStorage) {
    _recordStorage = recordStorage;
    if (_log.isDebugEnabled()) {
      _log.debug("RecordStorage is bound");
    }
  }

  /**
   * Un-set the record storage service. To be used by Declarative Services as the un-bind method.
   * 
   * @param recordStorage
   *          RecordStorage - the record storage service interface
   */
  public void unsetRecordStorage(final RecordStorage recordStorage) {
    if (recordStorage == _recordStorage) {
      _recordStorage = null;
    }
    if (_log.isDebugEnabled()) {
      _log.debug("RecordStorage is unbound");
    }
  }

  /**
   * Set the binary service for blackboard. To be used by Declarative Services as the bind method.
   * 
   * @param binaryStorage
   *          BinaryStorageService - the binary storage service interface
   */
  public void setBinaryStorage(final BinaryStorageService binaryStorage) {
    _binaryStorage = binaryStorage;
    if (_log.isDebugEnabled()) {
      _log.debug("BinaryStorage is bound");
    }
  }

  /**
   * Un-set the binary storage service. To be used by Declarative Services as the un-bind method.
   * 
   * @param binaryStorage
   *          -
   */
  public void unsetBinaryStorage(final BinaryStorageService binaryStorage) {
    if (binaryStorage == _binaryStorage) {
      _binaryStorage = null;
    }
    if (_log.isDebugEnabled()) {
      _log.debug("BinaryStorage is unbound");
    }
  }

}
