/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record;

import java.util.Date;

/**
 * Interface of literal attribute values. The value object itself can be of one of these classes:
 * <ul>
 * <li>java.lang.String
 * <li>java.lang.Long
 * <li>java.lang.Double
 * <li>java.lang.Boolean
 * <li>java.util.Date and subclasses
 * </ul>
 * 
 * @author jschumacher
 * 
 */
public interface Literal extends AttributeValue {

  /**
   * Enumeration of supported data types.
   */
  enum DataType {
    /**
     * String values_ represented by String objects.
     */
    STRING,
    /**
     * Integer (fixed point) values: represented by Long objects.
     */
    INT,
    /**
     * Floating point values: represented by Double objects.
     */
    FP,
    /**
     * Boolean values: represented by Boolean objects.
     */
    BOOL,
    /**
     * Date values: represented by java.util.Date objects.
     */
    DATE,
    /**
     * Time values: represented by java.util.Date objects.
     */
    TIME,
    /**
     * Date&Time values: represented by java.util.Date objects.
     */
    DATETIME
  };

  /**
   * data type of literal value. See DataType enum in this interface for values.
   * 
   * @return data type object.
   */
  DataType getDataType();

  /**
   * get the value object.
   * 
   * @return the value object.
   */
  Object getValue();

  /**
   * get the value object as a string. If the actual object is instance of string already, it is returned immediately.
   * Else, toString() is used on the object to create a string representation.
   * 
   * @return the string value object.
   */
  String getStringValue(); // return toString() of value, if not a string

  /**
   * get the integer value of the literal if this is an integer literal.
   * 
   * @return the value Long object if this literal is an integer literal, else null
   */
  Long getIntValue();

  /**
   * get the floating point value of the literal if this is a floating point literal.
   * 
   * @return the value Double object if this literal is a floating point literal, else null
   */
  Double getFpValue();

  /**
   * get the boolean value of the literal if this is a boolean literal.
   * 
   * @return the value Boolean object if this literal is a boolean literal, else null
   */
  Boolean getBoolValue();

  /**
   * get the date value of the literal if this is a date literal.
   * 
   * @return the value Date object if this literal is a date literal, else null
   */
  Date getDateValue();

  /**
   * get the time value of the literal if this is a time literal.
   * 
   * @return the value Date object if this literal is a time literal, else null
   */
  Date getTimeValue();

  /**
   * get the datetime value of the literal if this is a datetime literal.
   * 
   * @return the value Date object if this literal is a datetime literal, else null
   */
  Date getDateTimeValue();

  /**
   * set a new value object for this literal. An exception is throws if the object is not an instance of one of the
   * supported classes or cannot be converted to one in a useful way.
   * 
   * @param value
   *          new value object.
   * @throws InvalidTypeException
   *           illegal class of value object.
   */
  void setValue(Object value) throws InvalidTypeException;

  /**
   * set a string value for this literal.
   * 
   * @param value
   *          new string value.
   */
  void setStringValue(String value);

  /**
   * set an integer value for this literal.
   * 
   * @param value
   *          new integer value.
   */
  void setIntValue(Long value);

  /**
   * set an integer value for this literal. just a convenience method that converts to java.lang.Long
   * 
   * @param value
   *          new integer value.
   */
  void setIntValue(Integer value);

  /**
   * set a floating point value for this literal.
   * 
   * @param value
   *          new floating point value.
   */
  void setFpValue(Double value);

  /**
   * set a boolean value for this literal.
   * 
   * @param value
   *          new boolean value
   */
  void setBoolValue(Boolean value);

  /**
   * set a date value for this literal.
   * 
   * @param value
   *          new date value
   */
  void setDateValue(Date value);

  /**
   * set a time value for this literal.
   * 
   * @param value
   *          new date/time/datetime value
   */
  void setTimeValue(Date value);

  /**
   * set a datetime value for this literal.
   * 
   * @param value
   *          new datetime value
   */
  void setDateTimeValue(Date value);

}
