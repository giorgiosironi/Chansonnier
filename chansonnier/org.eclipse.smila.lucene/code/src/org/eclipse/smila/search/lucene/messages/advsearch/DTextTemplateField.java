/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DTextTemplateField extends DTemplateField implements Cloneable {

  private DTextFieldParameter _parameter;

  /**
   * 
   */
  DTextTemplateField() {
  }

  /**
   * @param fieldNo -
   * @param sourceFieldNo -
   */
  public DTextTemplateField(int fieldNo, int sourceFieldNo) {
    super(fieldNo, sourceFieldNo);
  }

  public DTextFieldParameter getParameter() {
    return _parameter;
  }

  public void setParameter(DTextFieldParameter parameter) {
    this._parameter = parameter;
  }

  @Override
  public Object clone() {
    final DTextTemplateField obj = (DTextTemplateField) super.clone();
    if (_parameter != null) {
      obj.setParameter((DTextFieldParameter) _parameter.clone());
    }
    return obj;
  }

}
