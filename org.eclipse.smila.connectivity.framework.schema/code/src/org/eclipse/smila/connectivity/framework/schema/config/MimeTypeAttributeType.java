/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * The Enum MimeTypeAttributeType.
 */
@XmlType(name = "MimeTypeAttributeType")
@XmlEnum
public enum MimeTypeAttributeType {

  /**
   * The file extension.
   */
  @XmlEnumValue("FileExtension")
  FILE_EXTENSION("FileExtension"),

  /**
   * The CONTENT.
   */
  @XmlEnumValue("Content")
  CONTENT("Content"),

  /**
   * The MIME type.
   */
  @XmlEnumValue("MimeType")
  MIME_TYPE("MimeType");

  /**
   * The value.
   */
  private final String _value;

  /**
   * Instantiates a new mime type attribute type.
   * 
   * @param v
   *          the v
   */
  MimeTypeAttributeType(final String v) {
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
   * @return the mime type attribute type
   */
  public static MimeTypeAttributeType fromValue(final String v) {
    for (final MimeTypeAttributeType c : MimeTypeAttributeType.values()) {
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
  public static String toValue(final MimeTypeAttributeType t) {
    if (t == null) {
      return null;
    }
    return t.value();
  }

}
