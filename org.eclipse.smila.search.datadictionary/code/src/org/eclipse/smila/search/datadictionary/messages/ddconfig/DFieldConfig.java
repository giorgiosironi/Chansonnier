/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformer;
import org.eclipse.smila.search.utils.search.parameterobjects.DTransformer;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DFieldConfig {

  Integer weight;

  String fieldTemplate;

  DTransformer transformer;

  DNodeTransformer nodeTransformer;

  String constraint;

  String type;

  /**
   * @return String
   */
  public String getConstraint() {
    return constraint;
  }

  /**
   * @return String
   */
  public String getFieldTemplate() {
    return fieldTemplate;
  }

  /**
   * @return String
   */
  public DNodeTransformer getNodeTransformer() {
    return nodeTransformer;
  }

  /**
   * @return String
   */
  public DTransformer getTransformer() {
    return transformer;
  }

  /**
   * @return String
   */
  public String getType() {
    return type;
  }

  /**
   * @return int
   */
  public Integer getWeight() {
    return weight;
  }

  /**
   * Sets the constraint.
   * 
   * @param constraint
   *          The constraint to set
   */
  public void setConstraint(String constraint) {
    this.constraint = constraint;
  }

  /**
   * Sets the field template.
   * 
   * @param fieldTemplate
   *          The field template to set
   */
  public void setFieldTemplate(String fieldTemplate) {
    this.fieldTemplate = fieldTemplate;
  }

  /**
   * Sets the nodeTransformer.
   * 
   * @param nodeTransformer
   *          The nodeTransformer to set
   */
  public void setNodeTransformer(DNodeTransformer nodeTransformer) {
    this.nodeTransformer = nodeTransformer;
  }

  /**
   * Sets the transformer.
   * 
   * @param transformer
   *          The transformer to set
   */
  public void setTransformer(DTransformer transformer) {
    this.transformer = transformer;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          The type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Sets the weight.
   * 
   * @param weight
   *          The weight to set
   */
  public void setWeight(Integer weight) {
    this.weight = weight;
  }

}
