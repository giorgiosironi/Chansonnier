/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.jaxb;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.xml.SchemaUtils;
import org.xml.sax.SAXException;

/**
 * The Class JaxbHelper.
 */
public final class JaxbUtils {

  /**
   * prevents instantiating of new jaxb utils object.
   */
  private JaxbUtils() {
  }

  /**
   * Creates the validating unmarshaller.
   * 
   * @param context
   *          the context
   * @param schema
   *          the schema
   * 
   * @return the unmarshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   */
  public static Unmarshaller createValidatingUnmarshaller(final JAXBContext context, final Schema schema)
    throws JAXBException {
    if (schema == null) {
      throw new IllegalArgumentException("Schema is not found!");
    }
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    unmarshaller.setSchema(schema);
    unmarshaller.setEventHandler(createValidationEventHandler());
    return unmarshaller;
  }

  /**
   * Creates the validating marshaller.
   * 
   * @param context
   *          the context
   * @param schema
   *          the schema
   * 
   * @return the marshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   */
  public static Marshaller createValidatingMarshaller(final JAXBContext context, final Schema schema)
    throws JAXBException {
    if (schema == null) {
      throw new IllegalArgumentException("Schema is not found!");
    }
    final Marshaller marshaller = context.createMarshaller();
    marshaller.setSchema(schema);
    marshaller.setEventHandler(createValidationEventHandler());
    return marshaller;
  }

  /**
   * Creates the validation event handler.
   * 
   * @return the validation event handler
   */
  public static ValidationEventHandler createValidationEventHandler() {
    return new ValidationEventHandler() {
      public boolean handleEvent(final ValidationEvent ve) {
        final Log log = LogFactory.getLog(JaxbUtils.class);
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

  /**
   * Creates the validating unmarshaller.
   * 
   * @param context
   *          the context
   * @param bundleId
   *          the bundle id
   * @param resourcePath
   *          the resource path
   * 
   * @return the unmarshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public static Unmarshaller createValidatingUnmarshaller(final JAXBContext context, final String bundleId,
    final String resourcePath) throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(bundleId, resourcePath);
    return createValidatingUnmarshaller(context, schema);
  }

  /**
   * Creates the validating marshaller.
   * 
   * @param context
   *          the context
   * @param bundleId
   *          the bundle id
   * @param resourcePath
   *          the resource path
   * 
   * @return the marshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public static Marshaller createValidatingMarshaller(final JAXBContext context, final String bundleId,
    final String resourcePath) throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(bundleId, resourcePath);
    return createValidatingMarshaller(context, schema);
  }

  /**
   * Unmarshall.
   * 
   * @param bundleId
   *          the bundle id
   * @param contextPackage
   *          the context package
   * @param classLoader
   *          the class loader
   * @param schemaLocation
   *          the schema location
   * @param inputStream
   *          the input stream
   * 
   * @return the object
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public static Object unmarshall(final String bundleId, final String contextPackage,
    final ClassLoader classLoader, final String schemaLocation, final InputStream inputStream)
    throws JAXBException, SAXException {
    final Schema schema = SchemaUtils.loadSchema(bundleId, schemaLocation);
    return unmarshall(contextPackage, classLoader, schema, inputStream);
  }

  /**
   * Unmarshall.
   * 
   * @param contextPackage
   *          the context package
   * @param classLoader
   *          the class loader
   * @param schema
   *          the schema
   * @param inputStream
   *          the input stream
   * 
   * @return the object
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  @SuppressWarnings("unchecked")
  public static Object unmarshall(final String contextPackage, final ClassLoader classLoader, final Schema schema,
    final InputStream inputStream) throws JAXBException, SAXException {
    try {
      final JAXBContext context = JAXBContext.newInstance(contextPackage, classLoader);
      final Unmarshaller unmarshaller = JaxbUtils.createValidatingUnmarshaller(context, schema);
      final Object o = unmarshaller.unmarshal(inputStream);
      // guess impossible
      // if (o == null) {
      // return null;
      // }
      if (JAXBElement.class.isAssignableFrom(o.getClass())) {
        return ((JAXBElement) o).getValue();
      }
      return o;
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  /**
   * Marshall.
   * 
   * @param jaxbElement
   *          the jaxb element
   * @param bundleId
   *          the bundle id
   * @param contextPackage
   *          the context package
   * @param classLoader
   *          the class loader
   * @param schemaPath
   *          the schema path
   * @param outputStream
   *          the output stream
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public static void marshall(final Object jaxbElement, final String bundleId, final String contextPackage,
    final ClassLoader classLoader, final String schemaPath, final OutputStream outputStream) throws JAXBException,
    SAXException {
    try {
      final JAXBContext context = JAXBContext.newInstance(contextPackage, classLoader);
      final Marshaller marshaller = JaxbUtils.createValidatingMarshaller(context, bundleId, schemaPath);
      marshaller.marshal(jaxbElement, outputStream);
    } finally {
      IOUtils.closeQuietly(outputStream);
    }
  }

  /**
   * Marshall.
   * 
   * @param jaxbElement
   *          the jaxb element
   * @param contextPackage
   *          the context package
   * @param classLoader
   *          the class loader
   * @param schema
   *          the schema
   * @param outputStream
   *          the output stream
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  public static void marshall(final Object jaxbElement, final String contextPackage, final ClassLoader classLoader,
    final Schema schema, final OutputStream outputStream) throws JAXBException, SAXException {
    try {
      final JAXBContext context = JAXBContext.newInstance(contextPackage, classLoader);
      final Marshaller marshaller = JaxbUtils.createValidatingMarshaller(context, schema);
      marshaller.marshal(jaxbElement, outputStream);
    } finally {
      IOUtils.closeQuietly(outputStream);
    }
  }

}
