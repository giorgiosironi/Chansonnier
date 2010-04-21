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
 * <p>Java class for FollowLinksType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FollowLinksType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Follow"/>
 *     &lt;enumeration value="NoFollow"/>
 *     &lt;enumeration value="FollowLinksWithCorrespondingSelectFilter"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FollowLinksType")
@XmlEnum
public enum FollowLinksType {

    @XmlEnumValue("Follow")
    FOLLOW("Follow"),
    @XmlEnumValue("NoFollow")
    NO_FOLLOW("NoFollow"),
    @XmlEnumValue("FollowLinksWithCorrespondingSelectFilter")
    FOLLOW_LINKS_WITH_CORRESPONDING_SELECT_FILTER("FollowLinksWithCorrespondingSelectFilter");
    private final String value;

    FollowLinksType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FollowLinksType fromValue(String v) {
        for (FollowLinksType c: FollowLinksType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
