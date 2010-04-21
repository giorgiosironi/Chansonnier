
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OperationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}PropertyType">
 *       &lt;sequence>
 *         &lt;element name="parameter" type="{http://www.eclipse.org/smila/management/jmx/client}ParameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="voidType" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationType", propOrder = {
    "parameter"
})
public class OperationType
    extends PropertyType
{

    protected List<ParameterType> parameter;
    @XmlAttribute
    protected Boolean voidType;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterType }
     * 
     * 
     */
    public List<ParameterType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParameterType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the voidType property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isVoidType() {
        if (voidType == null) {
            return false;
        } else {
            return voidType;
        }
    }

    /**
     * Sets the value of the voidType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVoidType(Boolean value) {
        this.voidType = value;
    }

}

// CHECKSTYLE:ON
