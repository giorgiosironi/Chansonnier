/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing;

/**
 * Collection of Annotations and named values used for JMS message properties.
 */
public final class JMSMessageAnnotations {

  /**
   * Constant for the annotation 'MessageProperties'.
   */
  public static final String ANNOTATION_MESSAGE_PROPERTIES = "MessageProperties";

  /**
   * Name of 'Operation' property.
   */
  public static final String PROPERTY_OPERATION = "Operation";

  /**
   * Name of 'DataSourceID' property.
   */
  public static final String PROPERTY_SOURCE = "DataSourceID";

  /**
   * Name of 'isXmlSnippet' property.
   */
  public static final String PROPERTY_IS_XML_SNIPPET = "isXmlSnippet";

  /**
   * Private constructor to avoid instance creation.
   */
  private JMSMessageAnnotations() {
  }
}
