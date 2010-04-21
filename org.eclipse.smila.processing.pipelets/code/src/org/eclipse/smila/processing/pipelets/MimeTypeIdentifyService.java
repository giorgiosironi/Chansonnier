/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH - initial API and implementation, Daniel Stucky (empolis GmbH) -
 * initial API and implementation, Ivan Churkin(brox IT-Solutions GmbH) - simple, based on regular expression, web mime
 * type identification., Daniel Stucky
 * 
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.common.mimetype.MimeTypeIdentifier;
import org.eclipse.smila.common.mimetype.MimeTypeParseException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The simple MIME type identifier is able to detect MIME types based on a static extension mapping.
 * 
 * @author August Georg Schmidt (BROX), Daniel Stucky
 */
public class MimeTypeIdentifyService implements ProcessingService {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.processing.pipelets";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIG_FILE = "MimeTypeConfig.xml";

  /**
   * Constant for the configuration property FileExtensionAttribute.
   */
  public static final String FILE_EXTENSION_ATTRIBUTE = "FileExtensionAttribute";

  /**
   * Constant for the configuration property ContentAttachment.
   */
  public static final String CONTENT_ATTACHMENT = "ContentAttachment";

  /**
   * Constant for the configuration property MimeTypeAttribute.
   */
  public static final String META_DATA_ATTRIBUTE = "MetaDataAttribute";

  /**
   * Constant for the configuration property MimeTypeAttribute.
   */
  public static final String MIME_TYPE_ATTRIBUTE = "MimeTypeAttribute";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(MimeTypeIdentifyService.class);

  /**
   * The name of the attribute containing the file extension.
   */
  private String _extensionAttributeName;

  /**
   * The name of the attachment containing the file content.
   */
  private String _contentAttachmentName;

  /**
   * The _meta data attribute name.
   */
  private String _metaDataAttributeName;

  /**
   * The name of the attribute to store the MimeType in.
   */
  private String _mimeTypeAttributeName;

  /**
   * The configuration.
   */
  private PipeletConfiguration _configuration;

  /**
   * MIME type mapper.
   */
  private MimeTypeIdentifier _mimeTypeIdentifier;

  /**
   * The _content type pattern.
   */
  private final Pattern _contentTypePattern =
    Pattern.compile("^CONTENT-TYPE\\s*:\\s*([^\\s;=]+)(?:.|\\s)*$", Pattern.CASE_INSENSITIVE);

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
   * @see org.eclipse.smila.processing.ProcessingService#process(Blackboard, Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    // load configuration
    if (_configuration == null) {
      readConfiguration();
      _extensionAttributeName = (String) _configuration.getPropertyFirstValue(FILE_EXTENSION_ATTRIBUTE);
      _contentAttachmentName = (String) _configuration.getPropertyFirstValue(CONTENT_ATTACHMENT);
      _metaDataAttributeName = (String) _configuration.getPropertyFirstValue(META_DATA_ATTRIBUTE);
      _mimeTypeAttributeName = (String) _configuration.getPropertyFirstValueNotNull(MIME_TYPE_ATTRIBUTE);
      if (_extensionAttributeName == null && _contentAttachmentName == null && _metaDataAttributeName == null) {
        throw new ProcessingException("One of the config properties " + FILE_EXTENSION_ATTRIBUTE + ", "
          + CONTENT_ATTACHMENT + " or " + META_DATA_ATTRIBUTE + " have to be specified!");
      }
    }

    // process records
    for (final Id id : recordIds) {
      try {
        identifyAndStore(blackboard, id);
      } catch (final Exception ex) {
        if (_log.isErrorEnabled()) {
          _log.error("error identifying MimeType for record " + id, ex);
        }
      }
    } // for
    return recordIds;
  }

  /**
   * Identifies the MimeType and stores it in the BlackboardService.
   * 
   * @param blackboard
   *          the BlackboardService
   * @param id
   *          the Id
   * 
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws MimeTypeParseException
   *           if any error occurs
   */
  private void identifyAndStore(final Blackboard blackboard, final Id id) throws BlackboardAccessException,
    MimeTypeParseException {
    String mimeType = null;
    // TODO: add logic to check if a mimetype attribute was already set and validate the value if it's a valid (or
    // at least syntactically valid) mime type.

    if (mimeType == null) {
      if (_extensionAttributeName != null && _contentAttachmentName != null) {
        String extension = null;
        byte[] content = null;

        // get extension if available
        final Path path = new Path(_extensionAttributeName);
        if (blackboard.hasAttribute(id, path)) {
          extension = blackboard.getLiteral(id, path).getStringValue();
        }

        // get content if available
        if (blackboard.hasAttachment(id, _contentAttachmentName)) {
          content = blackboard.getAttachment(id, _contentAttachmentName);
        }
        mimeType = _mimeTypeIdentifier.identify(content, extension);
      } else if (_extensionAttributeName != null) {
        // get extension if available
        final Path path = new Path(_extensionAttributeName);
        if (blackboard.hasAttribute(id, path)) {
          final String extension = blackboard.getLiteral(id, path).getStringValue();
          if (extension != null) {
            mimeType = _mimeTypeIdentifier.identify(extension);
          } // if
        } // if
      } else if (_contentAttachmentName != null) {
        // get content if available
        if (blackboard.hasAttachment(id, _contentAttachmentName)) {
          final byte[] content = blackboard.getAttachment(id, _contentAttachmentName);
          mimeType = _mimeTypeIdentifier.identify(content);
        }
      } // if

      // if mimeType is still null try to get mimeType from metadata
      if (mimeType == null && _metaDataAttributeName != null) {
        // get metadata if available
        final Path path = new Path(_metaDataAttributeName);
        if (blackboard.hasAttribute(id, path)) {
          final List<Literal> metaDataList = blackboard.getLiterals(id, path);
          for (Literal metaData : metaDataList) {
            final String metaDataValue = metaData.getStringValue();
            if (metaDataValue != null) {
              final Matcher matcher = _contentTypePattern.matcher(metaDataValue);
              if (matcher.find()) {
                mimeType = matcher.group(1);
                break;
              }
            } // if
          } // for
        } // if
      } // if

      // if mimeType exists set mime type attribute
      if (mimeType != null) {
        final Literal literal = RecordFactory.DEFAULT_INSTANCE.createLiteral();
        literal.setStringValue(mimeType);
        blackboard.setLiteral(id, new Path(_mimeTypeAttributeName), literal);
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("Unable to identify MimeType for Id " + id + ". No values found for "
            + FILE_EXTENSION_ATTRIBUTE);
        }
      } // if
    } // if
  }

  /**
   * Read configuration property file.
   * 
   * @throws ProcessingException
   *           error reading configuration file
   */
  private void readConfiguration() throws ProcessingException {
    InputStream configurationFileStream = null;
    try {
      configurationFileStream = ConfigUtils.getConfigStream(BUNDLE_NAME, CONFIG_FILE);
      final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller();
      _configuration = (PipeletConfiguration) unmarshaller.unmarshal(configurationFileStream);
    } catch (final Exception ex) {
      if (_log.isErrorEnabled()) {
        _log.error("Could not read configuration property file " + CONFIG_FILE, ex);
      }
      throw new ProcessingException("Could not read configuration property file " + CONFIG_FILE, ex);
    } finally {
      IOUtils.closeQuietly(configurationFileStream);
    }
  }

}
