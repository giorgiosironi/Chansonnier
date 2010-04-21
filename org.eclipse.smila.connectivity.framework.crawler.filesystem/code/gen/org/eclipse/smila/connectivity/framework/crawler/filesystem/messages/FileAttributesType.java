/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.filesystem.messages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FileAttributesType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FileAttributesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Name"/>
 *     &lt;enumeration value="Path"/>
 *     &lt;enumeration value="Size"/>
 *     &lt;enumeration value="LastModifiedDate"/>
 *     &lt;enumeration value="Content"/>
 *     &lt;enumeration value="FileExtension"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FileAttributesType")
@XmlEnum
public enum FileAttributesType {

    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("Path")
    PATH("Path"),
    @XmlEnumValue("Size")
    SIZE("Size"),
    @XmlEnumValue("LastModifiedDate")
    LAST_MODIFIED_DATE("LastModifiedDate"),
    @XmlEnumValue("Content")
    CONTENT("Content"),
    @XmlEnumValue("FileExtension")
    FILE_EXTENSION("FileExtension");
    private final String value;

    FileAttributesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FileAttributesType fromValue(String v) {
        for (FileAttributesType c: FileAttributesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
