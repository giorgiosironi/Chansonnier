/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.agent.jobfile.messages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ElementAttributeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ElementAttributeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Path"/>
 *     &lt;enumeration value="Size"/>
 *     &lt;enumeration value="LastModifiedDate"/>
 *     &lt;enumeration value="Content"/>
 *     &lt;enumeration value="Name"/>
 *     &lt;enumeration value="FileExtension"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ElementAttributeType")
@XmlEnum
public enum ElementAttributeType {

    @XmlEnumValue("Path")
    PATH("Path"),
    @XmlEnumValue("Size")
    SIZE("Size"),
    @XmlEnumValue("LastModifiedDate")
    LAST_MODIFIED_DATE("LastModifiedDate"),
    @XmlEnumValue("Content")
    CONTENT("Content"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("FileExtension")
    FILE_EXTENSION("FileExtension");
    private final String value;

    ElementAttributeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ElementAttributeType fromValue(String v) {
        for (ElementAttributeType c: ElementAttributeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
