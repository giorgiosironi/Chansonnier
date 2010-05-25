/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.enginedata;

/**
 * @author gschmidt
 * 
 */
public class DSDK {
  /**
   * _mStrVersion.
   */
  private String _mStrVersion;

  /**
   * Constructor.
   */
  public DSDK() {
  }

  /**
   * @return String
   */
  public String getVersion() {
    return _mStrVersion;
  }

  /**
   * @param version -
   */
  public void setVersion(String version) {
    _mStrVersion = version;
  }

}
