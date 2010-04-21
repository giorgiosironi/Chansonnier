/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.ArrayList;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DFieldConstraints {

  private int _fieldNo;

  private String _occurrence;

  private final ArrayList<String> _fieldTemplates = new ArrayList<String>();

  private final ArrayList<String> _nodeTransformers = new ArrayList<String>();

  private final ArrayList<String> _constraints = new ArrayList<String>();

  public void addConstraint(String constraint) {
    if (constraint != null) {
      constraint = constraint.trim();
    } else {
      constraint = "";
    }
    this._constraints.add(constraint);
  }

  public void addFieldTemplate(String template) {
    if (template != null) {
      template = template.trim();
    } else {
      template = "";
    }
    this._fieldTemplates.add(template);
  }

  public void addNodeTransformer(String nodeTransformer) {
    if (nodeTransformer != null) {
      nodeTransformer = nodeTransformer.trim();
    } else {
      nodeTransformer = "";
    }
    this._nodeTransformers.add(nodeTransformer);
  }

  public String getConstraint(int index) {
    return _constraints.get(index);
  }

  public int getConstraintCount() {
    return _constraints.size();
  }

  public String[] getConstraints() {
    final String[] constraints = _constraints.toArray(new String[0]);
    return constraints;
  }

  /**
   * @return String
   */
  public int getFieldNo() {
    return _fieldNo;
  }

  public String getFieldTemplate(int index) {
    return _fieldTemplates.get(index);
  }

  public int getFieldTemplateCount() {
    return _fieldTemplates.size();
  }

  public String[] getFieldTemplates() {
    final String[] templates = _fieldTemplates.toArray(new String[0]);
    return templates;
  }

  public String getNodeTransformer(int index) {
    return _nodeTransformers.get(index);
  }

  public int getNodeTransformerCount() {
    return _nodeTransformers.size();
  }

  public String[] getNodeTransformers() {
    final String[] nodeTransformers = _nodeTransformers.toArray(new String[0]);
    return nodeTransformers;
  }

  /**
   * @return String
   */
  public String getOccurrence() {
    return _occurrence;
  }

  public void removeConstraint(int index) {
    this._constraints.remove(index);
  }

  public void removeFieldTemplate(int index) {
    this._fieldTemplates.remove(index);
  }

  public void removeNodeTransformer(int index) {
    this._nodeTransformers.remove(index);
  }

  public void setConstraints(String[] constraints) {
    this._constraints.clear();
    for (int i = 0; i < constraints.length; i++) {
      addConstraint(constraints[i]);
    }
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          The name to set
   */
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  public void setFieldTemplates(String[] templates) {
    this._fieldTemplates.clear();
    for (int i = 0; i < templates.length; i++) {
      addFieldTemplate(templates[i]);
    }
  }

  public void setNodeTransformers(String[] nodeTransformers) {
    this._nodeTransformers.clear();
    for (int i = 0; i < nodeTransformers.length; i++) {
      addNodeTransformer(nodeTransformers[i]);
    }
  }

  /**
   * Sets the occurrence.
   * 
   * @param occurrence
   *          The occurrence to set
   */
  public void setOccurrence(String occurrence) {
    this._occurrence = occurrence;
  }
}
