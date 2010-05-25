/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments
 */
public class DHighlightingTransformer implements Cloneable {

  private DParameterSet parameterSet;

  private String name;

  @Override
  public Object clone() {
    DHighlightingTransformer t;
    try {
      t = (DHighlightingTransformer) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DHighlightingTransformer", e);
    }

    if (parameterSet != null) {
      t.setParameterSet((DParameterSet) parameterSet.clone());
    }
    return t;
  }

  /**
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * @return DParameterSet
   */
  public DParameterSet getParameterSet() {
    return parameterSet;
  }

  /**
   * Sets the className.
   * 
   * @param className
   *          The className to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the parameterSet.
   * 
   * @param parameterSet
   *          The parameterSet to set
   */
  public void setParameterSet(DParameterSet parameterSet) {
    this.parameterSet = parameterSet;
  }
}
