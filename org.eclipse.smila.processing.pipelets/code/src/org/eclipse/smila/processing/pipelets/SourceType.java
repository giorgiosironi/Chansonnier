/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets;

/**
 * The SourceType is a utility to allow a Pipelet to select if a value is to be read from a record Attribute or
 * Attachment.
 */
public enum SourceType {
  /**
   * Source type ATTRIBUTE.
   */
  ATTRIBUTE,
  /**
   * SourceType ATTACHMENT.
   */
  ATTACHMENT
}
