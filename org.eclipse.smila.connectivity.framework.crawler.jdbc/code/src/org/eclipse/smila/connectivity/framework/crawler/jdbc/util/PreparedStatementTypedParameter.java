/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Helper class to store a value for a parameter in a {@link PreparedStatement} along with the index of the
 * parameter within the statement und the SQL-Type of the parameter.
 * 
 * @author mbreidenband
 * 
 */
public class PreparedStatementTypedParameter {

  /** The value of the parameter. */
  private Object _parameterValue;

  /** The index of the parameter within the PreparedStatement object. */
  private int _parameterIndex;

  /** The type of the _parameterValue object as defined in {@link java.sql.Types}. */
  private int _parameterType;

  /**
   * Standard constructor assigning the passed parameters to member variables.
   * 
   * @param value
   *          The value of the parameter.
   * @param parameterIndex
   *          The index of the parameter within the PreparedStatement object.
   * @param parameterType
   *          The type of the _parameterValue object as defined in {@link java.sql.Types}.
   */
  public PreparedStatementTypedParameter(Object value, int parameterIndex, int parameterType) {
    _parameterValue = value;
    _parameterIndex = parameterIndex;
    _parameterType = parameterType;
  }

  /**
   * Calling this method will apply the parameter that this instance of {@link PreparedStatementTypedParameter}
   * incorporates to the passed {@link PreparedStatement}-object.
   * 
   * @param statement
   *          The {@link PreparedStatement} to which this parameter shall be applied.
   * @throws SQLException
   *           If JDBC fails to agree with something or other.
   */
  public void applyToPreparedStatement(PreparedStatement statement) throws SQLException {
    statement.setObject(_parameterIndex, _parameterValue, _parameterType);

  }

  /**
   * {@inheritDoc}
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("indexInStatement: %d, value %s", _parameterIndex, String.valueOf(_parameterValue));
  }

}
