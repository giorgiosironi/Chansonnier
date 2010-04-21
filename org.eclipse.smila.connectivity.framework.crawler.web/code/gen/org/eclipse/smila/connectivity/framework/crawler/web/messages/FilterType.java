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
 * <p>Java class for FilterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FilterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BeginningPath"/>
 *     &lt;enumeration value="RegExp"/>
 *     &lt;enumeration value="ContentType"/>
 *     &lt;enumeration value="CrawlScope"/>
 *     &lt;enumeration value="HtmlMetaTag"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FilterType")
@XmlEnum
public enum FilterType {

    @XmlEnumValue("BeginningPath")
    BEGINNING_PATH("BeginningPath"),
    @XmlEnumValue("RegExp")
    REG_EXP("RegExp"),
    @XmlEnumValue("ContentType")
    CONTENT_TYPE("ContentType"),
    @XmlEnumValue("CrawlScope")
    CRAWL_SCOPE("CrawlScope"),
    @XmlEnumValue("HtmlMetaTag")
    HTML_META_TAG("HtmlMetaTag");
    private final String value;

    FilterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FilterType fromValue(String v) {
        for (FilterType c: FilterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
