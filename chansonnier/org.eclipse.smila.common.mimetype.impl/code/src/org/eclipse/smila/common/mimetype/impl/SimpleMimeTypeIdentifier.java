/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH - initial API and implementation, Daniel Stucky (empolis GmbH) -
 * initial API and implementation, Ivan Churkin(brox IT-Solutions GmbH) - simple, based on regular expression, web mime
 * type identification.
 * 
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype.impl;

import java.io.IOException;

import org.eclipse.smila.common.mimetype.MimeTypeIdentifier;
import org.eclipse.smila.common.mimetype.MimeTypeParseException;

/**
 * The simple MIME type identifier is able to detect MIME types based on a static extension mapping.
 * 
 * @author August Georg Schmidt (BROX), Daniel Stucky
 */
public class SimpleMimeTypeIdentifier implements MimeTypeIdentifier {

  /**
   * MIME type mapper.
   */
  private MimeTypeMapper _mimeTypeMapper;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.common.mimetype.MimeTypeIdentifier#identify(byte[])
   */
  public String identify(final byte[] data) throws MimeTypeParseException {
    return identify(data, null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.common.mimetype.MimeTypeIdentifier#identify(java.lang.String)
   */
  public String identify(final String extension) throws MimeTypeParseException {
    return identify(null, extension);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.common.MimeTypeIdentifier#identify(org.eclipse.smila.datamodel.id.Id, byte[],
   *      java.lang.String)
   */
  public String identify(final byte[] data, final String extension) throws MimeTypeParseException {
    // prepare MIME type when not initialized
    if (_mimeTypeMapper == null) {
      try {
        _mimeTypeMapper = new MimeTypeMapper();
      } catch (final IOException exception) {
        throw new MimeTypeParseException("unable to load mime type mappings", exception);
      }
    }

    try {
      if (extension == null) {
        final String msg = "Could not detect mimetype because no extension is specified";
        throw new MimeTypeParseException(msg);
      }

      final String mimetype = _mimeTypeMapper.getContentType(extension);
      if (mimetype == null) {
        final String msg = "Could not detect mimetype";
        throw new MimeTypeParseException(msg);
      }
      return mimetype;
    } catch (final MimeTypeParseException e) {
      throw e;
    } catch (final Exception e) {
      final String msg = "Could not detect mimetype";
      throw new MimeTypeParseException(msg, e);
    }
  }

}
