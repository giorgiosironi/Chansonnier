/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.lucene.tools.search.lucene.DNumberFieldParameter;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DNumberTemplateField extends DTemplateField implements Cloneable {

  private DNumberFieldParameter _parameter;

  /**
   * 
   */
  DNumberTemplateField() {
  }

  /**
   * @param fieldNo -
   * @param sourceFieldNo -
   */
  public DNumberTemplateField(int fieldNo, int sourceFieldNo) {
    super(fieldNo, sourceFieldNo);
  }

  public DNumberFieldParameter getParameter() {
    return _parameter;
  }

  public void setParameter(DNumberFieldParameter parameter) {
    this._parameter = parameter;
  }

  @Override
  public Object clone() {
    final DNumberTemplateField obj = (DNumberTemplateField) super.clone();
    if (_parameter != null) {
      obj.setParameter((DNumberFieldParameter) _parameter.clone());
    }
    return obj;
  }

}
