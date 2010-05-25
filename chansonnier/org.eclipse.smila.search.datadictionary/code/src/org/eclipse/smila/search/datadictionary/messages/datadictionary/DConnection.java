/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2001 Company: BROX IT-Solutions GmbH
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.0
 */

public abstract class DConnection {

  private int maxConnections = 1;

  public DConnection() {
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

} // End class def.
