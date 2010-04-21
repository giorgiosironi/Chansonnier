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
import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * String format of attribute path is "attributeName1@index1/attributeName2@index2/...". the specification of index is
 * optional and defaults to 0. if the index refers to a literal or a sub-object depends on methods getting the argument
 */
public class Path implements Serializable, Iterable<PathStep> {

  /** The Constant SEPARATOR. */
  public static final char SEPARATOR = '/';

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4628822718743464377L;

  /** The path stack. */
  private Stack<PathStep> _pathStack = new Stack<PathStep>();

  /**
   * Instantiates a new path.
   */
  public Path() {

  }

  /**
   * Instantiates a new path by a given Path object.
   * 
   * @param path
   *          the path
   */
  public Path(Path path) {
    for (PathStep pathStep : path) {
      _pathStack.add(pathStep);
    }
  }

  /**
   * Instantiates a new path by parsing the attribute path string.
   * 
   * @param path
   *          the path
   */
  public Path(String path) {
    for (String pathStepString : StringUtils.split(path, SEPARATOR)) {
      final String[] pathStepParts = StringUtils.split(pathStepString, PathStep.LEFT_BRACKET);
      int index = 0;
      if ((pathStepParts.length > 1) && (pathStepParts[1].indexOf(PathStep.RIGHT_BRACKET) > 0)) {
        index = Integer.valueOf(pathStepParts[1].substring(0, pathStepParts[1].length() - 1));
      }
      _pathStack.add(new PathStep(pathStepParts[0], index));
    }
  }

  /**
   * Extends path with the given PathStep.
   * 
   * @param step
   *          the step
   * 
   * @return the path
   */
  public Path add(PathStep step) {
    _pathStack.add(step);
    return this;
  }

  /**
   * Creates PathStep and extends path with it.
   * 
   * @param attributeName
   *          the attribute name
   * 
   * @return the path
   */
  public Path add(String attributeName) {
    final PathStep pathStep = new PathStep(attributeName);
    return add(pathStep);
  }

  /**
   * Creates PathStep and extends path with it.
   * 
   * @param attributeName
   *          the attribute name
   * @param index
   *          the index
   * 
   * @return the path
   */
  public Path add(String attributeName, int index) {
    final PathStep pathStep = new PathStep(attributeName, index);
    return add(pathStep);
  }

  /**
   * Remove tail element of this. This modifies the object itself and returns it again for further modifications, e.g.
   * path.up().add("siblingAttribute");
   * 
   * @return the path
   */
  public Path up() {
    _pathStack.pop();
    return this;
  }

  /**
   * Increase index of tail step by 1.
   * 
   * @return the path
   */
  public Path incIndex() {
    _pathStack.peek().incIndex();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public Iterator<PathStep> iterator() {
    return _pathStack.iterator();
  }

  /**
   * Checks if the Path is empty.
   * 
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return _pathStack.isEmpty();
  }

  /**
   * Gets the PathStep at the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the string
   */
  public PathStep get(int index) {
    return _pathStack.get(index);
  }

  /**
   * Gets the PathStep name at the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the string
   */
  public String getName(int index) {
    return _pathStack.get(index).getName();
  }

  /**
   * Gets the PathStep index at the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the string
   */
  public int getIndex(int index) {
    return _pathStack.get(index).getIndex();
  }

  /**
   * Length of the Path.
   * 
   * @return the int
   */
  public int length() {
    return _pathStack.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other instanceof Path) {
      final Path otherPath = (Path) other;
      final Iterator<PathStep> pathIterator1 = this.iterator();
      final Iterator<PathStep> pathIterator2 = otherPath.iterator();
      while (pathIterator1.hasNext() && pathIterator2.hasNext()) {
        final PathStep pathStep1 = pathIterator1.next();
        final PathStep pathStep2 = pathIterator2.next();
        if (!(pathStep1 == null ? pathStep2 == null : pathStep1.equals(pathStep2))) {
          return false;
        }
      }
      return !(pathIterator1.hasNext() || pathIterator2.hasNext());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _pathStack.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder str = new StringBuilder();
    for (PathStep pathStep : _pathStack) {
      str.append(pathStep.toString());
      str.append(SEPARATOR);
    }
    return str.toString();
  }

}
