
// CHECKSTYLE:OFF

package it.polimi.chansonnier.agent.messages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MockAttributesType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MockAttributesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Identifier"/>
 *     &lt;enumeration value="MimeType"/>
 *     &lt;enumeration value="LastModifiedDate"/>
 *     &lt;enumeration value="Content"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MockAttributesType")
@XmlEnum
public enum MockAttributesType {

    @XmlEnumValue("Identifier")
    IDENTIFIER("Identifier"),
    @XmlEnumValue("MimeType")
    MIME_TYPE("MimeType"),
    @XmlEnumValue("LastModifiedDate")
    LAST_MODIFIED_DATE("LastModifiedDate"),
    @XmlEnumValue("Content")
    CONTENT("Content");
    private final String value;

    MockAttributesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MockAttributesType fromValue(String v) {
        for (MockAttributesType c: MockAttributesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

// CHECKSTYLE:ON
