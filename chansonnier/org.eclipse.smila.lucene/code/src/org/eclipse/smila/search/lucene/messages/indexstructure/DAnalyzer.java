/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.smila.search.utils.param.set.DParameter;
import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author gschmidt
 * 
 */
public class DAnalyzer {

  /**
   * _className.
   */
  private String _className;

  /**
   * _parameterSet.
   */
  private DParameterSet _parameterSet;

  /**
   * Constructor.
   */
  public DAnalyzer() {
  }

  /**
   * 
   * @return String.
   */
  public String getClassName() {
    return _className;
  }

  /**
   * @param className -
   */
  public void setClassName(String className) {
    this._className = className;
  }

  /**
   * @return DParameterSet
   */
  public DParameterSet getParameterSet() {
    return _parameterSet;
  }

  /**
   * @param parameterSet -
   */
  public void setParameterSet(DParameterSet parameterSet) {
    this._parameterSet = parameterSet;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DAnalyzer) {
      return equals((DAnalyzer) obj);
    } else {
      return false;
    }
  }

  /**
   * @param analyzer -
   * @return boolean
   */
  public boolean equals(DAnalyzer analyzer) {

    if (analyzer == null) {
      return false;
    }

    if (!_className.equals(analyzer.getClassName())) {
      return false;
    }

    if ((analyzer.getParameterSet() == null) && (_parameterSet != null)) {
      return false;
    }
    if ((analyzer.getParameterSet() != null) && (_parameterSet == null)) {
      return false;
    }
    if (analyzer.getParameterSet() != null) {
      final Map<String, DParameter> a = getParameters(analyzer.getParameterSet().getParameters());
      final Map<String, DParameter> b = getParameters(_parameterSet.getParameters());

      if (a.size() != b.size()) {
        return false;
      }

      for (final Iterator<String> it = a.keySet().iterator(); it.hasNext();) {
        final String key = it.next();
        if (!b.containsKey(key)) {
          return false;
        }

        final DParameter pA = a.get(key);
        final DParameter pB = b.get(key);
        if (!pA.equals(pB)) {
          return false;
        }
      }
    }

    return true;
  }

  private Map<String, DParameter> getParameters(DParameter[] params) {
    final Map<String, DParameter> parameterMap = new HashMap<String, DParameter>();
    for (final DParameter param : params) {
      parameterMap.put(param.getName(), param);
    }
    return parameterMap;
  }

}
