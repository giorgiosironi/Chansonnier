/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.eclipse.smila.utils.xml.SchemaUtils;

/**
 * The Class ConfigurationLoader.
 */
public final class PipeletConfigurationLoader {

  /** The Constant PIPELET_CONFIGURATION_SCHEMA. */
  public static final Schema PIPELET_CONFIGURATION_SCHEMA =
    SchemaUtils.loadSchemaRuntimeEx("org.eclipse.smila.processing", "schemas/PipeletConfiguration.xsd");

  /**
   * prevents instantiating configuration loader.
   */
  private PipeletConfigurationLoader() {

  }

  /**
   * Creates a validating pipelet configuration unmarshaller.
   * 
   * @return the unmarshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   */
  public static Unmarshaller createPipeletConfigurationUnmarshaller() throws JAXBException {
    return createPipeletConfigurationUnmarshaller(true);
  }

  /**
   * Creates the pipelet configuration unmarshaller.
   * 
   * @param validating
   *          if true create a validating marshaller, else a non-validating marshaller is returned.
   * 
   * @return the unmarshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   */
  public static Unmarshaller createPipeletConfigurationUnmarshaller(final boolean validating) throws JAXBException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(PipeletConfiguration.class.getClassLoader());
    try {
      final JAXBContext context = JAXBContext.newInstance(PipeletConfiguration.class);
      if (validating) {
        return JaxbUtils.createValidatingUnmarshaller(context, PIPELET_CONFIGURATION_SCHEMA);
      } else {
        return context.createUnmarshaller();
      }
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }
}
