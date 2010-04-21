/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype;

/**
 * Service interface to identify a MimeType.
 */
public interface MimeTypeIdentifier {

  /**
   * Identifies a MimeType based an the given data.
   * 
   * @param data
   *          a byte[] containing the data
   * @return the detected MimeType
   * @throws MimeTypeParseException
   *           if any error occurs
   */
  String identify(byte[] data) throws MimeTypeParseException;

  /**
   * Identifies a MimeType based an the file extension.
   * 
   * @param extension
   *          the extension of the filename
   * @return the detected MimeType
   * @throws MimeTypeParseException
   *           if any error occurs
   */
  String identify(String extension) throws MimeTypeParseException;

  /**
   * Identifies a MimeType based an the given data and file extension.
   * 
   * @param data
   *          a byte[] containing the data
   * @param extension
   *          the extension of the filename
   * @return the detected MimeType
   * @throws MimeTypeParseException
   *           if any error occurs
   */
  String identify(byte[] data, String extension) throws MimeTypeParseException;
}
