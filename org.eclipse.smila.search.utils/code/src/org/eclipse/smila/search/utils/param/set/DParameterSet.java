/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import java.util.Vector;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class DParameterSet implements Cloneable {

  /**
   * Internal list of parameters.
   */
  private Vector _parameters = new Vector();

  /**
   * Adds a parameter to the internal list.
   * 
   * @param param
   *          the parameter
   */
  public void addParameter(DParameter param) {
    _parameters.addElement(param);
  }

  /**
   * Returns a parameter with the given name or null if none exists.
   * 
   * @param name
   *          the name of the parameter
   * @return a parameter with the given name or null if none exists.
   */
  public DParameter getParameter(String name) {
    for (int i = 0; i < _parameters.size(); i++) {
      final DParameter p = (DParameter) _parameters.elementAt(i);
      if (p.getName().equals(name)) {
        return p;
      }

    }
    return null;
  }

  /**
   * Get all parameters as an array of DParameter.
   * 
   * @return an array of DParameter
   */
  public DParameter[] getParameters() {
    final DParameter[] params = new DParameter[_parameters.size()];
    _parameters.copyInto(params);
    return params;
  }

  /**
   * Checks if this ParameterSet has Parameters set.
   * 
   * @return true if it contains parameters, false otherwise.
   */
  public boolean hasParameters() {
    return !_parameters.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object clone() {
    DParameterSet pset;
    try {
      pset = (DParameterSet) super.clone();
    } catch (final CloneNotSupportedException ex) {
      throw new RuntimeException("unable to clone parameter set");
    }
    pset._parameters = new Vector();
    for (int i = 0; i < _parameters.size(); i++) {
      pset.addParameter((DParameter) ((DParameter) _parameters.elementAt(i)).clone());
    }
    return pset;
  }
}
