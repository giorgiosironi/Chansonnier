/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.helpers;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.jmx.client.Main;
import org.eclipse.smila.management.jmx.client.config.JmxClientConfigType;
import org.eclipse.smila.management.jmx.client.exceptions.ConfigurationLoadException;

/**
 * The Class ConfigLoader.
 */
public final class ConfigLoader {

  /**
   * Prevents instantiating config loader.
   */
  private ConfigLoader() {

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
    try {
      final JAXBContext context = JAXBContext.newInstance("org.eclipse.smila.management.jmx.client.config");
      final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
      final Schema schema = schemaFactory.newSchema(new File("schemas/jmxclient.xsd"));
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      unmarshaller.setSchema(schema);
      unmarshaller.setEventHandler(createValidationEventHandler());
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
      if (is != null) {
        try {
          is.close();
        } catch (final Throwable e) {
          ;// nothing
        }
      }
    }
  }

  /**
   * Creates the validation event handler.
   * 
   * @return the validation event handler
   */
  public static ValidationEventHandler createValidationEventHandler() {
    return new ValidationEventHandler() {
      public boolean handleEvent(final ValidationEvent ve) {
        final Log log = LogFactory.getLog(Main.class);
        if (ve.getSeverity() != ValidationEvent.WARNING) {
          final ValidationEventLocator vel = ve.getLocator();
          if (log.isErrorEnabled()) {
            log.error("Line:Col[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
          }
          return false;
        }
        return true;
      }
    };
  }

}
