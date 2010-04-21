/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.xml;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * The Class SchemaHelper.
 */
public final class SchemaUtils {

  // /** The Constant SCHEMA_FACTORY. */
  // private static final SchemaFactory SCHEMA_FACTORY =
  // SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

  /**
   * prevents instantiating of schema utils.
   */
  private SchemaUtils() {

  }

  /**
   * Load schema.
   * 
   * @param bundleId
   *          the bundle id
   * @param schemaLocation
   *          the schema location
   * 
   * @return the schema
   * 
   * @throws SAXException
   *           the SAX exception
   */
  public static Schema loadSchema(final String bundleId, final String schemaLocation) throws SAXException {
    final Bundle bundle = Platform.getBundle(bundleId);
    return loadSchema(bundle, schemaLocation);
  }

  /**
   * Load schema runtime ex.
   * 
   * @param bundleId
   *          the bundle id
   * @param schemaLocation
   *          the schema location
   * 
   * @return the schema
   */
  public static Schema loadSchemaRuntimeEx(final String bundleId, final String schemaLocation) {
    final Bundle bundle = Platform.getBundle(bundleId);
    return loadSchemaRuntimeEx(bundle, schemaLocation);
  }

  /**
   * Load schema.
   * 
   * @param resourcePath
   *          the schema location
   * @param bundle
   *          the bundle
   * 
   * @return the schema
   * 
   * @throws SAXException
   *           the SAX exception
   */
  public static Schema loadSchema(final Bundle bundle, final String resourcePath) throws SAXException {
    // TODO: remove it when DS will work ok
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(SchemaUtils.class.getClassLoader());
    final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    final Schema schema = schemaFactory.newSchema(bundle.getEntry(resourcePath));
    Thread.currentThread().setContextClassLoader(oldCL);
    return schema;
  }

  /**
   * Load schema runtime ex.
   * 
   * @param bundle
   *          the bundle
   * @param resourcePath
   *          the resource path
   * 
   * @return the schema
   */
  public static Schema loadSchemaRuntimeEx(final Bundle bundle, final String resourcePath) {
    final Log log = LogFactory.getLog(SchemaUtils.class);
    try {
      return loadSchema(bundle, resourcePath);
    } catch (Throwable e) {
      if (log.isErrorEnabled()) {
        log.error(e);
      }
      throw new RuntimeException(e);
    }
  }

}
