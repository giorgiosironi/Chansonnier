/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search.parameterobjects;

import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DNodeTransformer implements Cloneable {

  private DParameterSet _parameterSet;

  private String _name;

  @Override
  public Object clone() {
    DNodeTransformer nt;
    try {
      nt = (DNodeTransformer) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DNodeTransformer", e);
    }

    if (_parameterSet != null) {
      nt.setParameterSet((DParameterSet) _parameterSet.clone());
    }
    return nt;
  }

  /**
   * @return String
   */
  public String getName() {
    return _name;
  }

  /**
   * @return DParameterSet
   */
  public DParameterSet getParameterSet() {
    return _parameterSet;
  }

  /**
   * Sets the className.
   * 
   * @param className
   *          The className to set
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * Sets the parameterSet.
   * 
   * @param parameterSet
   *          The parameterSet to set
   */
  public void setParameterSet(DParameterSet parameterSet) {
    this._parameterSet = parameterSet;
  }

}
