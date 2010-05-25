/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.internal;

import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * The Class XSDContextURIResolver.
 */
public class XSDContextURIResolver implements LSResourceResolver {

  /**
   * The _base resolver.
   */
  private final LSResourceResolver _baseResolver;

  /**
   * Instantiates a new xSD context uri resolver.
   * 
   * @param baseResolver
   *          the base resolver
   */
  public XSDContextURIResolver(final LSResourceResolver baseResolver) {
    if (baseResolver == null) {
      _baseResolver = new XMLCatalogResolver();
    } else {
      _baseResolver = baseResolver;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public LSInput resolveResource(final String type, final String namespaceURI, final String publicId,
    final String systemId, final String baseURI) {
    // type=http://www.w3.org/2001/XMLSchema
    // namespaceURI=null
    // publicId=null
    // systemId=../../org.eclipse.smila.connectivity.framework.schema/schemas/RootDataSourceConnectionConfigSchema.xsd
    // baseURI=bundleentry://406/schemas/FileSystemDataSourceConnectionConfigSchema.xsd
    if (systemId != null && systemId.toLowerCase().endsWith("rootdatasourceconnectionconfigschema.xsd")) {
      final LSInput input = new org.apache.xerces.dom.DOMInputImpl();
      input.setPublicId(publicId);
      input.setSystemId(systemId);
      input.setBaseURI(baseURI);
      input.setByteStream(JaxbPluginContext.class.getClassLoader().getResourceAsStream(
        "/schemas/RootDataSourceConnectionConfigSchema.xsd"));
      return input;
    } else {
      return _baseResolver.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
    }
  }
}
