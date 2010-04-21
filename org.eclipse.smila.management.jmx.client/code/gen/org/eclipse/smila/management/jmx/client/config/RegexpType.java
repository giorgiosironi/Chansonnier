
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegexpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegexpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}ItemType">
 *       &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegexpType")
public class RegexpType
    extends ItemType
{

    @XmlAttribute(required = true)
    protected String pattern;
    @XmlAttribute(required = true)
    protected int group;

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

    /**
     * Gets the value of the group property.
     * 
     */
    public int getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     */
    public void setGroup(int value) {
        this.group = value;
    }

}

// CHECKSTYLE:ON
