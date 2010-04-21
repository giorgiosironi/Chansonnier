/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.smila.connectivity.framework.schema.internal.JaxbThreadContext;

/**
 * The Class SchemaIdAdapter.
 */
public class SchemaIdAdapter extends XmlAdapter<String, String> {

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
   */
  @Override
  public String unmarshal(final String arg0) throws Exception {
    JaxbThreadContext.setPluginContext(Thread.currentThread(), arg0);
    return arg0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
   */
  @Override
  public String marshal(final String arg0) throws Exception {
    JaxbThreadContext.setPluginContext(Thread.currentThread(), arg0);
    return arg0;
  }

}
