/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.jpa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

/**
 * A JPA Entity to store Records.
 */
public class BinaryStorageDao implements Serializable {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 6500268394234442139L;

  /**
   * The string representation of a record Id.
   */
  private String _id;

  /**
   * The serialized components of a record.
   */
  private byte[] _binObject;

  /**
   * Default Constructor, used by JPA.
   */
  protected BinaryStorageDao() {
  }

  /**
   * Conversion Constructor. Converts an id and byte array into a BinaryStorageDao object.
   * 
   * @param id
   *          the id of the binary data
   * @param data
   *          the binary data
   */
  public BinaryStorageDao(final String id, final byte[] data) {
    // check parameters
    if (id == null) {
      throw new IllegalArgumentException("parameter id is null");
    }
    if (id.length() == 0) {
      throw new IllegalArgumentException("parameter id is an empty String");
    }

    _id = id;
    _binObject = data;
  }

  /**
   * Conversion Constructor. Converts an id and InputStream into a BinaryStorageDao object.
   * 
   * @param id
   *          the id of the data
   * @param input
   *          InputStream of the binary data
   * @throws IOException
   *           if any error occurs
   */
  public BinaryStorageDao(final String id, final InputStream input) throws IOException {
    // check parameters
    if (id == null) {
      throw new IllegalArgumentException("parameter id is null");
    }
    if (id.length() == 0) {
      throw new IllegalArgumentException("parameter id is an empty String");
    }
    if (input == null) {
      throw new IllegalArgumentException("parameter input is null");
    }

    _id = id;
    _binObject = IOUtils.toByteArray(input);
  }

  /**
   * Get the id.
   * 
   * @return the id.
   */
  public String getId() {
    return _id;
  }

  /**
   * Get the bytes of the binary object.
   * 
   * @return the bytes
   */
  public byte[] getBytes() {
    return _binObject;
  }

  /**
   * Get the bytes of the binary object as an input stream.
   * 
   * @return the ByteArrayInputStream
   */
  public ByteArrayInputStream getBytesAsStream() {
    return new ByteArrayInputStream(_binObject);
  }

}
