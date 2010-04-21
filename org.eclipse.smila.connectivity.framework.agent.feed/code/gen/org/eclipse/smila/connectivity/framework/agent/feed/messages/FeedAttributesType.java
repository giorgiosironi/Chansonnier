/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.agent.feed.messages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FeedAttributesType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FeedAttributesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FeedAuthors"/>
 *     &lt;enumeration value="FeedCategories"/>
 *     &lt;enumeration value="FeedContributors"/>
 *     &lt;enumeration value="FeedCopyright"/>
 *     &lt;enumeration value="FeedDescription"/>
 *     &lt;enumeration value="FeedEncoding"/>
 *     &lt;enumeration value="FeedType"/>
 *     &lt;enumeration value="FeedImage"/>
 *     &lt;enumeration value="FeedLanguage"/>
 *     &lt;enumeration value="FeedLinks"/>
 *     &lt;enumeration value="FeedPublishDate"/>
 *     &lt;enumeration value="FeedTitle"/>
 *     &lt;enumeration value="FeedUri"/>
 *     &lt;enumeration value="Authors"/>
 *     &lt;enumeration value="Categories"/>
 *     &lt;enumeration value="Contents"/>
 *     &lt;enumeration value="Contributors"/>
 *     &lt;enumeration value="Description"/>
 *     &lt;enumeration value="Enclosures"/>
 *     &lt;enumeration value="Links"/>
 *     &lt;enumeration value="PublishDate"/>
 *     &lt;enumeration value="Title"/>
 *     &lt;enumeration value="Uri"/>
 *     &lt;enumeration value="UpdateDate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FeedAttributesType")
@XmlEnum
public enum FeedAttributesType {

    @XmlEnumValue("FeedAuthors")
    FEED_AUTHORS("FeedAuthors"),
    @XmlEnumValue("FeedCategories")
    FEED_CATEGORIES("FeedCategories"),
    @XmlEnumValue("FeedContributors")
    FEED_CONTRIBUTORS("FeedContributors"),
    @XmlEnumValue("FeedCopyright")
    FEED_COPYRIGHT("FeedCopyright"),
    @XmlEnumValue("FeedDescription")
    FEED_DESCRIPTION("FeedDescription"),
    @XmlEnumValue("FeedEncoding")
    FEED_ENCODING("FeedEncoding"),
    @XmlEnumValue("FeedType")
    FEED_TYPE("FeedType"),
    @XmlEnumValue("FeedImage")
    FEED_IMAGE("FeedImage"),
    @XmlEnumValue("FeedLanguage")
    FEED_LANGUAGE("FeedLanguage"),
    @XmlEnumValue("FeedLinks")
    FEED_LINKS("FeedLinks"),
    @XmlEnumValue("FeedPublishDate")
    FEED_PUBLISH_DATE("FeedPublishDate"),
    @XmlEnumValue("FeedTitle")
    FEED_TITLE("FeedTitle"),
    @XmlEnumValue("FeedUri")
    FEED_URI("FeedUri"),
    @XmlEnumValue("Authors")
    AUTHORS("Authors"),
    @XmlEnumValue("Categories")
    CATEGORIES("Categories"),
    @XmlEnumValue("Contents")
    CONTENTS("Contents"),
    @XmlEnumValue("Contributors")
    CONTRIBUTORS("Contributors"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("Enclosures")
    ENCLOSURES("Enclosures"),
    @XmlEnumValue("Links")
    LINKS("Links"),
    @XmlEnumValue("PublishDate")
    PUBLISH_DATE("PublishDate"),
    @XmlEnumValue("Title")
    TITLE("Title"),
    @XmlEnumValue("Uri")
    URI("Uri"),
    @XmlEnumValue("UpdateDate")
    UPDATE_DATE("UpdateDate");
    private final String value;

    FeedAttributesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FeedAttributesType fromValue(String v) {
        for (FeedAttributesType c: FeedAttributesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
