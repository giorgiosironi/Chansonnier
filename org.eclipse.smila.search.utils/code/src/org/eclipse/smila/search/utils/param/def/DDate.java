/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

import java.util.Date;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDate extends DParameter {

  private Date _value;

  /**
   * @return Date
   */
  public Date getDefault() {
    return _value;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void setDefault(Date value) {
    _defaultExists = true;
    this._value = value;
  }

}
