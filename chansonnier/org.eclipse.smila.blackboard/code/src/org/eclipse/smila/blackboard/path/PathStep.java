/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.blackboard.path;

import java.io.Serializable;

/**
 * The Class PathStep.
 */
public class PathStep implements Serializable {

  /** The Constant LEFT_BRACKET. */
  public static final char LEFT_BRACKET = '[';

  /** The Constant RIGHT_BRACKET. */
  public static final char RIGHT_BRACKET = ']';

  /** The Constant ATTRIBUTE_ANNOTATION. */
  public static final int ATTRIBUTE_ANNOTATION = -1;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2937181266053506265L;
  
  /** The _name. */
  private String _name;

  /** The _index. */
  private int _index; // index of value in multivalued attributes. default is first value.

  /**
   * Instantiates a new path step.
   * 
   * @param name the name
   */
  public PathStep(String name) {
    _name = name;
  }

  /**
   * Instantiates a new path step.
   * 
   * @param name the name
   * @param index the index
   */
  public PathStep(String name, int index) {
    _name = name;
    _index = index;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return _name;
  }

  /**
   * Gets the index.
   * 
   * @return the index
   */
  public int getIndex() {
    return _index;
  }

  /**
   * Increase the index by 1.
   */
  public void incIndex() {
    _index++;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other instanceof PathStep) {
      final PathStep otherPathStep = (PathStep) other;
      if (!_name.equals(otherPathStep.getName())) {
        return false;
      }
      if (!(_index == otherPathStep.getIndex())) {
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _name.hashCode() + _index;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return _name + LEFT_BRACKET + _index + RIGHT_BRACKET;
  }
}
