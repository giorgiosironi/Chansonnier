/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.internal;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaNotFoundException;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;

/**
 * The Class JaxbPluginContext.
 */
public class JaxbPluginContext {

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(JaxbPluginContext.class);

  /**
   * The _id.
   */
  private String _id;

  /**
   * The _context.
   */
  private JAXBContext _context;

  /**
   * The _plug in.
   */
  private DataSourceConnectionConfigPlugin _plugIn;

  /**
   * The _initialized.
   */
  private boolean _initialized;

  /**
   * The _binder.
   */
  @SuppressWarnings("unchecked")
  private Binder _binder;

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public String getId() {
    return _id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   *          the new id
   */
  public void setId(final String id) {
    _id = id;
  }

  /**
   * Gets the context.
   * 
   * @return the context
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public JAXBContext getContext() throws SchemaNotFoundException, JAXBException {
    initilize();
    return _context;
  }

  /**
   * Initilize.
   * 
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   * @throws JAXBException
   *           the JAXB exception
   */
  public void initilize() throws SchemaNotFoundException, JAXBException {
    if (!_initialized) {
      _initialized = true;
      _plugIn = ConfigurationLoader.getPlugin(_id);
      if (_plugIn == null) {
        throw new SchemaNotFoundException(_id);
      }
      _context = ConfigurationLoader.newContext(_id);
      if (_context == null) {
        throw new SchemaNotFoundException(_id);
      }
      _binder = _context.createBinder();
    }
  }

  /**
   * Load class.
   * 
   * @param className
   *          the clazz
   * 
   * @return the class
   * 
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  @SuppressWarnings("unchecked")
  private Class loadClassByName(final String className) throws ClassNotFoundException {
    return _plugIn.getClass().getClassLoader().loadClass(_plugIn.getMessagesPackage() + "." + className);
  }

  /**
   * Unmarshall.
   * 
   * @param v
   *          the v
   * @param className
   *          the clazz
   * 
   * @return the jAXB element
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws ClassNotFoundException
   *           the class not found exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  @SuppressWarnings("unchecked")
  public JAXBElement unmarshall(final Object v, final String className) throws JAXBException,
    ClassNotFoundException, SchemaNotFoundException {
    initilize();
    assertNotNull(_binder);
    return _binder.unmarshal(v, loadClassByName(className));
  }

  /**
   * Creates the validating unmarshaller.
   * 
   * @return the unmarshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public Unmarshaller createValidatingUnmarshaller() throws JAXBException, SchemaNotFoundException {
    initilize();
    assertNotNull(_context);
    final Unmarshaller unmarshaller = _context.createUnmarshaller();
    final SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    try {
      sf.setResourceResolver(new XSDContextURIResolver(sf.getResourceResolver()));
      final javax.xml.validation.Schema schema =
        sf.newSchema(Platform.getBundle(_id).getEntry(_plugIn.getSchemaLocation()));
      unmarshaller.setSchema(schema);
      unmarshaller.setEventHandler(new ValidationEventHandler() {
        public boolean handleEvent(final ValidationEvent ve) {
          if (ve.getSeverity() != ValidationEvent.WARNING) {
            final ValidationEventLocator vel = ve.getLocator();
            _log.error("Line:Col[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
            return false;
          }
          return true;
        }
      });
    } catch (final org.xml.sax.SAXException se) {
      throw new SchemaRuntimeException("Unable to validate due to following error.", se);
    }
    return unmarshaller;
  }

  /**
   * Assert not null.
   * 
   * @param o
   *          the o
   * 
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  private void assertNotNull(final Object o) throws SchemaNotFoundException {
    if (o == null) {
      throw new SchemaNotFoundException(_id);
    }
  }

}
