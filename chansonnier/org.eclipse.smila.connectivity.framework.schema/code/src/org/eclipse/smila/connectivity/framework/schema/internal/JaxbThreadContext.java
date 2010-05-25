/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH) 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaNotFoundException;

/**
 * The Class JaxbThreadContext.
 */
public final class JaxbThreadContext {

  /** The _ids. */
  private static Map<Long, JaxbPluginContext> s_contexts = new HashMap<Long, JaxbPluginContext>();

  /**
   * Does not instantiates a new jaxb thread context.
   */
  private JaxbThreadContext() {
  }

  /**
   * Sets the id.
   * 
   * @param thread
   *          the thread
   * @param id
   *          the id
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SchemaNotFoundException
   *           the index order schema not found exception
   */
  public static void setPluginContext(final Thread thread, final String id) throws JAXBException,
    SchemaNotFoundException {
    JaxbPluginContext context = null;
    context = s_contexts.get(thread.getId());
    if (context == null) {
      context = new JaxbPluginContext();
    }
    context.setId(id);
    s_contexts.put(thread.getId(), context);
  }

  /**
   * Gets the id.
   * 
   * @param thread
   *          the thread
   * 
   * @return the id
   */
  public static JaxbPluginContext getPluginContext(final Thread thread) {
    return s_contexts.get(thread.getId());
  }

  /**
   * Removes the key.
   * 
   * @param thread
   *          the thread
   */
  public static void removeKey(final Thread thread) {
    s_contexts.remove(thread.getId());
  }

}
