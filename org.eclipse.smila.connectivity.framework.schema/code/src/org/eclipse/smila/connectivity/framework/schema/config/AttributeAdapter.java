/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;
import org.eclipse.smila.connectivity.framework.schema.internal.JaxbPluginContext;
import org.eclipse.smila.connectivity.framework.schema.internal.JaxbThreadContext;

/**
 * The Class AttributeAdapter.
 */
public class AttributeAdapter extends XmlAdapter<Object, IAttribute> {

  /** The Constant LOG. */
  private final Log _log = LogFactory.getLog(AttributeAdapter.class);

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public IAttribute unmarshal(final Object v) {
    try {
      final JaxbPluginContext pluginContext = JaxbThreadContext.getPluginContext(Thread.currentThread());
      final JAXBElement jElement = pluginContext.unmarshall(v, "Attribute");
      return (IAttribute) jElement.getValue();
    } catch (final Throwable e) {
      _log.error(e);
      throw new SchemaRuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
   */
  @Override
  public Object marshal(final IAttribute v) {
    return v;
  }

}
