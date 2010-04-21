/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.osgi;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.eclipse.smila.management.jmx.client.config.JmxClientConfigType;
import org.eclipse.smila.management.jmx.client.exceptions.ConfigurationLoadException;
import org.eclipse.smila.management.jmx.client.helpers.ConfigLoader;
import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.xml.SchemaUtils;

/**
 * The Class ConfigLoader.
 */
public final class OSGiConfigLoader {

  /**
   * Prevents instantiating config loader.
   */
  private OSGiConfigLoader() {

  }

  /**
   * Load.
   *
   * @param is
   *          the is
   *
   * @return the cmd config type
   *
   * @throws ConfigurationLoadException
   *           the configuration load exception
   */
  @SuppressWarnings("unchecked")
  public static JmxClientConfigType load(final InputStream is) throws ConfigurationLoadException {
    final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(OSGiConfigLoader.class.getClassLoader());
    try {
      final JAXBContext context = JAXBContext.newInstance("org.eclipse.smila.management.jmx.client.config");
      final Schema schema =
        SchemaUtils.loadSchemaRuntimeEx(SMILACommandProvider.BUNDLE_ID, "schemas/jmxclient.xsd");
      final Unmarshaller unmarshaller = JaxbUtils.createValidatingUnmarshaller(context, schema);
      unmarshaller.setEventHandler(ConfigLoader.createValidationEventHandler());
      Object result = unmarshaller.unmarshal(is);
      if (result != null) {
        if (result instanceof JAXBElement) {
          result = ((JAXBElement) result).getValue();
        }
      }
      return (JmxClientConfigType) result;
    } catch (final Throwable e) {
      throw new ConfigurationLoadException("Unable to load configuration", e);
    } finally {
      Thread.currentThread().setContextClassLoader(tccl);
      if (is != null) {
        try {
          is.close();
        } catch (final Throwable e) {
          ;// nothing
        }
      }
    }
  }

}
