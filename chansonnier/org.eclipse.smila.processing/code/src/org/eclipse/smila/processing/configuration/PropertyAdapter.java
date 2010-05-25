/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.processing.configuration.PipeletConfiguration.Property;
import org.eclipse.smila.processing.configuration.PipeletConfiguration.PropertySource;
import org.eclipse.smila.utils.conversion.DefaultConversionUtils;

/**
 * The Class PropertyAdapter.
 */
class PropertyAdapter extends XmlAdapter<PipeletConfiguration.PropertySource, Property> {

  /** The Constant LOG. */
  private final Log _log = LogFactory.getLog(PropertyAdapter.class);

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
   */
  @Override
  public PropertySource marshal(final Property v) throws Exception {
    // TODO
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
   */
  @Override
  public Property unmarshal(final PropertySource v) throws Exception {
    final List<Object> values = new ArrayList<Object>();
    if (v.getType() != null && !"".equals(v.getType()) && !"java.lang.String".equals(v.getType())) {
      if (v.getValues() != null) {
        try {
          final Class<?> clazz = Class.forName(v.getType());
          for (final String value : v.getValues()) {            
            values.add(DefaultConversionUtils.convert(value, clazz));
          }
          // System.out.println("Clazz=" + clazz.getName() + " Value class=" + value.getClass().getName());
        } catch (final Throwable e) {
          _log.error(e);
          throw new RuntimeException(e);
        }
      }
    } else {
      values.addAll(v.getValues());
    }
    return new Property(v.getName(), values);
  }
}
