/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformer;
import org.eclipse.smila.search.utils.search.parameterobjects.DTransformer;

public abstract class DTemplateField extends DTermContent // implements Cloneable
{
  private int _fieldNo;

  private int _sourceFieldNo;

  private DNodeTransformer _nodeTransformer;

  private DTransformer _transformer;

  protected DTemplateField() {
  }

  protected DTemplateField(int fieldNo, int sourceFieldNo) {
    setFieldNo(fieldNo);
    setSourceFieldNo(sourceFieldNo);
  }

  public int getFieldNo() {
    return _fieldNo;
  }

  void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  public int getSourceFieldNo() {
    return _sourceFieldNo;
  }

  void setSourceFieldNo(int sourceFieldNo) {
    this._sourceFieldNo = sourceFieldNo;
  }

  @Override
  public String getType() {
    return TC_TEMPLATEFIELD;
  }

  /**
   * @return DNodeTransformer
   */
  public DNodeTransformer getNodeTransformer() {
    return _nodeTransformer;
  }

  /**
   * Sets the nodeTransformer.
   * 
   * @param nodeTransformer
   *          The nodeTransformer to set
   */
  public void setNodeTransformer(DNodeTransformer nodeTransformer) {
    this._nodeTransformer = nodeTransformer;
  }

  /**
   * @return DTransformer
   */
  public DTransformer getTransformer() {
    return _transformer;
  }

  /**
   * Sets the transformer.
   * 
   * @param transformer
   *          The transformer to set
   */
  public void setTransformer(DTransformer transformer) {
    this._transformer = transformer;
  }

  @Override
  public Object clone() {
    final DTemplateField tf = (DTemplateField) super.clone();

    if (_nodeTransformer != null) {
      tf.setNodeTransformer((DNodeTransformer) _nodeTransformer.clone());
    }
    if (_transformer != null) {
      tf.setTransformer((DTransformer) _transformer.clone());
    }
    return tf;
  }

}
