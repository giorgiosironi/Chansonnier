/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.common.mimetype.MimeTypeIdentifier;
import org.eclipse.smila.common.mimetype.MimeTypeParseException;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.compound.CompoundException;
import org.eclipse.smila.connectivity.framework.compound.CompoundHandler;
import org.eclipse.smila.connectivity.framework.compound.CompoundManager;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Basic Implementation of a CompoundManager.
 */
public class CompoundManagerImpl implements CompoundManager {

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(CompoundManagerImpl.class);

  /**
   * A List of references to CompoundHandlers.
   */
  private List<CompoundHandler> _compoundHandlers;

  /**
   * A reference to a MimeTypeIdentifier.
   */
  private MimeTypeIdentifier _mimeTypeIdentifier;

  /**
   * Default Constructor.
   */
  public CompoundManagerImpl() {
    if (_log.isTraceEnabled()) {
      _log.trace("Creating CompoundManagerImpl");
    }
  }

  /**
   * Adds a CompoundHandler to the internal List of CompoundHandler. Used by OSGi Declarative Services.
   * 
   * @param compoundHandler
   *          the crawler FactoryComponent to add
   */
  public void addCompoundHandler(final CompoundHandler compoundHandler) {
    if (_compoundHandlers == null) {
      _compoundHandlers = new ArrayList<CompoundHandler>();
    }
    _compoundHandlers.add(compoundHandler);
    _log.debug("CompoundHandler registered: " + compoundHandler.toString() + " - " + this.toString());
  }

  /**
   * Removes a CompoundHandler from the internal List of CompoundHandler. Used by OSGi Declarative Services.
   * 
   * @param compoundHandler
   *          the crawler FactoryComponent to remove
   */
  public void removeCompoundHandler(final CompoundHandler compoundHandler) {
    if (_compoundHandlers != null) {
      _compoundHandlers.remove(compoundHandler);
    }
    _log.debug("CompoundHandler unregistered: " + compoundHandler.toString() + " - " + this.toString());
  }

  /**
   * Sets the MimeTypeIdentifier. Used by OSGi Declarative Services.
   * 
   * @param mimeTypeIdentifier
   *          the mimeTypeIdentifier to set
   */
  public void setMimeTypeIdentifier(final MimeTypeIdentifier mimeTypeIdentifier) {
    _mimeTypeIdentifier = mimeTypeIdentifier;
  }

