
// CHECKSTYLE:OFF

package org.eclipse.smila.management.jmx.client.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WaitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WaitType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/smila/management/jmx/client}ItemType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="equals" type="{http://www.eclipse.org/smila/management/jmx/client}EqualsOpType"/>
 *           &lt;element name="in" type="{http://www.eclipse.org/smila/management/jmx/client}InOpType"/>
 *         &lt;/choice>
 *         &lt;element name="cmd" type="{http://www.eclipse.org/smila/management/jmx/client}CmdConfigType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="pause" type="{http://www.w3.org/2001/XMLSchema}int" default="5000" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WaitType", propOrder = {
    "equals",
    "in",
    "cmd"
})
public class WaitType
    extends ItemType
{

    protected EqualsOpType equals;
    protected InOpType in;
    protected CmdConfigType cmd;
    @XmlAttribute
    protected Integer pause;

    /**
     * Gets the value of the equals property.
     * 
     * @return
     *     possible object is
     *     {@link EqualsOpType }
     *     
     */
    public EqualsOpType getEquals() {
        return equals;
    }

    /**
     * Sets the value of the equals property.
     * 
     * @param value
     *     allowed object is
     *     {@link EqualsOpType }
     *     
     */
    public void setEquals(EqualsOpType value) {
        this.equals = value;
    }

    /**
     * Gets the value of the in property.
     * 
     * @return
     *     possible object is
     *     {@link InOpType }
     *     
     */
    public InOpType getIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     * 
     * @param value
     *     allowed object is
     *     {@link InOpType }
     *     
     */
    public void setIn(InOpType value) {
        this.in = value;
    }

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

    /**
     * Gets the value of the pause property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getPause() {
        if (pause == null) {
            return  5000;
        } else {
            return pause;
        }
    }

    /**
     * Sets the value of the pause property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPause(Integer value) {
        this.pause = value;
    }

}

// CHECKSTYLE:ON
