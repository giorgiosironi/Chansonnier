
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CmdConfigType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdConfigType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}ItemType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="attribute" type="{http://www.eclipse.org/smila/management/jmx/client}AttributeType"/>
 *           &lt;element name="operation" type="{http://www.eclipse.org/smila/management/jmx/client}OperationType"/>
 *           &lt;element name="regexp" type="{http://www.eclipse.org/smila/management/jmx/client}RegexpType"/>
 *           &lt;element name="wait" type="{http://www.eclipse.org/smila/management/jmx/client}WaitType"/>
 *           &lt;element name="custom" type="{http://www.eclipse.org/smila/management/jmx/client}CustomType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdConfigType", propOrder = {
    "attributeOrOperationOrRegexp"
})
public class CmdConfigType
    extends ItemType
{

    @XmlElements({
        @XmlElement(name = "regexp", type = RegexpType.class),
        @XmlElement(name = "custom", type = CustomType.class),
        @XmlElement(name = "operation", type = OperationType.class),
        @XmlElement(name = "wait", type = WaitType.class),
        @XmlElement(name = "attribute", type = AttributeType.class)
    })
    protected List<ItemType> attributeOrOperationOrRegexp;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the attributeOrOperationOrRegexp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeOrOperationOrRegexp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeOrOperationOrRegexp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegexpType }
     * {@link CustomType }
     * {@link OperationType }
     * {@link WaitType }
     * {@link AttributeType }
     * 
     * 
     */
    public List<ItemType> getAttributeOrOperationOrRegexp() {
        if (attributeOrOperationOrRegexp == null) {
            attributeOrOperationOrRegexp = new ArrayList<ItemType>();
        }
        return this.attributeOrOperationOrRegexp;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}

// CHECKSTYLE:ON