  /**
   * Set the MimeTypeIdentifier to null. Used by OSGi Declarative Services.
   * 
   * @param mimeTypeIdentifier
   *          the mimeTypeIdentifier to unset
   */
  public void unsetMimeTypeIdentifier(final MimeTypeIdentifier mimeTypeIdentifier) {
    if (_mimeTypeIdentifier == mimeTypeIdentifier) {
      _mimeTypeIdentifier = null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundManager#isCompound(Record, DataSourceConnectionConfig)
   */
  public boolean isCompound(final Record record, final DataSourceConnectionConfig config) throws CompoundException {
    // check parameters
    if (record == null) {
      throw new CompoundException("parameter record is null");
    }
    if (config == null) {
      throw new CompoundException("parameter config is null");
    }

    final String mimeType = getMimeType(record, config);
    return (findCompoundHandler(mimeType) != null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundManager#extract(Record, DataSourceConnectionConfig)
   */
  public Crawler extract(final Record record, final DataSourceConnectionConfig config) throws CompoundException {
    // check parameters
    if (record == null) {
      throw new CompoundException("parameter record is null");
    }
    if (config == null) {
      throw new CompoundException("parameter config is null");
    }

    final String mimeType = getMimeType(record, config);
    final CompoundHandler compoundHandler = findCompoundHandler(mimeType);
    if (compoundHandler != null) {

      if (record.hasAttachment(config.getCompoundHandling().getContentAttachment())) {
        return compoundHandler.extract(record, config);
      } else {
        throw new CompoundException("record " + record.getId() + " does not contain attachment "
          + config.getCompoundHandling().getContentAttachment());
      }
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundManager#adaptCompoundRecord(Record, DataSourceConnectionConfig)
   */
  public Record adaptCompoundRecord(final Record record, final DataSourceConnectionConfig config)
    throws CompoundException {
    // check parameters
    if (record == null) {
      throw new CompoundException("parameter record is null");
    }
    if (config == null) {
      throw new CompoundException("parameter config is null");
    }

    // TODO: adapt original record according to config (e.g. inherit attributes/annotations)
    return record;
  }

  /**
   * Searches the registered CompoundHandlers and returns the first CompoundHandler that supports the given mime type.
   * 
   * @param mimeType
   *          the mime type to support
   * @return a CompoundHandler or null if the mime type is not supported
   */
  private CompoundHandler findCompoundHandler(final String mimeType) {
    if (_compoundHandlers != null) {
      for (CompoundHandler compoundHandler : _compoundHandlers) {
        final Collection<String> supportedMimeTypes = compoundHandler.getSupportedMimeTypes();
        if (supportedMimeTypes != null && supportedMimeTypes.contains(mimeType)) {
          return compoundHandler;
        }
      }
    }
    return null;
  }

  /**
   * Gets the mime type of the record.
   * 
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return a String containing the mimeType
   * @throws CompoundException
   *           if no mime type exists
   */
  private String getMimeType(final Record record, final DataSourceConnectionConfig config) throws CompoundException {
    String mimeType = null;
    final String mimeTypeAttributeName = config.getCompoundHandling().getMimeTypeAttribute();
    if (mimeTypeAttributeName != null) {
      final Attribute mimeTypeAttribute = record.getMetadata().getAttribute(mimeTypeAttributeName);
      if (mimeTypeAttribute != null && mimeTypeAttribute.hasLiterals()) {
        mimeType = mimeTypeAttribute.getLiteral().getStringValue();
      }
    }

    // try to identify mimetype if not set
    if (mimeType == null) {
      mimeType = identifyMimeType(record, config);
      setMimeTypeAttribute(record, mimeTypeAttributeName, mimeType);
    }
    return mimeType;
  }

  /**
   * Tries to identify the mime type of the given record using a registered MimeTypeIdentifier.
   * 
   * @param record
   *          the Record
   * @param config
   *          the DataSourceConnectionConfig
   * @return a String containing the mimeType
   * @throws CompoundException
   *           if the mime type cannot be detected
   */
  private String identifyMimeType(final Record record, final DataSourceConnectionConfig config)
    throws CompoundException {
    String extension = null;
    final String extensionAttributeName = config.getCompoundHandling().getExtensionAttribute();
    if (extensionAttributeName != null) {
      final Attribute extensionAttribute = record.getMetadata().getAttribute(extensionAttributeName);
      if (extensionAttribute != null && extensionAttribute.hasLiterals()) {
        extension = extensionAttribute.getLiteral().getStringValue();
      }
    }

    final String attachmentName = config.getCompoundHandling().getContentAttachment();
    final byte[] content = record.getAttachment(attachmentName);
    try {
      return _mimeTypeIdentifier.identify(content, extension);
    } catch (MimeTypeParseException e) {
      final String msg = "Error identifying mimetype for record " + record.getId();
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CompoundException(msg, e);
    }
  }

  /**
   * Sets the the value of the mimeTypeAttribute.
   * 
   * @param record
   *          the Record to set the attribute
   * @param mimeTypeAttributeName
   *          the name of the attribute to set
   * @param mimeType
   *          the mimeType to set
   */
  private void setMimeTypeAttribute(final Record record, final String mimeTypeAttributeName, final String mimeType) {
    // check if a mimeType exists and if a attribute is configured to contain the mimetype
    if (mimeType != null && mimeTypeAttributeName != null) {
      final Literal value = record.getFactory().createLiteral();
      value.setStringValue(mimeType);

      if (record.getMetadata().hasAttribute(mimeTypeAttributeName)) {
        record.getMetadata().getAttribute(mimeTypeAttributeName).setLiteral(value);
      } else {
        final Attribute attribute = record.getFactory().createAttribute();
        attribute.setLiteral(value);
        record.getMetadata().setAttribute(mimeTypeAttributeName, attribute);
      }
    }
  }

}
