/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator, Ivan Churkin (brox IT Solutions GmbH)
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.web.messages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FieldAttributeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FieldAttributeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Url"/>
 *     &lt;enumeration value="Title"/>
 *     &lt;enumeration value="Content"/>
 *     &lt;enumeration value="MimeType"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FieldAttributeType")
@XmlEnum
public enum FieldAttributeType {

    @XmlEnumValue("Url")
    URL("Url"),
    @XmlEnumValue("Title")
    TITLE("Title"),
    @XmlEnumValue("Content")
    CONTENT("Content"),
    @XmlEnumValue("MimeType")
    MIME_TYPE("MimeType");
    private final String value;

    FieldAttributeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FieldAttributeType fromValue(String v) {
        for (FieldAttributeType c: FieldAttributeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
