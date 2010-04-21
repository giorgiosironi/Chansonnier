/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound.zip;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.smila.connectivity.framework.compound.AbstractCompoundHandler;

/**
 * A CompoundHandler for zip files.
 */
public class ZipCompoundHandler extends AbstractCompoundHandler {

  /**
   * /** The list of supported mime types.
   */
  private ArrayList<String> _supportedMimeTypes;

  /**
   * {@inheritDoc}
   * 
   * @see CompoundHandler#getSupportedMimeTypes()
   */
  public Collection<String> getSupportedMimeTypes() {
    if (_supportedMimeTypes == null) {
      _supportedMimeTypes = new ArrayList<String>();
      _supportedMimeTypes.add("application/zip");
      _supportedMimeTypes.add("application/java-archive");
    }
    return _supportedMimeTypes;
  }
}
