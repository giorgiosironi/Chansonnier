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
 * The Enum DeltaIndexingType.
 */
@XmlType(name = "DeltaIndexingType")
@XmlEnum
public enum DeltaIndexingType {

  /**
   * DeltaIndexing is full activated..
   */
  @XmlEnumValue("full")
  FULL("full"),

  /**
   * DeltaIndexing is done additive, no delta delete is performed.
   */
  @XmlEnumValue("additive")
  ADDITIVE("additive"),

  /**
   * DeltaIndexing is only initialized for future runs. No checks for update are done, no delta delete is performed.
   */
  @XmlEnumValue("initial")
  INITIAL("initial"),

  /**
   * DeltaIndexing is disabled.
   */
  @XmlEnumValue("disabled")
  DISABLED("disabled");

  /**
   * The value.
   */
  private final String _value;

  /**
   * Instantiates a delta indexing type.
   * 
   * @param v
   *          the v
   */
  DeltaIndexingType(final String v) {
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
   * @return the delta indexing type
   */
  public static DeltaIndexingType fromValue(final String v) {
    for (final DeltaIndexingType c : DeltaIndexingType.values()) {
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
  public static String toValue(final DeltaIndexingType t) {
    if (t == null) {
      return null;
    }
    return t.value();
  }

}
