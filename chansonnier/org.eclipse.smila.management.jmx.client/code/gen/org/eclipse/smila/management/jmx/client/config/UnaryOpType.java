
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnaryOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnaryOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}BooleanOpType">
 *       &lt;sequence>
 *         &lt;element name="cmd" type="{http://www.eclipse.org/smila/management/jmx/client}CmdConfigType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnaryOpType", propOrder = {
    "cmd"
})
public abstract class UnaryOpType
    extends BooleanOpType
{

    @XmlElement(required = true)
    protected CmdConfigType cmd;

    /**
     * Gets the value of the cmd property.
     * 
     * @return
     *     possible object is
     *     {@link CmdConfigType }
     *     
     */
    public CmdConfigType getCmd() {
        return cmd;
    }

    /**
     * Sets the value of the cmd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CmdConfigType }
     *     
     */
    public void setCmd(CmdConfigType value) {
        this.cmd = value;
    }

}

// CHECKSTYLE:ON
