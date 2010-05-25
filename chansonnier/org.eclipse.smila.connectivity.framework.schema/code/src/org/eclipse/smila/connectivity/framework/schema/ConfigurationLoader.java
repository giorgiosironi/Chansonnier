/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfigSimple;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaNotFoundException;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;
import org.eclipse.smila.connectivity.framework.schema.internal.JaxbPluginContext;
import org.eclipse.smila.connectivity.framework.schema.internal.JaxbThreadContext;
import org.eclipse.smila.utils.extensions.AbstractCollectionPluginRegistry;

/**
 * The Class ConfigurationLoader.
 */
public final class ConfigurationLoader {

  /**
   * The Constant REGISTRY.
   */
  private static final ConfigurationRegistry REGISTRY = new ConfigurationRegistry();

  /**
   * Does not instantiates - its a static.
   */
  private ConfigurationLoader() {

  }

  /**
   * New context.
   * 
   * @param bundleID
   *          the bundle id
   * 
   * @return the jAXB context
   * 
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  @SuppressWarnings("unchecked")
  public static JAXBContext newContext(final String bundleID) throws SchemaNotFoundException {
    final DataSourceConnectionConfigPlugin plugIn = getPlugin(bundleID);
    if (plugIn == null) {
      throw new SchemaNotFoundException(bundleID);
    }
    ClassLoader cl = plugIn.getClass().getClassLoader();
    final Class[] allClasses = new Class[2 + 1];
    allClasses[0] = DataSourceConnectionConfig.class;
    try {
      allClasses[1] = cl.loadClass(plugIn.getMessagesPackage() + ".Attribute");
      allClasses[2] = cl.loadClass(plugIn.getMessagesPackage() + ".Process");
    } catch (final ClassNotFoundException e) {
      throw new SchemaRuntimeException(e);
    }
    cl = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(ConfigurationLoader.class.getClassLoader());
      return JAXBContext.newInstance(allClasses);
    } catch (final JAXBException e) {
      throw new SchemaRuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * New simple context.
   * 
   * @return the jAXB context
   */
  private static JAXBContext newSimpleContext() {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(ConfigurationLoader.class.getClassLoader());
      return JAXBContext.newInstance(DataSourceConnectionConfigSimple.class);
    } catch (final JAXBException e) {
      throw new SchemaRuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * Unmarshall index order configuration.
   * 
   * @param is
   *          xml input stream
   * 
   * @return index order configuration
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public static DataSourceConnectionConfig unmarshall(final InputStream is) throws JAXBException, IOException,
    SchemaNotFoundException {
    final Log log = LogFactory.getLog(ConfigurationLoader.class);
    if (is == null) {
      throw new SchemaRuntimeException("Configaration stream is null!");
    }
    InputStream inputStream = null;
    try {
      java.io.ByteArrayOutputStream bos = new ByteArrayOutputStream();
      IOUtils.copy(is, bos);
      final byte[] array = bos.toByteArray();
      bos = null;
      inputStream = new ByteArrayInputStream(array);
      // preinit context
      JaxbThreadContext.setPluginContext(Thread.currentThread(), null);
      // unmarshall first time to locate plug-in
      final JAXBContext jaxbContext = newSimpleContext();
      jaxbContext.createUnmarshaller().unmarshal(inputStream);
      IOUtils.closeQuietly(inputStream);

      final JaxbPluginContext pluginContext = JaxbThreadContext.getPluginContext(Thread.currentThread());
      final Unmarshaller finalUnmarshaller = pluginContext.createValidatingUnmarshaller();
      inputStream = new ByteArrayInputStream(array);
      // unmarshall second time by plug-in
      return (DataSourceConnectionConfig) finalUnmarshaller.unmarshal(inputStream);
    } catch (final SchemaNotFoundException e) {
      if (log.isErrorEnabled()) {
        log.error(e);
      }
      throw e;
    } finally {
      JaxbThreadContext.removeKey(Thread.currentThread());
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(inputStream);
    }

  }

  /**
   * Crate marshaller.
   * 
   * @param configuration
   *          the configuration
   * 
   * @return the marshaller
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public static Marshaller crateMarshaller(final DataSourceConnectionConfig configuration) throws JAXBException,
    SchemaNotFoundException {
    final String schemaID = configuration.getSchemaID();
    final DataSourceConnectionConfigPlugin schema = getPlugin(schemaID);
    if (schema == null) {
      throw new SchemaNotFoundException("Schema " + schemaID + " is not found");
    }
    System.out.println("Schema ID= " + schemaID);
    JaxbThreadContext.setPluginContext(Thread.currentThread(), null);
    final JAXBContext jaxbContext = newContext(schemaID);
    return jaxbContext.createMarshaller();
  }

  /**
   * Marshall.
   * 
   * @param configuration
   *          the configuration
   * @param writer
   *          the writer
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public static void marshall(final DataSourceConnectionConfig configuration, final Writer writer)
    throws JAXBException, SchemaNotFoundException {
    final Marshaller marshaller = crateMarshaller(configuration);
    marshaller.marshal(configuration, writer);
  }

  /**
   * Gets the plugin by id.
   * 
   * @param id
   *          the id
   * 
   * @return the plugin
   */
  public static DataSourceConnectionConfigPlugin getPlugin(final String id) {
    return REGISTRY.getPlugin(id);
  }

  /**
   * The Class CrowlerRegistry.
   */
  private static class ConfigurationRegistry extends AbstractCollectionPluginRegistry<DataSourceConnectionConfigPlugin> {
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractCollectionPluginRegistry#createEmptyArray(int)
     */
    @Override
    protected DataSourceConnectionConfigPlugin[] createEmptyArray(final int size) {
      return new DataSourceConnectionConfigPlugin[size];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointNameSpace()
     */
    @Override
    protected String getExtensionPointNameSpace() {
      return "org.eclipse.smila.connectivity.framework.schema";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.utils.extensions.AbstractPluginRegistryBase#getExtensionPointLocalName()
     */
    @Override
    protected String getExtensionPointLocalName() {
      return "extension";
    }
  }

}
