/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.internal;

import javax.xml.validation.Schema;

import org.eclipse.smila.utils.xml.SchemaUtils;
import org.xml.sax.SAXException;

/**
 * The Class BinaryStorageSchemaProvider.
 */
public final class BinaryStorageSchemaProvider {

  /** The Constant SCHEMA. */
  public static final Schema CONFIGURATION_SCHEMA;

  /** The Constant BUNDLE_ID. */
  private static final String BUNDLE_ID = "org.eclipse.smila.binarystorage.impl";

  /** The Constant SCHEMA_LOCATION. */
  private static final String SCHEMA_LOCATION = "schemas/BinaryStorageConfiguration.xsd";

  /** Static init. **/
  static {
    try {
      CONFIGURATION_SCHEMA = SchemaUtils.loadSchema(BUNDLE_ID, SCHEMA_LOCATION);
    } catch (final SAXException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Instantiates a new binary storage schema provider.
   */
  private BinaryStorageSchemaProvider() {
  }

}
