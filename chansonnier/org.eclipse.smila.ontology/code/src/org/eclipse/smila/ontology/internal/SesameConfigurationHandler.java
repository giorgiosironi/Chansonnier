/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.internal;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.config.SesameConfiguration;
import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.xml.SchemaUtils;

/**
 * Utility class for reading and writing of configurations.
 *
 * @author jschumacher
 *
 */
public final class SesameConfigurationHandler {
  /**
   * prevent instance creation.
   */
  private SesameConfigurationHandler() {
  }

  /**
   * read configuration from stream, including validation.
   *
   * @param configStream
   *          stream containing SesameConfiguration
   * @return parsed configuration
   * @throws JAXBException
   *           parse or validation error
   */
  public static SesameConfiguration readConfiguration(final InputStream configStream) throws JAXBException {
    final Unmarshaller parser = SesameConfigurationHandler.createConfigurationUnmarshaller(true);
    return (SesameConfiguration) parser.unmarshal(configStream);
  }

  /**
   * read configuration from stream, including validation.
   *
   * @param configuration
   *          SesameConfiguration to write
   * @param configStream
   *          target stream
   * @throws JAXBException
   *           parse or validation error
   */
  public static void writeConfiguration(final SesameConfiguration configuration, final OutputStream configStream)
    throws JAXBException {
    final Marshaller writer = SesameConfigurationHandler.createConfigurationMarshaller(true);
    writer.marshal(configuration, configStream);
  }

  /**
   * create parser for SesameConfiguration.
   *
   * @param validating
   *          true to use a validating parser
   * @return parser
   * @throws JAXBException
   *           error creating parser.
   */
  public static Unmarshaller createConfigurationUnmarshaller(final boolean validating) throws JAXBException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(SesameConfiguration.class.getClassLoader());
    try {
      final JAXBContext context = JAXBContext.newInstance(SesameConfiguration.class);
      if (validating) {
        final Schema schema =
          SchemaUtils.loadSchemaRuntimeEx(SesameOntologyManager.BUNDLE_ID, "schemas/sesameConfig.xsd");
        return JaxbUtils.createValidatingUnmarshaller(context, schema);
      } else {
        return context.createUnmarshaller();
      }
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * create writer for SesameConfiguration.
   *
   * @param validating
   *          true to use a validating parser
   * @return writer
   * @throws JAXBException
   *           error creating parser.
   */
  public static Marshaller createConfigurationMarshaller(final boolean validating) throws JAXBException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(SesameConfiguration.class.getClassLoader());
    try {
      final JAXBContext context = JAXBContext.newInstance(SesameConfiguration.class);
      if (validating) {
        final Schema schema =
          SchemaUtils.loadSchemaRuntimeEx(SesameOntologyManager.BUNDLE_ID, "schemas/sesameConfig.xsd");
        return JaxbUtils.createValidatingMarshaller(context, schema);
      } else {
        return context.createMarshaller();
      }
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }
}
