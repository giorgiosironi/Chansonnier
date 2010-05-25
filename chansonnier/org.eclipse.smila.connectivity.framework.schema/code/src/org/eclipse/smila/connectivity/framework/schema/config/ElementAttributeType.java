/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * The Enum ElementAttributeType.
 */
@XmlType(name = "ElementAttributeType")
@XmlEnum
public enum ElementAttributeType {

  /**
   * The Path.
   */
  @XmlEnumValue("Path")
  PATH("Path"),
  
  /**
   * The Size.
   */
  @XmlEnumValue("Size")
  SIZE("Size"),
  
  /**
   * The LastModifiedDate.
   */
  @XmlEnumValue("LastModifiedDate")
  LAST_MODIFIED_DATE("LastModifiedDate"),
  
  /**
   * The CONTENT.
   */
  @XmlEnumValue("Content")
  CONTENT("Content"),
  
  /**
   * The file extension.
   */
  @XmlEnumValue("FileExtension")
  FILE_EXTENSION("FileExtension"),

  /**
   * The file name.
   */
  @XmlEnumValue("Name")
  NAME("Name");
  

  /**
   * The value.
   */
  private final String _value;

  /**
   * Instantiates a element attribute type.
   * 
   * @param v
   *          the v
   */
  ElementAttributeType(final String v) {
    _value = v;
  }

  /**
   * Value.
   * 
   * @return the string
   */
  public String value() {
    return _value;
  }

  /**
   * From value.
   * 
   * @param v
   *          the v
   * 
   * @return the element attribute type
   */
  public static ElementAttributeType fromValue(final String v) {
    for (final ElementAttributeType c : ElementAttributeType.values()) {
      if (c._value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  /**
   * @param t
   *          the t
   * 
   * @return the string
   */
  public static String toValue(final ElementAttributeType t) {
    if (t == null) {
      return null;
    }
    return t.value();
  }

}
