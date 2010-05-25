/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

import org.eclipse.smila.search.utils.search.parameterobjects.DNodeTransformer;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public class DField implements Cloneable {

  /**
   * Field no.
   */
  private int _fieldNo;

  /**
   * Weight.
   */
  private Integer _weight;

  /**
   * Node transformer.
   */
  private DNodeTransformer _nodeTransformer;

  /**
   * Field template name.
   */
  private String _fieldTemplate;

  /**
   * Parameter descriptor.
   */
  private String _parameterDescriptor;

  /**
   * Constraint.
   */
  private String _constraint;

  /**
   * Type.
   */
  private String _type;

  /**
   * 
   */
  public DField() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    DField obj = null;
    try {
      obj = (DField) super.clone();
    } catch (final CloneNotSupportedException ex) {
      throw new RuntimeException("unable to clone field");
    }

    if (_weight != null) {
      obj.setWeight(new Integer(_weight.intValue()));
    }

    if (_nodeTransformer != null) {
      obj.setNodeTransformer((DNodeTransformer) _nodeTransformer.clone());
    }
    return obj;
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  public String getConstraint() {
    return _constraint;
  }

  public int getFieldNo() {
    return _fieldNo;
  }

  public String getFieldTemplate() {
    return _fieldTemplate;
  }

  public DNodeTransformer getNodeTransformer() {
    return _nodeTransformer;
  }

  public String getParameterDescriptor() {
    return _parameterDescriptor;
  }

  /**
   * @return String
   */
  public String getType() {
    return _type;
  }

  public Integer getWeight() {
    return _weight;
  }

  public void setConstraint(String constraint) {
    this._constraint = constraint;
  }

  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  public void setFieldTemplate(String fieldTemplate) {
    this._fieldTemplate = fieldTemplate;
  }

  public void setNodeTransformer(DNodeTransformer nodeTransformer) {
    this._nodeTransformer = nodeTransformer;
  }

  public void setParameterDescriptor(String parameterDescriptor) {
    this._parameterDescriptor = parameterDescriptor;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          The type to set
   */
  public void setType(String type) {
    this._type = type;
  }

  public void setWeight(Integer weight) {
    this._weight = weight;
  }

  @Override
  public String toString() {
    try {
      final Element el = DFieldCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

}
