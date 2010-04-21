
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.jdbc.messages;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attribute">
 *   &lt;complexContent>
 *     &lt;extension base="{}Attribute">
 *       &lt;redefine>
 *         &lt;complexType name="Attribute">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;attribute name="KeyAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="HashAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;attribute name="MimeTypeAttribute" type="{}MimeTypeAttributeType" />
 *               &lt;attribute name="Attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;sequence>
 *         &lt;element name="ColumnName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SqlType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="string"/>
 *               &lt;enumeration value="long"/>
 *               &lt;enumeration value="date"/>
 *               &lt;enumeration value="double"/>
 *               &lt;enumeration value="blob"/>
 *               &lt;enumeration value="clob"/>
 *               &lt;enumeration value="boolean"/>
 *               &lt;enumeration value="byte[]"/>
 *               &lt;enumeration value="timestamp"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder = {
    "columnName",
    "sqlType"
})
public class Attribute
    extends OriginalAttribute
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ColumnName", required = true)
    protected String columnName;
    @XmlElement(name = "SqlType", required = true)
    protected String sqlType;

    /**
     * Gets the value of the columnName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the value of the columnName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnName(String value) {
        this.columnName = value;
    }

    /**
     * Gets the value of the sqlType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSqlType() {
        return sqlType;
    }

    /**
     * Sets the value of the sqlType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSqlType(String value) {
        this.sqlType = value;
    }

}

// CHECKSTYLE:ON
