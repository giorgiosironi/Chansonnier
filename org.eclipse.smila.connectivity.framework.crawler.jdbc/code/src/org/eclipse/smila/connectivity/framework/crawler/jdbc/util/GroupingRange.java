/***************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc.util;

/**
 * Instances of this class store two {@link PreparedStatementTypedParameter[]} thus incorporating all necessary
 * data for processing groupings as defined in {@link Grouping}.
 * 
 * @author mbreidenband
 * 
 */
public class GroupingRange {
  /** The Start-Values of this particular grouping. */
  private PreparedStatementTypedParameter[] _startValues;

  /** The End-Values of this particular grouping. */
  private PreparedStatementTypedParameter[] _endValues;

  /**
   * Standard constructor assigning the passed parameters to member variables.
   * 
   * @param startValues
   *          The Start-Values of this particular grouping.
   * @param endValues
   *          The End-Values of this particular grouping.
   */
  public GroupingRange(PreparedStatementTypedParameter[] startValues, PreparedStatementTypedParameter[] endValues) {
    _startValues = startValues;
    _endValues = endValues;
  }

  /**
   * Getter for {@link GroupingRange#_startValues.
   * 
   * @return The Start-Values of this particular grouping.
   */
  public PreparedStatementTypedParameter[] getStartValues() {
    return _startValues;
  }

  /**
   * Getter for {@link GroupingRange#_endValues.
   * 
   * @return The End-Values of this particular grouping.
   */
  public PreparedStatementTypedParameter[] getEndValues() {
    return _endValues;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer tempBuffer = new StringBuffer(2048);
    tempBuffer.append("startValues:[");
    for (int i = 0; i < _startValues.length; i++) {
      tempBuffer.append("[" + _startValues[i].toString() + "]");
    }
    tempBuffer.append("]");
    tempBuffer.append("endValues:[");
    for (int i = 0; i < _endValues.length; i++) {
      tempBuffer.append("[" + _endValues[i].toString() + "]");
    }
    tempBuffer.append("]");
    return tempBuffer.toString();

  }

}
